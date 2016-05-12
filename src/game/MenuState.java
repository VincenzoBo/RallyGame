package game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MenuState extends AbstractAppState implements ScreenController {

	private Node localRootNode = new Node("Pause Screen RootNode");
	private Node localGuiNode = new Node("Pause Screen GuiNode");
	
	public MenuState() {
		super();
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

	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		InputManager i = App.rally.getInputManager();
		i.addMapping("Pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
		i.addMapping("TabMenu", new KeyTrigger(KeyInput.KEY_TAB));
		i.addListener(actionListener, "Pause");
		i.addListener(actionListener, "TabMenu");
		
		Rally r = App.rally;
		r.getRootNode().attachChild(localRootNode);
		r.getGuiNode().attachChild(localGuiNode);
	}

	public void togglePause() {
		Screen cur = App.nifty.getCurrentScreen();
		if (cur.getScreenId().equals("drive-paused")) {
			//then un pause
			App.nifty.gotoScreen("drive-noop");
			App.rally.drive.setEnabled(true);
		} else {
			//then pause
			App.nifty.gotoScreen("drive-paused");
			App.rally.drive.setEnabled(false);
		}
	}
	public void toggleMenu() {
		Screen cur = App.nifty.getCurrentScreen();
		if (cur.getScreenId().equals("drive-pause")) return; //can't open the menu on the pause screen
		
		if (cur.getScreenId().equals("drive-tabmenu")) {
			App.nifty.gotoScreen("drive-noop");
		} else {
			App.nifty.gotoScreen("drive-tabmenu");
		}
	}
	
	public void mainMenu() {
		//TODO, basically reset everything, detach all children and the sun and other things..
		//rootnode.detachAllChildren();
	}

	public void update(float tpf) {
		super.update(tpf);
		//useless i know..
	}
	
	@Override
	public void cleanup() {
		Rally r = App.rally;
		r.getRootNode().detachChild(localRootNode);
		r.getGuiNode().detachChild(localGuiNode);
	}

	public void bind(Nifty arg0, Screen arg1) { }
	public void onEndScreen() { }
	public void onStartScreen() { }

	//////////////////
	//test
	public String speed() {
		MyPhysicsVehicle car = App.rally.drive.cb.get(0);
		if (car == null) return "0";
		return car.getLinearVelocity().length()+"";
	}
}
