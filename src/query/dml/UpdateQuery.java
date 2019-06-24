package query.dml;

import common.DatabaseHelper;
import io.model.InternalCondition;
import query.model.parser.Condition;
import query.base.IQuery;
import query.model.parser.Literal;
import query.model.result.Result;
import common.Utils;
import datatypes.base.DataType;
import exceptions.InternalException;
import io.IOManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateQuery implements IQuery {
    public String databaseName;
    public String tableName;
    private String columnName;
    public Literal value;
    public Condition condition;

    public UpdateQuery(String databaseName, String tableName, String columnName, Literal value, Condition condition){
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.value = value;
        this.condition = condition;
    }

    @Override
    public Result ExecuteQuery() {
        try {
            IOManager manager = new IOManager();
            DatabaseHelper helper = DatabaseHelper.getDatabaseHelper();

            HashMap<String, Integer> columnDataTypeMapping = helper.fetchAllTableColumnDataTypes(this.databaseName, tableName);
            List<String> retrievedColumns = helper.fetchAllTableColumns(this.databaseName, tableName);
            InternalCondition internalCondition = getSearchCondition(retrievedColumns, columnDataTypeMapping);
            List<Byte> updateColumnIndexList = getUpdateColumnIndexList(retrievedColumns);
            List<Object> updateColumnValueList = getUpdateColumnValueList(columnDataTypeMapping);

            int rowsAffected = manager.updateRecord(databaseName, tableName, internalCondition, updateColumnIndexList, updateColumnValueList, false);

            return new Result(rowsAffected);
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

            DatabaseHelper helper = DatabaseHelper.getDatabaseHelper();

            List<String> retrievedColumns = helper.fetchAllTableColumns(this.databaseName, tableName);
            HashMap<String, Integer> columnDataTypeMapping = helper.fetchAllTableColumnDataTypes(this.databaseName, tableName);

            if (this.condition == null) {

                return checkColumnValidity(retrievedColumns, false)
                        && checkValueDataTypeValidity(columnDataTypeMapping, retrievedColumns, false);

            } else {

                if (!checkColumnValidity(retrievedColumns, true)) {
                    return false;
                }

                if (!checkColumnValidity(retrievedColumns, false)) {
                    return false;
                }

                if (!checkValueDataTypeValidity(columnDataTypeMapping, retrievedColumns, true)) {
                    return false;
                }

                if (!checkValueDataTypeValidity(columnDataTypeMapping, retrievedColumns, false)) {
                    return false;
                }
            }

            return true;
        }
        catch (InternalException e) {
            Utils.printMessage(e.getMessage());
        }
        return false;
    }

    private boolean checkValueDataTypeValidity(HashMap<String, Integer> columnDataTypeMapping, List<String> columnsList, boolean isConditionCheck) {
        String invalidColumn = "";

        String column = isConditionCheck ? condition.column : columnName;
        Literal columnValue = isConditionCheck ? condition.value : value;

        if (columnsList.contains(column)) {
            int dataTypeIndex = columnDataTypeMapping.get(column);

            if (columnValue.type != Utils.internalDataTypeToModelDataType((byte)dataTypeIndex)) {

                if (Utils.canUpdateLiteralDataType(columnValue, dataTypeIndex)) {
                    return true;
                }
                invalidColumn = column;
            }
        }

        boolean valid = invalidColumn.length() <= 0;
        if (!valid) {
            Utils.printMessage("ERROR(111CV): The value of the column " + invalidColumn + " is invalid.");
        }

        return valid;
    }

    private boolean checkColumnValidity(List<String> retrievedColumns, boolean isConditionCheck) {
        boolean columnsValid = true;
        String invalidColumn = "";

        String tableColumn = isConditionCheck ? condition.column : columnName;
        if (!retrievedColumns.contains(tableColumn.toLowerCase())) {
            columnsValid = false;
            invalidColumn = tableColumn;
        }

        if (!columnsValid) {
            Utils.printMessage("ERROR(111CM): Column " + invalidColumn + " is not present in the table " + tableName + ".");
            return false;
        }

        return true;
    }

    private InternalCondition getSearchCondition(List<String> fetchedColumnList, HashMap<String, Integer> columnDataTypeMapping) {
        InternalCondition internalCondition = new InternalCondition();
        if(condition != null) {
            internalCondition.setIndex((byte) fetchedColumnList.indexOf(condition.column));
            byte dataTypeIndex = (byte)columnDataTypeMapping.get(this.condition.column).intValue();
            DataType dataTypeObject = DataType.createSystemDT(this.condition.value.value, dataTypeIndex);
            internalCondition.setValue(dataTypeObject);
            internalCondition.setConditionType(Utils.ConvertFromOperator(condition.operator));
        }
        return internalCondition;
    }

    private List<Byte> getUpdateColumnIndexList(List<String>retrievedList) {
        List<Byte> list = new ArrayList<>();
        int idx = retrievedList.indexOf(columnName);
        list.add((byte)idx);

        return list;
    }

    private List<Object> getUpdateColumnValueList(HashMap<String, Integer> columnDataTypeMapping) {
        List<Object> list = new ArrayList<>();
        byte dataTypeIndex = (byte) columnDataTypeMapping.get(columnName).intValue();

        DataType dataType = DataType.createSystemDT(value.value, dataTypeIndex);
        list.add(dataType);

        return list;
    }
}
