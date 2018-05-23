
public class Pixel {
	
	float r, g, b; //final color
	Ray[] rays;
	Color[] colors; //super sampling rays colors
	int index_i, index_j;
	
	public Pixel(int samplingLevel, int i, int j)
	{
		rays = new Ray[samplingLevel*samplingLevel];
		colors = new Color[samplingLevel*samplingLevel];
		index_i = i;
		index_j = j;
		
	}
	
	/*
	 * Set rays vectors from the pixel, acoording to 
	 * super-sampling method 
	 * 
	 */
	public void setRaysFromPixel(Camera cam)
	{
		int t=0;
		Vector pixelPoint = this.mostLeftUp.add(this.pixelWidthDirection.scalarProduct((float)(j)).add(this.pixelHeightDirection.scalarProduct((float)(i))));
		
		float random = generatorRandoms.nextFloat();
		for(int k=0;k<this.superSamplingLevel;k++)
		{
			for(int p=0;p<this.superSamplingLevel;p++)
			{
				while (random < 0.6)
					random = generatorRandoms.nextFloat();
				Vector inPixelPoint = pixelPoint.add(this.inPixelWidthDirection.scalarProduct((float)(p)+random).add(this.inPixelHeightDirection.scalarProduct((float)(k)+random)));
				rays[t].setNewRay(inPixelPoint, new Vector(this.location, pixelPoint).toUnitVector());
				t++;
			}
		}
		
	}
	
	
	/*
	 * Go over the colors array and calculate the avarage pixel's color
	 * 
	 * update the rgbData array for the output 
	 * 
	 */
	public byte[] calculatePixelColor(int index)
	{
		
		return 1;
		
	/*	Color c = Color.average(colors);
		rgbData[(i*this.imageWidth + j)*3] = (byte)(c.red*255);
		rgbData[(i*this.imageWidth + j)*3+1] = (byte)(c.green*255);
		rgbData[(i*this.imageWidth + j)*3+2] = (byte)(c.blue*255);
	*/	
		
		
	}

	
	public void calculateInPixelColor(int index, int time)
	{
		//colors[index]=...
		//rays[index]
		
		if(ray.d < Float.MAX_VALUE-3 && time < this.maximumRecursionLevel)
		{
	
			//System.out.println(ray.surface.getDiffuseColor());
			Color backGround = this.black;
			if(ray.surface.getTransparency() > 0)
			{
				backGround = backGround.add(getBackGroundColor(ray,time+1).scalarProduct(ray.surface.getTransparency()));
			//	System.out.println(backGround + " " + ray.surface.getTransparency());
			}
			Color diffuzedAndSpecular = this.black;
			for (Light light : this.lights)
			{
				float numOfLightHits = light.numOfLightRaysHits(ray, this.surfaces, this.numberOfShadowRays);
				//System.out.println(numOfLightHits);
				diffuzedAndSpecular = diffuzedAndSpecular.add(getDiffusedColor(ray,light, numOfLightHits));
				if (light.mainRay > 0.001)
					diffuzedAndSpecular = diffuzedAndSpecular.add(getSpecularColor(ray,light));
				
			}
			diffuzedAndSpecular = diffuzedAndSpecular.scalarProduct(1-ray.surface.getTransparency());
			return backGround.add(diffuzedAndSpecular).add(getReflectionColor(ray,time+1));
		}
		else
			return this.backgroundColor;
	}

}
