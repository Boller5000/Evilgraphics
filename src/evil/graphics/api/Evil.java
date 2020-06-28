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
	private boolean localTime = true;

	private Window w;
	private ContentPanel cp;
	private BufferedImage contentBuffer;
	private Graphics2D g2d;
	private Vector3D cam;

	// Projection variables
	private double zScreen = 1;
	private double zFar = 1000.0;
	private float fov = 90f;
	private double aspectRatio;
	private double fovRad = 1.0 / Math.tan(Math.toRadians(fov) / 2);

	public Vector3D vCamera = new Vector3D(0, 0, 1);
	private Vector3D vLookDir = new Vector3D(0, 0, 2);

	public Evil(Window w) {
		contentBuffer = new BufferedImage(w.getContentPanel().getWidth(), w.getContentPanel().getHeight(),
				BufferedImage.TYPE_INT_RGB);
		g2d = contentBuffer.createGraphics();
		g2d.setBackground(Color.BLACK);
		cp = w.getContentPanel();
		this.w = w;
		this.cam = new Vector3D(0, 0, 0);
		this.aspectRatio = this.contentBuffer.getWidth() / this.contentBuffer.getHeight();

	}

	public int drawCall() {
		cp.setImg(contentBuffer);
		cp.repaint();

		return 0;
	}

	public void drawMeshes(ArrayList<Mesh> meshes) {
		g2d.clearRect(0, 0, 800, 800);
		for (Mesh m : meshes) {
			for (Triangle t : m.list) {
				this.drawTriangle(t);
			}
		}
	}

	public int drawMesh(Mesh mesh) {
		g2d.clearRect(0, 0, 800, 800);
		Mesh result = new Pipe(mesh,mesh.getRotation()).process();
		for (Triangle t : result.list) {
			this.fillTriangle(t);
		}
		return 0;
	}

	public void drawTriangle(Triangle tg) {

		g2d.drawLine((int) tg.verticies[0].getX(), (int) tg.verticies[0].getY(), (int) tg.verticies[1].getX(),
				(int) tg.verticies[1].getY());
		g2d.drawLine((int) tg.verticies[1].getX(), (int) tg.verticies[1].getY(), (int) tg.verticies[2].getX(),
				(int) tg.verticies[2].getY());
		g2d.drawLine((int) tg.verticies[2].getX(), (int) tg.verticies[2].getY(), (int) tg.verticies[0].getX(),
				(int) tg.verticies[0].getY());
	}

	public void fillTriangle(Triangle tg) {
		int color = tg.getLighting();
		g2d.setColor(new Color(color,color,color));
		g2d.fillPolygon(
				new int[] { (int) tg.verticies[0].getX(), (int) tg.verticies[1].getX(), (int) tg.verticies[2].getX() },
				new int[] { (int) tg.verticies[0].getY(), (int) tg.verticies[1].getY(), (int) tg.verticies[2].getY() },
				3);
	}


	/**
	 * defines the pipeline a triangle has to go through
	 * 
	 * Rotation->Offset->3Dprojection
	 * 
	 * @author tromp
	 *
	 */
	public class Pipe {
		private Mesh mesh;
		private Vector3D rotation;

		public Pipe(Mesh mesh) {
			this.mesh = mesh;
		}

		public Pipe(Mesh mesh, Vector3D rotation) {
			this.mesh = mesh;
			this.rotation = rotation;
		}

		public Mesh process() {
			Mesh calculatedMesh = new Mesh();
			for (Triangle tg : this.mesh.list) {
				//Pipeline Objects
				Triangle triFinal,triTranslated,triProjected,triOffset, triRotated;
				int lighting = 0;
				
				triRotated = new Triangle();
				triOffset = new Triangle();
				triProjected = new Triangle();
				triTranslated = new Triangle();
				triFinal = new Triangle();

				/*
				 * Rotation Part of pipeline
				 */
				//for looop for all pipelines objects where translation is applied to all verticies
				for(int i = 0;i < tg.verticies.length;i++) {
					if (this.rotation != null) {
							Matrix rotationVector = tg.verticies[i].getVector();
							rotationVector = Matrix.multiplie(Evil.rotateX(this.rotation.getX()),rotationVector);
							rotationVector = Matrix.multiplie(Evil.rotateY(this.rotation.getY()),rotationVector);
							rotationVector = Matrix.multiplie(Evil.rotateZ(this.rotation.getZ()),rotationVector);
							triRotated.verticies[i].setVector(rotationVector);
							//System.out.println(triRotated.verticies[i].getVector().getformatetMatrix());
	
					} else {
						triRotated = tg;
						//System.out.println(triRotated.verticies[i].getVector().getformatetMatrix());
					}
				}
					/*
					 * Offset into screen space
					 */
				for(int i = 0;i < tg.verticies.length;i++) {
					triOffset = triRotated;
					triOffset.verticies[i].setZ(triRotated.verticies[i].getZ()+3.0);
					//System.out.println(triOffset.verticies[i].getVector().getformatetMatrix());
				}
					/*
					 * Projection to 3D
					 */
					//System.out.println(triOffset.calculateIfcn(cam));
					if(triOffset.calculateIfcn(cam) < 0) {
						//calculate lighting 
						lighting = triOffset.calculateLighting();
						for(int i = 0;i < tg.verticies.length;i++) {
						triProjected.verticies[i] = new Vector3D((Matrix.multiplie(Evil.transformMatrix(aspectRatio, fovRad, zScreen, zFar), triOffset.verticies[i].getVector())));
						if (triProjected.verticies[i].getW() != 0.0f) {
							triProjected.verticies[i].setX(triProjected.verticies[i].getX() / triProjected.verticies[i].getW());
							triProjected.verticies[i].setY(triProjected.verticies[i].getY() / triProjected.verticies[i].getW());
							triProjected.verticies[i].setZ(triProjected.verticies[i].getZ() / triProjected.verticies[i].getW());
						}
						//System.out.println(triProjected.verticies[i].getVector().getformatetMatrix());
						/*
						 * Translation onto screen
						 * 
						 */
						triTranslated = triProjected;
						triTranslated.verticies[i].setX(triTranslated.verticies[i].getX()+1);
						triTranslated.verticies[i].setY(triTranslated.verticies[i].getY()+1);
						
						triTranslated.verticies[i].setX(triTranslated.verticies[i].getX()*0.5*contentBuffer.getWidth());
						triTranslated.verticies[i].setY(triTranslated.verticies[i].getY()*0.5*contentBuffer.getHeight());
						//System.out.println(triTranslated.verticies[i].getVector().getformatetMatrix());
					}
				}
				
					 triFinal = triTranslated;
					 triFinal.setLighting(lighting);
				
				calculatedMesh.addTriangle(triFinal);
			}
			return calculatedMesh;

		}
		

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
		this.fps = Math.round(1000 / (System.currentTimeMillis() - this.timeStamp));
		this.w.setTitle(fps + " FPS");
	}
	
	
	/*
	 * Rotation matrizes
	 */
	public static Matrix rotateX(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{1,	0,				0,				0},
					  	{0,	Math.cos(theta),-Math.sin(theta),	0},
					  	{0,	Math.sin(theta),Math.cos(theta),	0},
					  	{0,		0,				0,				0}};	
		Matrix rotationMatrix = new Matrix(rdm);
		return rotationMatrix;
		
	}

	public static Matrix rotateY(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{Math.cos(theta),	0,Math.sin(theta),	0},
						{0,					1,			0,		0},
						{-Math.sin(theta),	0,Math.cos(theta),	0},
						{0,					0,			0,		1}};
		Matrix rotationMatrix = new Matrix(rdm);
		return rotationMatrix;
	}

	public static Matrix rotateZ(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{Math.cos(theta),	-Math.sin(theta),0,	0},
					  	{Math.sin(theta),	Math.cos(theta), 0,	0},
					  	{0,					0,				 1,	0},
					  	{0,					0,				 0,	1}};	
		Matrix rotationMatrix = new Matrix(rdm);
		return rotationMatrix;
	}
	public static Matrix transformMatrix(double aspectRatio,double fovRad,double zScreen,double zFar) {
		double[][] tmm = {{aspectRatio * fovRad, 			0, 			0, 														  0 },
						 { 0, 								fovRad,		0, 													   	  0 },
						 { 0, 								0, 			zFar / (zFar - zScreen),								  1 },
						 { 0,								0, 			(-zFar * zScreen) / (zFar - zScreen),				  	  0 }};
		Matrix transformationMatrix = new Matrix(tmm);
		return transformationMatrix;
		
	}
}
