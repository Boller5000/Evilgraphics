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

		Triangle tg = new Triangle(new Vector3D(-0.5, 0, 1), new Vector3D(0.5, 0, 1), new Vector3D(0, 0.5, 1));

		while (true) {
			try {
				for (int i = 0; i < 360; i++) {
					tg.verticies[0] = (evil.rotateX(i, tg.verticies[0]));
					tg.verticies[1] = (evil.rotateX(i, tg.verticies[1]));
					tg.verticies[2] = (evil.rotateX(i, tg.verticies[2]));
					evil.drawTriangle(tg);
					evil.drawCall();
					Thread.sleep(20);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
