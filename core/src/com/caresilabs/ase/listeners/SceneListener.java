package com.caresilabs.ase.listeners;

import com.caresilabs.ase.models.GameObject;
import com.caresilabs.ase.models.Scene;

public interface SceneListener {
	public Scene getScene();
	
	public void selected(GameObject selected);
	
	public void deselect();
}
