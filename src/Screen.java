import java.util.Random;

public class Screen {
	
	float screen_distance;
    float screen_width;
    float screen_height;
    Vector topLeft_screen_corner;
    
    //The camera's screen size (distance units) is different than the final image size (pixels),
    //Therefore we need to save vectors of 1-pixel size on the screen, 
    //with the screen directions:
    Vector onePixel_right_direction;
    Vector onePixel_up_direction;
    
    //Same goe's for part-of-pixel (there are N*N parts,
    //when N=superSamplingLevel
    Vector PixelCell_right_direction;
    Vector PixelCell_up_direction;
  
    public Screen(float distance, float width)
    {
    	screen_distance = distance;
    	screen_width = width;  	
    }
    
    
    public void updateScreenParams(int imageHeight, int imageWidth, Camera cam, int samplingLevel)
    {
    	//The screen has the same proportion as the image
    	screen_height = screen_width*imageHeight/imageWidth; 
		
		Vector screenCenter = cam.position.add(cam.look_at_direction.multiply_scalar(screen_distance));
		
		Vector right = cam.up_vector.cross_product(cam.look_at_direction);
		right.normalize();
		
		//Go to the top
		topLeft_screen_corner = screenCenter.add(cam.up_vector.multiply_scalar(screen_height/2));
		
		//Go to the left
		topLeft_screen_corner = topLeft_screen_corner.add(right.multiply_scalar((-1)*screen_width/2));
			
		onePixel_up_direction = cam.up_vector.multiply_scalar(screen_height/imageHeight);
		onePixel_right_direction = right.multiply_scalar(screen_width/imageWidth);
		
	//	this.backToStart = this.pixelWidthDirection.scalarProduct(-x);
		
		PixelCell_up_direction = onePixel_up_direction.multiply_scalar(1/samplingLevel); 
		PixelCell_right_direction = onePixel_right_direction.multiply_scalar(1/samplingLevel);
		
		//TODO - Avoiding borders - check this 
	//	this.mostLeftUp = this.mostLeftUp.add(this.inPixelWidthDirection.scalarProduct(0.5f));				
	//	this.mostLeftUp = this.mostLeftUp.add(this.inPixelHeightDirection.scalarProduct(0.5f));

    }
}
