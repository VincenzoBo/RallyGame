package world.wp;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public enum City implements WP {
	CROSS("cross.blend", new Vector3f(2,0,0), WP.STRIAGHT),
	STRAIGHT("straight.blend", new Vector3f(3,0,0), WP.STRIAGHT),
	
	LEFT("left.blend", new Vector3f(1,0,-1), WP.LEFT_90),
	LEFT_LONG("left_long.blend", new Vector3f(2,0,-2), WP.LEFT_90),
	LEFT_CHICANE("left_chicane.blend", new Vector3f(2,0,-1), WP.STRIAGHT),
	
	RIGHT("right.blend", new Vector3f(1,0,1), WP.RIGHT_90),
	RIGHT_LONG("right_long.blend", new Vector3f(2,0,2), WP.RIGHT_90),
	RIGHT_CHICANE("right_chicane.blend", new Vector3f(2,0,1), WP.STRIAGHT),
	
	HILL_UP("hill_up.blend", new Vector3f(4,0.5f,0), WP.STRIAGHT),
	HILL_DOWN("hill_down.blend", new Vector3f(4,-0.5f,0), WP.STRIAGHT),
	;
	
	private static String dir = "assets/wb/city/";
	
	String name;
	Vector3f newPos; //what the piece does to the track
	Quaternion newRot; //change of angle (deg) for the next piece
	NodeType startNode;
	NodeType endNode;

	City(String s, Vector3f a, Quaternion g) {
		this.name = s;
		this.newPos = a;
		this.newRot = g;
		this.startNode = NodeType.A;
		this.endNode = NodeType.A;
	}
	public float getScale() { return 25; }
	public boolean needsMaterial() { return true; }
	
	public String getName() { return dir+name; }
	public Vector3f getNewPos() { return new Vector3f(newPos); }
	public Quaternion getNewAngle() { return new Quaternion(newRot); }
	
	public NodeType startNode() {
		return startNode;
	}
	public NodeType endNode() {
		return endNode;
	}
	
	static class Builder extends DefaultBuilder {
		Builder() {
			super(City.values());
		}
		
		public DefaultBuilder copy() {
			return new Builder();
		}
	}
}
