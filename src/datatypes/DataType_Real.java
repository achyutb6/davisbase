package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType_Numeric;
public class DataType_Real extends DataType_Numeric<Float>
{
    public DataType_Real()
    {
        this(0, true);
    }
    public DataType_Real(Float value)
    {
        this(value == null ? 0 : value, value == null);
    }
    public DataType_Real(float value, boolean isNull)
    {
        super(DatabaseConstants.REAL_SERIAL_TYPE_CODE, DatabaseConstants.FOUR_BYTE_NULL_SERIAL_TYPE_CODE, Float.BYTES);
        this.value = value;
        this.isNull = isNull;
    }
    @Override
    public void increment(Float value)
    {
        this.value += value;
    }
    @Override
    public boolean compare(DataType_Numeric<Float> object2, short condition)
    {
        if(value == null) 
        	return false;
        switch (condition)
        {
            case DataType_Numeric.EQUALS:
                return Float.floatToIntBits(value) == Float.floatToIntBits(object2.getValue());
            case DataType_Numeric.GREATER_THAN:
                return value > object2.getValue();
            case DataType_Numeric.LESS_THAN:
                return value < object2.getValue();
            case DataType_Numeric.GREATER_THAN_EQUALS:
                return Float.floatToIntBits(value) >= Float.floatToIntBits(object2.getValue());
            case DataType_Numeric.LESS_THAN_EQUALS:
                return Float.floatToIntBits(value) <= Float.floatToIntBits(object2.getValue());
            default:
                return false;
        }
    }
    public boolean compare(DataType_Double object2, short condition)
    {
        DataType_Double object = new DataType_Double(value, false);
        return object.compare(object2, condition);
    }
};