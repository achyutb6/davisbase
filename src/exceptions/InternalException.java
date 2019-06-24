package exceptions;
public class InternalException extends Exception
{

    public static String BASE_ERROR_STRING = "ERROR(200): ";
    public static String INVALID_DATATYPE_EXCEPTION = BASE_ERROR_STRING + "Invalid datatype given.";
    public static String DATATYPE_MISMATCH_EXCEPTION = BASE_ERROR_STRING + "Invalid datatype given in WHERE clause. Expected %1.";
    public static String INVALID_CONDITION_EXCEPTION = BASE_ERROR_STRING + "Invalid condition given. Currently only %1 supported.";
    public static String GENERIC_EXCEPTION = BASE_ERROR_STRING + "An error was encountered while performing the given operation.";
    public InternalException(String m, String p)
    {
        super(m.replace("%1", p));
    }
    public InternalException(String m)
    {
        super(m);
    }
};