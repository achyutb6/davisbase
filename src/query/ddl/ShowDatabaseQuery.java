package query.ddl;

import common.DatabaseConstants;
import query.base.IQuery;
import query.model.parser.Literal;
import query.model.result.Record;
import query.model.result.Result;
import query.model.result.ResultSet;

import java.io.File;
import java.util.ArrayList;

public class ShowDatabaseQuery implements IQuery {
    @Override
    public Result ExecuteQuery() {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Database");
        ResultSet resultSet = ResultSet.CreateResultSet();
        resultSet.setColumns(columns);
        ArrayList<Record> records = GetDatabases();

        for(Record record : records){
            resultSet.addRecord(record);
        }

        return resultSet;
    }

    @Override
    public boolean ValidateQuery() {
        return true;
    }

    private ArrayList<Record> GetDatabases(){
        ArrayList<Record> records = new ArrayList<>();

        File baseData = new File(DatabaseConstants.DEFAULT_DATA_DIRNAME);

        for(File data : baseData.listFiles()){
            if(!data.isDirectory()) continue;
            Record record = Record.CreateRecord();
            record.put("Database", Literal.CreateLiteral(String.format("\"%s\"", data.getName())));
            records.add(record);
        }

        return records;
    }
}
