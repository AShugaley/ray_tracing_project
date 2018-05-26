public class Light {

    public Vector position;
    public Color color;
    public float specular_intencity;
    public float shadow_intencity;
    public float radius;
    Ray lightRay;


    public float isHitByLight(Vector intersection, Vector currentLightVector, Scene scene)
	{
		float value = 1;
		
		Vector lightDir = intersection.substract(currentLightVector);
		lightDir.toUnitVector();
		
		lightRay = new Ray(currentLightVector, lightDir);
		
		
		/////////********? delete *******////// 
		//why do we care about a specific ray when we suppose to go over all lights and all surfaces? 
		float distLightToIntersect = currentLightVector.substract(intersection).calcLength();
		
		for (Surface surface : scene.surfaces)
		{
			if(surface.inDistance(lightRay, 0, distLightToIntersect))
				value *= surface.getMaterial(scene).transparency;
			if(value < 0.01)
				break;
		}
		return value;
		
	}
	
	public float numOfLightRaysHits(Ray ray, iSurface[] surfaces, int rootNumberOfShadowRays )
	{
		this.direction =  ray.intersection.subtruct(this.position).toUnitVector();
		this.mainRay = this.isHitByLight(ray.intersection, this.position, surfaces,ray.surface);
		if(rootNumberOfShadowRays == 1)
			return this.mainRay;
		
		Vector tempX = Vector.crossProduct(this.direction, this.v1);
		if(tempX.x == 0 && tempX.y == 0 && tempX.z == 0)
			tempX = Vector.crossProduct(this.direction, this.v2);
		
		Vector tempY = Vector.crossProduct(this.direction, tempX);
		tempX = tempX.toUnitVector();
		tempY = tempY.toUnitVector();
		Vector mostLeftUp = this.position.add(tempX.scalarProduct(-this.radius/2)).add(tempY.scalarProduct(-this.radius/2));
		tempX = tempX.scalarProduct(this.radius/rootNumberOfShadowRays);
		tempY = tempY.scalarProduct(this.radius/rootNumberOfShadowRays);
		
		float numOfHits = 0;
		for(int i=0;i<rootNumberOfShadowRays;i++)
			for(int j=0;j<rootNumberOfShadowRays;j++)
				numOfHits += this.isHitByLight(ray.intersection, 
						mostLeftUp.add(tempX.scalarProduct(i+generatorRandoms.nextFloat())).add(tempY.scalarProduct(j+generatorRandoms.nextFloat())), surfaces,ray.surface);
		return numOfHits;
	}
}
