import java.util.Random;

public class Pixel {
	
	float r, g, b; //final color
	Ray[] rays;
	Color[] colors; //super sampling rays colors
	int index_i, index_j;
	Random rand;
	Color cellColor;
	
	public Pixel(int samplingLevel, int i, int j)
	{
		rays = new Ray[samplingLevel*samplingLevel];
		colors = new Color[samplingLevel*samplingLevel];
		index_i = i;
		index_j = j;
		rand = new Random();
		cellColor = new Color(0,0,0);
	}
	
	/*
	 * Set rays vectors from the pixel, acoording to 
	 * super-sampling method 
	 * 
	 */
	public void setRaysFromPixel(Camera cam, int samplingLevel)
	{
		int index=0;
		
		Vector toPixel = cam.screen.onePixel_right_direction.multiply_scalar((float)index_j).
				add(cam.screen.onePixel_up_direction.multiply_scalar((float)index_i));
		Vector topLeftPixelCorner = cam.screen.topLeft_screen_corner.add(toPixel);
		
		double randomX, randomY;
		
		for(int m=0; m<samplingLevel; m++)
		{
			for(int n=0; n<samplingLevel; n++)
			{
				randomX = rand.nextDouble();
				randomY = rand.nextDouble();
				
				Vector PixelCellPoint = cam.screen.onePixel_right_direction.multiply_scalar((float)(m+randomX)).
						add(cam.screen.onePixel_up_direction.multiply_scalar((float)(n+randomY)));
				PixelCellPoint = PixelCellPoint.add(topLeftPixelCorner);
				
				Vector rayDirection = cam.position.substract(PixelCellPoint);
				rayDirection.normalize();
				
		//		System.out.format("looking for index %d , length is %d\n", index, rays.length);
				rays[index] = new Ray(PixelCellPoint, rayDirection);
				index++;
			//	System.out.format("index: %d \n", index);
			}
		}
		System.out.println("Finished set rays from pixel\n");
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
		
		rgbData[(index_i* imageWidth + index_j)*3] = (byte)(finalColor.r*255);
		rgbData[(index_i*imageWidth + index_j)*3 + 1] = (byte)(finalColor.g*255);
		rgbData[(index_i*imageWidth + index_j)*3 + 2] = (byte)(finalColor.b*255);
	
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
		cellColor = new Color(0,0,0);
		Color current = calculateInPixelColor(ray, recursionCount, scene, index);
		colors[index] = new Color(current.r, current.g, current.b);
	}
	
	
	private Color calculateInPixelColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		//colors[index]=...
		//rays[index]
		if(ray.min_distance_intersect < Float.MAX_VALUE && recursionCount < scene.max_recursion_level)
		{	
			addBackgroundColor(ray, recursionCount, scene, index);
			
			//Color diffuzedAndSpecular = new Color(0,0,0);
			for (Light light : scene.lights)
			{
				Vector start = light.position;
				Vector dir = ray.getIntersectionPoint().substract(start);
				light.lightRay = new Ray(start, dir);
				
				addDiffuseColor(ray, scene, index, light); 
				if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
					System.out.format("dir: %f , after diffuse\n", ray.direction.x);
					
				addSpecularColor(ray, light, scene);
				System.out.format("dir: %f , after spec\n", ray.direction.x);
				if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
					System.out.format("inside!!!!!!! dir: %f , after spec\n", ray.direction.x);
				
				addReflectiveColor(ray, recursionCount, scene, index, light);
				if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
					System.out.format("dir: %f , after reflect\n", ray.direction.x);
				
			}
			System.out.format("%f\n", cellColor.r);

			return cellColor;
		}
		//a Ray that doesn't hit any surface 
		else
			return scene.background_color;
	}
	
	
	private void addBackgroundColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		if(ray.closest_intersect.getMaterial(scene).transparency <= 0)
			return;
		
		//if the material is transparent- the recursion should continue to the background
		Vector rayIntersection = ray.startPosition.add(ray.direction.multiply_scalar(ray.min_distance_intersect));	
		Ray tansparancyRay = new Ray(rayIntersection, ray.direction);
		tansparancyRay.checkTransRayIntersection(ray, scene);
		Color backColor = calculateInPixelColor(tansparancyRay, recursionCount+1, scene, index);
		
		//cellColor += color of back surface * the front surface transparency
		cellColor.add(backColor.multiply_scalar(ray.closest_intersect.getMaterial(scene).transparency));
		
		if(Float.isNaN(ray.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		//System.out.format("Finished add background color, recursionCount is %d\n", recursionCount);

	}
	
	private void addDiffuseColor(Ray ray, Scene scene, int index, Light light) 
	{	
		//final color = ... + (diffuse + specular) * (1- transp)
		if(ray.closest_intersect.getMaterial(scene).transparency == 1)
			return; 
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		//if(Vector.dotProduct(ray.getNormal(), light.direction.getProjection(ray.getNormal())) >0 && ray.surface.getTransparency() < 0.2)
		//	return this.black;
		
		Vector normal = ray.closest_intersect.calcNormal(ray.getIntersectionPoint());
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		float normalLightCos = Math.abs(normal.dot_product(light.lightRay.direction));
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		float softShadowPrecent = calcSoftShadowPrecent(light, ray, scene);
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Color lightColor = light.color;
		Color MaterialDiffuse = ray.closest_intersect.getMaterial(scene).diffuse_color;
		
		Color lightIntensity = lightColor.multiply_color(MaterialDiffuse);
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Color diff = lightIntensity.multiply_scalar(softShadowPrecent);
		diff = diff.multiply_scalar(normalLightCos);
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		cellColor.add(diff.multiply_scalar(1 - ray.closest_intersect.getMaterial(scene).transparency));
		
	//	System.out.println("Finished add diffuse color\n");
	}

	private void addSpecularColor(Ray ray, Light light, Scene scene) 
	{
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Vector normal = ray.closest_intersect.calcNormal(ray.getIntersectionPoint());
		float dotproduct = light.lightRay.direction.dot_product(normal);
		dotproduct /= Math.pow(normal.calcLength(),2);
		Vector normalized = normal.multiply_scalar(dotproduct);
		
		Vector temp = normalized.multiply_scalar(-2);
		Vector lightReflection = light.lightRay.direction.add(temp);
		lightReflection.normalize();
		Vector intersection_flip = ray.direction.multiply_scalar(-1.0f);
		float cos = intersection_flip.dot_product(lightReflection);

		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Color specular = new Color(0,0,0);
		if(cos>0)
		{
			float phong = ray.closest_intersect.getMaterial(scene).phong_specularity_coefficient;
			specular = ray.closest_intersect.getMaterial(scene).specular_color;
			specular = specular.multiply_color(light.color);
			specular= specular.multiply_scalar((float)(Math.pow(cos, phong)*light.specular_intensity));
		}
		
		cellColor.add(specular.multiply_scalar(1 - ray.closest_intersect.getMaterial(scene).transparency));
		//System.out.println("Finished add specular color\n");
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
	}
	
	private void addReflectiveColor(Ray ray, int recursionCount, Scene scene, int index, Light light)
	{
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Vector normal = ray.closest_intersect.calcNormal(ray.getIntersectionPoint());
		float dotproduct = light.lightRay.direction.dot_product(normal);
		dotproduct /= Math.pow(normal.calcLength(),2);
		Vector normalized = normal.multiply_scalar(dotproduct);
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Vector temp = normalized.multiply_scalar(-2);
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		ray.updateRay(ray.startPosition, ray.direction.add(temp));
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		ray.checkTransRayIntersection(ray, scene);
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		Color reflective = calculateInPixelColor(ray, recursionCount+1, scene, index);
		reflective = reflective.multiply_color(ray.closest_intersect.getMaterial(scene).reflection_color);
			
		cellColor.add(reflective);
		//System.out.format("Finished add reflective color, recursionCount is %d\n", recursionCount);
		
		if(Float.isNaN(ray.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
	}

	private float calcSoftShadowPrecent(Light light, Ray ray, Scene scene)
	{
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		light.lightRay.startPosition = light.position;
		light.lightRay.direction = ray.getIntersectionPoint().substract(light.lightRay.startPosition);
		light.lightRay.direction.normalize();
		light.lightRay.min_distance_intersect = light.position.substract(ray.getIntersectionPoint()).calcLength();
	
		//light from the center point only 
		float pointLightIntense = light.lightRay.checkLightRayIntersection(scene);
		if(scene.number_shadow_rays == 1)
			return pointLightIntense;
		
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		//light from radius, numofShadowRays rays will be shoot
		float lightIntese = 0;
		
		//////////////
		Vector aroundLightX = light.lightRay.direction.cross_product(new Vector(1,1,1));
		if(aroundLightX.x == 0 && aroundLightX.y == 0 && aroundLightX.z == 0)
			aroundLightX =  light.lightRay.direction.cross_product(new Vector(1,0,0));
		
		Vector aroundLightY = light.lightRay.direction.cross_product(aroundLightX);
		aroundLightX.normalize();
		aroundLightY.normalize();
		Vector circlePoint = light.position.add(aroundLightX.multiply_scalar(-light.radius/2)).add(aroundLightY.multiply_scalar(-light.radius/2));
		aroundLightX = aroundLightX.multiply_scalar(light.radius/scene.number_shadow_rays);
		aroundLightY = aroundLightY.multiply_scalar(light.radius/scene.number_shadow_rays);
	////////////
		
		for(int i=0; i<scene.number_shadow_rays; i++)		
		{
			for(int j=0; j<scene.number_shadow_rays; j++)
			{
				if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
					System.out.format("dir: %f , after reflect\n", ray.direction.x);
				
				light.lightRay.startPosition = circlePoint.add(aroundLightX.multiply_scalar((float)(i+rand.nextDouble()))).add(aroundLightY.multiply_scalar((float)(j+rand.nextDouble())));
				light.lightRay.direction = ray.getIntersectionPoint().substract(light.lightRay.startPosition);
				
				if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
					System.out.format("dir: %f , after reflect\n", ray.direction.x);
				
				light.lightRay.direction.normalize(); 
				
				lightIntese += light.lightRay.checkLightRayIntersection(scene);
			}
		}
		if(Float.isNaN(ray.direction.x) || Float.isNaN(light.lightRay.direction.x))
			System.out.format("dir: %f , after reflect\n", ray.direction.x);
		
		return Math.max(lightIntese, 1-light.shadow_intensity)/(float)Math.pow(scene.number_shadow_rays, 2);
	}

}