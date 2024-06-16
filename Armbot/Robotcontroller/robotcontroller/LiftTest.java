package robotcontroller;

import robotinterface.RobotController;
import robotinterface.Time;
import robotinterface.manipulator.ManipulatorResult;
import robotinterface.motors.Servo;
import robotinterface.motors.LinearMotor;
import robotinterface.passives.LinearPassive;


/**
* A robot controller for closed-chain tests.
* @author J&ouml;rg Roth (<a href="mailto:Joerg.Roth@Wireless-earth.org">Joerg.Roth@Wireless-earth.org</a>)
*/
public class LiftTest extends RobotController {

    private LinearMotor linear;
    private LinearPassive passive;


@Override
    public String getName() {
        return "Lift Test";
    }


@Override
    public String getDescription() {
        return getName()+" (no configuration)";
    }


@Override
    public void init() {
        linear=manipulator.getLinearMotor(0);
        passive=manipulator.getLinearPassive(0);
    }


@Override
    public void run() throws Exception {

        new Thread(()-> {
          while (isRunning()) {
               Time.sleep(500);
               debugOut.println("passive len: "+Math.round(passive.getLength())+" cm");
          }
        }).start();


        linear.setTargetLength(60);
        manipulator.waitCurrentMovementCompleted();
    }


@Override
    public void stop() throws Exception{
    }

}