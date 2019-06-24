package datatypes;

import common.DatabaseConstants;
import datatypes.base.DataType_Numeric;
public class DataType_SmallInt extends DataType_Numeric<Short>
{
    public DataType_SmallInt()
    {
        this((short) 0, true);
    }
    public DataType_SmallInt(Short value)
    {
        this(value == null ? 0 : value, value == null);
    }
    public DataType_SmallInt(short value, boolean isNull)
    {
        super(DatabaseConstants.SMALL_INT_SERIAL_TYPE_CODE, DatabaseConstants.TWO_BYTE_NULL_SERIAL_TYPE_CODE, Short.BYTES);
        this.value = value;
        this.isNull = isNull;
    }
    @Override
    public void increment(Short value)
    {
        this.value = (short)(this.value + value);
    }
    @Override
    public boolean compare(DataType_Numeric<Short> object2, short condition)
    {
        if(value == null) return false;
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
        DataType_SmallInt object = new DataType_SmallInt(object2.getValue(), false);
        return this.compare(object, condition);
    }
    public boolean compare(DataType_Int object2, short condition)
    {
        DataType_Int object = new DataType_Int(value, false);
        return object.compare(object2, condition);
    }
    public boolean compare(DataType_BigInt object2, short condition)
    {
        DataType_BigInt object = new DataType_BigInt(value, false);
        return object.compare(object2, condition);
    }
};