package query.base;
import query.model.result.Result;

public interface IQuery
{
    Result ExecuteQuery();
    boolean ValidateQuery();
}
