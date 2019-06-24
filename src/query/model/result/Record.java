package query.model.result;

import query.model.parser.Literal;
import java.util.HashMap;

public class Record
{
    HashMap<String, Literal> valueMap;
    public static Record CreateRecord()
    {
        return new Record();
    }
    private Record()
    {
        this.valueMap = new HashMap<>();
    }
    public void put(String columnName, Literal value)
    {
        if(columnName.length() == 0) return;
        if(value == null) return;
        this.valueMap.put(columnName, value);
    }
    public String get(String column)
    {
        Literal literal = this.valueMap.get(column);
        return literal.toString();
    }
};