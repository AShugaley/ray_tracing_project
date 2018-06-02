import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

 
import javax.imageio.ImageIO;
import java.net.URL;
/**
 *  Main class for ray tracing exercise.
 */
public class RayTracer {
 
    public int imageWidth;
    public int imageHeight;
    Scene scene;
	float[][] image;
	//Ray[] rays;
	Pixel[][] pixels;
	
	
	public RayTracer()
	{
		image = new float[imageWidth][imageHeight];
	}
 
    /**
     * Runs the ray tracer. Takes scene file, output image file and image size as input.
     */
    public static void main(String[] args) {
 
    	/////////DELETE////////
    	//runTests();
    	
        try {
 
            RayTracer tracer = new RayTracer();
 
                        // Default values:
            tracer.imageWidth = 500;
            tracer.imageHeight = 500;
 
            if (args.length < 2)
                throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");
 
            String sceneFileName = args[0];
            String outputFileName = args[1];
 
            if (args.length > 3)
            {
                tracer.imageWidth = Integer.parseInt(args[2]);
                tracer.imageHeight = Integer.parseInt(args[3]);
            }

            sceneFileName = "Pool.txt";
            outputFileName = "Pool_res.png";
 
            // Parse scene file:
            //TODO - light that line when parser will be update 
            tracer.parseScene(sceneFileName);
            
            //delete that after parser update 
            //tracer.scene = Parser.parseFile(sceneFileName);
         
            // Render scene:
            tracer.renderScene(outputFileName);
 
//      } catch (IOException e) {
//          System.out.println(e.getMessage());
        } catch (RayTracerException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
 
    ///////DELETE//////
    public static void runTests(){
        //Tests.vectorTests();
        //Tests.parserTests();
    }
    
    
    //TODO - ALEX - merge with our parser
    /**
     * Parses the scene file and creates the scene. Change this function so it generates the required objects.
     */
    public void parseScene(String sceneFileName) throws IOException, RayTracerException
    {
        scene = new Scene();
        URL url = scene.getClass().getResource(sceneFileName);
        File f = new File(url.getPath()); //change this if you pass file from cmd - delete this row and the one above, change 'f' to 'file' in the row below

        FileReader fr = new FileReader(f);
 
        BufferedReader r = new BufferedReader(fr);
        String line = null;
        int lineNum = 0;
        System.out.println("Started parsing scene file " + sceneFileName);
 
 
 
        while ((line = r.readLine()) != null)
        {
            line = line.trim();
            ++lineNum;
 
            if (line.isEmpty() || (line.charAt(0) == '#'))
            {  // This line in the scene file is a comment
                continue;
            }
            else
            {
                String code = line.substring(0, 3).toLowerCase();
                // Split according to white space characters:
                String[] params = line.toLowerCase().split("\\s+");
 
                if (code.equals("cam"))
                {
                    Vector position = new Vector(params[1],params[2],params[3]);
                    Vector look_at_point = new Vector(params[4],params[5],params[6]);
                    Vector up_vector = new Vector(params[7],params[8],params[9]);
                    float screen_distance = Float.parseFloat(params[10]);
                    float screen_width = Float.parseFloat(params[11]);

                    Camera c = new Camera(position, look_at_point, up_vector, screen_distance, screen_width);
                    scene.camera = c;
                    System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
                }
                else if (code.equals("set"))
                {
                    scene.background_color = new Color(params[1],params[2],params[3]);
                    scene.number_shadow_rays = Integer.parseInt(params[4]);
                    scene.max_recursion_level = Integer.parseInt(params[5]);
                    scene.super_sampling_level = Integer.parseInt(params[6]);

                    System.out.println(String.format("Parsed general settings (line %d)", lineNum));
                }
                else if (code.equals("mtl"))
                {
                    Material m = new Material();

                    m.diffuse_color = new Color(params[1],params[2],params[3]);
                    m.specular_color = new Color(params[4],params[5],params[6]);
                    m.reflection_color = new Color(params[7],params[8],params[9]);
                    m.phong_specularity_coefficient = Float.parseFloat(params[10]);
                    m.transparency = Float.parseFloat(params[11]);

                    scene.materials.add(m);
                    System.out.println(String.format("Parsed material (line %d)", lineNum));
                }
                else if (code.equals("sph"))
                {
                    Sphere sc = new Sphere();

                    sc.center_position =  new Vector(params[1],params[2],params[3]);
                    sc.radius = Float.parseFloat(params[4]);
                    sc.material = Integer.parseInt(params[5]);

                    scene.surfaces.add(sc);

                    System.out.println(String.format("Parsed sphere (line %d)", lineNum));
                }
                else if (code.equals("pln"))
                {
                    InfinitePlane p = new InfinitePlane();

                    p.normal = new Vector(params[1],params[2],params[3]);
                    p.offset = Float.parseFloat(params[4]);
                    p.material = Integer.parseInt(params[5]);


                    scene.surfaces.add(p);            // Add code here to parse plane parameters
 
                    System.out.println(String.format("Parsed plane (line %d)", lineNum));
                }
                else if (code.equals("lgt"))
                {
                    Light l = new Light();

                    l.position = new Vector(params[1],params[2],params[3]);
                    l.color = new Color(params[4],params[5],params[6]);
                    l.specular_intensity = Float.parseFloat(params[7]);
                    l.shadow_intensity = Float.parseFloat(params[8]);
                    l.radius = Float.parseFloat(params[9]);

                    scene.lights.add(l);

                    System.out.println(String.format("Parsed light (line %d)", lineNum));
                }
                else
                {
                    System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
                }
            }
        }
 
                // It is recommended that you check here that the scene is valid,
                // for example camera settings and all necessary materials were defined.
 
        System.out.println("Finished parsing scene file " + sceneFileName);
 
    }
 
    
    
    /**
     * Renders the loaded scene and saves it to the specified file location.
     */
    public void renderScene(String outputFileName)
    {
        long startTime = System.currentTimeMillis();
 
        // Create a byte array to hold the pixel data:
        byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];
 
        
        // Put your ray tracing code here!
        pixels = new Pixel[imageHeight][imageWidth];
        scene.camera.screen.updateScreenParams(imageHeight, imageWidth, scene.camera, scene.super_sampling_level);
        scene.max_recursion_level = 2;
		//TODO - if there are no surfaces - create background and exit
		
		for(int i=0; i<imageHeight; i++)
		{
		    if(i == 400)
		        System.out.println("HELLLOOOOOO");
			for(int j=0; j<imageWidth; j++)
			{
                if(j == 100)
                    System.out.println("HELLLOOOOOO");
				pixels[i][j] = new Pixel(scene.super_sampling_level, i, j);
				
				//For each pixel fill the rays array 
				pixels[i][j].setRaysFromPixel(scene.camera, scene.super_sampling_level);
				
				//For each ray - check which surface intersects with it on the closest position 
				for(int k=0; k<pixels[i][j].rays.length; k++)
				{
					pixels[i][j].rays[k].checkRayIntersection(scene);
					
					//pixels[i][j].rays[k].updateRayLength(dist);
					pixels[i][j].updateInPixelColor(pixels[i][j].rays[k],1, scene, k);
				}
				
				//The color from all the rays is already update
				pixels[i][j].calculatePixelColor(rgbData, imageWidth); 	
			}
		}
               
        long endTime = System.currentTimeMillis();
        Long renderTime = endTime - startTime;
 
                // The time is measured for your own conveniece, rendering speed will not affect your score
                // unless it is exceptionally slow (more than a couple of minutes)
        System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");
 
                // This is already implemented, and should work without adding any code.
        saveImage(this.imageWidth, rgbData, outputFileName);
 
        System.out.println("Saved file " + outputFileName);
 
    }
 
 
    //////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////
 
    /*
     * Saves RGB data as an image in png format to the specified location.
     */
    public static void saveImage(int width, byte[] rgbData, String fileName)
    {
        try {
 
            BufferedImage image = bytes2RGB(width, rgbData);
            ImageIO.write(image, "png", new File(fileName));
 
        } catch (IOException e) {
            System.out.println("ERROR SAVING FILE: " + e.getMessage());
        }
 
    }
 
    /*
     * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
     */
    public static BufferedImage bytes2RGB(int width, byte[] buffer) {
        int height = buffer.length / width / 3;
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        DataBufferByte db = new DataBufferByte(buffer, width * height);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        BufferedImage result = new BufferedImage(cm, raster, false, null);
 
        return result;
    }
 
    public static class RayTracerException extends Exception {
        public RayTracerException(String msg) {  super(msg); }
    }
 
 
}


