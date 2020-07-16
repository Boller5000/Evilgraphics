package evil.graphics.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	public void loadMesh(String file) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file)))) {
			String line;
			ArrayList<Vector3D> verticieBuffer = new ArrayList<Vector3D>();
			while((line = br.readLine()) != null) {
				if(!line.contains("#") && !line.contains("s")&& !line.contains("vn")) {
					if(line.contains("v")) {
						String[] coordinates = line.split("\\s+");
						verticieBuffer.add(new Vector3D(Double.parseDouble(coordinates[1]),Double.parseDouble(coordinates[2]),Double.parseDouble(coordinates[3])));
					}
					if(line.contains("f")) {
						String[] verticieIndex = line.split("\\s+");
						for(String s : verticieIndex) {
							if(s.contains("//")) {
								s = s.split("/")[0];
							}
						}
						this.list.add(new Triangle(verticieBuffer.get(Integer.parseInt(verticieIndex[1])-1),verticieBuffer.get(Integer.parseInt(verticieIndex[2])-1),verticieBuffer.get(Integer.parseInt(verticieIndex[3])-1)));

					}
				}
			}
//			for(Vector3D v3d : verticieBuffer) {
//				System.out.println(v3d.getX() + " " + v3d.getY() + " " + v3d.getZ());
//			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not load file: File not Found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not load file: IOException");
			e.printStackTrace();
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
