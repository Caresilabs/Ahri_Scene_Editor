package com.caresilabs.ase.desktop;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFileChooser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.caresilabs.ase.SceneEditor;
import com.caresilabs.ase.listeners.Bridge;
import com.caresilabs.ase.listeners.FileChooserListener;

public class DesktopLauncher implements Bridge{
	static DesktopLauncher app;
	
	public static void main (String[] arg) {
		if(app == null)
			 app = new DesktopLauncher();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.title = "Ahri Scene Editor - ALPHA";
		new LwjglApplication(new SceneEditor(app), config);
	}

	@Override
	public void openFilePicker (final FileChooserListener listener) {
		EventQueue.invokeLater(new Runnable() {
            public void run () {
           	 JFileChooser fc = new JFileChooser();
               int returnVal = fc.showOpenDialog(null);

               if (returnVal == JFileChooser.APPROVE_OPTION) {
            	   listener.onOpen(fc.getSelectedFile());
               } else {
               	Gdx.app.log("Open command cancelled by user.", "");
               }
            }
         });
	}
}
