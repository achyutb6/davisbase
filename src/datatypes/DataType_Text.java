package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType;
public class DataType_Text extends DataType<String>
{
    public DataType_Text()
    {
        this("", true);
    }
    public DataType_Text(String value)
    {
        this(value, value == null);
    }
    public DataType_Text(String value, boolean isNull)
    {
        super(DatabaseConstants.TEXT_SERIAL_TYPE_CODE, DatabaseConstants.ONE_BYTE_NULL_SERIAL_TYPE_CODE);
        this.value = value;
        this.isNull = isNull;
    }
    public byte getSerialCode()
    {
        if(isNull)
            return nullSerialCode;
        else
            return (byte)(valueSerialCode + this.value.length());
    }
    public int getSize() 
    {
        if(isNull)
            return 0;
        return this.value.length();
    }
};