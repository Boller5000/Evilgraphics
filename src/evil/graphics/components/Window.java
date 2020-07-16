package evil.graphics.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Window {
	
	private JFrame frame;
	private KeyListener listener;
	
	private ContentPanel contentPanel;
	public Window(KeyListener k) {
		this.listener = k;
	}
	
	/**
	 * Creates a swing type window in which you can call EvilGraphics api calls to draw
	 * @return
	 */
	public int createWindow(int width, int height) {
		
				contentPanel = new ContentPanel();
				contentPanel.setPreferredSize(new Dimension(width,height));
				
				frame = new JFrame() {
					@Override
					public void paint(Graphics g) {}
				}; 
				frame.addComponentListener(new ComponentAdapter() {
					public void componentResized(ComponentEvent e) {
						contentPanel.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()));
					}
				});
				frame.setContentPane(contentPanel);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.addKeyListener(this.listener);
				frame.requestFocus();
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				
				System.out.println("Window Created " + contentPanel.getWidth() + "x" + contentPanel.getHeight());
		
		return 0;
	}

	public ContentPanel getContentPanel() {
		return contentPanel;
	}

	public void setContentPanel(ContentPanel contentPanel) {
		this.contentPanel = contentPanel;
	}
	
	public void setTitle(String title) {
		this.frame.setTitle(title);
	}
	public  void addComponentListener(ComponentAdapter adapter) {
		this.frame.addComponentListener(adapter);
	}
	
}
