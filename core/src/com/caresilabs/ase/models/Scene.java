package com.caresilabs.ase.models;

import java.util.HashMap;

public class Scene {
	public HashMap<String, Object> properties = new HashMap<String, Object>();
	
	public HashMap<String, Resource> resources = new HashMap<String, Resource>();
	
	public GameObject rootNode;
	
	public String path;
}
