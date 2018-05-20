import java.lang.Math;

public class RayTracer {
	Scene scene;
	float[][] image;
	int image_width;
	
	public RayTracer()
	{
		image = new float[500][500];
		image_width = 500;
	}
	
	public RayTracer(int width)
	{
		image = new float[width][width];
		image_width = width;
	}
	
	float getPixelPosX(int i, int j)
	{
		
	}
	
	public void shootRays()
	{
		for(int i=0; i<image.length; i++)
		{
			for(int j=0; j<image[0].length; j++)
			{
				//num of rays to shoot
				for(int r = 0; r< Math.pow(scene.camera.screen_distance, scene.camera.screen_distance); r++)
				{
					float z = scene.camera.screen_distance;
					float x = ;
					float y = ;
					Vector ray = new Vector(x,y,z);
				}
				
			}
		}
	}
}
