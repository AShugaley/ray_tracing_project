public class InfinitePlane extends Surface {
    public Vector normal;
    public Float offset;
    
    @Override
    public Vector calcNormal(Vector point)
    {
    	normal.normalize();
    	return normal;
    }

    @Override
    public float intersectDist(Ray ray) 
    {
		// assuming vectors are all normalized
		Vector intersectionPoint = normal.multiply_scalar(offset);
		normal.normalize();
	    float denom = normal.dot_product(ray.direction);
	    if (Math.abs(denom) > epsilon)
	    { 
	        Vector rayPlane = ray.startPosition.substract(intersectionPoint);
	        float dist = rayPlane.dot_product(normal) / denom; 
	        if(dist < 0)
	        	return -1;
	        else 
	        	return dist;
	    } 
	 
	    return -1;     	  	
    }

}