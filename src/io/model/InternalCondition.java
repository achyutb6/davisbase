package io.model;

public class InternalCondition {

    public static final short EQUALS = 0;
    public static final short LESS_THAN = 1;
    public static final short GREATER_THAN = 2;
    public static final short LESS_THAN_EQUALS = 3;
    public static final short GREATER_THAN_EQUALS = 4;

    private byte index;

    private short conditionType;

    private Object value;

    public static InternalCondition CreateCondition(byte index, short conditionType, Object value) {
        InternalCondition condition = new InternalCondition(index, conditionType, value);
        return condition;
    }

    public static InternalCondition CreateCondition(int index, short conditionType, Object value) {
        InternalCondition condition = new InternalCondition(index, conditionType, value);
        return condition;
    }

    public InternalCondition() {}

    private InternalCondition(byte index, short conditionType, Object value) {
        this.index = index;
        this.conditionType = conditionType;
        this.value = value;
    }

    private InternalCondition(int index, short conditionType, Object value) {
        this.index = (byte) index;
        this.conditionType = conditionType;
        this.value = value;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public short getConditionType() {
        return conditionType;
    }

    public void setConditionType(short conditionType) {
        this.conditionType = conditionType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
