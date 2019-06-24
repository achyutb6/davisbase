package query;

import query.base.IQuery;
public class QueryParser
{

    public static boolean isExit = false;
    public static void parseCommand(String userCommand)
    {
        if(userCommand.toLowerCase().equals(QueryHandler.SHOW_TABLES_COMMAND.toLowerCase()))
        {
            IQuery query = QueryHandler.ShowTableListQueryHandler();
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().equals(QueryHandler.SHOW_DATABASES_COMMAND.toLowerCase()))
        {
            IQuery query = QueryHandler.ShowDatabasesQueryHandler();
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().equals(QueryHandler.HELP_COMMAND.toLowerCase()))
            QueryHandler.HelpQueryHandler();
        else if(userCommand.toLowerCase().equals(QueryHandler.VERSION_COMMAND.toLowerCase()))
            QueryHandler.ShowVersionQueryHandler();
        else if(userCommand.toLowerCase().equals(QueryHandler.EXIT_COMMAND.toLowerCase()))
        {
            System.out.println("Exiting Database...");
            isExit = true;
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.USE_DATABASE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.USE_DATABASE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String databaseName = userCommand.substring(QueryHandler.USE_DATABASE_COMMAND.length());
            IQuery query = QueryHandler.UseDatabaseQueryHandler(databaseName.trim());
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.DESC_TABLE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.DESC_TABLE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String tableName = userCommand.substring(QueryHandler.DESC_TABLE_COMMAND.length());
            IQuery query = QueryHandler.DescTableQueryHandler(tableName.trim());
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.DROP_TABLE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.DROP_TABLE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String tableName = userCommand.substring(QueryHandler.DROP_TABLE_COMMAND.length());
            IQuery query = QueryHandler.DropTableQueryHandler(tableName.trim());
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.DROP_DATABASE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.DROP_DATABASE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String databaseName = userCommand.substring(QueryHandler.DROP_DATABASE_COMMAND.length());
            IQuery query = QueryHandler.DropDatabaseQueryHandler(databaseName.trim());
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.SELECT_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.SELECT_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            int index = userCommand.toLowerCase().indexOf("from");
            if(index == -1)
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Expected FROM keyword");
                return;
            }
            String attributeList = userCommand.substring(QueryHandler.SELECT_COMMAND.length(), index).trim();
            String restUserQuery = userCommand.substring(index + "from".length());
            index = restUserQuery.toLowerCase().indexOf("where");
            if(index == -1)
            {
                String tableName = restUserQuery.trim();
                IQuery query = QueryHandler.SelectQueryHandler(attributeList.split(","), tableName, "");
                QueryHandler.ExecuteQuery(query);
                return;
            }
            String tableName = restUserQuery.substring(0, index);
            String conditions = restUserQuery.substring(index + "where".length());
            IQuery query = QueryHandler.SelectQueryHandler(attributeList.split(","), tableName.trim(), conditions);
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.INSERT_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.INSERT_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String tableName = "";
            String columns = "";
            int valuesIndex = userCommand.toLowerCase().indexOf("values");
            if(valuesIndex == -1)
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Expected VALUES keyword");
                return;
            }
            String columnOptions = userCommand.toLowerCase().substring(0, valuesIndex);
            int openBracketIndex = columnOptions.indexOf("(");
            if(openBracketIndex != -1)
            {
                tableName = userCommand.substring(QueryHandler.INSERT_COMMAND.length(), openBracketIndex).trim();
                int closeBracketIndex = userCommand.indexOf(")");
                if(closeBracketIndex == -1)
                {
                    QueryHandler.UnrecognisedCommand(userCommand, "Expected ')'");
                    return;
                }
                columns = userCommand.substring(openBracketIndex + 1, closeBracketIndex).trim();
            }
            if(tableName.equals(""))
                tableName = userCommand.substring(QueryHandler.INSERT_COMMAND.length(), valuesIndex).trim();
            String valuesList = userCommand.substring(valuesIndex + "values".length()).trim();
            if(!valuesList.startsWith("("))
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Expected '('");
                return;
            }
            if(!valuesList.endsWith(")"))
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Expected ')'");
                return;
            }
            valuesList = valuesList.substring(1, valuesList.length()-1);
            IQuery query = QueryHandler.InsertQueryHandler(tableName, columns, valuesList);
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.DELETE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.DELETE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String tableName = "";
            String condition = "";
            int index = userCommand.toLowerCase().indexOf("where");
            if(index == -1)
            {
                tableName = userCommand.substring(QueryHandler.DELETE_COMMAND.length()).trim();
                IQuery query = QueryHandler.DeleteQueryHandler(tableName, condition);
                QueryHandler.ExecuteQuery(query);
                return;
            }
            if(tableName.equals(""))
                tableName = userCommand.substring(QueryHandler.DELETE_COMMAND.length(), index).trim();
            condition = userCommand.substring(index + "where".length());
            IQuery query = QueryHandler.DeleteQueryHandler(tableName, condition);
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.UPDATE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.UPDATE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String conditions = "";
            int setIndex = userCommand.toLowerCase().indexOf("set");
            if(setIndex == -1)
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Expected SET keyword");
                return;
            }
            String tableName = userCommand.substring(QueryHandler.UPDATE_COMMAND.length(), setIndex).trim();
            String clauses = userCommand.substring(setIndex + "set".length());
            int whereIndex = userCommand.toLowerCase().indexOf("where");
            if(whereIndex == -1)
            {
                IQuery query = QueryHandler.UpdateQuery(tableName, clauses, conditions);
                QueryHandler.ExecuteQuery(query);
                return;
            }
            clauses = userCommand.substring(setIndex + "set".length(), whereIndex).trim();
            conditions = userCommand.substring(whereIndex + "where".length());
            IQuery query = QueryHandler.UpdateQuery(tableName, clauses, conditions);
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.CREATE_DATABASE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.CREATE_DATABASE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            String databaseName = userCommand.substring(QueryHandler.CREATE_DATABASE_COMMAND.length());
            IQuery query = QueryHandler.CreateDatabaseQueryHandler(databaseName.trim());
            QueryHandler.ExecuteQuery(query);
        }
        else if(userCommand.toLowerCase().startsWith(QueryHandler.CREATE_TABLE_COMMAND.toLowerCase()))
        {
            if(!PartsEqual(userCommand, QueryHandler.CREATE_TABLE_COMMAND))
            {
                QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
                return;
            }
            int openBracketIndex = userCommand.toLowerCase().indexOf("(");
            if(openBracketIndex == -1)
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Expected (");
                return;
            }
            if(!userCommand.endsWith(")"))
            {
                QueryHandler.UnrecognisedCommand(userCommand, "Missing )");
                return;
            }
            String tableName = userCommand.substring(QueryHandler.CREATE_TABLE_COMMAND.length(), openBracketIndex).trim();
            String columnsPart = userCommand.substring(openBracketIndex + 1, userCommand.length()-1);
            IQuery query = QueryHandler.CreateTableQueryHandler(tableName, columnsPart);
            QueryHandler.ExecuteQuery(query);
        }
        else
            QueryHandler.UnrecognisedCommand(userCommand, QueryHandler.USE_HELP_MESSAGE);
    }
    private static boolean PartsEqual(String userCommand, String expectedCommand)
    {
        String[] userParts = userCommand.toLowerCase().split(" ");
        String[] actualParts = expectedCommand.toLowerCase().split(" ");
        for(int i=0;i<actualParts.length;i++)
        {
            if(!actualParts[i].equals(userParts[i]))
            {
                return false;
            }
        }
        return true;
    }
};