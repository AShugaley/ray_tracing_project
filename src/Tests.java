import java.util.Arrays;

public class Tests {

    public static void vectorTests(){
        Vector v = new Vector(1,2,3);
        Vector u = new Vector(1,2,3);


        v.multiply(5);
        System.out.println(Arrays.toString(v.members));


        v = new Vector(1,2,3);
        v.add(u);
        System.out.println(Arrays.toString(v.members));


        v = new Vector(1,2,3);
        v.substract(u);
        System.out.println(Arrays.toString(v.members));


        v = new Vector(1,2,3);
        v.multiply_vectorwise(u);
        System.out.println(Arrays.toString(v.members));


        v = new Vector(1,2,3);
        System.out.println(Arrays.toString(v.cross_product(u).members));


        v = new Vector(1,2,3);
        System.out.println(v.dot_product(u));



        System.out.println("ok");
    }
}
