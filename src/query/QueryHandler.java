package query;

import common.DatabaseConstants;
import query.base.IQuery;
import query.ddl.*;
import query.dml.DeleteQuery;
import query.dml.InsertQuery;
import query.dml.UpdateQuery;
import query.model.parser.*;
import query.model.result.Result;
import query.vdl.DescTableQuery;
import query.vdl.SelectQuery;
import query.vdl.UseDatabaseQuery;
import java.util.ArrayList;

public class QueryHandler
{

    static final String SELECT_COMMAND = "SELECT";
    static final String DROP_TABLE_COMMAND = "DROP TABLE";
    static final String DROP_DATABASE_COMMAND = "DROP DATABASE";
    static final String HELP_COMMAND = "HELP";
    static final String VERSION_COMMAND = "VERSION";
    static final String EXIT_COMMAND = "EXIT";
    static final String SHOW_TABLES_COMMAND = "SHOW TABLES";
    static final String SHOW_DATABASES_COMMAND = "SHOW DATABASES";
    static final String INSERT_COMMAND = "INSERT INTO";
    static final String DELETE_COMMAND = "DELETE FROM";
    static final String UPDATE_COMMAND = "UPDATE";
    static final String CREATE_TABLE_COMMAND = "CREATE TABLE";
    static final String CREATE_DATABASE_COMMAND = "CREATE DATABASE";
    static final String USE_DATABASE_COMMAND = "USE";
    static final String DESC_TABLE_COMMAND = "DESC";
    private static final String NO_DATABASE_SELECTED_MESSAGE = "No database selected";
    public static final String USE_HELP_MESSAGE = "\nType 'help;' to display supported commands.";

    public static String ActiveDatabaseName = "";

    private static String getVersion()
    {
        return DatabaseConstants.VERSION;
    }

    private static String getCopyright()
    {
        return DatabaseConstants.COPYRIGHT;
    }

    public static String line(String s, int num)
    {
        String a = "";
        for(int i=0;i<num;i++)
            a += s;
        return a;
    }

    static IQuery ShowTableListQueryHandler()
    {
        if(QueryHandler.ActiveDatabaseName.equals(""))
        {
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }
        return new ShowTableQuery(QueryHandler.ActiveDatabaseName);
    }

    static IQuery DropTableQueryHandler(String tableName)
    {
        if(QueryHandler.ActiveDatabaseName.equals(""))
        {
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }
        return new DropTableQuery(QueryHandler.ActiveDatabaseName, tableName);
    }

    public static void UnrecognisedCommand(String userCommand, String message)
    {
        System.out.println("ERROR(100) Unrecognised Command " + userCommand);
        System.out.println("Message : " + message);
    }

    static IQuery SelectQueryHandler(String[] attributes, String tableName, String conditionString)
    {
        if(QueryHandler.ActiveDatabaseName.equals(""))
        {
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }
        boolean isSelectAll = false;
        SelectQuery query;
        ArrayList<String> columns = new ArrayList<>();
        for(String attribute : attributes)
            columns.add(attribute.trim());
        if(columns.size() == 1 && columns.get(0).equals("*"))
        {
            isSelectAll = true;
            columns = null;
        }
        if(conditionString.equals(""))
        {
            query = new SelectQuery(QueryHandler.ActiveDatabaseName, tableName, columns, null, isSelectAll);
            return query;
        }
        Condition condition = Condition.CreateCondition(conditionString);
        if(condition == null) return null;
        ArrayList<Condition> conditionList = new ArrayList<>();
        conditionList.add(condition);
        query = new SelectQuery(QueryHandler.ActiveDatabaseName, tableName, columns, conditionList, isSelectAll);
        return query;
    }
    public static void ShowVersionQueryHandler()
    {
        System.out.println("DavisBaseLite Version " + getVersion());
        System.out.println(getCopyright());
    }
    static void HelpQueryHandler()
    {
        System.out.println(line("*",80));
        System.out.println("SUPPORTED COMMANDS");
        System.out.println("All commands below are case insensitive");
        System.out.println();
        System.out.println("\tUSE DATABASE database_name;                      Changes current database.");
        System.out.println("\tCREATE DATABASE database_name;                   Creates an empty database.");
        System.out.println("\tSHOW DATABASES;                                  Displays all databases.");
        System.out.println("\tDROP DATABASE database_name;                     Deletes a database.");
        System.out.println("\tSHOW TABLES;                                     Displays all tables in current database.");
        System.out.println("\tDESC table_name;                                 Displays table schema.");
        System.out.println("\tCREATE TABLE table_name (                        Creates a table in current database.");
        System.out.println("\t\t<column_name> <datatype> [PRIMARY KEY | NOT NULL]");
        System.out.println("\t\t...);");
        System.out.println("\tDROP TABLE table_name;                           Deletes a table data and its schema.");
        System.out.println("\tSELECT <column_list> FROM table_name             Display records whose rowid is <id>.");
        System.out.println("\t\t[WHERE rowid = <value>];");
        System.out.println("\tINSERT INTO table_name                           Inserts a record into the table.");
        System.out.println("\t\t[(<column1>, ...)] VALUES (<value1>, <value2>, ...);");
        System.out.println("\tDELETE FROM table_name [WHERE condition];        Deletes a record from a table.");
        System.out.println("\tUPDATE table_name SET <conditions>               Updates a record from a table.");
        System.out.println("\t\t[WHERE condition];");
        System.out.println("\tVERSION;                                         Display current database engine version.");
        System.out.println("\tHELP;                                            Displays help information");
        System.out.println("\tEXIT;                                            Exits the program");
        System.out.println();
        System.out.println();
        System.out.println(line("*",80));
    }
    static IQuery InsertQueryHandler(String tableName, String columnsString, String valuesList)
    {
        if(QueryHandler.ActiveDatabaseName.equals("")){
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }
        IQuery query = null;
        ArrayList<String> columns = null;
        ArrayList<Literal> values = new ArrayList<>();
        if(!columnsString.equals(""))
        {
            columns = new ArrayList<>();
            String[] columnList = columnsString.split(",");
            for(String column : columnList){
                columns.add(column.trim());
            }
        }
        for(String value : valuesList.split(","))
        {
            Literal literal = Literal.CreateLiteral(value.trim());
            if(literal == null) return null;
            values.add(literal);
        }
        if(columns != null && columns.size() != values.size())
        {
            QueryHandler.UnrecognisedCommand("", "Number of columns and values don't match");
            return null;
        }
        query = new InsertQuery(QueryHandler.ActiveDatabaseName, tableName, columns, values);
        return query;
    }
    static IQuery DeleteQueryHandler(String tableName, String conditionString)
    {
        if(QueryHandler.ActiveDatabaseName.equals(""))
        {
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }
        IQuery query;
        if(conditionString.equals(""))
        {
            query = new DeleteQuery(QueryHandler.ActiveDatabaseName, tableName, null);
            return query;
        }
        Condition condition = Condition.CreateCondition(conditionString);
        if(condition == null) return null;

        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(condition);

        query = new DeleteQuery(QueryHandler.ActiveDatabaseName, tableName, conditions);
        return query;
    }

