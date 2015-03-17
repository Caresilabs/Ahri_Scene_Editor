package com.caresilabs.ase;

import java.awt.List;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.caresilabs.ase.io.JsonSceneLoader;
import com.caresilabs.ase.io.SceneLoader;
import com.caresilabs.ase.listeners.Bridge;
import com.caresilabs.ase.listeners.SceneListener;
import com.caresilabs.ase.models.GameObject;
import com.caresilabs.ase.models.Resource;
import com.caresilabs.ase.models.Scene;

public class SceneEditor extends ApplicationAdapter {
	public static WatchService FileWatcher;

	private World world;
	private Stage stage;
	private Skin skin;
	private AssetManager assets;
	private Bridge bridge;

	private Scene currentMap;

	private Tree uiHierarchy;
	private Window uiProperties;
	private SelectBox<Resource> assetsPicker;

	private GameObject selectedObject;

	public SceneEditor(Bridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void create () {
		this.stage = new Stage(new ExtendViewport(1280 * 1.5f, 720 * 1.5f)); //

		SceneListener editor = new SceneListener() {
			@Override
			public Scene getScene () {
				return currentMap;
			}

			@Override
			public void selected ( GameObject selected ) {
				selectedObject = selected;
				uiHierarchy.getSelection().set(uiHierarchy.findNode(selected));
				uiHierarchy.getSelection().getLastSelected().expandTo();

				updatePropertiesWindow();
				stage.unfocusAll();
			}

			@Override
			public void deselect () {
				selectedObject = null;
				uiHierarchy.getSelection().clear();
				stage.unfocusAll();

				updatePropertiesWindow();
			}

			@Override
			public GameObject getSelected () {
				return selectedObject;
			}
		};

		this.world = new World(editor);
		this.assets = new AssetManager(new AbsoluteFileHandleResolver());

		/*
		 * try { FileWatcher = FileSystems.getDefault().newWatchService();
		 * startListening(); } catch (IOException e1) { e1.printStackTrace(); }
		 */
		currentMap = loadScene("");

		// INIT UI
		initUI();
		updateTree();

		/*
		 * bridge.openFilePicker(new FileChooserListener() {
		 * 
		 * @Override public void onOpen ( File file ) { Resource res = new
		 * Resource(file.getAbsolutePath(), currentMap.path);
		 * currentMap.resources.put(res.getFullPath(), res);
		 * assets.load(res.getFullPath(), Model.class); } });
		 */

		// Set input
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, world.getController()));
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
		// scene.resources.put("C:/Users/Simon/Downloads/teapot.g3db", new
		// Resource("C:/Users/Simon/Downloads/teapot.g3db", ""));

		for (Resource res : scene.resources.values()) {
			assets.load(res.getFullPath(), Model.class);
		}

		// assets.finishLoading();

