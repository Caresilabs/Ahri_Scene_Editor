package com.caresilabs.ase.models;

import java.io.File;

public class Resource{
	private String relativePath, fullPath, name;
	
	public Resource(String path, String scenePath) {
		this.relativePath =   new File(scenePath).toURI().relativize(new File(path).toURI()).getPath();
		this.fullPath = path;
		this.name = new File(path).getName();
		
		//try {
			//WatchKey watchKey = Paths.get(path).register(SceneEditor.FileWatcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
//		} catch (IOException e) {
	//		e.printStackTrace();
		//}
	}
	
	public Resource() {
		this.relativePath =   "";
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
}
