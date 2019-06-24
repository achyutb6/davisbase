package query.ddl;

import common.DatabaseConstants;
import common.DatabaseHelper;
import common.Utils;
import query.base.IQuery;
import query.dml.DeleteQuery;
import query.model.parser.Condition;
import query.model.result.Result;

import java.io.File;
import java.util.ArrayList;
public class DropTableQuery implements IQuery {

    public String databaseName;
    public String tableName;

    public DropTableQuery(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    @Override
    public Result ExecuteQuery() {

        ArrayList<Condition> conditionList = new ArrayList<>();
        conditionList.add(Condition.CreateCondition(String.format("database_name = '%s'", this.databaseName)));
        conditionList.add(Condition.CreateCondition(String.format("table_name = '%s'", this.tableName)));

        IQuery deleteQuery = new DeleteQuery(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_TABLES_TABLENAME, conditionList, true);
        deleteQuery.ExecuteQuery();

        deleteQuery  = new DeleteQuery(DatabaseConstants.DEFAULT_CATALOG_DATABASENAME, DatabaseConstants.SYSTEM_COLUMNS_TABLENAME, conditionList, true);
        deleteQuery.ExecuteQuery();

        File table = new File(String.format("%s/%s/%s%s", DatabaseConstants.DEFAULT_DATA_DIRNAME, this.databaseName, this.tableName, DatabaseConstants.DEFAULT_FILE_EXTENSION));

        if(!Utils.RecursivelyDelete(table)){
            Utils.printMessage(String.format("ERROR(200): Unable to delete table '%s.%s'", this.databaseName, this.tableName));
            return null;
        }
        return new Result(1);
    }

    @Override
    public boolean ValidateQuery() {
        if(!DatabaseHelper.getDatabaseHelper().tableExists(this.databaseName, this.tableName)){
            Utils.printMissingTableError(this.databaseName, this.tableName);
            return false;
        }

        return true;
    }
}
