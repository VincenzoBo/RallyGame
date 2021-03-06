package game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;

import helper.H;

public class DriveMenu extends AbstractAppState {

	//GUI objects
	Container pauseMenu;
	Container infoHint;
	Container info;
	
	DriveSimple drive;
	
	public DriveMenu(DriveSimple drive) {
		super();
		this.drive = drive;
	}

	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (keyPressed) return; 
			if (name.equals("Pause")) {
				togglePause();
			}
			if (name.equals("TabMenu")) {
				toggleMenu();
			}
		}
	};

	@SuppressWarnings("unchecked")
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		InputManager i = App.rally.getInputManager();
		
		i.addMapping("Pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
		i.addMapping("TabMenu", new KeyTrigger(KeyInput.KEY_TAB));
		
		i.addListener(actionListener, "Pause");
		i.addListener(actionListener, "TabMenu");
		
		//init gui
		pauseMenu = new Container();
		Button button = pauseMenu.addChild(new Button("UnPause"));
		button.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
            	togglePause();
            }
        });
		
		Button button2 = pauseMenu.addChild(new Button("MainMenu"));
		button2.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
            	mainMenu();
            	App.rally.getGuiNode().detachChild(pauseMenu);
            }
        });
		pauseMenu.setLocalTranslation(H.screenMiddle().add(pauseMenu.getPreferredSize().mult(-0.5f)));
		
		infoHint = new Container();
		infoHint.attachChild(new Label("TAB for info"));
		infoHint.setLocalTranslation(H.screenTopLeft());
		App.rally.getGuiNode().attachChild(infoHint);
		
		info = new Container();
		info.attachChild(new Label("Controls: move: wasd and arrows , flip: f, handbrake: space, reverse: leftshift, camera: e,z, tab: this, pause: esc, reset: enter, jump: q, nitro: leftcontrol, telemetry: home"));
		info.setLocalTranslation(H.screenTopLeft());
	}

	public void togglePause() {
		Node guiRoot = App.rally.getGuiNode();
		if (guiRoot.hasChild(pauseMenu)) {
			guiRoot.detachChild(pauseMenu);
            App.rally.drive.setEnabled(true);
		} else {
			guiRoot.attachChild(pauseMenu);
			App.rally.drive.setEnabled(false);
		}
	}
	public void toggleMenu() {
		Node guiRoot = App.rally.getGuiNode();
		if (guiRoot.hasChild(info)) {
			guiRoot.attachChild(infoHint);
			guiRoot.detachChild(info);
		} else {
			guiRoot.attachChild(info);
			guiRoot.detachChild(infoHint);
		}
	}
	
	
	public void mainMenu() {
		drive.next();
	}

	public void update(float tpf) {
		super.update(tpf);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		InputManager i = App.rally.getInputManager();
		i.deleteMapping("Pause");
		i.deleteMapping("TabMenu");
		
		i.removeListener(actionListener);
	}
}
