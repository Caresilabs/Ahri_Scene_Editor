package com.caresilabs.ase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.caresilabs.ase.listeners.SceneListener;
import com.caresilabs.ase.models.GameObject;
import com.caresilabs.ase.models.Scene;

public class World {
	private ModelBatch batch;
	private ShapeRenderer shapes;
	private Environment environment;

	private ModelInstance test;
	private SceneController controller;
	private PerspectiveCamera cam;

	private SceneListener editor;

	public World(SceneListener editor) {
		this.editor = editor;
		this.shapes = new ShapeRenderer();
		this.batch = new ModelBatch();

		this.environment = new Environment();
		this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.7f, 0.7f, 0.7f, 1f));
		this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		this.environment.add(new PointLight().set(Color.RED, 0, 0, 0, 10));
		
		// Fog
		ColorAttribute fog = new ColorAttribute(ColorAttribute.Fog);
		fog.color.set(Color.GRAY);
		environment.set(fog);

		this.cam = new PerspectiveCamera(80, 10, 10);
		this.cam.position.set(0f, 0f, 5);
		this.cam.lookAt(0, 0, 0);
		this.cam.near = .1f;
		this.cam.far = 150f;

		this.controller = new SceneController(this);

		// Model m = assets.get("teapot.g3db", Model.class);
		// test = new ModelInstance(m);
		// test.transform.setTranslation(0, 0, -20);
		// test.transform.setToScaling(10, 10, 10);

	}

	public void update ( float delta ) {
		controller.update();

	}

	int aa;

	public void render () {
		// Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());

		batch.begin(cam);

		Scene scene = editor.getScene();

		// scene.rootNode.getChild(0).setPosition(++aa/200f,0, 0);
		// if (gameObject == scene.gameObjects.get(0))
		// gameObject.setRotation(0, ++aa, 0);
		scene.rootNode.render(batch, environment);
		// }

		batch.end();

		if (editor.getSelected() != null) {
			shapes.setProjectionMatrix(cam.combined);
			shapes.begin(ShapeType.Line);
			shapes.setColor(Color.GRAY);
			BoundingBox b = editor.getSelected().bounds;
			shapes.box(b.getCenterX() - b.getWidth() / 2, b.getCenterY() - b.getHeight() / 2, b.getCenterZ() + b.getDepth() / 2,
					b.getWidth(), b.getHeight(), b.getDepth());
			shapes.end();
		}
	}

	public void select () {
		Ray ray = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());

		Array<GameObject> hits = new Array<GameObject>();
		updateSelection(ray, editor.getScene().rootNode.children, hits);

		// FIXME check distance
		if (hits.size >= 1) {
			editor.selected(hits.get(0));
		} else {
			editor.deselect();
		}
	}
	
	public void insertNew () {
		Ray ray = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());

		Array<GameObject> hits = new Array<GameObject>();
		updateSelection(ray, editor.getScene().rootNode.children, hits);

		// FIXME check distance
		if (hits.size >= 1) {
			Vector3 inter = new Vector3();
			Intersector.intersectRayBounds(ray, hits.get(0).bounds, inter);
			editor.newNode(inter);
		}
	}

	private void updateSelection ( Ray ray, Array<GameObject> nodes, Array<GameObject> hits ) {
		for (GameObject child : nodes) {
			if (Intersector.intersectRayBoundsFast(ray, child.bounds)) {
				if (child.getModel() == null || intersect(ray, child.getModel().model, child.getGlobal(), new Vector3())) {
					hits.add(child);
				}
			}
			updateSelection(ray, child.children, hits);
		}
	}
	
	private boolean intersect(Ray ray, Model model, Matrix4 transform, Vector3 intersection) {
	    final Matrix4 reverse = new Matrix4(transform).inv();
	    for (Mesh mesh : model.meshes) {
	        mesh.transform(transform);
	        float[] vertices = new float[mesh.getNumVertices() * 6];
	        short[] indices = new short[mesh.getNumIndices()];
	        mesh.getVertices(vertices);
	        mesh.getIndices(indices);
	        try {
	            if (Intersector.intersectRayTriangles(ray, vertices, indices, 4, intersection)) {
	                intersection.mul(transform);
	                return true;
	            }
	        } finally {
	            mesh.transform(reverse);
	        }
	    }
	    return false;
	}

	public SceneController getController () {
		return controller;
	}

	public PerspectiveCamera getCamera () {
		return cam;
	}
	
	public SceneListener getEditor () {
		return editor;
	}
}
