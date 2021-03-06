import java.util.Random;

public class Pixel {
	
	float r, g, b; //final color
	Ray[] rays;
	Color[] colors; //super sampling rays colors
	int index_i, index_j;
	Random rand;
	static final double randomFactor = 0.44;

	
	public Pixel(int samplingLevel, int i, int j)
	{
		rays = new Ray[samplingLevel*samplingLevel];
		colors = new Color[samplingLevel*samplingLevel];
		index_i = i;
		index_j = j;
		rand = new Random();
	}
	
	/*
	 * Set rays vectors from the pixel, according to 
	 * super-sampling method 
	 * 
	 */
	public void setRaysFromPixel(Camera cam, int samplingLevel)
	{
		int index=0;
		
		Vector toPixel = cam.screen.onePixel_right_direction.multiply_scalar((float)index_j).
				add(cam.screen.onePixel_up_direction.multiply_scalar((float)index_i));
		Vector topLeftPixelCorner = cam.screen.topLeft_screen_corner.add(toPixel);
		
		double randomX  = 0;
		double randomY = 0;
		
		for(int m=0; m<samplingLevel; m++)
		{
			for(int n=0; n<samplingLevel; n++)
			{
				while(randomX < randomFactor)
					randomX = rand.nextDouble();
				while (randomY < randomFactor)
					randomY = rand.nextDouble();

				Vector PixelCellPoint = cam.screen.PixelCell_right_direction.multiply_scalar((float)(m+randomX)).
						add(cam.screen.PixelCell_up_direction.multiply_scalar((float)(n+randomY)));
				PixelCellPoint = PixelCellPoint.add(topLeftPixelCorner);
				
				Vector rayDirection = cam.position.substract(PixelCellPoint);
				rayDirection.normalize();
				
				rays[index] = new Ray(PixelCellPoint, rayDirection);
				index++;
			}
		}
	}
	
	
	/*
	 * Go over the colors array and calculate the avarage pixel's color
	 * 
	 * update the rgbData array for the output 
	 * 
	 */
	public void calculatePixelColor(byte[] rgbData, int imageWidth)
	{
		Color finalColor = Color.average(colors);

		rgbData[(index_i*imageWidth + index_j)*3] = (byte)(Math.min(finalColor.r*255, 254));
		rgbData[(index_i*imageWidth + index_j)*3 + 1] = (byte)(Math.min(finalColor.g*255, 254));
		rgbData[(index_i*imageWidth + index_j)*3 + 2] = (byte)(Math.min(finalColor.b*255, 254));
	
		//
        // Write pixel color values in RGB format to rgbData:
        // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
        //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
        //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
        //
        // Each of the red, green and blue components should be a byte, i.e. 0-255		
	}

