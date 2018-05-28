public class Triangle extends Surface {
    public Vector v1;
    public Vector v2;
    public Vector v3;
    
    @Override
    public Vector calcNormal(Vector point)
    {
    	// compute triangle's normal
	    Vector v1v2 = v2.substract(v1); 
	    Vector v1v3 = v3.substract(v1); 
	   
	    // no need to normalize
	    Vector normal = v1v2.cross_product(v1v3); 
	    normal.normalize();
	    return normal;
    }
    
    
    /*
     * The function calculate a ray-surface intersect, according to the surface type.
     *  
     * @Return - float distance of the ray-surface intersection, 
     * the return value will be -1 if there is no intersection
     * 
     */
    @Override
    public float intersectDist(Ray ray)
	{ 
	     Vector normal = calcNormal(new Vector());
	   	    
	    // First: finding the point of the ray's intersection with the triangle
	    // check if ray and plane are parallel:
	    float NdotRayDirection = normal.dot_product(ray.direction); 
	    if (Math.abs(NdotRayDirection) < epsilon) // almost 0 
	        return -1; // they are parallel so they don't intersect ! 
	 
	    float d = normal.dot_product(v1); 
	    float dist = (normal.dot_product(ray.startPosition) + d) / NdotRayDirection; 
	    if (dist < 0) 
	    	return -1; // the triangle is behind the ray
	 
	    // compute the intersection point
	    Vector intersection_point = ray.startPosition.add(ray.direction.multiply_scalar(dist)); 
	 
	    // Second: inside-outside test
	    Vector C; // vector perpendicular to triangle's plane 
	 
	    //side 1 of the triangle
	    Vector edge0 = v2.substract(v1); 
	    Vector v1p = intersection_point.substract(v1); 
	    C = edge0.cross_product(v1p); 
	    if (normal.dot_product(C) < 0) 
	    	return -1; // P is on the right side 
	 
	    // side 2 of the triangle
	    Vector edge1 = v3.substract(v2); 
	    Vector v2p = intersection_point.substract(v2); 
	    C = edge1.cross_product(v2p); 
	    if (normal.dot_product(C) < 0)  
	    	return -1; // P is on the right side 
	 
	    // side 3 of the triangle
	    Vector edge2 = v1.substract(v3); 
	    Vector v3p = intersection_point.substract(v3); 
	    C = edge2.cross_product(v3p); 
	    if (normal.dot_product(C) < 0) 
	    	return -1; // P is on the right side; 
	 
	    return dist; // the ray hits the triangle in t distance 
	 } 
    
    /*@Override
    public boolean inDistance(Ray ray, float min, float max)
	{
		float temp = Vector.dotProduct(this.normal, ray.direction);
		if(temp == 0)
			return false;
		float d = -(Vector.dotProduct(this.normal, ray.origin) - this.offset) / temp; 
		if(d<=min + this.epsilon || d +  this.epsilon > max)
			return false;
		
		Vector intersection = ray.origin.add(ray.direction.scalarProduct(d));
		
		Vector C0 = new Vector(this.first, intersection); 
		if (Vector.dotProduct(C0,this.firstNormal) < 0)
			return false;
					
		Vector C1 = new Vector(this.second, intersection); 
		if (Vector.dotProduct(C1,this.secondNormal) < 0)
			return false;
		
		Vector C2 = new Vector(this.third, intersection); 
		if (Vector.dotProduct(C2,this.thirdNormal) < 0)
			return false;
		
		return true;
	}*/
}
