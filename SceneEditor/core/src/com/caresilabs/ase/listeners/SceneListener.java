package com.caresilabs.ase.listeners;

import com.badlogic.gdx.math.Vector3;
import com.caresilabs.ase.models.GameObject;
import com.caresilabs.ase.models.Scene;

public interface SceneListener {
	public Scene getScene();
	
	public GameObject getSelected();
	
	public void selected(GameObject selected);
	
	public void deselect();

	public void newNode ( Vector3 position );
}
