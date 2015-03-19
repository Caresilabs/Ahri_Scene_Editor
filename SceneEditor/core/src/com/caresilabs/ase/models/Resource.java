package com.caresilabs.ase.models;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Resource {
	private String relativePath, fullPath, name;
	private File file;
	private long lastModified;

	public Resource(String path, String scenePath) {
		calculateRelativePath(path, scenePath);
		lastModified = file.lastModified();
		
		// try {
		// WatchKey watchKey = Paths.get(path).register(SceneEditor.FileWatcher,
		// StandardWatchEventKinds.ENTRY_MODIFY,
		// StandardWatchEventKinds.ENTRY_DELETE,
		// StandardWatchEventKinds.ENTRY_MODIFY);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run () {
				if (file.lastModified() > lastModified) {
					// Modified! reload!
				}
			}
		}, 5 * 1000, 5 * 1000);
	}

	private void calculateRelativePath ( String path, String scenePath ) {
		this.relativePath = new File(scenePath).toURI().relativize(new File(path).toURI()).getPath();
		this.file = new File(path);
		this.name = file.getName();
		this.fullPath = file.getAbsolutePath().replace("\\", "/");
		
	}

	public Resource() {
		this.relativePath = "";
		this.fullPath = "";
		this.name = "No File";
	}

	public String getFullPath () {
		return fullPath;
	}

	public String getRelativePath () {
		return relativePath;
	}

	public String getName () {
		return name;
	}

	public boolean isNull () {
		return fullPath.equals("");
	}

	@Override
	public String toString () {
		return name;
	}

	public String getExtension () {
		String name = file.getName();
	    int lastIndexOf = name.lastIndexOf(".");
	    if (lastIndexOf == -1) {
	        return "";
	    }
	    return name.substring(lastIndexOf);
	}
}
