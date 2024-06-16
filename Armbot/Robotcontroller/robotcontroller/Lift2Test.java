package robotcontroller;

import robotinterface.RobotController;
import robotinterface.Time;
import robotinterface.manipulator.ManipulatorResult;
import robotinterface.motors.Servo;
import robotinterface.motors.LinearMotor;
import robotinterface.passives.AxisRotationPassive;


/**
* A robot controller for closed-chain tests.
* @author J&ouml;rg Roth (<a href="mailto:Joerg.Roth@Wireless-earth.org">Joerg.Roth@Wireless-earth.org</a>)
*/
public class Lift2Test extends RobotController {

    private LinearMotor linear;
    private AxisRotationPassive passive;


@Override
    public String getName() {
        return "Lift 2 Test";
    }


@Override
    public String getDescription() {
        return getName()+" (no configuration)";
    }


@Override
    public void init() {
        linear=manipulator.getLinearMotor(0);
        passive=manipulator.getAxisRotationPassive(0);
    }


@Override
    public void run() throws Exception {

        new Thread(()-> {
          while (isRunning()) {
               Time.sleep(500);
               debugOut.println("passive angle: "+Math.round(passive.getAngle()*180/Math.PI)+" \u00B0");
          }
        }).start();

        linear.setTargetLength(60);
        manipulator.waitCurrentMovementCompleted();
    }


@Override
    public void stop() throws Exception{
    }

}