package com.caresilabs.ase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class SceneEditor extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	PerspectiveCamera cam;
	FirstPersonCameraController c;

	private Stage stage;

	Object[] listEntries = { "This is a list entry1", "And another one1", "The meaning of life1", "Is hard to come by1",
			"This is a list entry2", "And another one2", "The meaning of life2", "Is hard to come by2", "This is a list entry3",
			"And another one3", "The meaning of life3", "Is hard to come by3", "This is a list entry4", "And another one4",
			"The meaning of life4", "Is hard to come by4", "This is a list entry5", "And another one5", "The meaning of life5",
			"Is hard to come by5" };

	Label fpsLabel;
	Skin skin;

	@Override
	public void create () {
		this.batch = new SpriteBatch();
		this.stage = new Stage(new ExtendViewport(1280, 720)); //

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

		Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin, "toggle");

		Label myLabel = new Label("this is some text.", skin);
		myLabel.setWrap(true);

		Table t = new Table();
		t.row();
		t.add(myLabel);

		t.layout();

		final CheckBox checkBox = new CheckBox(" Continuous rendering", skin);
		checkBox.setChecked(true);
		final Slider slider = new Slider(0, 10, 1, false, skin);
		slider.setAnimateDuration(0.3f);
		TextField textfield = new TextField("", skin);
		textfield.setMessageText("Click here!");
		textfield.setAlignment(Align.center);
		final SelectBox<String> dropdown = new SelectBox<String>(skin);
		dropdown.addListener(new ChangeListener() {
			public void changed ( ChangeEvent event, Actor actor ) {
				System.out.println(dropdown.getSelected());
			}
		});
		dropdown.setItems("Android1", "Windows1", "Linux1", "OSX1", "Android2", "Windows2", "Linux2", "OSX2", "Android3",
				"Windows3", "Linux3", "OSX3", "Android4", "Windows4", "Linux4", "OSX4", "Android5", "Windows5", "Linux5", "OSX5",
				"Android6", "Windows6", "Linux6", "OSX6", "Android7", "Windows7", "Linux7", "OSX7");
		dropdown.setSelected("Linux6");

		List<Object> list = new List<Object>(skin);
		list.setItems(listEntries);
		list.getSelection().setMultiple(true);
		list.getSelection().setRequired(false);
		// list.getSelection().setToggle(true);
		ScrollPane scrollPane2 = new ScrollPane(list, skin);
		scrollPane2.setFlickScroll(false);
		SplitPane splitPane = new SplitPane(scrollPane2, scrollPane2, false, skin, "default-horizontal");
		fpsLabel = new Label("fps:", skin);

		// configures an example of a TextField in password mode.
		final Label passwordLabel = new Label("Textfield in password mode: ", skin);
		final TextField passwordTextField = new TextField("", skin);
		passwordTextField.setMessageText("password");
		passwordTextField.setPasswordCharacter('*');
		passwordTextField.setPasswordMode(true);

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

				payload.setObject("Some payload!");

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
				// getActor().setColor(Color.GREEN);

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
		scroll.setScrollBarPositions(true, true);
		scroll.setWidth(10);

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
		window.row();
		window.add(fpsLabel).colspan(4);
		window.pack();

		// stage.addActor(new Button("Behind Window", skin));
		stage.addActor(window);

		// Old

		img = new Texture("badlogic.jpg");
		cam = new PerspectiveCamera(90, 16, 9); // Gdx.graphics.getWidth(),
												// Gdx.graphics.getHeight() );
		cam.position.z = 1;
		cam.near = 0;

		c = new FirstPersonCameraController(cam);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, c));
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(img, 0, 0, 1, 1);
		batch.end();

		stage.draw();
	}

	private void update ( float delta ) {
		c.update();
		stage.act(delta);

		fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
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
