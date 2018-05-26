import java.util.Random;

public class Pixel {
	
	float r, g, b; //final color
	Ray[] rays;
	Color[] colors; //super sampling rays colors
	int index_i, index_j;
	Random rand;
	
	public Pixel(int samplingLevel, int i, int j)
	{
		rays = new Ray[samplingLevel*samplingLevel];
		colors = new Color[samplingLevel*samplingLevel];
		index_i = i;
		index_j = j;
		rand = new Random();
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
				
				Vector rayDirection = new Vector(cam.position, PixelCellPoint);
				rayDirection.toUnitVector();
				
				rays[index].updateRay(PixelCellPoint, rayDirection);
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
		Color current = calculateInPixelColor(ray, recursionCount, scene, index);
		colors[index].r = current.r;
		colors[index].g = current.g;
		colors[index].b = current.b;
	}
	
	
	private Color calculateInPixelColor(Ray ray, int recursionCount, Scene scene, int index)
	{
		//colors[index]=...
		//rays[index]
		
		Color cellColor = new Color(0,0,0);
		
		if(ray.min_distance_intersect < Float.MAX_VALUE && recursionCount < scene.max_recursion_level)
		{	
			addBackgroundColor(ray, recursionCount, scene, index, cellColor);
			
			Color diffuzedAndSpecular = new Color(0,0,0);
			for (Light light : scene.lights)
			{
				float numOfLightHits = light.numOfLightRaysHits(ray, this.surfaces, this.numberOfShadowRays);
				diffuzedAndSpecular = diffuzedAndSpecular.add(getDiffusedColor(ray,light, numOfLightHits));
				if (light.mainRay > 0.001)
					diffuzedAndSpecular = diffuzedAndSpecular.add(getSpecularColor(ray,light));
				
			}
			diffuzedAndSpecular = diffuzedAndSpecular.scalarProduct(1-ray.surface.getTransparency());
			return backGround.add(diffuzedAndSpecular).add(getReflectionColor(ray,time+1));
		}
		else
			return scene.background_color;
	}
	
	
	private void addBackgroundColor(Ray ray, int recursionCount, Scene scene, int index, Color cellColor)
	{
		int mtl = ray.closest_intersect.material;
		
		//if the material is transparent- the recursion should continue to the background
		if(scene.materials.get(mtl).transparency > 0) 
		{	
			Vector rayIntersection = ray.startPosition.add(ray.direction.multiply_scalar(ray.min_distance_intersect));	
			Ray tansparancyRay = new Ray(rayIntersection, ray.direction);
			tansparancyRay.checkTransRayIntersection(ray, scene);
			Color backColor = calculateInPixelColor(tansparancyRay, recursionCount+1, scene, index);
			
			//cellColor += color of back surface * the front surface transparency
			cellColor.add(backColor.multiply(scene.materials.get(mtl).transparency));
		}
	}
	
	private void addDiffuseColor(Ray ray, Scene scene, int index, Color cellColor) 
	{	
		int mtl = ray.closest_intersect.material;
		
		if(scene.materials.get(mtl).transparency == 1)
			return; 
		
		
		if(Vector.dotProduct(ray.getNormal(), light.direction.getProjection(ray.getNormal())) >0 && ray.surface.getTransparency() < 0.2)
			return this.black;
		
		float cos = Vector.dotProduct(ray.getNormal(), light.direction); //they both unit vector so no need to divide by the length
		
		Color lightIntensity = ray.surface.getDiffuseColor().multiply(light.color);
		
		if (numOfLightHits == 0)
			lightIntensity = lightIntensity.scalarProduct(1-light.shadowIntensity); 
		else	
			lightIntensity = lightIntensity.scalarProduct((float)(numOfLightHits)/this.squareNumberOfShadowRays);
		
		
		//System.out.println("a - " +resultDiffuseColor);
		return lightIntensity.scalarProduct(Math.abs(cos));
	}

	private Color getSpecularColor(Ray ray, Light light) //need to be complete:
	{
		Vector b = light.direction.getProjection(ray.getNormal()).scalarProduct(-2);
		Vector lightReflection = light.direction.add(b).toUnitVector();
		float cos = Vector.dotProduct(ray.toCam, lightReflection);
		//System.out.println(ray.surface.getSpecularColor());
		if(cos>0)
		{
			Color c = ray.surface.getSpecularColor().multiply(light.color).
					scalarProduct((float)(Math.pow(cos, ray.surface.getPhong())*light.specularIntensity));
			return c;
		}
		return this.black;
	}
	
	private Color getReflectionColor(Ray ray, int time)
	{
		Vector b = ray.direction.getProjection(ray.getNormal()).scalarProduct(-2);
		
		iSurface i = ray.surface;
		ray.setNewRay(ray.intersection, ray.direction.add(b));
		
		for(iSurface surface: this.surfaces)
		{
			if(surface == i)
				continue;
			surface.intersectes(ray);
		}
		ray.getIntersection();
		
		return getColorFromRay(ray,time).multiply(i.getReflectionColor());
	}

}
