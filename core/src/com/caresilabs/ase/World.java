package com.caresilabs.ase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.caresilabs.ase.listeners.SceneListener;
import com.caresilabs.ase.models.GameObject;
import com.caresilabs.ase.models.Scene;

public class World {
	private ModelBatch batch;
	private Environment environment;

	private ModelInstance test;
	private CameraController controller;
	private PerspectiveCamera cam;

	private SceneListener editor;
	
	public World(SceneListener editor) {
		this.editor = editor;
		
		this.batch = new ModelBatch();
		
		this.environment = new Environment();
		this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.9f, 0.9f, 0.9f, 1f));
		this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		this.cam = new PerspectiveCamera(67, 10, 10);
		this.cam.position.set(0f, 0f, 0);
		this.cam.lookAt(0, 0, 0);
		this.cam.near = 1f;
		this.cam.far = 300f;

		this.controller = new CameraController(cam);

		//Model m = assets.get("teapot.g3db", Model.class);
		//test = new ModelInstance(m);
		//test.transform.setTranslation(0, 0, -20);
		//test.transform.setToScaling(10, 10, 10);

	}

	public void update ( float delta ) {
		controller.update();

	}

	int aa;
	public void render () {
	    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
		batch.begin(cam);
		
		Scene scene = editor.getScene();
		for (GameObject gameObject : scene.gameObjects) {
			gameObject.setRotation(0, ++aa, 0);
			gameObject.render(batch, environment);
		}
		
		batch.end();
	}

	public CameraController getController () {
		return controller;
	}
}
