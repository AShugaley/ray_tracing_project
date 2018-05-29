
public class Vector {

    //public float[] members;
    float x,y,z;

    public Vector(){}

    /*public Vector(float[] members){
        if(members.length != 3)
            throw new IllegalArgumentException();
        this.members = new float[]{members[0],members[1],members[2]};
    }*/

    public Vector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(String x, String y, String z){
        this.x = Float.parseFloat(x);
        this.y = Float.parseFloat(y);
        this.z = Float.parseFloat(z);
    }

    public Vector(Vector origin, Vector destination)
	{
		this.x = destination.x - origin.x;
		this.y = destination.y - origin.y;
		this.z = destination.z - origin.z;
	}
    
    public Vector add(Vector other){ 
    	return new Vector(this.x + other.x, this.y + other.y,  this.z + other.z);
    }

    public Vector substract(Vector other){
    	return new Vector(other.x - this.x, other.y - this.y,  other.z - this.z);
    }

    public Vector multiply_scalar(float a){ 
    	return new Vector(this.x * a, this.y * a,  this.z * a);
    }

    public void multiply_vectorwise(Vector other){ //changes in place
    	this.x *= other.x;
    	this.y *= other.y;
    	this.z *= other.z;
    }

    public float dot_product(Vector other){ //scalar multiplication, returns float
        float ret = this.x*other.x;
        ret += this.y*other.y;
        ret += this.z*other.z;
        
        return ret;
    }

    public Vector cross_product(Vector other){ //vector multiplocation, returns new Vector
        return new Vector(this.y*other.z - this.z*other.y,
                          this.z*other.x - this.x*other.z,
                          this.x*other.y - this.y*other.x
        );

    }
    
    public float calcLength()
    {
    	return (float)Math.sqrt(x*x + y*y + z*z);
    }

    public void normalize()
	{
		Vector v = this.multiply_scalar(1/this.calcLength());
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}








}

