
public class Color {
	
	float r,g,b;
	
	public Color(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color(String r, String g, String b)
	{
		 this.r = Float.parseFloat(r);
	     this.g = Float.parseFloat(g);
	     this.b = Float.parseFloat(b);
	}
	
	public void add(Color c)
	{
		r += c.r;
		g += c.g;
		b += c.b;
	}
	
	public Color multiply_scalar(float a)
	{
		return new Color(r*a, g*a, b*a);
	}

	public static Color average(Color[] colors)
	{
		float avg_r=0;
		float avg_g=0;
		float avg_b=0;
		for(Color c:colors)
		{
			avg_r += c.r;
			avg_g += c.g;
			avg_b += c.b;
		}
		
		avg_r /= colors.length;
		avg_g /= colors.length;
		avg_b /= colors.length;
		
		Color res = new Color(avg_r, avg_g, avg_b);
		return res; 
	}
	
	
	public Color multiply_color(Color other)
	{
		Color ret = new Color(r * other.r, g * other.g, b * other.b);
		return ret;
	}
}
