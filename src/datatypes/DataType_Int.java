package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType_Numeric;
public class DataType_Int extends DataType_Numeric<Integer>
{
    public DataType_Int()
    {
        this(0, true);
    }
    public DataType_Int(Integer value)
    {
        this(value == null ? 0 : value, value == null);
    }
    public DataType_Int(int value, boolean isNull)
    {
        super(DatabaseConstants.INT_SERIAL_TYPE_CODE, DatabaseConstants.FOUR_BYTE_NULL_SERIAL_TYPE_CODE, Integer.BYTES);
        this.value = value;
        this.isNull = isNull;
    }
    @Override
    public void increment(Integer value)
    {
        this.value += value;
    }
    @Override
    public boolean compare(DataType_Numeric<Integer> object2, short condition)
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
    public boolean compare(DataType_TinyInt object2, short condition)
    {
        DataType_Int object = new DataType_Int(object2.getValue(), false);
        return this.compare(object, condition);
    }
    public boolean compare(DataType_SmallInt object2, short condition)
    {
        DataType_Int object = new DataType_Int(object2.getValue(), false);
        return this.compare(object, condition);
    }
    public boolean compare(DataType_BigInt object2, short condition)
    {
        DataType_BigInt object = new DataType_BigInt(value, false);
        return object.compare(object2, condition);
    }
};