package evil.test.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import evil.graphics.api.Evil;
import evil.graphics.components.Mesh;
import evil.graphics.components.Triangle;
import evil.graphics.components.Vector3D;
import evil.graphics.components.Window;
import evil.math.algebra.Matrix;

public class Main {

	public static Evil evil;

	public static void main(String[] args) {
		Mesh m = new Mesh();
		m.loadMesh("demoMeshes/balls.obj");
		Window w = new Window(new KeyListener() {
			double speed = 12.0;

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				Vector3D forward = new Vector3D(Matrix.multiplie(evil.deltaTime * speed, evil.vLookDir.getVector()));
				Vector3D forwardX = new Vector3D(Matrix.multiplie(evil.deltaTime * speed,Vector3D.crossProductVector(evil.vLookDir, new Vector3D(0, -1, 0, 0)).getVector()));
				// non zeroed w value will corrupt movment in terms of deapth
				forwardX.setW(0);
				if (e.getKeyCode() == KeyEvent.VK_A) {
//					evil.vCamera.setX(evil.vCamera.getX() + speed*evil.deltaTime);
					evil.vCamera = new Vector3D(Matrix.add(evil.vCamera.getVector(), forwardX.getVector()));
				}
				if (e.getKeyCode() == KeyEvent.VK_D) {
//					evil.vCamera.setX(evil.vCamera.getX() - speed*evil.deltaTime);
					evil.vCamera = new Vector3D(Matrix.subtract(evil.vCamera.getVector(), forwardX.getVector()));
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					evil.vCamera.setY(evil.vCamera.getY() + speed * evil.deltaTime);
				}
				if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					evil.vCamera.setY(evil.vCamera.getY() - speed * evil.deltaTime);
				}
				if (e.getKeyCode() == KeyEvent.VK_W) {
//					evil.vCamera.setZ(evil.vCamera.getZ() + speed*evil.deltaTime);
					evil.vCamera = new Vector3D(Matrix.add(evil.vCamera.getVector(), forward.getVector()));

				}
				if (e.getKeyCode() == KeyEvent.VK_S) {
//					evil.vCamera.setZ(evil.vCamera.getZ() - speed*evil.deltaTime);
					evil.vCamera = new Vector3D(Matrix.subtract(evil.vCamera.getVector(), forward.getVector()));
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					evil.yaw += speed *4* evil.deltaTime;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					evil.yaw -= speed *4* evil.deltaTime;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					evil.pitch += speed *4* evil.deltaTime;
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					evil.pitch -= speed *4* evil.deltaTime;
				}
				if (e.getKeyCode() == KeyEvent.VK_F1) {

					if (evil.wireMode) {
						evil.wireMode = false;
						System.out.println("Wire mode off");
					} else {
						evil.wireMode = true;
						System.out.println("Wire mode on");
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_F2) {

					if (evil.debug) {
						evil.debug = false;
						System.out.println("debug mode off");
					} else {
						evil.debug = true;
						System.out.println("debug mode on");
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_F3) {

					if (evil.frameMode) {
						evil.frameMode = false;
						System.out.println("frame mode off");
					} else {
						evil.frameMode = true;
						System.out.println("frame mode on");
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_F4) {

					if (evil.renderMode) {
						evil.renderMode = false;
						System.out.println("render  mode off");
					} else {
						evil.renderMode = true;
						System.out.println("render  mode on");
					}
				}
				evil.start();
				evil.drawMesh(m);
				evil.drawCall();
				evil.end();

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
		w.createWindow(800, 800);
		evil = new Evil(w);

		Triangle tg1 = new Triangle(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), new Vector3D(1, 1, 0));
		Triangle tg2 = new Triangle(new Vector3D(0, 0, 0), new Vector3D(1, 1, 0), new Vector3D(1, 0, 0));

		Triangle tg3 = new Triangle(new Vector3D(1, 0, 0), new Vector3D(1, 1, 0), new Vector3D(1, 1, 1));
		Triangle tg4 = new Triangle(new Vector3D(1, 0, 0), new Vector3D(1, 1, 1), new Vector3D(1, 0, 1));

		Triangle tg5 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(1, 1, 1), new Vector3D(0, 1, 1));
		Triangle tg6 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(0, 1, 1), new Vector3D(0, 0, 1));

		Triangle tg7 = new Triangle(new Vector3D(0, 0, 1), new Vector3D(0, 1, 1), new Vector3D(0, 1, 0));
		Triangle tg8 = new Triangle(new Vector3D(0, 0, 1), new Vector3D(0, 1, 0), new Vector3D(0, 0, 0));

		Triangle tg9 = new Triangle(new Vector3D(0, 1, 0), new Vector3D(0, 1, 1), new Vector3D(1, 1, 1));
		Triangle tg10 = new Triangle(new Vector3D(0, 1, 0), new Vector3D(1, 1, 1), new Vector3D(1, 1, 0));

		Triangle tg11 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(0, 0, 1), new Vector3D(0, 0, 0));
		Triangle tg12 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));

//		m.addTriangle(tg1);
//		m.addTriangle(tg2);
//		m.addTriangle(tg3);
//		m.addTriangle(tg4);
//		m.addTriangle(tg5);
//		m.addTriangle(tg6);
//		m.addTriangle(tg7);
//		m.addTriangle(tg8);
//		m.addTriangle(tg9);
//		m.addTriangle(tg10);
//		m.addTriangle(tg11);
//		m.addTriangle(tg12);
//just dont use it...
		
		//m.moveMesh(new Vector3D(0, 0, 5));
		evil.start();
		evil.drawMesh(m);
		evil.drawCall();
		evil.end();
//produces lag
//		long rotation = 0;
//		while (true) {
//			try {
//				evil.start();
////				m.setRotation(new Vector3D(rotation,0,0));
//				evil.drawMesh(m);
//				evil.drawCall();
//				Thread.sleep(20);
//				evil.end();
//				rotation++;
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

	}

}
