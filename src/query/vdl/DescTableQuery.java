package query.vdl;

import common.DatabaseConstants;
import common.DatabaseHelper;
import common.Utils;
import query.base.IQuery;
import query.model.parser.Condition;
import query.model.result.Result;
import java.util.ArrayList;

public class DescTableQuery implements IQuery
{

    public String databaseName;
    public String tableName;
    public DescTableQuery(String databaseName, String tableName)
    {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }
    @Override
    public Result ExecuteQuery()
    {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("column_name");
        columns.add("data_type");
        columns.add("column_key");
        columns.add("is_nullable");
        ArrayList<Condition> conditionList = new ArrayList<>();
        conditionList.add(Condition.CreateCondition(String.format("database_name = '%s'", this.databaseName)));
        conditionList.add(Condition.CreateCondition(String.format("table_name = '%s'", this.tableName)));
        IQuery query = new SelectQuery(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME, columns, conditionList, false);
        if(query.ValidateQuery())
            return query.ExecuteQuery();
        return null;
    }
    @Override
    public boolean ValidateQuery()
    {
        boolean tableExists = DatabaseHelper.getDatabaseHelper().tableExists(this.databaseName, this.tableName);
        if(!tableExists)
        {
            Utils.printMissingTableError(this.databaseName, this.tableName);
            return false;
        }
        return true;
    }
};