package robotcontroller;

import robotinterface.RobotController;
import robotinterface.Time;
import robotinterface.manipulator.ManipulatorResult;
import robotinterface.motors.Servo;
import robotinterface.motors.LinearMotor;


/**
* A robot controller for closed-chain tests.
* @author J&ouml;rg Roth (<a href="mailto:Joerg.Roth@Wireless-earth.org">Joerg.Roth@Wireless-earth.org</a>)
*/
public class TripodTest extends RobotController {

    private LinearMotor leg1;
    private LinearMotor leg2;
    private LinearMotor leg3;


@Override
    public void init() {
        leg1=manipulator.getLinearMotor(0);
        leg2=manipulator.getLinearMotor(1);
        leg3=manipulator.getLinearMotor(2);
    }


@Override
    public void run() throws Exception {
        leg1.setTargetLength(70);
        leg2.setTargetLength(60);
        leg3.setTargetLength(50);
        manipulator.waitCurrentMovementCompleted();
    }


@Override
    public void stop() throws Exception{
    }

}