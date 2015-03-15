package com.caresilabs.ase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

public class SceneEditor extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	PerspectiveCamera cam;
	FirstPersonCameraController c;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		cam = new PerspectiveCamera(90, 40, 20);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
		cam.position.z = 1;
		cam.near=0;
		
		 c = new FirstPersonCameraController(cam);
		Gdx.input.setInputProcessor(c);
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		c.update();

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(img, 0, 0, 1, 1);
		batch.end();
	}
	
	private void update ( float deltaTime ) {
		
	}

	@Override
	public void resize ( int width, int height ) {
		super.resize(width, height);
	}
}
