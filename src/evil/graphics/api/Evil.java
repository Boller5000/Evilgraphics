package evil.graphics.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import evil.graphics.components.ContentPanel;
import evil.graphics.components.Mesh;
import evil.graphics.components.Pipe;
import evil.graphics.components.Triangle;
import evil.graphics.components.Vector3D;
import evil.graphics.components.Window;
import evil.math.algebra.Matrix;

public class Evil {

	private double timeStamp = 0;
	public double fps = 0;
	private boolean localTime = true;
	public double deltaTime = 0;
	private long triangles = 0;
	
	public boolean wireMode = false;
	public boolean debug = false;
	public boolean frameMode = false;
	public boolean renderMode = false;

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

	public Vector3D vCamera = new Vector3D(0, 0, 0);
	//not setting w to 0 on LookDir Vector may cause problems when translating z movment duo to wrong subtraction
	public Vector3D vLookDir = new Vector3D(0,0,0,0);
	public double yaw = 0;
	public double pitch = 0;

	public Evil(Window w) {
		contentBuffer = new BufferedImage(w.getContentPanel().getWidth(), w.getContentPanel().getHeight(),BufferedImage.TYPE_INT_RGB);
		g2d = contentBuffer.createGraphics();
		g2d.setBackground(Color.BLACK);
		cp = w.getContentPanel();
		this.w = w;
		this.cam = new Vector3D(0, 0, 0);
		this.aspectRatio = this.contentBuffer.getWidth() / this.contentBuffer.getHeight();
		
		w.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				reload();
			}
		});

	}
	public void reload() {
		contentBuffer = new BufferedImage(w.getContentPanel().getWidth(), w.getContentPanel().getHeight(),BufferedImage.TYPE_INT_RGB);
		g2d = contentBuffer.createGraphics();
		g2d.setBackground(Color.BLACK);
		cp = w.getContentPanel();
		this.aspectRatio = this.contentBuffer.getWidth() / this.contentBuffer.getHeight();
		
	}

	public int drawCall() {
		cp.setImg(contentBuffer);
		cp.repaint();

		return 0;
	}

	public void drawMeshes(ArrayList<Mesh> meshes) {
		g2d.clearRect(0, 0, this.w.getContentPanel().getWidth(), this.w.getContentPanel().getHeight());
		for (Mesh m : meshes) {
			for (Triangle t : m.list) {
				this.drawTriangle(t);
			}
		}
	}

	public int drawMesh(Mesh mesh) {
		this.triangles = mesh.list.size();
		g2d.clearRect(0, 0, this.w.getContentPanel().getWidth(), this.w.getContentPanel().getHeight());
		Mesh result = new Pipe(mesh,mesh.getRotation()).process();
		for (Triangle t : result.list) {
			t.calculateZ();
		}
		Collections.sort(result.list);
		Collections.reverse(result.list);
		if(!renderMode) {
			for (Triangle t : result.list) {
				if(!wireMode) {
					if(frameMode)
						t.setLighting(255);
				this.fillTriangle(t);
				if(frameMode) {
					t.setLighting(0);
					this.drawTriangle(t);
					}
				}
				else
					this.drawTriangle(t);
			}
		} else {
			renderMesh(result);
		}
		
		return 0;
	}
	//rendering the scene with a shader applying ray tracing
	/*	ContentBuffer doesn't start top left: ->/2
	 * 				|
	 * 		2		|	1
	 * 				|
	 * 	------------+----------
	 * 				|
	 * 		3		|	4
	 * 				|
	 */
	public void renderMesh(Mesh m) {
		for (int j = -contentBuffer.getHeight()/2; j < contentBuffer.getHeight()/2; ++j) { 
		    for (int i = -contentBuffer.getWidth()/2; i < contentBuffer.getWidth()/2; ++i) { 
		    	Vector3D ray = Vector3D.normale(new Vector3D(i,j,zScreen));
		    	ray.setZ(zFar);
		    	Vector3D intersection = new Vector3D(0,0,0);
		    	for(Triangle t : m.list) {
		    		if(Evil.rayIntersectsTriangle(new Vector3D(0,0,0), ray, t, intersection)) {
		    			contentBuffer.setRGB(i, j, t.getLighting());
		    		}
		    	}
		    }
		}
		//contentBuffer.setRGB(x, y, rgb);
	}

	public void drawTriangle(Triangle tg) {
		
		int color = tg.getLighting();
		g2d.setColor(new Color(color,color,color));

		g2d.drawLine((int) tg.verticies[0].getX(), (int) tg.verticies[0].getY(), (int) tg.verticies[1].getX(),
				(int) tg.verticies[1].getY());
		g2d.drawLine((int) tg.verticies[1].getX(), (int) tg.verticies[1].getY(), (int) tg.verticies[2].getX(),
				(int) tg.verticies[2].getY());
		g2d.drawLine((int) tg.verticies[2].getX(), (int) tg.verticies[2].getY(), (int) tg.verticies[0].getX(),
				(int) tg.verticies[0].getY());
	}

	public void fillTriangle(Triangle tg) {
		int color = tg.getLighting();
		try {
		g2d.setColor(new Color(color,color,color));
		}catch(Exception e) {
			//System.out.println("Lighting error");
		}
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
				Triangle triFinal,triTranslated,triProjected,triView,triOffset, triRotated;
				
				triRotated = new Triangle();
				triOffset = new Triangle();
				triProjected = new Triangle();
				triTranslated = new Triangle();
				triFinal = new Triangle();
				triView = new Triangle();
				
				int lighting = 0;
				
				//Vector3D vLookDir = new Vector3D(0, 0, 1);
				Vector3D vtmp = new Vector3D(0,1,0);
				vLookDir = new Vector3D(Matrix.multiplie(Evil.rotateY(yaw), new Vector3D(0,0,1).getVector()));
				vLookDir = new Vector3D(Matrix.multiplie(Evil.rotateX(pitch), vLookDir.getVector()));
				vLookDir.setW(0);
				Vector3D vTarget = new Vector3D(Matrix.add(vCamera.getVector(), vLookDir.getVector()));
				
				Matrix viewMat = Evil.pointAtMatrix(vCamera, vTarget, vtmp);
				if(debug)
					System.out.println("View Matrix: \n" + viewMat.getformatetMatrix());
				
				

				/*
				 * Rotation Part of pipeline
				 */
				//for looop for all pipelines objects where translation is applied to all verticies
				for(int i = 0;i < tg.verticies.length;i++) {
					if(debug) {
						System.out.println("vertecie: \n"+ tg.verticies[i].getVector().getformatetMatrix());
					}
					if (this.rotation != null) {
						Matrix rotationVector = tg.verticies[i].getVector();
						rotationVector = Matrix.multiplie(Evil.rotateX(this.rotation.getX()),rotationVector);
						rotationVector = Matrix.multiplie(Evil.rotateY(this.rotation.getY()),rotationVector);
						rotationVector = Matrix.multiplie(Evil.rotateZ(this.rotation.getZ()),rotationVector);
						triRotated.verticies[i].setVector(rotationVector);
//							Vector3D rotationVector = tg.verticies[i];
//							rotationVector = new Vector3D(Matrix.multiplie(Evil.rotateX(this.rotation.getX()),rotationVector.getVector()));
//							rotationVector = new Vector3D(Matrix.multiplie(Evil.rotateY(this.rotation.getY()),rotationVector.getVector()));
//							rotationVector = new Vector3D(Matrix.multiplie(Evil.rotateZ(this.rotation.getZ()),rotationVector.getVector()));
//							triRotated.verticies[i]= rotationVector;
							if(debug) {
								System.out.println("Rotated vertecie \n" + triRotated.verticies[i].getVector().getformatetMatrix());
								break;
							}
	
					} else {
						triRotated = tg;

					}
				}
					/*
					 * Offset into screen space
					 */
				for(int i = 0;i < tg.verticies.length;i++) {
					triOffset = triRotated;
					triOffset.verticies[i].setZ(triRotated.verticies[i].getZ()+3.0);
					if(debug) {
						System.out.println("Offseted vertecie \n" + triOffset.verticies[i].getVector().getformatetMatrix());
						break;
					}
				}
				/*
				 * calculate movment via view matrix
				 */
				for(int i = 0;i < tg.verticies.length;i++) {
					triView.verticies[i] = new Vector3D(Matrix.transposeMatrix(Matrix.multiplie(Matrix.transposeMatrix(triOffset.verticies[i].getVector()),viewMat)));
					if(debug) {
						System.out.println("Cam/movment vertciie: \n" +  triView.verticies[i].getVector().getformatetMatrix());
						break;
					}
				}
				
				
					/*
					 * Projection to 3D
					 */
					if(triView.calculateIfcn(cam) < 0 || wireMode || debug||renderMode) {
						//calculate lighting 
						if(!wireMode)
							lighting = triView.calculateLighting(vCamera);
						else
							lighting = 255;
						for(int i = 0;i < tg.verticies.length;i++) {
						triProjected.verticies[i] = new Vector3D((Matrix.multiplie(Evil.transformMatrix(aspectRatio, fovRad, zScreen, zFar), triView.verticies[i].getVector())));
						if(debug)
							System.out.println("calculation W: " + triProjected.verticies[i].getW());
						if (triProjected.verticies[i].getW() != 0.0f) {
							triProjected.verticies[i].setX(triProjected.verticies[i].getX() / triProjected.verticies[i].getW());
							triProjected.verticies[i].setY(triProjected.verticies[i].getY() / triProjected.verticies[i].getW());
							triProjected.verticies[i].setZ(triProjected.verticies[i].getZ() / triProjected.verticies[i].getW());
						}
						if(debug) {
						System.out.println("Projected Verticie \n" +triProjected.verticies[i].getVector().getformatetMatrix());
						}

						/*
						 * Translation onto screen
						 * 
						 */
						triTranslated = triProjected;
						triTranslated.verticies[i].setX(triTranslated.verticies[i].getX()+1);
						triTranslated.verticies[i].setY(triTranslated.verticies[i].getY()+1);
						
						triTranslated.verticies[i].setX(triTranslated.verticies[i].getX()*0.5*contentBuffer.getWidth());
						triTranslated.verticies[i].setY(triTranslated.verticies[i].getY()*0.5*contentBuffer.getHeight());
						if(debug) {
							System.out.println("Translated verticie \n" + triTranslated.verticies[i].getVector().getformatetMatrix());
							break;
						}
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
		if(!frameMode)
			this.deltaTime = (System.currentTimeMillis() - this.timeStamp)/1000;
		else
			this.deltaTime = 0.01;
		if(this.frameMode)
			this.w.setTitle(fps + " FPS(deltaTime = " + this.deltaTime + ") " + this.triangles + " triangles loaded");
		else
			this.w.setTitle(fps + " FPS(deltaTime = " + this.deltaTime + ")");
	}
	
	
	/*
	 * Rotation matrizes
	 */
	public static Matrix rotateX(double angle) {
		double theta = Math.toRadians(angle);
		double[][] rdm= {{1,	0,				0,				0},
					  	{0,	Math.cos(theta),-Math.sin(theta),	0},
					  	{0,	Math.sin(theta),Math.cos(theta),	0},
					  	{0,		0,				0,				1}};	
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
	public static Matrix pointAtMatrix(Vector3D from,Vector3D to,Vector3D tmp) {
		Vector3D forward = Vector3D.normale(new Vector3D(Matrix.subtract(to.getVector(), from.getVector())));
		//Vector3D up = Vector3D.crossProductVector(forward, right);
		Vector3D up = Vector3D.normale(new Vector3D(Matrix.subtract(tmp.getVector(), Matrix.multiplie(Vector3D.dotProduct(tmp, forward, new Vector3D(0,0,0)),forward.getVector()))));
		Vector3D right = Vector3D.crossProductVector(up, forward);
		//Point at matrix
		double[][] pam = {{right.getX(),	right.getY(),	right.getZ(),	0},
						  {up.getX(),		up.getY()	,	up.getZ()	 ,	0},
						  {forward.getX(),	forward.getY(),	forward.getZ(),	0},
						  {from.getX(),		from.getY(),	from.getZ(),	1}};
		//Look at Matrix
		double[][] lam = {{right.getX(),	up.getX(),	forward.getX(),	0},
						  {right.getY(),	up.getY(),	forward.getY(),	0},
						  {right.getZ(),	up.getZ(),	forward.getZ(),	0},
						  {-(Matrix.multiplie(Matrix.transposeMatrix(from.getVector()), right.getVector()).getMatrix()[0][0]),
						   -(Matrix.multiplie(Matrix.transposeMatrix(from.getVector()), up.getVector()).getMatrix()[0][0]),
						   -(Matrix.multiplie(Matrix.transposeMatrix(from.getVector()), forward.getVector()).getMatrix()[0][0]),1}};
		Matrix pointAtMatrix = new Matrix(lam);
		return pointAtMatrix;
	}
	
	/*
	 * Möller Trumbore intersection alogrithem
	 * https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
	 * This algorithm is neither written nor owned by me!
	 */
	private static final double EPSILON = 0.0000001;
	public static boolean rayIntersectsTriangle(Vector3D origin,Vector3D rayVector,Triangle inTriangle,Vector3D intersectionPoint) {
		Vector3D vertex0 = inTriangle.verticies[0];
		Vector3D vertex1 = inTriangle.verticies[1];
		Vector3D vertex2 = inTriangle.verticies[2];
		
		Vector3D edge1 = new Vector3D(0,0,0);
		Vector3D edge2 = new Vector3D(0,0,0);
		
		Vector3D h = new Vector3D(0,0,0);
		Vector3D s = new Vector3D(0,0,0);
		Vector3D q = new Vector3D(0,0,0);
		
		double a,f,u,v;
		
		edge1 = new Vector3D(Matrix.subtract(vertex1.getVector(), vertex0.getVector()));
		edge2 = new Vector3D(Matrix.subtract(vertex2.getVector(), vertex0.getVector()));
		
		h = Vector3D.crossProductVector(rayVector, edge2);
		
		a = Vector3D.dotProduct(edge1, h, new Vector3D(0,0,0));
		
		// same as a = 0, duo to rounding errors constant is used instead
        if (a > -EPSILON && a < EPSILON) {
            return false;    // This ray is parallel to this triangle.
        }
        f = 1.0/a;
        
        s = new Vector3D(Matrix.subtract(origin.getVector(), vertex0.getVector()));
        u = f * (Vector3D.dotProduct(s, h, new Vector3D(0,0,0)));
        
        if (u < 0.0 || u > 1.0) {
            return false;
        }
        q = Vector3D.crossProductVector(s, edge1);
        v = f * Vector3D.dotProduct(rayVector, q,  new Vector3D(0,0,0));
        
        if (v < 0.0 || u + v > 1.0) {
            return false;
        }
        double t = f * Vector3D.dotProduct(edge2, q, new Vector3D(0,0,0));
        if(t > EPSILON) {
        	intersectionPoint = new Vector3D(Matrix.multiplie(t, Matrix.add(rayVector.getVector(), origin.getVector())));
        	return true;
        }
        
		return false;
		
	}
}
