package com.caresilabs.ase;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.caresilabs.ase.io.JsonSceneLoader;
import com.caresilabs.ase.io.SceneLoader;
import com.caresilabs.ase.listeners.SceneListener;
import com.caresilabs.ase.models.GameObject;
import com.caresilabs.ase.models.Scene;
import com.caresilabs.ase.models.Resource;

public class SceneEditor extends ApplicationAdapter {
	public static WatchService FileWatcher;

	private World world;
	private Stage stage;
	private Skin skin;
	private AssetManager assets;

	private Scene currentMap;

	@Override
	public void create () {
		this.stage = new Stage(new ExtendViewport(1280, 720)); //
		
		SceneListener editor = new SceneListener() {
			@Override
			public Scene getScene () {
				return currentMap;
			}
		};
		
		this.world = new World(editor);
		this.assets = new AssetManager(new AbsoluteFileHandleResolver());

		/*try {
			FileWatcher = FileSystems.getDefault().newWatchService();
			startListening();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		*/

		// Resource r = new Resource("C:/Users/Simon/Downloads/monke.g3db", "");

		// INIT UI
		initUI();
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, world.getController()));

		currentMap = loadScene("");
	}

	/**
	 * WARINIG: This method is just a mock call
	 * 
	 * @param path
	 * @return
	 */
	private Scene loadScene ( String path ) {
		// Dispose old assets
		assets.clear();
		
		SceneLoader loader = new JsonSceneLoader();

		Scene scene = new Scene();
		scene.resources.put("C:/Users/Simon/Downloads/monkey.g3db", new Resource("C:/Users/Simon/Downloads/monkey.g3db", ""));

		for (Resource res : scene.resources.values()) {
			assets.load(res.getFullPath(), Model.class);
		}
		
		assets.finishLoading();
		
		scene.gameObjects.add(new GameObject() {{model = new ModelInstance(assets.get("C:/Users/Simon/Downloads/monkey.g3db", Model.class));}});
		scene.gameObjects.get(0).addChild(new GameObject(-3, 0, 0) {{model = new ModelInstance(assets.get("C:/Users/Simon/Downloads/monkey.g3db", Model.class));}});
		return scene;
	}

	private volatile Thread processingThread;

	public void startListening () {
		processingThread = new Thread() {
			public void run () {
				while (true) {
					try {
						WatchKey key = SceneEditor.FileWatcher.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		processingThread.start();
	}

	public void shutDownListener () {
		Thread thr = processingThread;
		if (thr != null) {
			thr.interrupt();
		}
	}

	private void initUI () {
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

		final Tree hierarchy = new Tree(skin);
		hierarchy.getSelection().setMultiple(false);

		final Node moo1 = new Node(new Label("moo1", skin));
		final Node moo2 = new Node(new Label("moo2", skin));
		final Node moo3 = new Node(new Label("moo3", skin));
		final Node moo4 = new Node(new Label("moo4", skin));
		final Node moo5 = new Node(new Label("moo5", skin));
		hierarchy.add(moo1);
		hierarchy.add(moo2);
		moo2.add(moo3);
		moo3.add(moo4);
		hierarchy.add(moo5);

		DragAndDrop dragAndDrop = new DragAndDrop();
		dragAndDrop.addSource(new Source(hierarchy) {
			public Payload dragStart ( InputEvent event, float x, float y, int pointer ) {
				Payload payload = new Payload();

				// Select node under mouse if not over the selection.
				Node overNode = hierarchy.getNodeAt(y);
				if (overNode == null && hierarchy.getSelection().isEmpty())
					return null;
				if (overNode != null && !hierarchy.getSelection().contains(overNode))
					hierarchy.getSelection().set(overNode);

				// payload.setDragActor(hierarchy.getSelection().getLastSelected().getActor());
				payload.setObject(overNode);

				return payload;
			}
		});
		dragAndDrop.addTarget(new Target(hierarchy) {
			private Node selected;

			public boolean drag ( Source source, Payload payload, float x, float y, int pointer ) {
				if (selected != null)
					selected.getActor().setColor(Color.WHITE);

				selected = hierarchy.getNodeAt(y); // .getActor().setColor(Color.CYAN);

				if (selected != null)
					selected.getActor().setColor(Color.CYAN);
				return true;
			}

			public void reset ( Source source, Payload payload ) {
				getActor().setColor(Color.WHITE);

				if (selected != null)
					selected.getActor().setColor(Color.WHITE);
			}

			public void drop ( Source source, Payload payload, float x, float y, int pointer ) {
				if (selected == payload.getObject())
					return;

				// System.out.println("Accepted: " + payload.getObject() + " " +
				// x + ", " + y);

				Node drop = ((Node) payload.getObject());
				Node target = hierarchy.getNodeAt(y);

				if (target == null) {
					drop.remove();
					hierarchy.add(drop);
					return;
				}

				// Check if dropped node isnt ones parent
				target.setObject("target");
				if (drop.findNode("target") != null) {
					target.setObject(null);
					return;
				}
				target.setObject(null);

				drop.remove();
				target.add(drop);
				drop.expandTo();
			}
		});

		ScrollPane scroll = new ScrollPane(hierarchy, skin);

		// window.debug();
		Window window = new Window("Hierarchy", skin);
		// window.setRound(false);
		window.defaults().padLeft(30);
		window.defaults().padRight(30);

		window.getButtonTable().add(new TextButton("X", skin)).height(window.getPadTop());
		window.setPosition(10, 100);
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		window.add(scroll).fill().expand().minWidth(200).minHeight(500);
		window.pack();

		// stage.addActor(new Button("Behind Window", skin));
		stage.addActor(window);
	}

	@Override
	public void render () {
		if (assets.update()) {
			update(Gdx.graphics.getDeltaTime());
		}

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		world.render();

		stage.draw();
	}

	private void update ( float delta ) {
		world.update(delta);
		stage.act(delta);

	}

	@Override
	public void resize ( int width, int height ) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose () {
		super.dispose();

		stage.dispose();
		skin.dispose();
	}
}