	public void updateInPixelColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		Color current = calculateInPixelColor(ray, recursionCount, scene, index);
		colors[index] = new Color(current.r, current.g, current.b);
	}
	
	
	private Color calculateInPixelColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		Color resColor = new Color(0,0,0);
		if(ray.min_distance_intersect < Float.MAX_VALUE && recursionCount < scene.max_recursion_level)
		{	
			resColor.add(addBackgroundColor(ray, recursionCount+1, scene, index).multiply_scalar(ray.closest_intersect.getMaterial(scene).transparency));
			
			for (Light light : scene.lights)
			{
				Vector start = light.position;
				Vector dir = start.substract(ray.getIntersectionPoint());

				dir.normalize();
				light.lightRay = new Ray(start, dir);
				resColor.add(addDiffuseColor(ray, scene, index, light));

 				dir = ray.getIntersectionPoint().substract(start);

				dir.normalize();
				light.lightRay = new Ray(start, dir);

				resColor.add(addSpecularColor(ray, light, scene));


			}
			resColor.add(addReflectiveColor(ray, recursionCount+1, scene, index));

			return resColor;
		}
		//a Ray that doesn't hit any surface 
		else
			return scene.background_color;
	}
	
	
	private Color addBackgroundColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		if(ray.closest_intersect.getMaterial(scene).transparency <= 0)
			return new Color(0,0,0);
		
		//if the material is transparent- the recursion should continue to the background
		Ray tansparancyRay = new Ray(ray.getIntersectionPoint(), ray.direction);
		tansparancyRay.checkTransRayIntersection(ray, scene);
		Color backColor = calculateInPixelColor(tansparancyRay, recursionCount, scene, index);

		//cellColor += color of back surface * the front surface transparency
		return backColor;//.multiply_scalar(ray.closest_intersect.getMaterial(scene).transparency);

		//System.out.format("Finished add background color, recursionCount is %d\n", recursionCount);

	}
	
	private Color addDiffuseColor(Ray ray, Scene scene, int index, Light light)
	{
		if(ray.closest_intersect.getMaterial(scene).transparency == 1)
			return new Color(0,0,0);
		Vector normal = ray.closest_intersect.calcNormal(ray.getIntersectionPoint());

		float dotproduct = light.lightRay.direction.dot_product(normal);
		dotproduct /= normal.calcLength();
		Vector normalized = normal.multiply_scalar(dotproduct);
		float a = normal.dot_product(normalized);
		if(a>0&&ray.closest_intersect.getMaterial(scene).transparency < 0.2)
			return new Color(0,0,0);

		//final color = ... + (diffuse + specular) * (1- transp)
		if(ray.closest_intersect.getMaterial(scene).transparency == 1)
			return new Color(0,0,0);

		float normalLightCos = Math.abs(normal.dot_product(light.lightRay.direction));

		float softShadowPrecent = calcSoftShadowPrecent(light, ray, scene);

		Color lightColor = light.color;
		Color MaterialDiffuse = ray.closest_intersect.getMaterial(scene).diffuse_color;
		
		Color lightIntensity = lightColor.multiply_color(MaterialDiffuse);

		Color diff = lightIntensity.multiply_scalar(softShadowPrecent);  
		diff = diff.multiply_scalar(normalLightCos);

		return diff.multiply_scalar(1 - ray.closest_intersect.getMaterial(scene).transparency);

	}

	private Color addSpecularColor(Ray ray, Light light, Scene scene)
	{

		Vector normal = ray.closest_intersect.calcNormal(ray.getIntersectionPoint());

		float dotproduct = light.lightRay.direction.dot_product(normal);
		dotproduct /= Math.pow(normal.calcLength(),2);
		Vector normalized = normal.multiply_scalar(dotproduct);
		
		Vector temp = normalized.multiply_scalar(-2);
		Vector lightReflection = light.lightRay.direction.add(temp);
		lightReflection.normalize();
		float cos = ray.direction.dot_product(lightReflection);


		Color specular = new Color(0,0,0);
		if(cos>0)
		{
			float phong = ray.closest_intersect.getMaterial(scene).phong_specularity_coefficient;
			specular = ray.closest_intersect.getMaterial(scene).specular_color;
			specular = specular.multiply_color(light.color);
			specular= specular.multiply_scalar((float)(Math.pow(cos, phong)*light.specular_intensity));
		}
		return specular.multiply_scalar(1 - ray.closest_intersect.getMaterial(scene).transparency);
		}
	
	private Color addReflectiveColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		Ray origin = new Ray(ray.startPosition, ray.direction);
		origin.min_distance_intersect = ray.min_distance_intersect;
		origin.closest_intersect = ray.closest_intersect;
		
		Vector normal = ray.closest_intersect.calcNormal(ray.getIntersectionPoint());
		float dotproduct = ray.direction.dot_product(normal);
		dotproduct /= Math.pow(normal.calcLength(),2);
		Vector normalized = normal.multiply_scalar(dotproduct);
		Material m = ray.closest_intersect.getMaterial(scene);

		Vector temp = normalized.multiply_scalar(-2);
		temp = temp.add(ray.direction);
		temp.normalize();
		ray.updateRay(ray.getIntersectionPoint(), temp);
		

		ray.checkTransRayIntersection(origin, scene);
		

		Color reflective = calculateInPixelColor(ray, recursionCount, scene, index);
		reflective = reflective.multiply_color(m.reflection_color);
		return reflective;
	}

	private float calcSoftShadowPrecent(Light light, Ray ray, Scene scene)
	{

		light.lightRay.startPosition = light.position;
		light.lightRay.direction = light.lightRay.startPosition.substract(ray.getIntersectionPoint());
		light.lightRay.direction.normalize();
		light.lightRay.min_distance_intersect = light.position.substract(ray.getIntersectionPoint()).calcLength();
		if(light.shadow_intensity == 0)
			return 1;
		
		//light from the center point only 
		float pointLightIntense = light.lightRay.checkLightRayIntersection(scene);

		if(scene.number_shadow_rays == 1)
			return pointLightIntense;
		

		//light from radius, numofShadowRays rays will be shoot
		float lightIntese = 0;
		
		Vector aroundLightX = light.lightRay.direction.cross_product(new Vector(1,1,1));
		if(aroundLightX.x == 0 && aroundLightX.y == 0 && aroundLightX.z == 0)
			aroundLightX =  light.lightRay.direction.cross_product(new Vector(1,0,0));
		
		Vector aroundLightY = light.lightRay.direction.cross_product(aroundLightX);
		aroundLightX.normalize();
		aroundLightY.normalize();
		Vector circlePoint = light.position.add(aroundLightX.multiply_scalar(-light.radius/2)).add(aroundLightY.multiply_scalar(-light.radius/2));
		aroundLightX = aroundLightX.multiply_scalar(light.radius/scene.number_shadow_rays);
		aroundLightY = aroundLightY.multiply_scalar(light.radius/scene.number_shadow_rays);
		
		for(int i=0; i<scene.number_shadow_rays; i++)		
		{
			for(int j=0; j<scene.number_shadow_rays; j++)
			{

				light.lightRay.startPosition = circlePoint.add(aroundLightX.multiply_scalar((float)(i+rand.nextDouble()))).add(aroundLightY.multiply_scalar((float)(j+rand.nextDouble())));
				light.lightRay.direction = light.lightRay.startPosition.substract(ray.getIntersectionPoint());
				

				light.lightRay.direction.normalize();

				lightIntese += light.lightRay.checkLightRayIntersection(scene);
			}
		}

		return Math.max(lightIntese, 1-light.shadow_intensity) / (float)Math.pow(scene.number_shadow_rays, 2);
	}

}
