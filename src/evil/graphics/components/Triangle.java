package evil.graphics.components;

import java.util.ArrayList;

public class Triangle {
	public Vector3D[] verticies = new Vector3D[3];
	
	public Triangle(Vector3D a,Vector3D b,Vector3D c) {
		verticies[0] = a;
		verticies[1] = b;
		verticies[2] = c;
	}

	
	
}
