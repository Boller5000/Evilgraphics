package evil.graphics.components;

public class Triangle {
	public Vector3D[] verticies = new Vector3D[3];
	public Vector3D normale = new Vector3D(0, 0, 0);
	private int lighting;

	public Triangle(Vector3D a, Vector3D b, Vector3D c) {
		verticies[0] = a;
		verticies[1] = b;
		verticies[2] = c;

	}
	public Triangle() {
		verticies[0] = new Vector3D(0,0,0);
		verticies[1] = new Vector3D(0,0,0);
		verticies[2] = new Vector3D(0,0,0);
	}

	/**
	 * Calcalutes the birghtness of the Material-> Lighting
	 * @return
	 */
	public int calculateLighting() {
		// lighting

		Vector3D light = new Vector3D(0, 0, -1);
		light = Vector3D.normale(light);

		
		//Note fix offset 
		double dp = Vector3D.dotProduct(light, this.normale, new Vector3D(0,0,0));

		
		this.lighting = (int) (dp * 255);
		return this.lighting;
	}

	/**
	 * Calculates weather the triangle is visibil or not, if so return a double > 0
	 * @param camera
	 * @return
	 */
	public double calculateIfcn(Vector3D camera) {
		// visibility
		Vector3D cv1, cv2;
		cv1 = new Vector3D(0, 0, 0);
		cv2 = new Vector3D(0, 0, 0);

		//calculate cv1 and cv2
		
		cv1.setX(this.verticies[1].getX() - this.verticies[0].getX());
		cv1.setY(this.verticies[1].getY() - this.verticies[0].getY());
		cv1.setZ(this.verticies[1].getZ() - this.verticies[0].getZ());

		cv2.setX(this.verticies[2].getX() - this.verticies[0].getX());
		cv2.setY(this.verticies[2].getY() - this.verticies[0].getY());
		cv2.setZ(this.verticies[2].getZ() - this.verticies[0].getZ());
	
		
			
		this.normale = Vector3D.crossProductVector(cv1, cv2);
		this.normale = Vector3D.normale(normale);
		
		//calculate the dot product
		double ifcn = Vector3D.dotProduct(normale, verticies[0], camera);

		return ifcn;
	}
	public int getLighting() {
		return lighting;
	}
	public void setLighting(int lighting) {
		this.lighting = lighting;
	}
	

}
