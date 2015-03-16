package com.caresilabs.ase.models;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

public class Scene {
	public HashMap<String, Object> properties = new HashMap<String, Object>();
	
	public HashMap<String, Resource> resources = new HashMap<String, Resource>();
	
	public Array<GameObject> gameObjects = new Array<GameObject>();
	
	public String path;
}
