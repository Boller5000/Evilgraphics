package evil.graphics.components;

import evil.math.algebra.Matrix;
import evil.math.algebra.Vector;

public class Vector3D {
	
	private Matrix vector;

	public Vector3D(double x,double y,double z) {
		double[][] matrix = {{x},{y},{z},{1}};
		vector = new Matrix(matrix);
	}
	
	public Vector3D(Matrix m) {
		double[][] tempMatrix = m.getMatrix();
		double[][] matrix = {{tempMatrix[0][0]},{tempMatrix[1][0]},{tempMatrix[2][0]},{tempMatrix[3][0]}};
		vector = new Matrix(matrix);
	}

	public double getX() {
		return vector.getMatrix()[0][0];
	}

	public void setX(double x) {
		this.vector.getMatrix()[0][0] = x;
	}

	public double getY() {
		return vector.getMatrix()[1][0];
	}

	public void setY(double y) {
		this.vector.getMatrix()[1][0] = y;
	}

	public double getZ() {
		return vector.getMatrix()[2][0];
	}

	public void setZ(double z) {
		this.vector.getMatrix()[2][0] = z;
	}
	public double getW() {
		return vector.getMatrix()[3][0];
	}

	public void setW(double w) {
		this.vector.getMatrix()[3][0] = w;
	}

	public Matrix getVector() {
		return vector;
	}

	public void setVector(Matrix vector) {
		this.vector = vector;
	}

	/**
	 * Returns the normal of the given vector
	 * @param v3d
	 * @return
	 */
	public static Vector3D normale(Vector3D v3d) {
		Vector3D normale = new Vector3D(0,0,0);
		double normalizer = Math.sqrt(v3d.getX() * v3d.getX() + v3d.getY() * v3d.getY() + v3d.getZ() * v3d.getZ());
		normale.setX(v3d.getX() / normalizer);
		normale.setY(v3d.getY() / normalizer);
		normale.setZ(v3d.getZ() / normalizer);
		return normale;
	}
	/**
	 * Returns the crossProduct <br>Vector</br> of the two given vectors
	 * @param v3d1
	 * @param v3d2
	 * @return
	 */
	public static Vector3D crossProductVector(Vector3D v3d1,Vector3D v3d2) {
		Vector3D crossProductVector = new Vector3D(0,0,0);
		
		crossProductVector.setX(v3d1.getY() * v3d2.getZ() - v3d1.getZ() * v3d2.getY());
		crossProductVector.setY(v3d1.getZ() * v3d2.getX() - v3d1.getX() * v3d2.getZ());
		crossProductVector.setZ(v3d1.getX() * v3d2.getY() - v3d1.getY() * v3d2.getX());
		return crossProductVector;
	}
	/**
	 * Return the dotProduct of the two given vectors
	 * offset ist added to the second vector(camera specific)
	 */
	public static double dotProduct(Vector3D v3d1,Vector3D v3d2,Vector3D offset) {
		
		double dotProduct = (v3d1.getX() * (v3d2.getX() - offset.getX())
							+ v3d1.getY() * (v3d2.getY() - offset.getY())
							+ v3d1.getZ() * (v3d2.getZ()  - offset.getZ()));
		
		
		return dotProduct;
	}
	
	

	

}
