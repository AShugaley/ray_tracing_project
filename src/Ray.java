public class Ray 
{
	Vector startPosition; 
	Vector direction;
	Surface closest_intersect;
	float min_distance_intersect;
	float length;
	
	
	public Ray() 
	{
		length = Float.MAX_VALUE;
	};
	
	public Ray(Vector start, Vector dir)
	{
		startPosition = start;
		direction = dir;
		length = Float.MAX_VALUE;
	}
	
	public void updateRay(Vector start, Vector dir)
	{
		startPosition = start;
		direction = dir;
	}
	
	public void updateRayLength(float l)
	{
		length = l;
	}

}
