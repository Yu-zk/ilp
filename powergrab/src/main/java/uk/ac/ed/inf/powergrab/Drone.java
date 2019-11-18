package uk.ac.ed.inf.powergrab;

public class Drone {
	protected double latitude;
	protected double longitude;
	protected double coins;
    protected double power;
    protected double setPower(double power) {
		double powerBefore = this.power;
		if (this.power - power <= 0) {
			this.power = 0;
			return -powerBefore;
		}else {
			this.power = this.power + power;
			return -power;
		}
	}

}
