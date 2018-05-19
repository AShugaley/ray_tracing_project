

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Parser {



    public static Scene parseFile(String file){
        Scene result = new Scene();


        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splited = line.split("\\s+");
                switch (splited[0]){
                    case "set":
                        addSettings(result,splited);
                    case "mtl":
                        addMaterial(result,splited);
                    case "cam":
                        addCamera(result,splited);
                    case "lgt":
                        addLight(result,splited);
                    case "sph":
                        addSphere(result,splited);
                    case "pln":
                        addPlane(result,splited);
                    case "trg":
                        addTriangle(result,splited);
                }
            }
        }
        catch (Exception e){
            System.out.println("error");
            //dosomething?
        }
        return result;


    }


    public static void addCamera(Scene s, String[] line){

    }
    public static void addMaterial(Scene s, String[] line){

    }
    public static void addLight(Scene s, String[] line){

    }
    public static void addSettings(Scene s, String[] line){

    }
    public static void addSphere(Scene s, String[] line){

    }
    public static void addPlane(Scene s, String[] line){

    }
    public static void addTriangle(Scene s, String[] line){

    }

}
