package world;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

//stands for world piece simple, as in its a simple piece of the world
public enum Simple implements WP {
	
	CROSS("cross.blend", new Vector3f(2,0,0), WorldBuilder.STRIAGHT),
	STRAIGHT("straight.blend", new Vector3f(2,0,0), WorldBuilder.STRIAGHT),
	
	LEFT("left.blend", new Vector3f(1,0,-1), WorldBuilder.LEFT_90),
	LEFT_SHARP("left_sharp.blend", new Vector3f(1,0,-1), WorldBuilder.LEFT_90),
	LEFT_LONG("left_long.blend", new Vector3f(2,0,-2), WorldBuilder.LEFT_90),
	LEFT_CHICANE("left_chicane.blend", new Vector3f(2,0,-1), WorldBuilder.STRIAGHT),
	
	RIGHT("right.blend", new Vector3f(1,0,1), WorldBuilder.RIGHT_90),
	RIGHT_SHARP("right_sharp.blend", new Vector3f(1,0,1), WorldBuilder.RIGHT_90),
	RIGHT_LONG("right_long.blend", new Vector3f(2,0,2), WorldBuilder.RIGHT_90),
	RIGHT_CHICANE("right_chicane.blend", new Vector3f(2,0,1), WorldBuilder.STRIAGHT),
	
	HILL_UP("hill_up.blend", new Vector3f(4,0.5f,0), WorldBuilder.STRIAGHT),
	HILL_DOWN("hill_down.blend", new Vector3f(4,-0.5f,0), WorldBuilder.STRIAGHT),
	;
	
	private static String dir = "assets/wbsimple/";
	
	String name;
	Vector3f newPos; //what the piece does to the track
	Quaternion newRot; //change of angle (deg) for the next peice

	Simple(String s, Vector3f a, Quaternion g) {
		this.name = s;
		this.newPos = a;
		this.newRot = g;
	}
	public float getScale() { return 25; }
	public String getName() { return dir+name;}
	public Vector3f getNewPos() { return new Vector3f(newPos); }
	public Quaternion getNewAngle() { return new Quaternion(newRot); }
}