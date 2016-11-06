package world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import game.App;

public class BasicWorld implements World {
	private boolean isInit;
	
	private Node rootNode;
	private PhysicsSpace phys;
	
	public BasicWorld() {
		rootNode = new Node("basicWorldRoot");	
	}
	
	@Override
	public WorldType getType() {
		return WorldType.BASIC;
	}
	
	@Override
	public boolean isInit() {
		return isInit;
	}
	
	@Override
	public Node init(PhysicsSpace space, ViewPort view) {
		isInit = true;
		phys = space;
		
		AssetManager am = App.rally.getAssetManager();
		
		Material matfloor = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		matfloor.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		matfloor.setColor("Color", ColorRGBA.Green);
		
		Box start = new Box(10, 0.25f, 10); //Something to spawn on (or in hint hint)
		Geometry startGeometry = new Geometry("Starting Box", start);
		startGeometry.setMaterial(matfloor);
		startGeometry.setLocalTranslation(0, -0.1f, 0);
		startGeometry.addControl(new RigidBodyControl(0));
		
		this.rootNode.attachChild(startGeometry);
		this.phys.add(startGeometry);
		
		return rootNode;
	}
	
	@Override
	public Node getRootNode() {
		return rootNode;
	}
	
	
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public void cleanup() {
		isInit = false;
	}

	@Override
	public Vector3f getNextPieceClosestTo(Vector3f pos) {
		return null;
	}

	@Override
	public Vector3f getWorldStart() {
		return new Vector3f();
	}
	@Override
	public Matrix3f getWorldRot() {
		return new Matrix3f(Matrix3f.IDENTITY);
	}

	@Override
	public void update(float tpf, Vector3f playerPos, boolean force) {
		// TODO Auto-generated method stub
		
	}
}
