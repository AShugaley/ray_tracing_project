

import java.io.File;
import java.net.URL;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Parser {



    public static Scene parseFile(String file){
        Scene result = new Scene();
       // URL url = result.getClass().getResource(file);
       // File f = new File(url.getPath()); //change this if you pass file from cmd - delete this row and the one above, change 'f' to 'file' in the row below

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splited = line.split("\\s+");
                switch (splited[0]){
                    case "set":
                        addSettings(result,splited);
                        break;
                    case "mtl":
                        addMaterial(result,splited);
                        break;
                    case "cam":
                        addCamera(result,splited);
                        break;
                    case "lgt":
                        addLight(result,splited);
                        break;
                    case "sph":
                        addSphere(result,splited);
                        break;
                    case "pln":
                        addPlane(result,splited);
                        break;
                    case "trg":
                        addTriangle(result,splited);
                        break;
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

        Vector position = new Vector(line[1],line[2],line[3]);
        Vector look_at_point = new Vector(line[4],line[5],line[6]);
        Vector up_vector = new Vector(line[7],line[8],line[9]);
        float screen_distance = Float.parseFloat(line[10]);
        float screen_width = Float.parseFloat(line[11]);

        Camera c = new Camera(position, look_at_point, up_vector, screen_distance, screen_width);

        
        s.camera = c;
    }


    public static void addMaterial(Scene s, String[] line){
        Material m = new Material();

        m.diffuse_color = new Color(line[1],line[2],line[3]);
        m.specular_color = new Color(line[4],line[5],line[6]);
        m.reflection_color = new Color(line[7],line[8],line[9]);
        m.phong_specularity_coefficient = Float.parseFloat(line[10]);
        m.transparency = Float.parseFloat(line[11]);

        s.materials.add(m);
    }


    public static void addLight(Scene s, String[] line){
        Light l = new Light();

        l.position = new Vector(line[1],line[2],line[3]);
        l.color = new Color(line[4],line[5],line[6]);
        l.specular_intensity = Float.parseFloat(line[7]);
        l.shadow_intensity = Float.parseFloat(line[8]);
        l.radius = Float.parseFloat(line[9]);

        s.lights.add(l);
    }



    public static void addSettings(Scene s, String[] line){
        s.background_color = new Color(line[1],line[2],line[3]);
        s.number_shadow_rays = Integer.parseInt(line[4]);
        s.max_recursion_level = Integer.parseInt(line[5]);
        s.super_sampling_level = Integer.parseInt(line[6]);
    }


    public static void addSphere(Scene s, String[] line){
        Sphere sc = new Sphere();

        sc.center_position =  new Vector(line[1],line[2],line[3]);
        sc.radius = Float.parseFloat(line[4]);
        sc.material = Integer.parseInt(line[5]);

        s.surfaces.add(sc);


    }
    public static void addPlane(Scene s, String[] line){
        InfinitePlane p = new InfinitePlane();

        p.normal = new Vector(line[1],line[2],line[3]);
        p.offset = Float.parseFloat(line[4]);
        p.material = Integer.parseInt(line[5]);


        s.surfaces.add(p);


    }
    public static void addTriangle(Scene s, String[] line){
        Triangle t = new Triangle();

        t.v1 = new Vector(line[1],line[2],line[3]);
        t.v2 = new Vector(line[4],line[5],line[6]);
        t.v3 = new Vector(line[7],line[8],line[9]);
        t.material = Integer.parseInt(line[10]);

        s.surfaces.add(t);
    }

}
