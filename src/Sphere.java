public class Sphere extends Surface {
    Vector center_position;
    float radius;

    
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
    	//	System.out.println(ray.origin);
		float b = 2*(ray.direction.dot_product(ray.startPosition.substract(center_position)));
		float c = (float)Math.pow(new Vector(ray.startPosition, center_position).calcLength(),2) -
				(float)Math.pow(radius, 2);
		float determinante = b*b-4*a*c;
		if(determinante>=0)
		{		
			float dist = (float)((-b - Math.sqrt(determinante))/2);
			if(dist>epsilon && dist + epsilon < ray.min_distance_intersect)
				return dist;
		}
	
    	
    	return -1;
    	
    	
    }
    

}
