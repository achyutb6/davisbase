package query.model.result;
public class Result
{
    int rowsAffected;
    private boolean isInternal = false;
    public Result(int rowsAffected)
    {
        this.rowsAffected = rowsAffected;
    }
    public Result(int rowsAffected, boolean isInternal)
    {
        this.rowsAffected = rowsAffected;
        this.isInternal = isInternal;
    }
    public void Display()
    {
        if(this.isInternal) return;
        System.out.println("Query Successful");
        if(this.rowsAffected == 1)
            System.out.println(String.format("%d row affected", this.rowsAffected));
        else
            System.out.println(String.format("%d rows affected", this.rowsAffected));
        System.out.println();
    }
};