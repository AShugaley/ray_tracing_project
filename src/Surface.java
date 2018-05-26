public class Surface {
    int material; //note that the indices start from 1, so you need to do -1 in order to access the array
    static float epsilon = 0.001f;
    
    public float intersectDist(Ray ray) 
    {	
    	return -1;	
    }
    
    public boolean inDistance(Ray ray, float min, float max)
    {
    	return false;
    }
    
    public Material getMaterial(Scene scene)
    {
    	return scene.materials.get(material);
    }
}
