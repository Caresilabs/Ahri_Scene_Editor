package com.caresilabs.ase.desktop;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.caresilabs.ase.SceneEditor;
import javax.swing.JSplitPane;
import java.awt.Canvas;
import java.awt.Button;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class DesktopLauncher extends JFrame {
	/**
		 * 
		 */
	private static final long serialVersionUID = -5985901283689445450L;

	LwjglAWTCanvas canvas1;
	LwjglAWTCanvas canvas2;
	LwjglAWTCanvas canvas3;

	public DesktopLauncher() {
		setTitle("Ahri Scene Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//Container container = getContentPane();
		

		//container.add(canvas1.getCanvas(), BorderLayout.LINE_START);
		//container.add(canvas2.getCanvas(), BorderLayout.CENTER);
		
		
		//container.add(canvas3.getCanvas(), BorderLayout.CENTER);

		pack();
		setVisible(true);
		setSize(720, 480);
		
		canvas3 = new LwjglAWTCanvas(new SceneEditor());//, canvas1);
		canvas3.getCanvas().setSize(720, 480);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		Canvas canvas_1 = new Canvas();
		getContentPane().add(canvas3.getCanvas(), BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("New tab", null, panel, null);
		
		JTree tree = new JTree();
		panel.add(tree);
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane_1, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		tabbedPane_1.addTab("New tab", null, panel_1, null);
		
		JLabel lblProperties = new JLabel("Properties");
		panel_1.add(lblProperties);
		
		
		//panel.add(canvas3.getCanvas());
		
		//JPanel panel_1 = new JPanel();
		//getContentPane().add(panel_1, BorderLayout.EAST);
	}

	class WindowCreator extends ApplicationAdapter {
		SpriteBatch batch;
		BitmapFont font;

		@Override
		public void create () {
			batch = new SpriteBatch();
			font = new BitmapFont();
		}

		@Override
		public void dispose () {
			font.dispose();
			batch.dispose();
		}

		@Override
		public void render () {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			font.draw(batch, "Click to create a new window", 10, 100);
			batch.end();

			if (Gdx.input.justTouched()) {
				createWindow();
			}
		}

		private void createWindow () {
			//JFrame window = new JFrame();
			//LwjglAWTCanvas canvas = new LwjglAWTCanvas(new DesktopLauncher(), canvas1);
			//window.getContentPane().add(canvas.getCanvas(), BorderLayout.CENTER);
			//window.pack();
			//window.setVisible(true);
			//window.setSize(200, 200);
		}
	}

	public static void main ( String[] args ) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				new DesktopLauncher();
			}
		});
	}

	@Override
	public void dispose () {
		canvas3.stop();
		//canvas2.stop();
		//canvas1.stop();

		super.dispose();
	}
}
