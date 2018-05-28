//import java.util.Arrays;
//import java.io.File;
//
//public class Tests {
//
//    public static void vectorTests(){
//        Vector v = new Vector(1,2,3);
//        Vector u = new Vector(1,2,3);
//
//
//        v.multiply_scalar(5);
//        System.out.println(Arrays.toString(v.members));
//
//
//        v = new Vector(1,2,3);
//        v.add(u);
//        System.out.println(Arrays.toString(v.members));
//
//
//        v = new Vector(1,2,3);
//        v.substract(u);
//        System.out.println(Arrays.toString(v.members));
//
//
//        v = new Vector(1,2,3);
//        v.multiply_vectorwise(u);
//        System.out.println(Arrays.toString(v.members));
//
//
//        v = new Vector(1,2,3);
//        System.out.println(Arrays.toString(v.cross_product(u).members));
//
//
//        v = new Vector(1,2,3);
//        System.out.println(v.dot_product(u));
//
//
//
//        System.out.println("ok");
//    }
//
//
//    public static void parserTests(){
//        String filename = "test1.txt";
//        Scene s = Parser.parseFile(filename);
//
//
//        System.out.println(s.max_recursion_level);
//        System.out.println(s.number_shadow_rays);
//        System.out.println(s.super_sampling_level);
//        System.out.println(s.background_color.members[1]);
//        System.out.println(s.materials.get(1).diffuse_color.members[1]);
//        System.out.println(s.surfaces.get(1).material);
//
//    }
//}
