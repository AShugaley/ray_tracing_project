import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;
import java.util.Arrays;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        runTests();

		//parse cmd args 
        //remember to set default size to 500*500 - if got 500, call the empty constructor,
        //call regular constructor 


//        if(args.length != 5 && args.length !=3){
//            System.out.println("Wrong number of arguments");
//            System.exit(1);
//        }
//
//
//        if(args.length == 3){
//            double x = 500;
//            double y = 500;
//        }
//        else{
//            double x = Integer.parseInt(args[4]);
//            double y = Integer.parseInt(args[5]);
//        }
//
//        Scene s = Parser.parseFile(args[1]); //need to change parser for this to work - see there
//        //s.doStuff;
    }


    public static void runTests(){
        //Tests.vectorTests();
        Tests.parserTests();
    }
}
