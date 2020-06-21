package evil.graphics.components;

import java.util.ArrayList;

import linearAlgebra.Matrix;

public class Mesh {

	private ArrayList<Vector3D> list = new ArrayList<Vector3D>();
	private Vector3D origin = new Vector3D(0,0,0);

	public Mesh() {

	}
	
	public void translateMesh(Vector3D v3d) {
		double[][] rdm= {{1,0,0,v3d.getX()},
						{0,1,0,v3d.getY()},
						{0,0,1,v3d.getZ()},
						{0,0,0,	1}};
		Matrix translateMatrix = new Matrix(rdm);
		for(Vector3D v : list) {
			v.setVector(Matrix.multiplie(translateMatrix,v.getVector()));
		}
	}
	public void scaleMesh(Vector3D v3d) {
		double[][] rdm = {{v3d.getX(),0,0,0},
						 {0,v3d.getY(),0,0},
						 {0,0,v3d.getZ(),0},
						 {0,0,0,1}};
		Matrix scaleMatrix = new Matrix(rdm);
		for(Vector3D v : list) {
			v.setVector(Matrix.multiplie(scaleMatrix,v.getVector()));
		}
	}

	public Mesh(ArrayList<Vector3D> list) {
		this.list = list;
	}

	public void addPoint(Vector3D point) {
		this.list.add(point);
	}

	public ArrayList<Vector3D> getMesh() {
		return this.list;
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
		for(Vector3D v3d : this.list) {
			v3d.setVector(Matrix.transposeMatrix(Matrix.multiplie(Matrix.transposeMatrix(v3d.getVector()),rm)));
			//System.out.println(v3d.getVector().getformatetMatrix());
		}
	}

}