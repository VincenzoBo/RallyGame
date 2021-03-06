package car.ai;

import car.MyPhysicsVehicle;

public abstract class CarAI {
	
	protected MyPhysicsVehicle car;

	public CarAI(MyPhysicsVehicle car) {
		this.car = car;
	}
	
	public abstract void update(float tpf);
}
