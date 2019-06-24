package io.model;

import common.DatabaseConstants;
import common.Utils;
import datatypes.*;

import java.util.ArrayList;
import java.util.List;

public class DataRecord {

    private List<Object> columnValueList;

    private short size;

    private int rowId;

    private int pageLocated;

    private short offset;

    public DataRecord() {
        size = 0;
        columnValueList = new ArrayList<>();
        pageLocated = -1;
        offset = -1;
    }

    public List<Object> getColumnValueList() {
        return columnValueList;
    }

    public short getSize() {
        return size;
    }

    public void setSize(short size) {
        this.size = size;
    }

    public short getHeaderSize() {
        return (short)(Short.BYTES + Integer.BYTES);
    }

    public void populateSize() {
        this.size = (short) (this.columnValueList.size() + 1);
        for(Object object: columnValueList) {
            if(object.getClass().equals(DataType_TinyInt.class)) {
                this.size += ((DataType_TinyInt) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_SmallInt.class)) {
                this.size += ((DataType_SmallInt) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_Int.class)) {
                this.size += ((DataType_Int) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_BigInt.class)) {
                this.size += ((DataType_BigInt) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_Real.class)) {
                this.size += ((DataType_Real) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_Double.class)) {
                this.size += ((DataType_Double) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_DateTime.class)) {
                size += ((DataType_DateTime) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_Date.class)) {
                this.size += ((DataType_Date) object).getSIZE();
            }
            else if(object.getClass().equals(DataType_Text.class)) {
                this.size += ((DataType_Text) object).getSize();
            }
        }
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getPageLocated() {
        return pageLocated;
    }

    public void setPageLocated(int pageLocated) {
        this.pageLocated = pageLocated;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public byte[] getSerialTypeCodes() {
        byte[] serialTypeCodes = new byte[columnValueList.size()];
        byte index = 0;
        for(Object object: columnValueList) {
            switch (Utils.resolveClass(object)) {
                case DatabaseConstants.TINYINT:
                    serialTypeCodes[index++] = ((DataType_TinyInt) object).getSerialCode();
                    break;

                case DatabaseConstants.SMALLINT:
                    serialTypeCodes[index++] = ((DataType_SmallInt) object).getSerialCode();
                    break;

                case DatabaseConstants.INT:
                    serialTypeCodes[index++] = ((DataType_Int) object).getSerialCode();
                    break;

                case DatabaseConstants.BIGINT:
                    serialTypeCodes[index++] = ((DataType_BigInt) object).getSerialCode();
                    break;

                case DatabaseConstants.REAL:
                    serialTypeCodes[index++] = ((DataType_Real) object).getSerialCode();
                    break;

                case DatabaseConstants.DOUBLE:
                    serialTypeCodes[index++] = ((DataType_Double) object).getSerialCode();
                    break;

                case DatabaseConstants.DATETIME:
                    serialTypeCodes[index++] = ((DataType_DateTime) object).getSerialCode();
                    break;

                case DatabaseConstants.DATE:
                    serialTypeCodes[index++] = ((DataType_Date) object).getSerialCode();
                    break;

                case DatabaseConstants.TEXT:
                    serialTypeCodes[index++] = ((DataType_Text) object).getSerialCode();
                    break;
            }
        }
        return serialTypeCodes;
    }
}
