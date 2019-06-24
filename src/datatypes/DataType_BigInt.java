package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType_Numeric;
public class DataType_BigInt extends DataType_Numeric<Long>
{
    public DataType_BigInt()
    {
        this(0, true);
    }
    public DataType_BigInt(Long value)
    {
        this(value == null ? 0 : value, value == null);
    }
    public DataType_BigInt(long v, boolean n)
    {
        super(DatabaseConstants.BIG_INT_SERIAL_TYPE_CODE, DatabaseConstants.EIGHT_BYTE_NULL_SERIAL_TYPE_CODE, Long.BYTES);
        this.value = v;
        this.isNull = n;
    }
    @Override
    public void increment(Long v)
    {
        this.value += v;
    }
    @Override
    public boolean compare(DataType_Numeric<Long> o, short condition)
    {
        if(value == null)
        	return false;
        switch (condition)
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
    public boolean compare(DataType_TinyInt o, short c)
    {
        DataType_BigInt object = new DataType_BigInt(o.getValue(), false);
        return this.compare(object, c);
    }
    public boolean compare(DataType_SmallInt o, short c)
    {
        DataType_BigInt object = new DataType_BigInt(o.getValue(), false);
        return this.compare(object, c);
    }
    public boolean compare(DataType_Int o, short c)
    {
        DataType_BigInt object = new DataType_BigInt(o.getValue(), false);
        return this.compare(object, c);
    }
};