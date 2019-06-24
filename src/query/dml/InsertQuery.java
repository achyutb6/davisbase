package query.dml;

import common.DatabaseConstants;
import common.DatabaseHelper;
import datatypes.base.DataType;
import common.Utils;
import datatypes.*;
import exceptions.InternalException;
import io.IOManager;
import io.model.DataRecord;
import query.base.IQuery;
import query.model.parser.Literal;
import query.model.result.Result;

import java.util.*;

public class InsertQuery implements IQuery {
    public String tableName;
    public ArrayList<String> columns;
    private ArrayList<Literal> values;
    public String databaseName;

    public InsertQuery(String databaseName, String tableName, ArrayList<String> columns, ArrayList<Literal> values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
        this.databaseName = databaseName;
    }

    @Override
    public Result ExecuteQuery() {
        try {
            IOManager manager = new IOManager();
            List<String> retrievedColumns = DatabaseHelper.getDatabaseHelper().fetchAllTableColumns(this.databaseName, tableName);
            HashMap<String, Integer> columnDataTypeMapping = DatabaseHelper.getDatabaseHelper().fetchAllTableColumnDataTypes(this.databaseName, tableName);

            DataRecord record = new DataRecord();
            generateRecords(record.getColumnValueList(), columnDataTypeMapping, retrievedColumns);

            int rowID = findRowID(retrievedColumns);
            record.setRowId(rowID);
            record.populateSize();

            Result result = null;
            boolean status = manager.writeRecord(this.databaseName, tableName, record);
            if (status) {
                result = new Result(1);
            } else {
                Utils.printMessage("ERROR(110F): Unable to insert record.");
            }

            return result;
        }
        catch (InternalException e) {
            Utils.printMessage(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean ValidateQuery() {
        try {
            IOManager manager = new IOManager();
            if (!manager.checkTableExists(this.databaseName, tableName)) {
                Utils.printMissingTableError(this.databaseName, tableName);
                return false;
            }

            List<String> retrievedColumns = DatabaseHelper.getDatabaseHelper().fetchAllTableColumns(this.databaseName, tableName);
            HashMap<String, Integer> columnDataTypeMapping = DatabaseHelper.getDatabaseHelper().fetchAllTableColumnDataTypes(this.databaseName, tableName);

            if (columns == null) {
                if (values.size() > retrievedColumns.size()) {
                    Utils.printMessage("ERROR(110C): Column count doesn't match value count at row 1");
                    return false;
                }

                Utils utils = new Utils();
                if (!utils.checkDataTypeValidity(columnDataTypeMapping, retrievedColumns, values)) {
                    return false;
                }
            } else {
                if (columns.size() > retrievedColumns.size()) {
                    Utils.printMessage("ERROR(110C): Column count doesn't match value count at row 1");
                    return false;
                }

                boolean areColumnsValid = checkColumnValidity(retrievedColumns);
                if (!areColumnsValid) {
                    return false;
                }

                boolean areColumnsDataTypeValid = validateColumnDataTypes(columnDataTypeMapping);
                if (!areColumnsDataTypeValid) {
                    return false;
                }
            }

            boolean isNullConstraintValid = checkNullConstraint(retrievedColumns);
            if (!isNullConstraintValid) {
                return false;
            }

            boolean isPrimaryKeyConstraintValid = checkPrimaryKeyConstraint(retrievedColumns);
            if (!isPrimaryKeyConstraintValid) {
                return false;
            }
        }
        catch (InternalException e) {
            Utils.printMessage(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean validateColumnDataTypes(HashMap<String, Integer> columnDataTypeMapping) {
        return checkColumnDataTypeValidity(columnDataTypeMapping);
    }

    private boolean checkColumnValidity(List<String> retrievedColumns) {
        boolean columnsValid = true;
        String invalidColumn = "";

        for (String tableColumn : columns) {
            if (!retrievedColumns.contains(tableColumn.toLowerCase())) {
                columnsValid = false;
                invalidColumn = tableColumn;
                break;
            }
        }

        if (!columnsValid) {
            Utils.printMessage("ERROR(110C): Invalid column '" + invalidColumn + "'");
            return false;
        }

        return true;
    }

    private boolean checkNullConstraint(List<String> retrievedColumnNames) throws InternalException {
        HashMap<String, Integer> columnsList = new HashMap<>();

        if (columns != null) {
            for (int i = 0; i < columns.size(); i++) {
                columnsList.put(columns.get(i), i);
            }
        }
        else {
            for (int i = 0; i < values.size(); i++) {
                columnsList.put(retrievedColumnNames.get(i), i);
            }
        }

        return DatabaseHelper.getDatabaseHelper().checkNullConstraint(this.databaseName, tableName, columnsList);
    }

    private boolean checkPrimaryKeyConstraint(List<String> retrievedColumnNames) throws InternalException {

        String primaryKeyColumnName = DatabaseHelper.getDatabaseHelper().getTablePrimaryKey(databaseName, tableName);
        List<String> columnList = (columns != null) ? columns : retrievedColumnNames;

        if (primaryKeyColumnName.length() > 0) {
                if (columnList.contains(primaryKeyColumnName.toLowerCase())) {
                    int primaryKeyIndex = columnList.indexOf(primaryKeyColumnName);
                    if (DatabaseHelper.getDatabaseHelper().checkIfValueForPrimaryKeyExists(this.databaseName, tableName, Integer.parseInt(values.get(primaryKeyIndex).value))) {
                        Utils.printMessage("ERROR(110P): Duplicate entry '" + values.get(primaryKeyIndex).value + "' for key 'PRIMARY'");
                        return false;
                    }
                }
        }

        return true;
    }

    private boolean checkColumnDataTypeValidity(HashMap<String, Integer> columnDataTypeMapping) {
        String invalidColumn = "";

        for (String columnName : columns) {
            int dataTypeIndex = columnDataTypeMapping.get(columnName);
            int idx = columns.indexOf(columnName);
            Literal literal = values.get(idx);

            if (literal.type != Utils.internalDataTypeToModelDataType((byte)dataTypeIndex)) {
                if (Utils.canUpdateLiteralDataType(literal, dataTypeIndex)) {
                    continue;
                }

                invalidColumn = columnName;
                break;
            }
        }

        boolean valid = invalidColumn.length() <= 0;

        if (!valid) {
            Utils.printMessage("ERROR(110CV): Invalid value for column '" + invalidColumn  + "'");
            return false;
        }

        return true;
    }

    private void generateRecords(List<Object> columnList, HashMap<String, Integer> columnDataTypeMapping, List<String> retrievedColumns) {
        for (int i=0; i < retrievedColumns.size(); i++) {
            String column = retrievedColumns.get(i);

            if (columns != null) {
                if (columns.contains(column)) {
                    Byte dataType = (byte)columnDataTypeMapping.get(column).intValue();

                    int idx = columns.indexOf(column);

                    datatypes.base.DataType obj = getDataTypeObject(dataType);
                    String val = values.get(idx).toString();

                    obj.setValue(getDataTypeValue(dataType, val));
                    columnList.add(obj);
                } else {
                    Byte dataType = (byte)columnDataTypeMapping.get(column).intValue();
                    DataType obj = getDataTypeObject(dataType);
                    obj.setNull(true);
                    columnList.add(obj);
                }
            }
            else {

                if (i < values.size()) {
                    Byte dataType = (byte) columnDataTypeMapping.get(column).intValue();

                    int columnIndex = retrievedColumns.indexOf(column);
                    DataType obj = getDataTypeObject(dataType);
                    String val = values.get(columnIndex).toString();

                    obj.setValue(getDataTypeValue(dataType, val));
                    columnList.add(obj);
                }
                else {
                    Byte dataType = (byte)columnDataTypeMapping.get(column).intValue();
                    DataType obj = getDataTypeObject(dataType);
                    obj.setNull(true);
                    columnList.add(obj);
                }
            }
        }
    }

    private DataType getDataTypeObject(byte dataType) {

        switch (dataType) {
            case DatabaseConstants.TINYINT: {
                return new DataType_TinyInt();
            }
            case DatabaseConstants.SMALLINT: {
                return new DataType_SmallInt();
            }
            case DatabaseConstants.INT: {
                return new DataType_Int();
            }
            case DatabaseConstants.BIGINT: {
                return new DataType_BigInt();
            }
            case DatabaseConstants.REAL: {
                return new DataType_Real();
            }
            case DatabaseConstants.DOUBLE: {
                return new DataType_Double();
            }
            case DatabaseConstants.DATE: {
                return new DataType_Date();

            }
            case DatabaseConstants.DATETIME: {
                return new DataType_DateTime();
            }
            case DatabaseConstants.TEXT: {
                return new DataType_Text();
            }
            default: {
                return new DataType_Text();
            }
        }
    }

    private Object getDataTypeValue(byte dataType, String value) {

        switch (dataType) {
            case DatabaseConstants.TINYINT: {
                return Byte.parseByte(value);
            }
            case DatabaseConstants.SMALLINT: {
                return Short.parseShort(value);
            }
            case DatabaseConstants.INT: {
                return Integer.parseInt(value);
            }
            case DatabaseConstants.BIGINT: {
                return Long.parseLong(value);
            }
            case DatabaseConstants.REAL: {
                return Float.parseFloat(value);
            }
            case DatabaseConstants.DOUBLE: {
                return Double.parseDouble(value);
            }
            case DatabaseConstants.DATE: {
                return Utils.getDateEpoc(value, true);
            }
            case DatabaseConstants.DATETIME: {
                return Utils.getDateEpoc(value, false);
            }
            case DatabaseConstants.TEXT: {
                return value;
            }
            default: {
                return value;
            }
        }
    }

    private int findRowID (List<String> retrievedList) throws InternalException {
        DatabaseHelper helper = DatabaseHelper.getDatabaseHelper();
        int rowCount = helper.getTableRecordCount(this.databaseName, tableName);
        String primaryKeyColumnName = helper.getTablePrimaryKey(databaseName, tableName);
        if (primaryKeyColumnName.length() > 0) {
            int primaryKeyIndex = (columns != null) ? columns.indexOf(primaryKeyColumnName) : retrievedList.indexOf(primaryKeyColumnName);
            return Integer.parseInt(values.get(primaryKeyIndex).value);
        }
        else {
            return rowCount + 1;
        }
    }
}