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
	
	//Projection variables
	private double zScreen = 0.1;
	private double zFar = 1000.0;
	private float fov = 90f;
	private double aspectRation;
	private double fovRad = 1.0/Math.tan(Math.toRadians(fov)/2);
	

	public Evil(Window w) {
		contentBuffer = new BufferedImage(w.getContentPanel().getWidth(), w.getContentPanel().getHeight(),
				BufferedImage.TYPE_INT_RGB);
		g2d = contentBuffer.createGraphics();
		g2d.setBackground(Color.BLACK);
		cp = w.getContentPanel();
		
		this.aspectRation = this.contentBuffer.getWidth()/this.contentBuffer.getHeight();

	}

	public int drawCall() {
		cp.setImg(contentBuffer);
		cp.repaint();
		
		return 0;
	}
	
	public int drawMeshes(ArrayList<Mesh> meshes) {
		for(Mesh m : meshes) {
			this.drawMesh(m);
			this.g2d.clearRect(0, 0, 800, 800);
		}
		return 0;
	}
	
	public int drawMesh(Mesh mesh) {
		for (int i = 0; i < mesh.getMesh().size(); i++) {
			Vector3D pt1t;
			Vector3D pt2t;
			pt1t = transformeVector(mesh.getMesh().get(i));
			for (int u = 0; u < mesh.getMesh().size() - i; u++) {
				pt2t = transformeVector(mesh.getMesh().get(i+u));
				if(pt2t.getZ() <= 0)
					continue;

				this.g2d.drawLine((int) ((pt1t.getX() / pt1t.getZ()) + (contentBuffer.getWidth() / 2)),
						(int) ((pt1t.getY() / pt1t.getZ()) + (contentBuffer.getHeight() / 2)),
						(int) ((pt2t.getX() / pt2t.getZ()) + (contentBuffer.getWidth() / 2)),
						(int) ((pt2t.getY() / pt2t.getZ()) + (contentBuffer.getHeight() / 2)));
			}


		}
		return 0;
	}

	// Experimental
	public int drawLine(Vector3D pt1, Vector3D pt2) {
		Vector3D pt1t = transformeVector(pt1);
		Vector3D pt2t = transformeVector(pt2);
		this.g2d.drawLine((int) (pt1t.getX() / pt1t.getZ()) + (contentBuffer.getWidth() / 2),
				(int) (pt1t.getY() / pt1t.getZ()) + (contentBuffer.getHeight() / 2),
				(int) (pt2t.getX() / pt2t.getZ()) + (contentBuffer.getWidth() / 2),
				(int) (pt2t.getY() / pt2t.getZ()) + (contentBuffer.getHeight() / 2));
		return 0;
	}

	public int drawTriangle(Triangle tg) {
		//foor loop lohnt sich nicht
		tg.verticies[0].setVector(this.transformeVector(tg.verticies[0]).getVector());
		tg.verticies[1].setVector(this.transformeVector(tg.verticies[1]).getVector());
		tg.verticies[2].setVector(this.transformeVector(tg.verticies[2]).getVector());
		
		
		tg.verticies[0].setX(tg.verticies[0].getX()*(this.contentBuffer.getWidth()/2));
		tg.verticies[1].setX(tg.verticies[1].getX()*(this.contentBuffer.getWidth()/2));
		tg.verticies[2].setX(tg.verticies[2].getX()*(this.contentBuffer.getWidth()/2));
		
		tg.verticies[0].setY(tg.verticies[0].getY()*(this.contentBuffer.getHeight()/2));
		tg.verticies[1].setY(tg.verticies[1].getY()*(this.contentBuffer.getHeight()/2));
		tg.verticies[2].setY(tg.verticies[2].getY()*(this.contentBuffer.getHeight()/2));
		
		
		g2d.drawLine((int)tg.verticies[0].getX(),(int)tg.verticies[0].getY(),(int)tg.verticies[1].getX(),(int)tg.verticies[1].getY());
		g2d.drawLine((int)tg.verticies[1].getX(),(int)tg.verticies[1].getY(),(int)tg.verticies[2].getX(),(int)tg.verticies[2].getY());
		g2d.drawLine((int)tg.verticies[2].getX(),(int)tg.verticies[2].getY(),(int)tg.verticies[0].getX(),(int)tg.verticies[0].getY());
		
		System.out.println(tg.verticies[0].getVector().getformatetMatrix());
		
		return 0;
	}


	public Vector3D transformeVector(Vector3D v3d) {
		double[][] rdm= {{this.aspectRation*this.fovRad,0,				0,													0},
			  			{0,								this.fovRad,	0,													0},
			  			{0,								0,				this.zFar/(this.zFar-this.zScreen),					1},
			  			{0,								0,				(-this.zFar*this.zScreen)/(this.zFar-this.zScreen), 0}};
		
		Matrix m = Matrix.transposeMatrix(Matrix.multiplie(Matrix.transposeMatrix(v3d.getVector()),new Matrix(rdm)));
		Vector3D nv3d = new Vector3D(0,0,0);
		nv3d.setVector(m);
		if(nv3d.getW() != 0.0f) {
			nv3d.setX(-nv3d.getX()/nv3d.getW()+1.0f);
			nv3d.setY(-nv3d.getY()/nv3d.getW()+1.0f);
			nv3d.setZ(nv3d.getZ()/nv3d.getW()+1.0f);
		}
		
		return nv3d;
	}
	public Vector3D rotateX(double angle,Vector3D v3d) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{1,	0,				0,				0},
					  	{0,	Math.cos(theta),-Math.sin(theta),	0},
					  	{0,	Math.sin(theta),Math.cos(theta),	0},
					  	{0,		0,				0,				0}};	
		Matrix rotationMatrix = new Matrix(rdm);
		Vector3D nv3d = new Vector3D(0,0,0);
		nv3d.setVector(Matrix.multiplie(rotationMatrix, v3d.getVector()));
		return nv3d;
	}
}
