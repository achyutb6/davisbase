package io.model;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    public static short PAGE_SIZE = 512;

    public static final byte INTERIOR_TABLE_PAGE = 0x05;

    public static final byte LEAF_TABLE_PAGE = 0x0D;

    public static final byte RIGHTMOST_PAGE = 0xFFFFFFFF;

    /*
     * Field identifies the page type
     * 2 or 0x02 : The page is an interior index b-tree page.
     * 5 or 0x05 : The page is an interior table b-tree page.
     * 10 or 0x0A : The page is an leaf index b-tree page.
     * 13 or 0x0D : The page is an leaf table b-tree page.
     */
    private byte pageType;

    /*
     * Field identifies the number of cells in the page.
     */
    private byte numberOfCells;

    private short startingAddress;

    private int rightNodeAddress;

    private List<Short> recordAddressList;

    private List<T> pageRecords;

    private int pageNumber;

    public Page() {
        this.recordAddressList = new ArrayList<>();
        this.pageRecords = new ArrayList<>();
    }

    public Page(int pageNumber) {
        this.recordAddressList = new ArrayList<>();
        this.pageRecords = new ArrayList<>();
        this.pageNumber = pageNumber;
        this.startingAddress = (short) (PAGE_SIZE - 1);
    }

    public static <T> Page<T> createNewEmptyPage(T object) {
        Page<T> page = new Page<>(0);
        page.setPageType(Page.LEAF_TABLE_PAGE);
        page.setRightNodeAddress(Page.RIGHTMOST_PAGE);
        page.setNumberOfCells((byte)0x00);
        page.setRecordAddressList(new ArrayList<>());
        page.setPageRecords(new ArrayList<>());
        return page;
    }

    public byte getPageType() {
        return pageType;
    }

    public void setPageType(byte pageType) {
        this.pageType = pageType;
    }

    public byte getNumberOfCells() {
        return numberOfCells;
    }

    public long getBaseAddress() {
        return pageNumber * PAGE_SIZE;
    }

    public void setNumberOfCells(byte numberOfCells) {
        this.numberOfCells = numberOfCells;
    }

    public short getStartingAddress() {
        return startingAddress;
    }

    public void setStartingAddress(short startingAddress) {
        this.startingAddress = startingAddress;
    }

    public int getRightNodeAddress() {
        return rightNodeAddress;
    }

    public void setRightNodeAddress(int rightNodeAddress) {
        this.rightNodeAddress = rightNodeAddress;
    }

    public List<Short> getRecordAddressList() {
        return recordAddressList;
    }

    public void setRecordAddressList(List<Short> recordAddressList) {
        this.recordAddressList = recordAddressList;
    }

    public List<T> getPageRecords() {
        return pageRecords;
    }

    public void setPageRecords(List<T> pageRecords) {
        this.pageRecords = pageRecords;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }


    public static int getHeaderFixedLength() {
        return Byte.BYTES + Byte.BYTES + Short.BYTES + Integer.BYTES;
    }
}
