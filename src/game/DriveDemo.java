package game;

import world.World;
import world.wp.DefaultBuilder;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

import car.*;
import car.ai.FollowWorldAI;

//TODO there is another appstate to try here called (base|basic?)appstate
public class DriveDemo extends DriveSimple {

	public DriveDemo (CarData car, World world) {
    	super(car, world);
    	
    	if (!(world instanceof DefaultBuilder)) {
    		System.exit(-1);
    	}
    }
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
    	super.initialize(stateManager, app);
    	
    	//remove all stuff we want and player from everything
    	
    	this.cb.removePlayer(0);
    	this.cb.addCar(0, car, world.getStartPos(), world.getStartRot(), true); //even though they aren't a player

    	MyPhysicsVehicle car = this.cb.get(0);
    	car.makeAI(new FollowWorldAI(car, (DefaultBuilder)world));
    	
    	uiNode.cleanup(); //TODO why?
    	app.getStateManager().detach(uiNode);
    	uiNode = new CarUI(cb.get(0));
		app.getStateManager().attach(uiNode);
		
		App.rally.getRootNode().detachChild(camera);
		app.getInputManager().removeRawInputListener(camera);
		camera = new CarCamera("Camera", App.rally.getCamera(), cb.get(0));
		App.rally.getRootNode().attachChild(camera);
		app.getInputManager().addRawInputListener(camera);
		
//		App.rally.getRootNode().detachChild(minimap.rootNode); //TODO causes crash
//		minimap = new MiniMap(cb.get(0));
	}
	
	public void update(float tpf) {
		super.update(tpf);
	}
}
