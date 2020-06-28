package evil.graphics.components;

import java.util.ArrayList;

import evil.math.algebra.Matrix;

public class Mesh {

	public ArrayList<Triangle> list = new ArrayList<Triangle>();
	private Vector3D rotation;
	

	public Mesh() {
		this.rotation = new Vector3D(0,0,0);
	}
	public void moveMesh(Vector3D vertex) {
		for(Triangle t : list) {
			for(Vector3D v : t.verticies)
			v.setVector(Matrix.add(v.getVector(), vertex.getVector()));
		}
	}
	public void translateMesh(Vector3D v3d) {
		double[][] rdm= {{1,0,0,v3d.getX()},
						{0,1,0,v3d.getY()},
						{0,0,1,v3d.getZ()},
						{0,0,0,	1}};
		Matrix translateMatrix = new Matrix(rdm);
		for(Triangle t : list) {
			for(Vector3D v : t.verticies)
			v.setVector(Matrix.multiplie(translateMatrix,v.getVector()));
		}
	}
	public void scaleMesh(Vector3D v3d) {
		double[][] rdm = {{v3d.getX(),0,0,0},
						 {0,v3d.getY(),0,0},
						 {0,0,v3d.getZ(),0},
						 {0,0,0,1}};
		Matrix scaleMatrix = new Matrix(rdm);
		for(Triangle t : list) {
			for(Vector3D v : t.verticies)
			v.setVector(Matrix.multiplie(scaleMatrix,v.getVector()));
		}
	}
	

	public void addTriangle(Triangle t) {
		this.list.add(t);
	}

	public Vector3D getRotation() {
		return rotation;
	}

	public void setRotation(Vector3D rotation) {
		this.rotation = rotation;
	}
	

}
