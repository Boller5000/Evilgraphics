package evil.graphics.components;

import linearAlgebra.Matrix;

public class Vector3D {
	
	private Matrix vector;

	public Vector3D(double x,double y,double z) {
		double[][] matrix = {{x},{y},{z},{1}};
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
	

	

}
