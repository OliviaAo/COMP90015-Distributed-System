package dsdemo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class DSDemo {

    /**
     * @param args the command line arguments
     */
    private static String name = "";
    private static String email = "";
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        Options options = new Options();

        options.addOption("name",true,"input name");
        options.addOption("email",true, "input email address");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try{
            cmd = parser.parse(options,args);      
        } catch (ParseException e){
            //help(options);
        }
        
        name = cmd.getOptionValue("name");

        if(cmd.hasOption("email")){
            email = cmd.getOptionValue("email");
        } else {
            email = "The user does not provide email address";
        }

        System.out.println("student name: "+ name+" email:"+email);
        
    }
    
}
