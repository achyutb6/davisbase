package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType_Numeric;
import java.text.SimpleDateFormat;
import java.util.Date;
public class DataType_Date extends DataType_Numeric<Long>
{
    public DataType_Date()
    {
        this(0, true);
    }
    public DataType_Date(Long v)
    {
        this(v == null ? 0 : v, v == null);
    }
    public DataType_Date(long v, boolean i)
    {
        super(DatabaseConstants.DATE_SERIAL_TYPE_CODE, DatabaseConstants.EIGHT_BYTE_NULL_SERIAL_TYPE_CODE, Long.BYTES);
        this.value = v;
        this.isNull = i;
    }
    public String getStringValue()
    {
        Date date = new Date(this.value);
        return new SimpleDateFormat("MM-dd-yyyy").format(date);
    }
    @Override
    public void increment(Long v)
    {
        this.value += v;
    }
    @Override
    public boolean compare(DataType_Numeric<Long> o, short c)
    {
        if(value == null)
        	return false;
        switch (c)
        {
            case DataType_Numeric.EQUALS:
                return value == o.getValue();
            case DataType_Numeric.GREATER_THAN:
                return value > o.getValue();
            case DataType_Numeric.LESS_THAN:
                return value < o.getValue();
            case DataType_Numeric.GREATER_THAN_EQUALS:
                return value >= o.getValue();
            case DataType_Numeric.LESS_THAN_EQUALS:
                return value <= o.getValue();
            default:
                return false;
        }
    }
};