		scene.rootNode = new GameObject("root");
		scene.rootNode.addChild(new GameObject("test") {
			{
				// /setModel(new
				// ModelInstance(assets.get("C:/Users/Simon/Downloads/monkey.g3db",
				// Model.class)));
			}
		});
		scene.rootNode.getChild(0).addChild(new GameObject("sub", -3, 0, 0) {
			{
				// setModel(new
				// ModelInstance(assets.get("C:/Users/Simon/Downloads/monkey.g3db",
				// Model.class)));
			}
		});
		return scene;
	}

	private void updateTree () {
		uiHierarchy.clearChildren();
		for (GameObject gameObject : currentMap.rootNode.children) {
			Node parent = new Node(new Label(gameObject.name, skin));
			parent.setObject(gameObject);
			uiHierarchy.add(parent);
			updateTreeChildren(parent, gameObject.children);
		}
	}

	private void updateTreeChildren ( Node parent, Array<GameObject> children ) {
		for (GameObject gameObject : children) {
			Node sub = new Node(new Label(gameObject.name, skin));
			sub.setObject(gameObject);
			parent.add(sub);
			updateTreeChildren(sub, gameObject.children);
		}
	}

	public void addNode ( GameObject node, GameObject parent ) {
		parent.addChild(node);
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
		// FIXME
		stage.clear();

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

		uiHierarchy = new Tree(skin);
		uiHierarchy.getSelection().setMultiple(false);
		uiHierarchy.addListener(new ClickListener(0) {
			@Override
			public void clicked ( InputEvent event, float x, float y ) {
				super.clicked(event, x, y);

				Node overNode = uiHierarchy.getNodeAt(y);
				if (overNode == null || uiHierarchy.getSelection().isEmpty())
					return;
				selectedObject = ((GameObject) overNode.getObject());

				updatePropertiesWindow();
			}
		});

		DragAndDrop dragAndDrop = new DragAndDrop();
		dragAndDrop.addSource(new Source(uiHierarchy) {
			public Payload dragStart ( InputEvent event, float x, float y, int pointer ) {
				Payload payload = new Payload();

				// Select node under mouse if not over the selection.
				Node overNode = uiHierarchy.getNodeAt(y);
				if (overNode == null && uiHierarchy.getSelection().isEmpty())
					return null;
				if (overNode != null && !uiHierarchy.getSelection().contains(overNode))
					uiHierarchy.getSelection().set(overNode);

				payload.setObject(overNode);

				return payload;
			}
		});
		dragAndDrop.addTarget(new Target(uiHierarchy) {
			private Node selected;

			public boolean drag ( Source source, Payload payload, float x, float y, int pointer ) {
				if (selected != null)
					selected.getActor().setColor(Color.WHITE);

				selected = uiHierarchy.getNodeAt(y); // .getActor().setColor(Color.CYAN);

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
				Node target = uiHierarchy.getNodeAt(y);

				if (target == null) {
					drop.remove();
					uiHierarchy.add(drop);

					((GameObject) drop.getObject()).detach();
					currentMap.rootNode.addChild(((GameObject) drop.getObject()));
					((GameObject) drop.getObject()).calculateTransforms(true);
					return;
				}

				// TODO
				// Check if dropped node isnt ones parent
				// target.setObject("target");
				// if (drop.findNode("target") != null) {
				// target.setObject(null);
				// return;
				// }
				// target.setObject(null);

				drop.remove();
				target.add(drop);
				drop.expandTo();

				// change map structure
				((GameObject) drop.getObject()).detach();
				((GameObject) target.getObject()).addChild(((GameObject) drop.getObject()));
				((GameObject) drop.getObject()).calculateTransforms(true);
			}
		});

		ScrollPane scroll = new ScrollPane(uiHierarchy, skin);

		TextButton newNode = new TextButton("New Node", skin);
		newNode.addListener(new ChangeListener() {

			@Override
			public void changed ( ChangeEvent event, Actor actor ) {
				GameObject node = new GameObject("new node");
				Node sub = new Node(new Label(node.name, skin));
				sub.setObject(node);

				if (selectedObject != null) {
					selectedObject.addChild(node);
					uiHierarchy.findNode(selectedObject).add(sub);
				} else {
					currentMap.rootNode.addChild(node);
					uiHierarchy.add(sub);
				}

				sub.expandTo();
				uiHierarchy.getSelection().set(sub);
				selectedObject = node;

				updatePropertiesWindow();
			}
		});

		// tree

		Window tree = new Window("Hierarchy", skin);
		// window.setRound(false);
		// tree.defaults().padLeft(30);
		// tree.defaults().padRight(30);

		tree.getButtonTable().add(new TextButton("X", skin)).height(tree.getPadTop());
		tree.setPosition(5, 100);
		tree.defaults().spaceBottom(10);
		tree.row().fill().expandX();
		tree.add(newNode).row();
		tree.add(scroll).fill().expand().minWidth(200).minHeight(500);

		tree.pack();

		// Properties

		Table transformTable = new Table(skin);
		Label nameText = new Label("Name: ", skin);

		TextField name = new TextField("", skin);
		name.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped ( TextField textField, char c ) {
				if (selectedObject != null) {
					selectedObject.name = textField.getText();
					((Label) uiHierarchy.findNode(selectedObject).getActor()).setText(selectedObject.name);
				}
			}
		});
		name.setName("name");
		transformTable.add(nameText).padRight(10);
		transformTable.add(name).spaceBottom(15).row();

		// transformTable.add(new Label("Transformation", skin)
		// {{setFontScale(1.2f);}}).spaceBottom(15).row();

		// pos
		transformTable.add(new Label("Position", skin)).row();
		// x
		transformTable.add(new Label("X", skin));
		final TextField tx = (new TextField("", skin));
		tx.setName("positionX");
		tx.setMessageText("0");
		tx.setTextFieldFilter(new TextFieldFloatFilter());
		tx.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped ( TextField textField, char c ) {
				if (selectedObject != null)
					selectedObject.setPositionX(Float.parseFloat(tx.getText()));
			}
		});

		transformTable.add(tx).width(50).padRight(10).padLeft(10);
		// y
		transformTable.add(new Label("Y", skin));
		TextField ty = (new TextField("", skin));
		ty.setName("positionY");
		ty.setMessageText("0");
		transformTable.add(ty).width(50).padRight(20).padLeft(10);
		// z
		transformTable.add(new Label("Z", skin));
		TextField tz = (new TextField("", skin));
		tz.setName("positionZ");
		tz.setMessageText("0");
		transformTable.add(tz).width(50).padRight(20).padLeft(10);
		transformTable.row();

		// rot
		transformTable.add(new Label("Rotation", skin)).row();
		transformTable.add(new Label("X", skin));
		transformTable.add(new TextField("", skin) {
			{
				setMessageText("0");
			}
		}).width(50).padRight(20).padLeft(10);
		transformTable.add(new Label("Y", skin));
		transformTable.add(new TextField("", skin) {
			{
				setMessageText("0");
			}
		}).width(50).padRight(20).padLeft(10);
		transformTable.add(new Label("Z", skin));
		transformTable.add(new TextField("", skin) {
			{
				setMessageText("0");
			}
		}).width(50).padRight(20).padLeft(10);
		transformTable.row();

		// Scale
		transformTable.add(new Label("Scale", skin)).row();
		transformTable.add(new Label("X", skin));
		transformTable.add(new TextField("", skin) {
			{
				setMessageText("1");
			}
		}).width(50).padRight(20).padLeft(10);
		transformTable.add(new Label("Y", skin));
		transformTable.add(new TextField("", skin) {
			{
				setMessageText("1");
			}
		}).width(50).padRight(20).padLeft(10);
		transformTable.add(new Label("Z", skin));
		transformTable.add(new TextField("", skin) {
			{
				setMessageText("1");
			}
		}).width(50).padRight(20).padLeft(10);

		transformTable.row();

		assetsPicker = new SelectBox<Resource>(skin);
		assetsPicker.addListener(new ChangeListener() {
			@Override
			public void changed ( ChangeEvent event, Actor actor ) {
				if (selectedObject != null) {
					Resource picked = assetsPicker.getSelected();

					if (!picked.isNull()) {
						ModelInstance instance = new ModelInstance(assets.get(picked.getFullPath(), Model.class));
						selectedObject.setModel(instance);
					} else {
						selectedObject.setModel(null);
					}
					selectedObject.resource = picked;
				}
			}
		});
		updateAssetsPickerList();
		transformTable.add(assetsPicker);

		scroll = new ScrollPane(transformTable, skin);

		uiProperties = new Window("Properties", skin);
		// properties.defaults().padLeft(10);
		// properties.defaults().padRight(10);

		uiProperties.getButtonTable().add(new TextButton("X", skin)).height(uiProperties.getPadTop());
		uiProperties.setPosition(stage.getWidth() - uiProperties.getWidth(), 100);
		uiProperties.defaults().spaceBottom(10);
		// uiProperties.row().fill().expandX();
		uiProperties.add(scroll).expand().fill().padBottom(15);// .minHeight(500);
		uiProperties.pack();

		// MENU
		/*
		 * Window menuWindow = new Window("Menu", skin); MenuBar menu = new
		 * MenuBar(skin); ContextMenu cont = new ContextMenu();
		 * menu.addContextMenu(new TextButton("File",skin),cont, stage);
		 * 
		 * cont.addMenu(new BasicMenuItem("Test me",skin));
		 * 
		 * menuWindow.add(menu); menuWindow.pack();
		 */

		// stage.addActor(menu);
		// stage.addActor(new Button("Behind Window", skin));
		stage.addActor(tree);
		stage.addActor(uiProperties);
		// stage.addActor(menuWindow);
	}

	private void updateAssetsPickerList () {
		ArrayList<Resource> list = new ArrayList<Resource>();
		list.add(new Resource());
		list.addAll(currentMap.resources.values());

		Resource[] res = new Resource[list.size()];
		list.toArray(res);
		assetsPicker.setItems(res);
	}

	private void updatePropertiesWindow () {
		GameObject selectedObject = this.selectedObject;
		if (this.selectedObject == null) {
			selectedObject = new GameObject("");
			uiProperties.setVisible(false);
		} else {
			uiProperties.setVisible(true);
		}

		((TextField) uiProperties.findActor("name")).setText(selectedObject.name);

		Vector3 pos = new Vector3();
		selectedObject.getLocal().getTranslation(pos);

		((TextField) uiProperties.findActor("positionX")).setText(pos.x + "");
		((TextField) uiProperties.findActor("positionY")).setText(pos.y + "");
		((TextField) uiProperties.findActor("positionZ")).setText(pos.z + "");

		assetsPicker.setSelected(selectedObject.resource);
	}

	@Override
	public void render () {
		// if (assets.update()) {
		assets.update();
		update(Gdx.graphics.getDeltaTime());
		// }

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		world.render();

		stage.draw();
	}

	private void update ( float delta ) {
		world.update(delta);
		stage.act(delta);

		// updatePropertiesWindow();

		if (Gdx.input.isKeyJustPressed(Keys.ENTER))
			stage.unfocusAll();
	}

	@Override
	public void resize ( int width, int height ) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
		// stage.getViewport().update((int)stage.getWidth(),
		// (int)stage.getHeight(), true);
	}

	@Override
	public void dispose () {
		super.dispose();
		assets.dispose();
		stage.dispose();
		skin.dispose();
	}
}
