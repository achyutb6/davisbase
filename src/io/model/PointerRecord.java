package io.model;

public class PointerRecord {

    private int leftPageNumber;

    private int key;

    private int pageNumber;

    private short offset;

    public PointerRecord() {
        leftPageNumber = -1;
        key = -1;
        offset = -1;
        pageNumber = -1;
    }

    public int getLeftPageNumber() {
        return leftPageNumber;
    }

    public void setLeftPageNumber(int leftPageNumber) {
        this.leftPageNumber = leftPageNumber;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getSize() {
        return Integer.BYTES + Integer.BYTES;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
