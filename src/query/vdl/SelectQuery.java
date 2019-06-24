package query.vdl;

import common.DatabaseConstants;
import common.DatabaseHelper;
import datatypes.base.DataType;
import common.CatalogDatabaseHelper;
import common.Utils;
import datatypes.DataType_Text;
import javafx.util.Pair;
import io.IOManager;
import io.model.DataRecord;
import io.model.InternalCondition;
import query.base.IQuery;
import query.model.parser.Condition;
import query.model.parser.Literal;
import query.model.result.Record;
import query.model.result.Result;
import query.model.result.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectQuery implements IQuery
{
    public String databaseName;
    public String tableName;
    public ArrayList<String> columns;
    private boolean isSelectAll;
    private ArrayList<Condition> conditions = new ArrayList<>();
    public SelectQuery(String databaseName, String tableName, ArrayList<String> columns, ArrayList<Condition> conditions, boolean isSelectAll)
    {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = columns;
        this.conditions = conditions;
        this.isSelectAll = isSelectAll;
    }
    @Override
    public Result ExecuteQuery()
    {
        try
        {
            ResultSet resultSet = ResultSet.CreateResultSet();
            ArrayList<Record> records = GetData();
            resultSet.setColumns(this.columns);
            for (Record record : records)
                resultSet.addRecord(record);
            return resultSet;
        } catch (Exception e)
        {
            Utils.printMessage(e.getMessage());
        }
        return null;
    }
    @Override
    public boolean ValidateQuery()
    {
        try
        {
            IOManager manager = new IOManager();
            if (!manager.checkTableExists(this.databaseName, tableName))
            {
                Utils.printMissingTableError(this.databaseName, tableName);
                return false;
            }
            Pair<HashMap<String, Integer>, HashMap<Integer, String>> maps = mapOrdinalIdToColumnName(this.tableName);
            HashMap<String, Integer> columnToIdMap = maps.getKey();
            HashMap<String, Integer> columnDataTypeMapping = DatabaseHelper.getDatabaseHelper().fetchAllTableColumnDataTypes(this.databaseName, tableName);
            if (conditions != null)
            {
                List<String> retrievedColumns = DatabaseHelper.getDatabaseHelper().fetchAllTableColumns(this.databaseName, tableName);
                for (Condition condition : conditions)
                {
                    if (!Utils.checkConditionValueDataTypeValidity(columnDataTypeMapping, retrievedColumns, condition))
                        return false;
                }
            }
            if (this.columns != null)
            {
                for (String column : this.columns)
                {
                    if (!columnToIdMap.containsKey(column))
                    {
                        Utils.printMessage(String.format("ERROR(112CM): Unknown column '%s' in table '%s'", column, this.tableName));
                        return false;
                    }
                }
            }

            if (conditions != null)
            {
                for (Condition condition : conditions)
                {
                    if (!columnToIdMap.containsKey(condition.column))
                    {
                        Utils.printMessage((String.format("ERROR(112CM): Unknown column '%s' in table '%s'", condition.column, this.tableName)));
                        return false;
                    }
                }
            }
        } catch (Exception e)
        {
            Utils.printMessage(e.getMessage());
            return false;
        }
        return true;
    }
    private ArrayList<Record> GetData() throws Exception
    {
        ArrayList<Record> records = new ArrayList<>();
        Pair<HashMap<String, Integer>, HashMap<Integer, String>> maps = mapOrdinalIdToColumnName(this.tableName);
        HashMap<String, Integer> columnToIdMap = maps.getKey();
        ArrayList<Byte> columnsList = new ArrayList<>();
        List<DataRecord> internalRecords;
        IOManager manager = new IOManager();
        List<InternalCondition> conditions = new ArrayList<>();
        InternalCondition internalCondition = null;
        if(this.conditions != null)
        {
            for(Condition condition : this.conditions)
            {
                internalCondition = new InternalCondition();
                if (columnToIdMap.containsKey(condition.column))
                    internalCondition.setIndex(columnToIdMap.get(condition.column).byteValue());
                datatypes.base.DataType dataType = DataType.CreateDT(condition.value);
                internalCondition.setValue(dataType);
                Short operatorShort = Utils.ConvertFromOperator(condition.operator);
                internalCondition.setConditionType(operatorShort);
                conditions.add(internalCondition);
            }
        }
        if(this.columns == null)
        {
            internalRecords = manager.findRecord(this.databaseName,this.tableName, conditions, false);
            HashMap<Integer, String> idToColumnMap = maps.getValue();
            this.columns = new ArrayList<>();
            for (int i=0; i<columnToIdMap.size();i++)
            {
                if(idToColumnMap.containsKey(i))
                {
                    columnsList.add((byte)i);
                    this.columns.add(idToColumnMap.get(i));
                }
            }
        }
        else
        {
            for (String column : this.columns)
            {
                if (columnToIdMap.containsKey(column))
                    columnsList.add(columnToIdMap.get(column).byteValue());
            }
            internalRecords = manager.findRecord(this.databaseName, this.tableName, conditions, columnsList, false);
        }
        Byte[] columnIds = new Byte[columnsList.size()];
        int k = 0;
        for(Byte column : columnsList)
        {
            columnIds[k] = column;
            k++;
        }
        HashMap<Integer, String> idToColumnMap = maps.getValue();
        for(DataRecord internalRecord : internalRecords)
        {
            Object[] dataTypes = new DataType[internalRecord.getColumnValueList().size()];
            k=0;
            for(Object columnValue : internalRecord.getColumnValueList())
            {
                dataTypes[k] = columnValue;
                k++;
            }
            Record record = Record.CreateRecord();
            for(int i=0;i<columnIds.length;i++)
            {
                Literal literal;
                if(idToColumnMap.containsKey((int)columnIds[i]))
                {
                    literal = Literal.CreateLiteral((DataType)dataTypes[i], Utils.resolveClass(dataTypes[i]));
                    record.put(idToColumnMap.get((int)columnIds[i]), literal);
                }
            }
            records.add(record);
        }
        return records;
    }
    private Pair<HashMap<String, Integer>, HashMap<Integer, String>> mapOrdinalIdToColumnName(String tableName) throws Exception
    {
        HashMap<Integer, String> idToColumnMap = new HashMap<>();
        HashMap<String, Integer> columnToIdMap = new HashMap<>();
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCondition(CatalogDatabaseHelper.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new DataType_Text(tableName)));
        IOManager manager = new IOManager();
        List<DataRecord> records = manager.findRecord(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME, conditions, false);
        for (int i = 0; i < records.size(); i++)
        {
            DataRecord record = records.get(i);
            Object object = record.getColumnValueList().get(CatalogDatabaseHelper.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);
            idToColumnMap.put(i, ((DataType) object).getStringValue());
            columnToIdMap.put(((DataType) object).getStringValue(), i);
        }
        return new Pair<>(columnToIdMap, idToColumnMap);
    }
};