public class Triangle extends Surface {
    public Vector v1;
    public Vector v2;
    public Vector v3;
	public Vector normal, normal1, normal2, normal3;
	private float offset;

	@Override
    public Vector calcNormal(Vector point)
    {
    	// compute triangle's normal
	    Vector v1v2 = v2.substract(v1);
	    Vector v1v3 = v3.substract(v2);
	   
	    // no need to normalize
	    Vector normal = v1v2.cross_product(v1v3); 
	    normal.normalize();
	    this.normal = normal;
		this.offset = this.v3.dot_product(this.normal);
		this.normal1 = this.normal.cross_product(this.v1.substract(this.v2));
		this.normal2 = this.normal.cross_product(this.v2.substract(this.v3));
		this.normal3 = this.normal.cross_product(this.v3.substract(this.v1));
		return normal;
    }

    @Override
    public float intersectDist(Ray ray) {
		calcNormal(ray.direction);
		float temp = this.normal.dot_product(ray.direction);

		if (temp == 0)
			return -1;

		float dist = -(this.normal.dot_product(ray.startPosition) - this.offset) / temp;

		if (dist <= this.epsilon || dist + this.epsilon > ray.min_distance_intersect)
			return -1;

		Vector intersection = ray.startPosition.add(ray.direction.multiply_scalar(dist));

		Vector one = new Vector(this.v1, intersection);
		if (one.dot_product(this.normal1) < 0) { return -1; }

		Vector two = new Vector(this.v2, intersection);
		if (two.dot_product(this.normal2) < 0) { return -1; }

		Vector three = new Vector(this.v3, intersection);
		if (three.dot_product(this.normal3) < 0) { return -1; }

		return dist;
	}
}



