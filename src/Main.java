import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;

import java.util.Arrays;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        runTests();




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
//        Scene s = Parser.parseFile(args[1]);
//        //s.doStuff;
    }


    public static void runTests(){
        Tests.vectorTests();
    }
}
