/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.caresilabs.ase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * Takes a {@link Camera} instance and controls it via w,a,s,d and mouse
 * panning.
 * 
 * @author badlogic
 */
public class SceneController implements InputProcessor {
	private final Camera camera;
	private final IntIntMap keys = new IntIntMap();
	private int STRAFE_LEFT = Keys.A;
	private int STRAFE_RIGHT = Keys.D;
	private int FORWARD = Keys.W;
	private int BACKWARD = Keys.S;
	private int UP = Keys.E;
	private int DOWN = Keys.Q;
	private float velocity = 5;
	private float degreesPerPixel = 0.1f;
	private final Vector3 tmp = new Vector3();

	private World world;

	public SceneController(World world) {
		this.world = world;
		this.camera = world.getCamera();
	}

	@Override
	public boolean keyDown ( int keycode ) {
		keys.put(keycode, keycode);

		if (keycode == Keys.F) {
			camera.lookAt(world.getEditor().getSelected().getGlobalPosition());
		}
		return true;
	}

	@Override
	public boolean keyUp ( int keycode ) {
		keys.remove(keycode, 0);
		return true;
	}

	/**
	 * Sets the velocity in units per second for moving forward, backward and
	 * strafing left/right.
	 * 
	 * @param velocity
	 *            the velocity in units per second
	 */
	public void setVelocity ( float velocity ) {
		this.velocity = velocity;
	}

	/**
	 * Sets how many degrees to rotate per pixel the mouse moved.
	 * 
	 * @param degreesPerPixel
	 */
	public void setDegreesPerPixel ( float degreesPerPixel ) {
		this.degreesPerPixel = degreesPerPixel;
	}

	@Override
	public boolean touchDragged ( int screenX, int screenY, int pointer ) {
		if (Gdx.input.isButtonPressed(1)) {
			float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
			float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
			camera.direction.rotate(camera.up, deltaX);
			tmp.set(camera.direction).crs(camera.up).nor();
			camera.direction.rotate(tmp, deltaY);
			// camera.up.rotate(tmp, deltaY);

		} else if (Gdx.input.isButtonPressed(2)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(-Gdx.input.getDeltaX() / camera.viewportWidth * .1f);
			camera.position.add(tmp);

			tmp.set(camera.up).nor().scl(Gdx.input.getDeltaY() / camera.viewportHeight * .1f);
			camera.position.add(tmp);
		}
		return true;
	}

	public void update () {
		update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public boolean touchDown ( int screenX, int screenY, int pointer, int button ) {
		if (button == 0) {
			world.select();
		}
		return false;// super.touchDown(screenX, screenY, pointer, button);
	}

	public void update ( float deltaTime ) {
		float velocity = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? this.velocity * 4 : this.velocity;

		if (keys.containsKey(FORWARD)) {
			tmp.set(camera.direction).nor().scl(deltaTime * velocity);
			camera.position.add(tmp);
		}
		if (keys.containsKey(BACKWARD)) {
			tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
			camera.position.add(tmp);
		}
		if (keys.containsKey(STRAFE_LEFT)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
			camera.position.add(tmp);
		}
		if (keys.containsKey(STRAFE_RIGHT)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
			camera.position.add(tmp);
		}

		camera.update(true);
	}

	@Override
	public boolean keyTyped ( char character ) {
		return false;
	}

	@Override
	public boolean touchUp ( int screenX, int screenY, int pointer, int button ) {
		return false;
	}

	@Override
	public boolean mouseMoved ( int screenX, int screenY ) {
		return false;
	}

	@Override
	public boolean scrolled ( int amount ) {
		tmp.set(camera.direction).nor().scl(-amount * velocity / 2f);
		camera.position.add(tmp);
		return false;
	}
}