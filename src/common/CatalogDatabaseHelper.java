package common;
import datatypes.DataType_Int;
import datatypes.DataType_Text;
import query.model.parser.DataTypeEnum;
import exceptions.InternalException;
import io.IOManager;
import io.model.DataRecord;
import io.model.InternalColumn;
import io.model.InternalCondition;
import io.model.Page;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CatalogDatabaseHelper
{

    public static final byte TABLES_TABLE_SCHEMA_ROWID = 0;
    public static final byte TABLES_TABLE_SCHEMA_DATABASE_NAME = 1;
    public static final byte TABLES_TABLE_SCHEMA_TABLE_NAME = 2;
    public static final byte TABLES_TABLE_SCHEMA_RECORD_COUNT = 3;
    public static final byte TABLES_TABLE_SCHEMA_COL_TBL_ST_ROWID = 4;
    public static final byte TABLES_TABLE_SCHEMA_NXT_AVL_COL_TBL_ROWID = 5;

    public static final byte COLUMNS_TABLE_SCHEMA_ROWID = 0;
    public static final byte COLUMNS_TABLE_SCHEMA_DATABASE_NAME = 1;
    public static final byte COLUMNS_TABLE_SCHEMA_TABLE_NAME = 2;
    public static final byte COLUMNS_TABLE_SCHEMA_COLUMN_NAME = 3;
    public static final byte COLUMNS_TABLE_SCHEMA_DATA_TYPE = 4;
    public static final byte COLUMNS_TABLE_SCHEMA_COLUMN_KEY = 5;
    public static final byte COLUMNS_TABLE_SCHEMA_ORDINAL_POSITION = 6;
    public static final byte COLUMNS_TABLE_SCHEMA_IS_NULLABLE = 7;
    public static final String PRIMARY_KEY_IDENTIFIER = "PRI";

    public static void InitializeDatabase()
    {
        File baseDir = new File(DatabaseConstants.DEFAULT_DATA_DIRNAME);
        if(!baseDir.exists())
        {
            File catalogDir = new File(DatabaseConstants.DEFAULT_DATA_DIRNAME + "/" + DatabaseConstants.DEFAULT_CATALOG_DATABASENAME);
            if(!catalogDir.exists())
            {
                if(catalogDir.mkdirs())
                    new CatalogDatabaseHelper().createCatalogDatabase();
            }
        }
    }

    public boolean createCatalogDatabase()
    {
        try
        {
            IOManager manager = new IOManager();
            manager.createTable(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME + DatabaseConstants.DEFAULT_FILE_EXTENSION);
            manager.createTable(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME + DatabaseConstants.DEFAULT_FILE_EXTENSION);
            int startingRowId = this.updateSystemTablesTable(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME, 6);
            startingRowId *= this.updateSystemTablesTable(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME, 8);
            if (startingRowId >= 0)
            {
                List<InternalColumn> columns = new ArrayList<>();
                columns.add(new InternalColumn("rowid", DataTypeEnum.INT.toString(), false, false));
                columns.add(new InternalColumn("database_name", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("table_name", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("record_count", DataTypeEnum.INT.toString(), false, false));
                columns.add(new InternalColumn("col_tbl_st_rowid", DataTypeEnum.INT.toString(), false, false));
                columns.add(new InternalColumn("nxt_avl_col_tbl_rowid", DataTypeEnum.INT.toString(), false, false));
                this.updateSystemColumnsTable(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME, 1, columns);
                columns.clear();
                columns.add(new InternalColumn("rowid", DataTypeEnum.INT.toString(), false, false));
                columns.add(new InternalColumn("database_name", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("table_name", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("column_name", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("data_type", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("column_key", DataTypeEnum.TEXT.toString(), false, false));
                columns.add(new InternalColumn("ordinal_position", DataTypeEnum.TINYINT.toString(), false, false));
                columns.add(new InternalColumn("is_nullable", DataTypeEnum.TEXT.toString(), false, false));
                this.updateSystemColumnsTable(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME, 7, columns);
            }
            return true;
        } catch (InternalException e)
        {
            Utils.printMessage(e.getMessage());
        }
        return false;
    }
    public int updateSystemTablesTable(String databaseName, String tableName, int columnCount)
    {
        try
        {
            IOManager manager = new IOManager();
            List<InternalCondition> conditions = new ArrayList<>();
            conditions.add(InternalCondition.CreateCondition(CatalogDatabaseHelper.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new DataType_Text(tableName)));
            conditions.add(InternalCondition.CreateCondition(CatalogDatabaseHelper.TABLES_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new DataType_Text(databaseName)));
            List<DataRecord> result = manager.findRecord(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME, conditions, true);
            if (result != null && result.size() == 0)
            {
                int returnValue = 1;
                Page<DataRecord> page = manager.getLastRecordAndPage(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME);
                DataRecord lastRecord = null;
                if (page.getPageRecords().size() > 0)
                    lastRecord = page.getPageRecords().get(0);
                DataRecord record = new DataRecord();
                if (lastRecord == null)
                    record.setRowId(1);
                else
                    record.setRowId(lastRecord.getRowId() + 1);
                record.getColumnValueList().add(new DataType_Int(record.getRowId()));
                record.getColumnValueList().add(new DataType_Text(databaseName));
                record.getColumnValueList().add(new DataType_Text(tableName));
                record.getColumnValueList().add(new DataType_Int(0));
                if (lastRecord == null)
                {
                    record.getColumnValueList().add(new DataType_Int(1));
                    record.getColumnValueList().add(new DataType_Int(columnCount + 1));
                }
                else
                {
                    DataType_Int startingColumnIndex = (DataType_Int) lastRecord.getColumnValueList().get(CatalogDatabaseHelper.TABLES_TABLE_SCHEMA_NXT_AVL_COL_TBL_ROWID);
                    returnValue = startingColumnIndex.getValue();
                    record.getColumnValueList().add(new DataType_Int(returnValue));
                    record.getColumnValueList().add(new DataType_Int(returnValue + columnCount));
                }
                record.populateSize();
                manager.writeRecord(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME, record);
                return returnValue;
            }
            else
            {
                Utils.printMessage(String.format("Table '%s.%s' already exists.", databaseName, tableName));
                return -1;
            }
        } catch (InternalException e)
        {
            Utils.printMessage(e.getMessage());
            return -1;
        }
    }
    public boolean updateSystemColumnsTable(String databaseName, String tableName, int startingRowId, List<InternalColumn> columns) {
        try
        {
            IOManager manager = new IOManager();
            if (columns != null && columns.size() == 0) return false;
            int i;
            for (i=0; i < columns.size(); i++)
            {
                DataRecord record = new DataRecord();
                record.setRowId(startingRowId++);
                record.getColumnValueList().add(new DataType_Int(record.getRowId()));
                record.getColumnValueList().add(new DataType_Text(databaseName));
                record.getColumnValueList().add(new DataType_Text(tableName));
                record.getColumnValueList().add(new DataType_Text(columns.get(i).getName()));
                record.getColumnValueList().add(new DataType_Text(columns.get(i).getDataType()));
                record.getColumnValueList().add(new DataType_Text(columns.get(i).getStringIsPrimary()));
                record.getColumnValueList().add(new DataType_Int(i + 1));
                record.getColumnValueList().add(new DataType_Text(columns.get(i).getStringIsNullable()));
                record.populateSize();
                if (!manager.writeRecord(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME, record))
                    break;
            }
            return true;
        } catch (InternalException e)
        {
            Utils.printMessage(e.getMessage());
        }
        return false;
    }

    public static int incrementRowCount(String d, String t)
    {
        return updateRowCount(d, t, 1);
    }

    public static int decrementRowCount(String d, String t)
    {
        return updateRowCount(d, t, -1);
    }
    private static int updateRowCount(String d, String t, int rc)
    {
        try
        {
            IOManager manager = new IOManager();
            List<InternalCondition> conditions = new ArrayList<>();
            conditions.add(InternalCondition.CreateCondition(CatalogDatabaseHelper.TABLES_TABLE_SCHEMA_DATABASE_NAME, InternalCondition.EQUALS, new DataType_Text(d)));
            conditions.add(InternalCondition.CreateCondition(CatalogDatabaseHelper.TABLES_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new DataType_Text(t)));
            List<Byte> updateColumnsIndexList = new ArrayList<>();
            updateColumnsIndexList.add(CatalogDatabaseHelper.TABLES_TABLE_SCHEMA_RECORD_COUNT);
            List<Object> updateValueList = new ArrayList<>();
            updateValueList.add(new DataType_Int(rc));
            return manager.updateRecord(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME, conditions, updateColumnsIndexList, updateValueList, true);
        } catch (InternalException e)
        {
            Utils.printMessage(e.getMessage());
        }
        return -1;
    }
};