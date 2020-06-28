package evil.graphics.components;

import java.util.ArrayList;

import evil.math.algebra.Matrix;

public class Mesh {

	public ArrayList<Triangle> list = new ArrayList<Triangle>();

	public Mesh() {

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
	


	public void rotateX(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{1,	0,				0,				0},
					  	{0,	Math.cos(theta),-Math.sin(theta),	0},
					  	{0,	Math.sin(theta),Math.cos(theta),	0},
					  	{0,		0,				0,				0}};	
		Matrix rotationMatrix = new Matrix(rdm);
		this.rotate(rotationMatrix);
		
	}

	public void rotateY(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{Math.cos(theta),	0,Math.sin(theta),	0},
						{0,					1,			0,		0},
						{-Math.sin(theta),	0,Math.cos(theta),	0},
						{0,					0,			0,		1}};
		Matrix rotationMatrix = new Matrix(rdm);
		this.rotate(rotationMatrix);
	}

	public void rotateZ(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{Math.cos(theta),	-Math.sin(theta),0,	0},
					  	{Math.sin(theta),	Math.cos(theta), 0,	0},
					  	{0,					0,				 1,	0},
					  	{0,					0,				 0,	1}};	
		Matrix rotationMatrix = new Matrix(rdm);
		this.rotate(rotationMatrix);
	}
	private void rotate(Matrix rm) {
		for(Triangle t : list) {
			for(Vector3D v : t.verticies)
				v.setVector(Matrix.transposeMatrix(Matrix.multiplie(Matrix.transposeMatrix(v.getVector()),rm)));
		}
	}
	public void addTriangle(Triangle t) {
		this.list.add(t);
	}

}
