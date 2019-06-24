package common;
import datatypes.base.DataType_Numeric;
import query.model.parser.Condition;
import query.model.parser.DataTypeEnum;
import query.model.parser.Literal;
import query.model.parser.Operator;
import datatypes.*;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Utils
{
    public static String getDatabasePath(String d)
    {
        return DatabaseConstants.DEFAULT_DATA_DIRNAME + "/" + d;
    }
    public static void printMissingDatabaseError(String d)
    {
        printMessage("ERROR: The database '" + d + "' does not exist");
    }
    public static void printMissingTableError(String d, String t)
    {
        printMessage("ERROR: Table '" + d + "." + t + "' doesn't exist.");
    }
    public static void printDuplicateTableError(String d, String t)
    {
        printMessage("ERROR: Table '" + d + "." + t + "' already exist.");
    }
    public static void printMessage(String str)
    {
        System.out.println(str);
    }
    public static void printUnknownColumnValueError(String c, String v)
    {
        printMessage(String.format("ERROR: Invalid value: '%s' for column '%s'", v, c));
    }
    public static byte resolveClass(Object object)
    {
        if(object.getClass().equals(DataType_TinyInt.class))
            return DatabaseConstants.TINYINT;
        else if(object.getClass().equals(DataType_SmallInt.class))
            return DatabaseConstants.SMALLINT;
        else if(object.getClass().equals(DataType_Int.class))
            return DatabaseConstants.INT;
        else if(object.getClass().equals(DataType_BigInt.class))
            return DatabaseConstants.BIGINT;
        else if(object.getClass().equals(DataType_Real.class))
            return DatabaseConstants.REAL;
        else if(object.getClass().equals(DataType_Double.class))
            return DatabaseConstants.DOUBLE;
        else if(object.getClass().equals(DataType_Date.class))
            return DatabaseConstants.DATE;
        else if(object.getClass().equals(DataType_DateTime.class))
            return DatabaseConstants.DATETIME;
        else if(object.getClass().equals(DataType_Text.class))
            return DatabaseConstants.TEXT;
        else
            return DatabaseConstants.INVALID_CLASS;
    }
    static byte stringToDataType(String string)
    {
        if(string.compareToIgnoreCase("TINYINT") == 0)
            return DatabaseConstants.TINYINT;
        else if(string.compareToIgnoreCase("SMALLINT") == 0)
            return DatabaseConstants.SMALLINT;
        else if(string.compareToIgnoreCase("INT") == 0)
            return DatabaseConstants.INT;
        else if(string.compareToIgnoreCase("BIGINT") == 0)
            return DatabaseConstants.BIGINT;
        else if(string.compareToIgnoreCase("REAL") == 0)
            return DatabaseConstants.REAL;
        else if(string.compareToIgnoreCase("DOUBLE") == 0)
            return DatabaseConstants.DOUBLE;
        else if(string.compareToIgnoreCase("DATE") == 0)
            return DatabaseConstants.DATE;
        else if(string.compareToIgnoreCase("DATETIME") == 0)
            return DatabaseConstants.DATETIME;
        else if(string.compareToIgnoreCase("TEXT") == 0)
            return DatabaseConstants.TEXT;
        else
            return DatabaseConstants.INVALID_CLASS;
    }
    public static DataTypeEnum internalDataTypeToModelDataType(byte type)
    {
        switch (type)
        {
            case DatabaseConstants.TINYINT:
                return DataTypeEnum.TINYINT;
            case DatabaseConstants.SMALLINT:
                return DataTypeEnum.SMALLINT;
            case DatabaseConstants.INT:
                return DataTypeEnum.INT;
            case DatabaseConstants.BIGINT:
                return DataTypeEnum.BIGINT;
            case DatabaseConstants.REAL:
                return DataTypeEnum.REAL;
            case DatabaseConstants.DOUBLE:
                return DataTypeEnum.DOUBLE;
            case DatabaseConstants.DATE:
                return DataTypeEnum.DATE;
            case DatabaseConstants.DATETIME:
                return DataTypeEnum.DATETIME;
            case DatabaseConstants.TEXT:
                return DataTypeEnum.TEXT;
            default:
                return null;
        }
    }
    public static boolean isvalidDateFormat(String date)
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        try
        {
            formatter.parse(date);
        }catch (ParseException e) 
        {
            return false;
        }
        return true;
    }
    public static boolean isvalidDateTimeFormat(String date)
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        try
        {
            formatter.parse(date);
        }catch (ParseException e)
        {
            return false;
        }
        return true;
    }
    public static Short ConvertFromOperator(Operator operator)
    {
        switch (operator)
        {
            case EQUALS: return DataType_Numeric.EQUALS;
            case GREATER_THAN_EQUAL: return DataType_Numeric.GREATER_THAN_EQUALS;
            case GREATER_THAN: return DataType_Numeric.GREATER_THAN;
            case LESS_THAN_EQUAL: return DataType_Numeric.LESS_THAN_EQUALS;
            case LESS_THAN: return DataType_Numeric.LESS_THAN;
        }
        return null;
    }
    public static boolean checkConditionValueDataTypeValidity(HashMap<String, Integer> columnDataTypeMapping, List<String> columnsList, Condition condition)
    {
        String invalidColumn = "";
        Literal l = null;
        if (columnsList.contains(condition.column))
        {
            int dataTypeIndex = columnDataTypeMapping.get(condition.column);
            l = condition.value;
            if (l.type != Utils.internalDataTypeToModelDataType((byte)dataTypeIndex))
            {
                if (Utils.canUpdateLiteralDataType(l, dataTypeIndex))
                    return true;
            }
        }
        boolean valid = invalidColumn.length() <= 0;
        if (!valid)
            Utils.printUnknownColumnValueError(invalidColumn, l.value);
        return valid;
    }
    public static long getDateEpoc(String value, Boolean isDate)
    {
        DateFormat formatter;
        if (isDate)
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        else
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        Date date;
        try
        {
            date = formatter.parse(value);
            ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            return zdt.toInstant().toEpochMilli() / 1000;
        }catch (ParseException ex)
        {
            return 0;
        }
    }
    public static String getDateEpocAsString(long value, Boolean isDate)
    {
        ZoneId zoneId = ZoneId.of ("America/Chicago" );
        Instant i = Instant.ofEpochSecond (value);
        ZonedDateTime zdt2 = ZonedDateTime.ofInstant (i, zoneId);
        Date date = Date.from(zdt2.toInstant());
        DateFormat formatter;
        if (isDate)
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        else
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        return formatter.format(date);
    }
    public boolean checkDataTypeValidity(HashMap<String, Integer> columnDataTypeMapping, List<String> columnsList, List<Literal> values)
    {
        String invalidColumn = "";
        Literal invalidLiteral = null;
        for (int i =0; i < values.size(); i++)
        {
            String columnName = columnsList.get(i);
            int dataTypeId = columnDataTypeMapping.get(columnName);
            int idx = columnsList.indexOf(columnName);
            Literal literal = values.get(idx);
            invalidLiteral = literal;
            if (literal.type != Utils.internalDataTypeToModelDataType((byte)dataTypeId))
            {
                if (Utils.canUpdateLiteralDataType(literal, dataTypeId))
                    continue;
                invalidColumn = columnName;
                break;
            }
            if (literal.type != Utils.internalDataTypeToModelDataType((byte)dataTypeId))
            {
                invalidColumn = columnName;
                break;
            }
        }
        boolean valid = invalidColumn.length() <= 0;
        if (!valid)
        {
            Utils.printUnknownColumnValueError(invalidColumn, invalidLiteral.value);
            return false;
        }
        return true;
    }
    public static boolean RecursivelyDelete(File file)
    {
        if(file == null) return true;
        boolean isDeleted;
        if(file.isDirectory())
        {
            for (File childFile : file.listFiles())
            {
                if (childFile.isFile())
                {
                    isDeleted = childFile.delete();
                    if (!isDeleted) return false;
                }
                else
                {
                    isDeleted = RecursivelyDelete(childFile);
                    if (!isDeleted) return false;
                }
            }
        }
        return file.delete();
    }
    public static boolean canUpdateLiteralDataType(Literal literal, int columnType)
    {
        if (columnType == DatabaseConstants.TINYINT)
        {
            if (literal.type == DataTypeEnum.INT)
            {
                if (Integer.parseInt(literal.value) <= Byte.MAX_VALUE)
                {
                    literal.type = DataTypeEnum.TINYINT;
                    return true;
                }
            }
        }
        else if (columnType == DatabaseConstants.SMALLINT)
        {
            if (literal.type == DataTypeEnum.INT)
            {
                if (Integer.parseInt(literal.value) <= Short.MAX_VALUE)
                {
                    literal.type = DataTypeEnum.SMALLINT;
                    return true;
                }
            }
        }
        else if (columnType == DatabaseConstants.BIGINT)
        {
            if (literal.type == DataTypeEnum.INT)
            {
                if (Integer.parseInt(literal.value) <= Long.MAX_VALUE)
                {
                    literal.type = DataTypeEnum.BIGINT;
                    return true;
                }
            }
        }
        else if (columnType == DatabaseConstants.DOUBLE)
        {
            if (literal.type == DataTypeEnum.REAL)
            {
                literal.type = DataTypeEnum.DOUBLE;
                return true;
            }
        }
        return false;
    }
};