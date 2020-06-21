package evil.graphics.components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ContentPanel extends JPanel {
	
	private BufferedImage img = null;
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
	
}
