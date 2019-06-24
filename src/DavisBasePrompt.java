import common.CatalogDatabaseHelper;
import common.DatabaseConstants;
import java.util.Scanner;
import static query.QueryParser.isExit;
import static query.QueryParser.parseCommand;

public class DavisBasePrompt
{
    private static Scanner sc = new Scanner(System.in).useDelimiter(";");
    public static void main(String[] args)
    {
        CatalogDatabaseHelper.InitializeDatabase();
        splashscreen();
        while(!isExit)
        {
            System.out.print(DatabaseConstants.PROMPT);
            String cmd = sc.next();
            cmd=cmd.replace("\n", "").replace("\r", " ").trim().toLowerCase();
            parseCommand(cmd);
        }
    }
    public static void splashscreen()
    {
    	System.out.println(line("-",80));
        System.out.println("Welcome to DavisBase Nano");
		System.out.println("DavisBase Nano Version " + getVersion());
		System.out.println(DatabaseConstants.COPYRIGHT);
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));	
    }
    public static String line(String s,int num)
    {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
    public static String getVersion()
    {
		return DatabaseConstants.VERSION;
	}
};