
public class Vector {

    public double[] members;

    public Vector(){
        this.members = new double[]{0.0,0.0,0.0};
    }

    public Vector(double[] members){
        if(members.length != 3)
            throw new IllegalArgumentException();
        this.members = new double[]{members[0],members[1],members[2]};
    }

    public Vector(double x, double y, double z){
        this.members = new double[]{x,y,z};
    }

    public Vector(String x, String y, String z){
        this.members = new double[]{Float.parseFloat(x) ,Float.parseFloat(y) ,Float.parseFloat(z)};
    }

    public void add(Vector other){ //changes in place
        for(int i = 0; i<3;i++)
            this.members[i] += other.members[i];
    }

    public void substract(Vector other){ //changes in place
        for(int i = 0; i<3;i++)
            this.members[i] -= other.members[i];
    }

    public void multiply(double a){ //changes in place
        for(int i = 0; i<3;i++)
            this.members[i] *=  a;
    }

    public void multiply_vectorwise(Vector other){ //changes in place
        for(int i = 0; i<3;i++)
            this.members[i] *= other.members[i];
    }

    public double dot_product(Vector other){ //scalar multiplication, returns double
        double x = 0;
        for(int i = 0; i<3;i++)
            x += this.members[i]*other.members[i];
        return x;
    }

    public Vector cross_product(Vector other){ //vector multiplocation, returns new Vector
        return new Vector(this.members[1]*other.members[2] - this.members[2]*other.members[1],
                          this.members[2]*other.members[0] - this.members[0]*other.members[2],
                          this.members[0]*other.members[1] - this.members[1]*other.members[0]
        );

    }










}

