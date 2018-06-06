public class Ray 
{
	Vector startPosition; 
	Vector direction;
	Surface closest_intersect;
	float min_distance_intersect;
	
	public Ray() 
	{
		min_distance_intersect = Float.MAX_VALUE;
	};
	
	public Ray(Vector start, Vector dir)
	{
		startPosition = start;
		direction = dir;
		min_distance_intersect = Float.MAX_VALUE;
	}
	
	public void updateRay(Vector start, Vector dir)
	{
		startPosition = start;
		direction = dir;
		min_distance_intersect = Float.MAX_VALUE;
		
	}
	
	public void checkRayIntersection(Scene scene)
	{
		float dist; 
		for(Surface surface: scene.surfaces)
		{
			dist = surface.intersectDist(this); //dist = -1 means there is no intersection
			if(dist > Surface.epsilon && dist + Surface.epsilon < min_distance_intersect)
			{
				min_distance_intersect = dist;
				closest_intersect = surface;
			}
		}
	}
	
	public float checkLightRayIntersection(Scene scene)
	{
		float res=1, dist;
		for(Surface surface: scene.surfaces)
		{
			dist = surface.intersectDist(this); //dist = -1 means there is no intersection
			if(dist > Surface.epsilon && dist + Surface.epsilon < min_distance_intersect)
			{
				res *= surface.getMaterial(scene).transparency;
			}
			if(res < Surface.epsilon ) //almost 0
				break;
		}
		return res;
	}
	
	
	public void checkTransRayIntersection(Ray originRay, Scene scene)
	{
		float dist;
		for(Surface surface: scene.surfaces)
		{
			//The first surface that was intersect
			if(surface == originRay.closest_intersect)
				continue;
			dist = surface.intersectDist(this);
			if(dist > 0 && dist < min_distance_intersect)
			{
				min_distance_intersect = dist;
				closest_intersect = surface;
			}
		}		
	}
	
	public Vector getIntersectionPoint()
	{
		return startPosition.add(direction.multiply_scalar(min_distance_intersect));
	}


}
