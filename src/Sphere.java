public class Sphere extends Surface {
    Vector center_position;
    float radius;

    
    @Override
    public Vector calcNormal(Vector point)
    {
    	Vector normal = center_position.substract(point);
//		Vector normal = point.substract(center_position);

		normal.normalize();
    	return normal;  	
    }
    
    /*
     * The function calculate a ray-surface intersect, according to the surface type.
     *  
     * @Return - float distance of the ray-surface intersect, 
     * the return value will be -1 if there is no intersect
     * 
     */
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
