package evil.test.main;

import java.io.IOException;
import java.util.ArrayList;

import evil.graphics.api.Evil;
import evil.graphics.components.Mesh;
import evil.graphics.components.Triangle;
import evil.graphics.components.Vector3D;
import evil.graphics.components.Window;

public class Main {

	public static void main(String[] args) {
		Window w = new Window();
		w.createWindow(800, 800);
		Evil evil = new Evil(w);

		Mesh m = new Mesh();
		
		Triangle tg1 = new Triangle(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0), new Vector3D(1, 1, 0));
		Triangle tg2 = new Triangle(new Vector3D(0, 0, 0), new Vector3D(1, 1, 0), new Vector3D(1, 0, 0));
		
		Triangle tg3 = new Triangle(new Vector3D(1, 0, 0), new Vector3D(1, 1, 0), new Vector3D(1, 1, 1));
		Triangle tg4 = new Triangle(new Vector3D(1, 0, 0), new Vector3D(1, 1, 1), new Vector3D(1, 0, 1));
		
		Triangle tg5 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(1, 1, 1), new Vector3D(0, 1, 1));
		Triangle tg6 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(0, 1, 1), new Vector3D(0, 0, 1));
		
		Triangle tg7 = new Triangle(new Vector3D(0, 0, 1), new Vector3D(0, 1, 1), new Vector3D(0, 1, 0));
		Triangle tg8 = new Triangle(new Vector3D(0, 0, 1), new Vector3D(0, 1, 0), new Vector3D(0, 0, 0));
		
		Triangle tg9 = new Triangle(new Vector3D(0, 1, 0), new Vector3D(0, 1, 1), new Vector3D(1, 1, 1));
		Triangle tg10 = new Triangle(new Vector3D(0, 1,0), new Vector3D(1, 1, 1), new Vector3D(1, 1, 0));
		
		Triangle tg11 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(0, 0, 1), new Vector3D(0, 0, 0));
		Triangle tg12 = new Triangle(new Vector3D(1, 0, 1), new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
		

		m.addTriangle(tg1);
		m.addTriangle(tg2);
		m.addTriangle(tg3);
		m.addTriangle(tg4);
		m.addTriangle(tg5);
		m.addTriangle(tg6);
		m.addTriangle(tg7);
		m.addTriangle(tg8);
		m.addTriangle(tg9);
		m.addTriangle(tg10);
		m.addTriangle(tg11);
		m.addTriangle(tg12);
		
		while (true) {
			try {
				
				m.rotateX(0.5);
				m.rotateY(1);
				m.rotateZ(2);
					
					evil.drawMesh(m);
					evil.drawCall();
					
					Thread.sleep(20);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
