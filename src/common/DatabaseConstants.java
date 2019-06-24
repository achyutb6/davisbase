package common;
public interface DatabaseConstants
{
    String PROMPT = "davisql> ";
    String VERSION = "v1.0";
    String COPYRIGHT = "©2019 Team Yellow (Achyut Bhandiwad , Mrugapphan Kannan, Matthew Le, Aishwarya Madabushi Gadavarthi)";
    String DEFAULT_FILE_EXTENSION = ".tbl";
    String DEFAULT_DATA_DIRNAME = "data";
    String DEFAULT_CATALOG_DATABASENAME = "catalog";
    String SYSTEM_TABLES_TABLENAME = "davisbase_tables";
    String SYSTEM_COLUMNS_TABLENAME = "davisbase_columns";

    byte INVALID_CLASS = -1;
    byte TINYINT = 0;
    byte SMALLINT = 1;
    byte INT = 2;
    byte BIGINT = 3;
    byte REAL = 4;
    byte DOUBLE = 5;
    byte DATE = 6;
    byte DATETIME = 7;
    byte TEXT = 8;
    
    byte ONE_BYTE_NULL_SERIAL_TYPE_CODE = 0x00;
    byte TWO_BYTE_NULL_SERIAL_TYPE_CODE = 0x01;
    byte FOUR_BYTE_NULL_SERIAL_TYPE_CODE = 0x02;
    byte EIGHT_BYTE_NULL_SERIAL_TYPE_CODE = 0x03;
    byte TINY_INT_SERIAL_TYPE_CODE = 0x04;
    byte SMALL_INT_SERIAL_TYPE_CODE = 0x05;
    byte INT_SERIAL_TYPE_CODE = 0x06;
    byte BIG_INT_SERIAL_TYPE_CODE = 0x07;
    byte REAL_SERIAL_TYPE_CODE = 0x08;
    byte DOUBLE_SERIAL_TYPE_CODE = 0x09;
    byte DATE_TIME_SERIAL_TYPE_CODE = 0x0A;
    byte DATE_SERIAL_TYPE_CODE = 0x0B;
    byte TEXT_SERIAL_TYPE_CODE = 0x0C;
}
