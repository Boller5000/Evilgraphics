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
import evil.math.algebra.Matrix;

public class Evil {

	private double timeStamp = 0;
	private double fps = 0;
	
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
	
	public Vector3D vCamera = new Vector3D(0,0,1);
	private Vector3D vLookDir = new Vector3D(0,0,2);
	

	public Evil(Window w) {
		contentBuffer = new BufferedImage(w.getContentPanel().getWidth(), w.getContentPanel().getHeight(),
				BufferedImage.TYPE_INT_RGB);
		g2d = contentBuffer.createGraphics();
		g2d.setBackground(Color.BLACK);
		cp = w.getContentPanel();
		this.w = w;
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
		
		//System.out.println(tg.calculateIfcn(cam));

		if ( tg.calculateIfcn(cam) < 0.0f) {
			int color = tg.getLighting();
			g2d.setColor(new Color(color,color,color));
			

			
			
			
			
			
			Vector3D pt1 = this.alignVector(this.combineCam(this.transformeVector(tg.verticies[0])));
			Vector3D pt2 = this.alignVector(this.combineCam(this.transformeVector(tg.verticies[1])));
			Vector3D pt3 = this.alignVector(this.combineCam(this.transformeVector(tg.verticies[2])));

			//die g2d polygon methode eignet sich nur zum fill da bei normalem "draw " call linien ausgelassen werden
			g2d.fillPolygon(new int[] {(int) pt1.getX(), (int) pt2.getX(), (int) pt3.getX()},
							 new int[] {(int) pt1.getY(), (int) pt2.getY(), (int) pt3.getY()},3);
			
			g2d.setColor(Color.BLACK);
			
			g2d.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) pt2.getX(), (int) pt2.getY());
			g2d.drawLine((int) pt2.getX(), (int) pt2.getY(), (int) pt3.getX(), (int) pt3.getY());
			g2d.drawLine((int) pt3.getX(), (int) pt3.getY(), (int) pt1.getX(), (int) pt1.getY());
			
			

		}
		return 0;
	}
	
	public void fillTriangle(Triangle tg) {
		
		g2d.fillPolygon(new int[] {(int) tg.verticies[0].getX(), (int) tg.verticies[1].getX(), (int) tg.verticies[2].getX()},
				 new int[] {(int) tg.verticies[0].getY(), (int) tg.verticies[1].getY(), (int) tg.verticies[2].getY()},3);
	}

	private Vector3D transformeVector(Vector3D v3d) {
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
	
	private Vector3D combineCam(Vector3D v3d) {
		Vector3D nv3d;
		Vector3D vUp = new Vector3D(0,1,0);
		Vector3D vTarget = new Vector3D(Matrix.add(this.vCamera.getVector(), this.vLookDir.getVector()));
		
		Matrix CameraV = this.lookAtMatrix(this.vCamera, vTarget, vUp);
		

		
		nv3d = new Vector3D(Matrix.multiplie(CameraV, v3d.getVector()));
		
		
		return nv3d;
	}
	
	private Matrix lookAtMatrix(Vector3D from,Vector3D to,Vector3D temp) {
		Vector3D tempForward = new Vector3D(0,0,0);
		tempForward.setX(to.getX()-from.getX());
		tempForward.setY(to.getY()-from.getY());
		tempForward.setZ(to.getZ()-from.getZ());
		tempForward.setW(to.getW()-from.getW());

		System.out.println(from.getVector().getformatetMatrix());
		
		Vector3D forward = Vector3D.normale(tempForward);

		
		Vector3D a = new Vector3D(Matrix.multiplie(forward.getVector(), new Matrix(new double[][] {{Vector3D.dotProduct(temp, forward, new Vector3D(0,0,5))}})));
		Vector3D up = new Vector3D(Matrix.subtract(temp.getVector(), a.getVector()));
		up = Vector3D.normale(up);
		
		
		Vector3D right = Vector3D.crossProductVector(up,forward);
		
//		Vector3D right = Vector3D.crossProductVector(Vector3D.normale(temp), forward);
//		
//		Vector3D up = Vector3D.crossProductVector(forward, right);
		
		double ta = -(Matrix.multiplie(Matrix.transposeMatrix(from.getVector()), right.getVector()).getMatrix()[0][0]);
		double tb = -(Matrix.multiplie(Matrix.transposeMatrix(from.getVector()), up.getVector()).getMatrix()[0][0]);
		double tc = -(Matrix.multiplie(Matrix.transposeMatrix(from.getVector()), forward.getVector()).getMatrix()[0][0]);
		
		double[][] matrix = {{right.getX(),up.getX(),forward.getX(),0},
					 		{ right.getY(),up.getY(),forward.getY(),0},
					 		{ right.getZ(),up.getZ(),forward.getZ(),0},
					 		{ ta ,		   tb,		 tc,			1}};
		
//		double[][] matrix = {{right.getX()	,right.getY(),right.getZ(),0},
//		 					{ up.getX()		,up.getY()		,up.getZ()	,0},
//		 					{ forward.getX(),forward.getY(),forward.getZ(),0},
//		 					{ from.getX() 	,from.getY()	,from.getZ(),1}};
		
		return new Matrix(matrix);
	}
	
	public Mesh loadObj() {
		return null;
	}
	
	/**
	 * starts frame measurment
	 */
	public void start() {
		this.timeStamp = System.currentTimeMillis();
	}
	/**
	 * ends frame measurment
	 */
	public void end() {
		this.fps = Math.round(1000/(System.currentTimeMillis()-this.timeStamp));
		this.w.setTitle(fps+" FPS");
	}
}
