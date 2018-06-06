public class Sphere extends Surface {
    Vector center_position;
    float radius;

    
    @Override
    public Vector calcNormal(Vector point)
    {
    	Vector normal = center_position.substract(point);
		normal.normalize();
    	return normal;  	
    }

    @Override
    public float intersectDist(Ray ray) 
    {
    	float a = (float)Math.pow(ray.direction.calcLength(), 2);
		float b = 2*(ray.direction.dot_product(center_position.substract(ray.startPosition)));
		float c = (float)Math.pow(new Vector(ray.startPosition, center_position).calcLength(),2) -
				(float)Math.pow(radius, 2);
		float determinante = b*b-4*a*c;
		if(determinante>=0)
		{		
			float dist = (float)((-b - Math.sqrt(determinante))/2);	
			return dist;
		}   	
    	return -1;   	
    }
}
