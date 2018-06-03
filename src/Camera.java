public class Camera {
    public Vector position;
    public Vector look_at_point;
    public Vector look_at_direction;
    public Vector up_vector;
    public Screen screen;
    
    public Camera(Vector pos, Vector lookAt, Vector up, float distance, float width)
    {
    	position = pos;
    	screen = new Screen(distance, width);
    	
    	look_at_point = lookAt;
    	up_vector = up;
    	
    	look_at_direction = new Vector(position, look_at_point);
    	look_at_direction.normalize();
    	

    	Vector lookAt_up_normal = look_at_direction.cross_product(up_vector);
		up_vector = look_at_direction.cross_product(lookAt_up_normal);
		up_vector = up_vector.multiply_scalar(-1);
		up_vector.normalize();
    }
   
}
