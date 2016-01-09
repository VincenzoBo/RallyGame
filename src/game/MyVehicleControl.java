package game;

import java.util.LinkedList;
import java.util.List;

import com.bulletphysics.dynamics.vehicle.WheelInfo;
import com.bulletphysics.dynamics.vehicle.WheelInfo.RaycastInfo;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class MyVehicleControl extends VehicleControl implements ActionListener {
	
	//skid stuff
	Node skidNode = new Node();
	LinkedList<Spatial> skidList = new LinkedList<Spatial>();
	
	//directions
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	Vector3f left = new Vector3f();
	
	//admin stuff
	AssetManager assetManager;
	Rally rally;
	
	//car data
	Car car;
	
	MyWheelNode[] wheel = new MyWheelNode[4];
	
	//driving stuff
	boolean togglePhys = false;
	boolean ifSmoke = false;

	int curGear = 1;
	int curRPM = 0;
	
	boolean ifAccel = false;
	float accelCurrent = 1;
	
	float steeringCurrent = 0;
	boolean steerLeft = false;
	boolean steerRight= false;
	
	float brakeCurrent = 0;
	
	boolean ifHandbrake = false;
	//- driving stuff
	
	float distance = 0;
	boolean ifLookBack = false;
	
	double t1, t2;
	
	MyVehicleControl(CollisionShape col, Car cartype, Node carNode, Rally rally) {
		super(col, cartype.mass);
		this.car = cartype;
		this.rally = rally;
		this.assetManager = rally.getAssetManager();
		
		this.setSuspensionCompression(car.susCompression);
		this.setSuspensionDamping(car.susDamping);
		this.setSuspensionStiffness(car.stiffness);
		this.setMaxSuspensionForce(car.maxSusForce);
		this.setMaxSuspensionTravelCm(car.maxSusTravel);
		
		
		wheel[0] = new MyWheelNode("wheel 0 node", this, 0);
		Spatial wheel0 = assetManager.loadModel(car.wheelModel);
		wheel0.center();
		wheel[0].attachChild(wheel0);
		addWheel(wheel[0], new Vector3f(-car.w_xOff, car.w_yOff, car.w_zOff),
				car.wheelDirection, car.wheelAxle, car.restLength, car.wheelRadius, true);

		wheel[1] = new MyWheelNode("wheel 1 node", this, 1);
		Spatial wheel1 = assetManager.loadModel(car.wheelModel);
		wheel1.rotate(0, FastMath.PI, 0);
		wheel1.center();
		wheel[1].attachChild(wheel1);
		addWheel(wheel[1], new Vector3f(car.w_xOff, car.w_yOff, car.w_zOff),
				car.wheelDirection, car.wheelAxle, car.restLength, car.wheelRadius, true);

		wheel[2] = new MyWheelNode("wheel 2 node", this, 2);
		Spatial wheel2 = assetManager.loadModel(car.wheelModel);
		wheel2.center();
		wheel[2].attachChild(wheel2);
		addWheel(wheel[2], new Vector3f(-car.w_xOff-0.05f, car.w_yOff, -car.w_zOff),
				car.wheelDirection, car.wheelAxle, car.restLength, car.wheelRadius, false);

		wheel[3] = new MyWheelNode("wheel 3 node", this, 3);
		Spatial wheel3 = assetManager.loadModel(car.wheelModel);
		wheel3.rotate(0, FastMath.PI, 0);
		wheel3.center();
		wheel[3].attachChild(wheel3);
		addWheel(wheel[3], new Vector3f(car.w_xOff+0.05f, car.w_yOff, -car.w_zOff),
				car.wheelDirection, car.wheelAxle, car.restLength, car.wheelRadius, false);

		//Friction
		setFrictionSlip(0, car.wheel0Slip);
		setFrictionSlip(1, car.wheel1Slip);
		setFrictionSlip(2, car.wheel2Slip);
		setFrictionSlip(3, car.wheel3Slip);
		
		
		for (MyWheelNode w: wheel) {
			//attaching all the things (wheels)
			carNode.attachChild(w);
			makeSmoke(w);
		}
		
		////////////////////////
		setupKeys();
//		skidNode.setShadowMode(ShadowMode.Off);
	}
	
	private void makeSmoke(MyWheelNode w) {
	    if (!ifSmoke) {
	    	return;
	    }
		
		w.smoke = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 40);
		w.smoke.setParticlesPerSec(10);
		
		w.smoke.setImagesX(15); //the smoke image is 15x * 1y (y is already the default of 1)
		w.smoke.setEndColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 0.3f));
		w.smoke.setStartColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 0f));

		w.smoke.setStartSize(0.1f);
		w.smoke.setEndSize(4f);
		w.smoke.setLowLife(4f);
		w.smoke.setHighLife(4f);
		w.smoke.getParticleInfluencer().setVelocityVariation(0.05f);
	    
	    Material emit = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
	    emit.setTexture("Texture", assetManager.loadTexture("Effects/Smoke/Smoke.png"));
	    w.smoke.setMaterial(emit);
    	w.attachChild(w.smoke);
	}
	
	//controls
	private void setupKeys() {
		rally.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
		rally.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
		rally.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
		rally.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
		rally.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_Q));
		rally.getInputManager().addMapping("Handbrake", new KeyTrigger(KeyInput.KEY_SPACE));
		rally.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
		rally.getInputManager().addMapping("Physics", new KeyTrigger(KeyInput.KEY_LCONTROL));
		rally.getInputManager().addMapping("LookBack", new KeyTrigger(KeyInput.KEY_Z));
		rally.getInputManager().addMapping("Reverse", new KeyTrigger(KeyInput.KEY_LSHIFT));
		
		rally.getInputManager().addListener(this, "Left");
		rally.getInputManager().addListener(this, "Right");
		rally.getInputManager().addListener(this, "Up");
		rally.getInputManager().addListener(this, "Down");
		rally.getInputManager().addListener(this, "Jump");
		rally.getInputManager().addListener(this, "Handbrake");
		rally.getInputManager().addListener(this, "Reset");
		rally.getInputManager().addListener(this, "Physics");
		rally.getInputManager().addListener(this, "LookBack");
		rally.getInputManager().addListener(this, "Reverse");
		
		//TODO use the Controller class
	}
	
	public void onAction(String binding, boolean value, float tpf) {
		//value == 'if pressed'
		//value = value of press, with keys its always 0 or 1
		//controllers on the otherhand...
		
		if (binding.equals("Left")) {
			steerLeft = value;
		} 
		if (binding.equals("Right")) {
			steerRight= value;
		}
		
		if (binding.equals("Up")) {
			if (value) {
				ifAccel = true;
			} else {
				ifAccel = false;
			}

		} 
		if (binding.equals("Down")) {
			if (value) {
				brakeCurrent += car.MAX_BRAKE;
			} else {
				brakeCurrent -= car.MAX_BRAKE;
			}
			
		} 
		if (binding.equals("Jump")) {
			if (value) {
				applyImpulse(car.JUMP_FORCE, new Vector3f(0,0,0)); //push up
				Vector3f old = getPhysicsLocation();
				old.y += 2; //and move up
				setPhysicsLocation(old);
			}
			
		} 
		if (binding.equals("Handbrake")) {
			ifHandbrake = !ifHandbrake;
			
		} 
		if (binding.equals("Physics")) {
			if (value) {
				togglePhys = !togglePhys;
				H.p("physics = "+!togglePhys);
			}
		} 
		if (binding.equals("Reset")) {
			if (value) {
				reset();
			} else {
			}
		} 
		if (binding.equals("Reverse")) {
			if (curGear == 1) { //i.e. first gear
				curGear = 0;
			} else {
				curGear = 1;
			}
		}
		if (binding.equals("LookBack")) {
			if (value) {
				ifLookBack = true;
			} else {
				ifLookBack = false;
			}
		}
	}
	//end controls
	
	
	//TODO Things taken out:
	//- handbrake
	//- the graphical grip values
	
	
	////////////////////////////////////////////////////
	
	private void specialPhysics(float tpf) {
		if (togglePhys){ return; }//no need to apply wheel forces now
		
		//NOTE: that z is forward, x is side
		// - the reference notes say that x is forward and y is sideways so just be careful
		
		Matrix3f w_angle = getPhysicsRotationMatrix();
		Vector3f w_velocity = getLinearVelocity();
		
		//* Linear Accelerations: = player.car.length * player.car.yawrate (in rad/sec)
		double yawspeed = car.length * getAngularVelocity().y;
		
		Vector3f velocity = w_angle.invert().mult(w_velocity);
		
		float steeringCur = steeringCurrent;
		if (velocity.z < 0) { //need to flip the steering on moving in reverse
			steeringCur *= -1;
		}

		if (velocity.z == 0) { //to avoid the divide by zero below
			velocity.z += 0.001;
		}
		//important angles
		double slipanglefront = Math.atan((velocity.x + yawspeed) / Math.abs(velocity.z)) - steeringCur;
		double slipanglerear = Math.atan((velocity.x - yawspeed) / Math.abs(velocity.z));
		
		//decay these values at slow speeds (< 2 m/s)
		//TODO this kind of breaks slow speed turning
		if (Math.abs(velocity.z) < 2) {
			slipanglefront *= velocity.z*velocity.z/4; //4 because 2*2
			slipanglerear *= velocity.z*velocity.z/4;
		}

		//////////////////////////////////////////////////
		//'Wheel'less forces (TODO braking in wheels)
		float braking = 0;
		if (wheel[0].contact || wheel[1].contact || wheel[2].contact || wheel[3].contact) {
			braking = -brakeCurrent*FastMath.sign(velocity.z);
		}
		Vector3f brakeVec = new Vector3f(w_velocity.normalize()).mult(braking);
		
		//linear resistance and quadratic drag
		float dragz = (float)(-(car.RESISTANCE * velocity.z + car.DRAG * velocity.z * Math.abs(velocity.z)));
		float dragx = (float)(-(car.RESISTANCE * velocity.x + car.DRAG * velocity.x * Math.abs(velocity.x)));
		float dragy = (float)(-(car.RESISTANCE * velocity.y + car.DRAG * velocity.y * Math.abs(velocity.y)));

		Vector3f totalNeutral = new Vector3f(dragx, dragy, dragz);
		applyCentralForce(w_angle.mult(totalNeutral).add(brakeVec)); //non wheel based forces on the car
		
		//////////////////////////////////
		double weightperwheel = car.mass*(-getGravity().y/1000)*0.25; //0.25 because its per wheel
		
		Vector3f fl = new Vector3f(); //front left
		Vector3f fr = new Vector3f(); //front right
		Vector3f rl = new Vector3f(); //rear  left
		Vector3f rr = new Vector3f(); //rear  right
		
		//latitudinal forces that are calculated off the slip angle
		fl.x = fr.x = (float)(slipanglefront * car.CA_F*weightperwheel);
		rl.x = rr.x = (float)(slipanglerear * car.CA_R*weightperwheel);

		//////////////////////////////////////
		//longitudinal forces
		float accel = 0;
		if (ifAccel) {
			accel = getEngineWheelForce(velocity.z)*accelCurrent;
//			totalaccel = car.MAX_ACCEL; //remove for 'geared' accel
//			totalaccel = FastMath.clamp(accel, (float)-weightperwheel*car.MAX_GRIP, (float)weightperwheel*car.MAX_GRIP);
		}

		accel /= 2; //because 2 wheels an axle
		if (car.driveFront && car.driveRear) { 
			accel /= 2; //if its split up between the front and rear axle
		}
			
		if (car.driveFront) {
			fl.z = accel;
			fr.z = accel;
		}
		fl.x = FastMath.cos(steeringCurrent)*fl.x;
		fr.x = FastMath.cos(steeringCurrent)*fr.x;
		
		if (car.driveRear) {
			rl.z = accel;
			rr.z = accel;
		}
		
		Vector3f wfl = new Vector3f(H.clamp(fl,weightperwheel*car.MAX_GRIP));
		Vector3f wfr = new Vector3f(H.clamp(fr,weightperwheel*car.MAX_GRIP));
		Vector3f wrl = new Vector3f(H.clamp(rl,weightperwheel*car.MAX_GRIP));
		Vector3f wrr = new Vector3f(H.clamp(rr,weightperwheel*car.MAX_GRIP));
		
		//and finally apply forces
		if (wheel[0].contact) 
			applyForce(w_angle.mult(wfl), w_angle.mult(wheel[0].getForceLocation(car.wheelRadius, car.rollFraction)));
		if (wheel[1].contact) 
			applyForce(w_angle.mult(wfr), w_angle.mult(wheel[1].getForceLocation(car.wheelRadius, car.rollFraction)));
		if (wheel[2].contact) 
			applyForce(w_angle.mult(wrl), w_angle.mult(wheel[2].getForceLocation(car.wheelRadius, car.rollFraction)));
		if (wheel[3].contact) 
			applyForce(w_angle.mult(wrr), w_angle.mult(wheel[3].getForceLocation(car.wheelRadius, car.rollFraction)));
		
	}
	
	private float getEngineWheelForce(float speedForward) {
		float wheelrot = speedForward/(2*FastMath.PI*car.wheelRadius); //w = v/(2*Pi*r) -> rad/sec
		
		float curGearRatio = car.gearRatios[curGear];//0 = reverse, >= 1 normal make sense
		float diffRatio = car.diffRatio;
		curRPM = (int)(wheelrot*curGearRatio*diffRatio*60); //rad/sec to rad/min and the drive ratios to engine
			//wheel rad/s, gearratio, diffratio, conversion from rad/sec to rad/min
		autoTransmission(curRPM);
		
		float engineTorque = lerpTorque(curRPM);
		float driveTorque = engineTorque*curGearRatio*diffRatio*car.transEffic;
		
		float totalTorque = driveTorque/car.wheelRadius;
		return totalTorque;
	}
	
	private void autoTransmission(int rpm) {
		if (rpm > car.gearUp && curGear < car.gearRatios.length-1) {
			curGear++;
		} else if (rpm < car.gearDown && curGear > 1) {
			curGear--;
		}
	}
	
	private float lerpTorque(int rpm) {
		if (rpm < 1000) rpm = 1000; //prevent stall values
		float RPM = (float)rpm / 1000;
		return H.lerpArray(RPM, car.torque);
	}
	
	//////////////////////////////////////////////////////////////
	public void myUpdate(float tpf) {
		distance += getLinearVelocity().length()*tpf;
		
		for (MyWheelNode w: wheel) {
			WheelInfo wi = getWheel(w.num).getWheelInfo();
			RaycastInfo ray = wi.raycastInfo;
			w.contact = (ray.groundObject != null);
		}
		
		specialPhysics(tpf); //yay 
		
		//skid marks
		rally.frameCount++;
		if (rally.frameCount % 4 == 0) {
			addSkidLines();
		}
				
		Matrix3f playerRot = new Matrix3f();
		getPhysicsRotationMatrix(playerRot);
		
		left = playerRot.mult(new Vector3f(1,0,0));
		right = playerRot.mult(new Vector3f(1,0,0).negate());

		//wheel turning logic -TODO
		steeringCurrent = 0;
		float speedFactor = 1;
//		speedFactor = car.steerFactor/(getLinearVelocity().length()*10);
		if (steerLeft) { //left
			steeringCurrent += car.steerAngle*speedFactor;
		}
		if (steerRight) { //right
			steeringCurrent -= car.steerAngle*speedFactor;
		}
		steer(steeringCurrent);
		H.pUI(steeringCurrent);
	}
	
	///////////////////////////////////////////////////////////
	private void addSkidLines() {
		for (MyWheelNode w: wheel) {
			w.addSkidLine();
		}
		
		int extra = skidList.size() - 500; //so i can remove more than one (like all 4 that frame)
		for (int i = 0; i < extra; i++) {
			skidNode.detachChild(skidList.getFirst());
			skidList.removeFirst();
		}
	}
	

	private void reset() {
		setPhysicsRotation(new Matrix3f());
		setLinearVelocity(new Vector3f(0,0,0));
		setAngularVelocity(new Vector3f(0,0,0));
		resetSuspension();
		
		rally.arrowNode.detachAllChildren();

		if (rally.dynamicWorld) {
			//TODO wow this is a mess
			List<Spatial> ne = new LinkedList<Spatial>(rally.worldB.pieces);
			for (Spatial s: ne) {
				rally.getPhysicsSpace().remove(s.getControl(0));
				rally.worldB.detachChild(s);
				rally.worldB.pieces.remove(s);
			}
			rally.worldB.start = new Vector3f(0,0,0);
			rally.worldB.nextPos = new Vector3f(0,0,0);
			rally.worldB.nextRot = new Quaternion();
			
			setPhysicsLocation(rally.worldB.start);
			Matrix3f p = new Matrix3f();
			p.fromAngleAxis(FastMath.DEG_TO_RAD*90, new Vector3f(0,1,0));
			setPhysicsRotation(p);

			setAngularVelocity(new Vector3f());
		} else {
			setPhysicsLocation(rally.world.start);
		}
		
		skidNode.detachAllChildren();
		skidList.clear();
		for (MyWheelNode w: wheel) {
			w.last = new Vector3f(0,0,0);
		}
	}
}
