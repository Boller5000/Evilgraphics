package evil.graphics.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import evil.graphics.components.ContentPanel;
import evil.graphics.components.Mesh;
import evil.graphics.components.Pipe;
import evil.graphics.components.Triangle;
import evil.graphics.components.Vector3D;
import evil.graphics.components.Window;
import linearAlgebra.Matrix;

public class Evil {

	private Window w;
	private ContentPanel cp;
	private BufferedImage contentBuffer;
	private Graphics2D g2d;
	private final Pipe pipe = new Pipe();
	private Vector3D cam;

	// Projection variables
	private double zScreen = 1.0;
	private double zFar = 1000.0;
	private float fov = 40f;
	private double aspectRation;
	private double fovRad = 1.0 / Math.tan(Math.toRadians(fov) / 2);
	

	public Evil(Window w) {
		contentBuffer = new BufferedImage(w.getContentPanel().getWidth(), w.getContentPanel().getHeight(),
				BufferedImage.TYPE_INT_RGB);
		g2d = contentBuffer.createGraphics();
		g2d.setBackground(Color.BLACK);
		cp = w.getContentPanel();
		this.cam = new Vector3D(0,0,0);
		this.aspectRation = this.contentBuffer.getWidth() / this.contentBuffer.getHeight();

	}

	public int drawCall() {
		cp.setImg(contentBuffer);
		cp.repaint();

		return 0;
	}

	public int drawMesh(Mesh mesh) {
		g2d.clearRect(0, 0, 800, 800);
		for (Triangle t : mesh.list) {
			this.drawTriangle(t);
		}
		return 0;
	}

	public int drawTriangle(Triangle tg) {
		// foor loop lohnt sich nicht
		Vector3D normale, cv1, cv2;
		normale = new Vector3D(0, 0, 0);
		cv1 = new Vector3D(0, 0, 0);
		cv2 = new Vector3D(0, 0, 0);

		cv1.setX(tg.verticies[1].getX() - tg.verticies[0].getX());
		cv1.setY(tg.verticies[1].getY() - tg.verticies[0].getY());
		cv1.setZ(tg.verticies[1].getZ() - tg.verticies[0].getZ());

		cv2.setX(tg.verticies[2].getX() - tg.verticies[0].getX());
		cv2.setY(tg.verticies[2].getY() - tg.verticies[0].getY());
		cv2.setZ(tg.verticies[2].getZ() - tg.verticies[0].getZ());

		normale.setX(cv1.getY() * cv2.getZ() - cv1.getZ() * cv2.getY());
		normale.setY(cv1.getZ() * cv2.getX() - cv1.getX() * cv2.getZ());
		normale.setZ(cv1.getX() * cv2.getY() - cv1.getY() * cv2.getX());

		float normalizer = (float) Math.sqrt(
				normale.getX() * normale.getX() + normale.getY() * normale.getY() + normale.getZ() * normale.getZ());
		normale.setX(normale.getX() / normalizer);
		normale.setY(normale.getY() / normalizer);
		normale.setZ(normale.getZ() / normalizer);
		
		
		double ifcn = (normale.getX() * (tg.verticies[0].getX() - this.cam.getX())
				   	+normale.getY() * (tg.verticies[0].getY() - this.cam.getY())
				   	+ normale.getZ() * (tg.verticies[0].getZ()+5 - this.cam.getZ()));

		if ( ifcn < 0.0f) {
//		if(normale.getZ()<0) {
			
			Vector3D pt1 = this.alignVector(this.transformeVector(tg.verticies[0]));
			Vector3D pt2 = this.alignVector(this.transformeVector(tg.verticies[1]));
			Vector3D pt3 = this.alignVector(this.transformeVector(tg.verticies[2]));

			g2d.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) pt2.getX(), (int) pt2.getY());
			g2d.drawLine((int) pt2.getX(), (int) pt2.getY(), (int) pt3.getX(), (int) pt3.getY());
			g2d.drawLine((int) pt3.getX(), (int) pt3.getY(), (int) pt1.getX(), (int) pt1.getY());

		}
		return 0;
	}

	public Vector3D transformeVector(Vector3D v3d) {
		v3d.setZ(v3d.getZ() + 5.0f);
		double[][] rdm = { { this.aspectRation * this.fovRad, 0, 0, 0 }, 
				{ 0, this.fovRad, 0, 0 },
				{ 0, 0, this.zFar / (this.zFar - this.zScreen), 1 },
				{ 0, 0, (-this.zFar * this.zScreen) / (this.zFar - this.zScreen), 0 } };

		Matrix m = (Matrix.multiplie(new Matrix(rdm),v3d.getVector()));
		v3d.setZ(v3d.getZ() - 5.0f);
		Vector3D nv3d = new Vector3D(0, 0, 0);
		nv3d.setVector(m);

		if (nv3d.getW() != 0.0f) {
			nv3d.setX(nv3d.getX() / nv3d.getW());
			nv3d.setY(-nv3d.getY() / nv3d.getW());
			nv3d.setZ(nv3d.getZ() / nv3d.getW());
		}

		return nv3d;
	}
	
	private Vector3D alignVector(Vector3D v3d) {
		Vector3D nv3d = v3d;
		nv3d.setX(v3d.getX()* (this.contentBuffer.getWidth() / 2) + (this.contentBuffer.getWidth() / 2));
		nv3d.setY(v3d.getY()* (this.contentBuffer.getHeight() / 2) + (this.contentBuffer.getHeight() / 2));
		
		return nv3d;
	}
	

	public Vector3D rotateX(double angle, Vector3D v3d) {
		double theta = Math.toRadians(angle);
		double[][] rdm = { { 1, 0, 0, 0 }, { 0, Math.cos(theta), -Math.sin(theta), 0 },
				{ 0, Math.sin(theta), Math.cos(theta), 0 }, { 0, 0, 0, 1 } };
		Matrix rotationMatrix = new Matrix(rdm);
		Vector3D nv3d = new Vector3D(0, 0, 0);
		nv3d.setVector(Matrix.multiplie(rotationMatrix, v3d.getVector()));
		return nv3d;
	}

	public Vector3D rotateY(double angle, Vector3D v3d) {
		double theta = Math.toRadians(angle);
		double[][] rdm = { { Math.cos(theta), 0, Math.sin(theta), 0 }, { 0, 1, 0, 0 },
				{ -Math.sin(theta), 0, Math.cos(theta), 0 }, { 0, 0, 0, 1 } };
		Matrix rotationMatrix = new Matrix(rdm);
		Vector3D nv3d = new Vector3D(0, 0, 0);
		nv3d.setVector(Matrix.multiplie(rotationMatrix, v3d.getVector()));
		return nv3d;
	}

	public Vector3D rotateZ(double angle, Vector3D v3d) {
		double theta = Math.toRadians(angle);
		double[][] rdm = { { Math.cos(theta), -Math.sin(theta), 0, 0 }, { Math.sin(theta), Math.cos(theta), 0, 0 },
				{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
		Matrix rotationMatrix = new Matrix(rdm);
		Vector3D nv3d = new Vector3D(0, 0, 0);
		nv3d.setVector(Matrix.multiplie(rotationMatrix, v3d.getVector()));
		return nv3d;
	}
}
