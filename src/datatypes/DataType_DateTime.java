package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType_Numeric;
import java.util.Date;
public class DataType_DateTime extends DataType_Numeric<Long>
{
    public DataType_DateTime()
    {
        this(0, true);
    }
    public DataType_DateTime(Long value)
    {
        this(value == null ? 0 : value, value == null);
    }
    public DataType_DateTime(long value, boolean isNull)
    {
        super(DatabaseConstants.DATE_TIME_SERIAL_TYPE_CODE, DatabaseConstants.EIGHT_BYTE_NULL_SERIAL_TYPE_CODE, Long.BYTES);
        this.value = value;
        this.isNull = isNull;
    }
    public String getStringValue()
    {
        Date date = new Date(this.value);
        return date.toString();
    }
    @Override
    public void increment(Long value)
    {
        this.value += value;
    }
    @Override
    public boolean compare(DataType_Numeric<Long> object2, short condition)
    {
        if(value == null)
        	return false;
        switch (condition)
        {
            case DataType_Numeric.EQUALS:
                return value == object2.getValue();
            case DataType_Numeric.GREATER_THAN:
                return value > object2.getValue();
            case DataType_Numeric.LESS_THAN:
                return value < object2.getValue();
            case DataType_Numeric.GREATER_THAN_EQUALS:
                return value >= object2.getValue();
            case DataType_Numeric.LESS_THAN_EQUALS:
                return value <= object2.getValue();
            default:
                return false;
        }
    }
};