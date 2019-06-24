package query.model.parser;

import query.QueryHandler;

public class Condition {
    public String column;
    public Operator operator;
    public Literal value;

    public static Condition CreateCondition(String conditionString) {
        Operator operator = GetOperator(conditionString);
        if(operator == null) {
            QueryHandler.UnrecognisedCommand(conditionString, "Unrecognised operator. \nValid operators include =, >, <, >=, <=. \nPlease follow <column> <operator> <value>");
            return null;
        }

        Condition condition = null;

        switch (operator){
            case GREATER_THAN:
                condition = getConditionInternal(conditionString, operator, ">");
                break;
            case LESS_THAN:
                condition = getConditionInternal(conditionString, operator, "<");
                break;
            case LESS_THAN_EQUAL:
                condition = getConditionInternal(conditionString, operator, "<=");
                break;
            case GREATER_THAN_EQUAL:
                condition = getConditionInternal(conditionString, operator, ">=");
                break;
            case EQUALS:
                condition = getConditionInternal(conditionString, operator, "=");
                break;
        }

        return condition;
    }

    private static Condition getConditionInternal(String conditionString, Operator operator, String operatorString) {
        String[] parts;
        String column;
        Literal literal;
        Condition condition;
        parts = conditionString.split(operatorString);
        if(parts.length != 2) {
            QueryHandler.UnrecognisedCommand(conditionString, "Unrecognised condition. Please follow <column> <operator> <value>");
            return null;
        }

        column = parts[0].trim();
        literal = Literal.CreateLiteral(parts[1].trim());

        if (literal == null) {
            return null;
        }

        condition = new Condition(column, operator, literal);
        return condition;
    }

    private Condition(String column, Operator operator, Literal value){
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    private static Operator GetOperator(String conditionString) {

        if(conditionString.contains("<=")){
            return Operator.LESS_THAN_EQUAL;
        }

        if(conditionString.contains(">=")){
            return Operator.GREATER_THAN_EQUAL;
        }

        if(conditionString.contains(">")){
            return Operator.GREATER_THAN;
        }

        if(conditionString.contains("<")){
            return Operator.LESS_THAN;
        }

        if(conditionString.contains("=")){
            return Operator.EQUALS;
        }

        return null;
    }
}