    static IQuery UpdateQuery(String tableName, String clauseString, String conditionString) 
    {
        if(QueryHandler.ActiveDatabaseName.equals(""))
        {
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }

        IQuery query;

        Condition clause = Condition.CreateCondition(clauseString);
        if(clause == null) return null;

        if(clause.operator != Operator.EQUALS)
        {
            QueryHandler.UnrecognisedCommand(clauseString, "SET clause should only contain = operator");
            return null;
        }
        if(conditionString.equals(""))
        {
            query = new UpdateQuery(QueryHandler.ActiveDatabaseName, tableName, clause.column, clause.value, null);
            return query;
        }

        Condition condition = Condition.CreateCondition(conditionString);
        if(condition == null) return null;

        query = new UpdateQuery(QueryHandler.ActiveDatabaseName, tableName, clause.column, clause.value, condition);
        return query;
    }

    static IQuery CreateTableQueryHandler(String tableName, String columnsPart)
    {
        if(QueryHandler.ActiveDatabaseName.equals(""))
        {
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }

        IQuery query;
        boolean hasPrimaryKey = false;
        ArrayList<Column> columns = new ArrayList<>();
        String[] columnsList = columnsPart.split(",");

        for(String columnEntry : columnsList)
        {
            Column column = Column.CreateColumn(columnEntry.trim());
            if(column == null) return null;
            columns.add(column);
        }
        for (int i = 0; i < columnsList.length; i++)
        {
            if (columnsList[i].toLowerCase().endsWith("primary key"))
            {
                if (i == 0)
                {
                    if (columns.get(i).type == DataTypeEnum.INT)
                    {
                        hasPrimaryKey = true;
                    }
                    else
                    {
                        QueryHandler.UnrecognisedCommand(columnsList[i], "PRIMARY KEY has to have INT datatype");
                        return null;
                    }
                }
                else
                {
                    QueryHandler.UnrecognisedCommand(columnsList[i], "Only first column should be PRIMARY KEY and has to have INT datatype.");
                    return null;
                }

            }
        }
        query = new CreateTableQuery(QueryHandler.ActiveDatabaseName, tableName, columns, hasPrimaryKey);
        return query;
    }

    static IQuery DropDatabaseQueryHandler(String databaseName) {
        return new DropDatabaseQuery(databaseName);
    }

    static IQuery ShowDatabasesQueryHandler() {
        return new ShowDatabaseQuery();
    }

    static IQuery UseDatabaseQueryHandler(String databaseName) {
        return new UseDatabaseQuery(databaseName);
    }

    static IQuery CreateDatabaseQueryHandler(String databaseName) {
        return new CreateDatabaseQuery(databaseName);
    }

    static IQuery DescTableQueryHandler(String tableName) {
        if(QueryHandler.ActiveDatabaseName.equals("")){
            System.out.println(QueryHandler.NO_DATABASE_SELECTED_MESSAGE);
            return null;
        }

        return new DescTableQuery(QueryHandler.ActiveDatabaseName, tableName);
    }

    public static void ExecuteQuery(IQuery query) {
        if(query!= null && query.ValidateQuery()){
            Result result = query.ExecuteQuery();
            if(result != null){
                result.Display();
            }
        }
    }
}
