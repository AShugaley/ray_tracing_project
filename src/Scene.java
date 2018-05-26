import java.util.*;


public class Scene {

    public Color background_color;
    public int number_shadow_rays;
    public int max_recursion_level;
    public int super_sampling_level;

    public Camera camera;
    public ArrayList<Light> lights;
    public ArrayList<Surface> surfaces;
    public ArrayList<Material> materials;


    public  Scene(){
        lights = new ArrayList<Light>();
        surfaces = new ArrayList<Surface>();
        materials = new ArrayList<Material>();

    }





}

