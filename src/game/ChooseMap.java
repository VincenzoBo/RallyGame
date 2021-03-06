package game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;

import helper.H;
import world.*;
import world.highway.HighwayWorld;
import world.curve.CurveWorld;
import world.wp.WP.DynamicType;

public class ChooseMap extends AbstractAppState {

	private static WorldType worldType = WorldType.NONE;
	private static World world = null;
	
	private BasicCamera camera;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		App.rally.bullet.setEnabled(true);
		
		camera = new BasicCamera("Camera", App.rally.getCamera(), new Vector3f(-70,50,0), new Vector3f(20,1,0));
		App.rally.getRootNode().attachChild(camera);
		
		//init gui
		Container myWindow = new Container();
		App.rally.getGuiNode().attachChild(myWindow);
		myWindow.setLocalTranslation(H.screenTopLeft());
		
		//these values are not x and y because they are causing confusion
		int i = 0;
		int j = 0;
		myWindow.addChild(new Label("Choose Map"), j, i);
		j++;
		myWindow.addChild(new Label("Static"), j, i);
		j++;
		for (StaticWorld s : StaticWorld.values()) {
			addButton(myWindow, WorldType.STATIC, s.name(), j, i);
			i++;
		}
		i = 0;
		j++;
		myWindow.addChild(new Label("Dynamic"), j, i);
		j++;
		for (DynamicType d : DynamicType.values()) {
			addButton(myWindow, WorldType.DYNAMIC, d.name(), j, i);
			i++;
		}
		i = 0;
		j++;
		myWindow.addChild(new Label("Other"), j, i);
		j++;
		for (WorldType t: WorldType.values()) {
			if (t != WorldType.STATIC && t != WorldType.DYNAMIC && t != WorldType.NONE) {
				addButton(myWindow, t, t.name(), j, i);
				i++;
			}
		}
		i = 0;
		j++;
		Button button = myWindow.addChild(new Button("Choose"), j, i);
		button.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
            	if (worldType == WorldType.NONE)
            		return; //do not select it
            	App.rally.getGuiNode().detachChild(myWindow);
            	chooseMap();
            }
        });
	}
	
	@SuppressWarnings("unchecked")
	private void addButton(Container myWindow, WorldType world, String s, int j, int i) {
		Button button = myWindow.addChild(new Button(s), j, i);
		button.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
            	setWorld(world.name(), s);
            }
        });
	}

	public void update(float tpf) {
		if (!isEnabled()) return;
		super.update(tpf);
	}

	public void cleanup() {
		App.rally.getRootNode().detachChild(camera);
		App.rally.getStateManager().detach(world);
	}

	////////////////////////
	//UI stuff
	public void chooseMap() {
		if (world == null) { H.e("no return value for ChooseMap()"); }
		App.rally.next(this);
	}
	public World getWorld() {
		World newWorld = null;
		try {
			newWorld = world.copy();
			App.rally.getStateManager().detach(world);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
		world = null;
		return newWorld;
	}

	public void setWorld(String typeStr, String subType) {
		if (world != null && world.isInitialized()) {
			App.rally.getStateManager().detach(world);
			world = null;
		}
		worldType = WorldType.valueOf(WorldType.class, typeStr);
		
		switch (worldType) {
			case STATIC:
				StaticWorld sworld = StaticWorld.valueOf(StaticWorld.class, subType);
				world = new StaticWorldBuilder(sworld);
				break;
			case DYNAMIC:
				DynamicType dworld = DynamicType.valueOf(DynamicType.class, subType);
				world = dworld.getBuilder();
				break;
			case OBJECT:
				world = new ObjectWorld();
				break;
			case FULLCITY:
				world = new FullCityWorld();
				break;
			case CURVE:
				world = new CurveWorld();
				break;
			case HIGHWAY:
				world = new HighwayWorld();
				break;
				
			default:
				H.e("Non valid world type in ChooseMap.setWorld() method");
				return;
		}
		
		App.rally.getStateManager().attach(world);
	}
}
