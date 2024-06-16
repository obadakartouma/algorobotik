package robotcontroller;

import robotinterface.RobotController;
import robotinterface.Time;
import robotinterface.manipulator.ManipulatorResult;
import robotinterface.motors.Motor;
import robotinterface.motors.Servo;
import robotinterface.motors.LinearMotor;
import robotinterface.cmd.CommandListener;
import robotinterface.debug.DebugPainterOverlay3D;
import robotinterface.dirkin.DirectKinematics;
import basics.math.rot.RotoTranslation;
// import manipulators.*;

import basics.math.Matrix;


/**
 * A robot controller for basic tests.
 * @author J&ouml;rg Roth (<a href="mailto:Joerg.Roth@Wireless-earth.org">Joerg.Roth@Wireless-earth.org</a>)
 */
public class Test extends RobotController implements CommandListener {

    private final static double BALL1_X=40;
    private final static double BALL1_Y=-40;

    private final static double BALL2_X=60;
    private final static double BALL2_Y=20;

    private final static double RELEASE_BALL_HEIGHT=70;
    private final static double ABOVE_BALL_HEIGHT=50;
    private final static double GRIP_BALL_HEIGHT=30;

    private final static double GRIPPER_XY=0*Math.PI/180;
    private final static double GRIPPER_XZ=-90*Math.PI/180;
    private final static double GRIPPER_YZ=45*Math.PI/180;


    private final static double GRIPPER_XY2=0*Math.PI/180;
    private final static double GRIPPER_XZ2=0*Math.PI/180;
    private final static double GRIPPER_YZ2=0*Math.PI/180;


    private double[] drawLineOld=null; // Nur für DebugPainter3D: alte Position, damit man von dort aus einen Pfeil zur neuen Position malen kann





    @Override
    public String getName() {
        return "Test Controller";
    }


    @Override
    public String getDescription() {
        return getName()+" (no configuration)";
    }


    @Override
    public void init() {
        DebugPainterOverlay3D ovlAxes=debugPainter3D.getOverlay3D("Axes");
        ovlAxes.drawXYZAxis(0,0,0,0,100);
        ovlAxes.drawDot(5,5,5,0,0,0,1);
        ovlAxes.paint();
    }


    @Override
    public void run() throws Exception {

        if (instanceNumber==0)
            commander.registerCommandListener(this);

        debugOut.println("Manipulator "+manipulator.getDescription()+", location ("+Matrix.dumpVectorToString(manipulator.worldPosition(),"0.0")+"), orientation "+Matrix.double2String(manipulator.worldOrientation()*180/Math.PI,"0.0")+"\u00B0");

        // Alle 1s die Gripper-Position visualisieren - das ist notwendig um die direkte Kinematik-Funktion zu testen
        new Thread(()->{
            while (isRunning()) {
                Time.sleep(100);
                drawDebugPainterGripper();
                // Motor[] motors = manipulator.getMotors();
                double[] jointvalues = new double[8];
                /* for (Motor m : motors) {
                    jointvalues[0] = m.getCurrentValue();
                    System.out.println("Motors: " + m.getDescription() + " , ID: " + m.getID());
                    System.out.println("Infos: " + manipulator.getServo(m.getID()).getCurrentServoAngle());
                } */
                // double[] jointvalues = manipulator.jointValues();
                for (int i = 0; i < 8; i++) {
                    jointvalues[i] = manipulator.getServo(i).getCurrentServoAngle();
                    System.out.println("Servo " + i + ": " + jointvalues[i] + "    " + manipulator.getServo(i).getDescription());
                }
                double[][] transformationMatrix = DirectKinematic.calculateEndEffectorPose(
                        jointvalues, 60, 50, 25, 25, 10, 15
                );
                double[] pos = {
                        transformationMatrix[0][3],
                        transformationMatrix[1][3],
                        transformationMatrix[2][3]
                };
                double[] realpos = manipulator.currentWristPosition();
                System.out.println("berechnete Postion ist: " + pos[0] + " " + pos[1] + " " + pos[2]);
                System.out.println("Postion ist: " + realpos[0] + " " + realpos[1] + " " + realpos[2]);
                DebugPainterOverlay3D  ovlGripperPos=debugPainter3D.getOverlay3D("Gripper Position1");
                ovlGripperPos.clear();
                double[][] rot = {
                        {transformationMatrix[0][0], transformationMatrix[0][1], transformationMatrix[0][2]},
                        {transformationMatrix[1][0], transformationMatrix[1][1], transformationMatrix[1][2]},
                        {transformationMatrix[2][0], transformationMatrix[2][1], transformationMatrix[2][2]}
                };
                ovlGripperPos.drawChar('X', pos[0], pos[1], pos[2], 10, rot, 165, 42, 42, 255);
                ovlGripperPos.drawDot(pos[0], pos[1], pos[2], 162, 42, 42, 1);
                ovlGripperPos.paint();

            }
        }).start();

        while (isRunning()) {

            if (!moveGripper(BALL1_X,BALL1_Y,ABOVE_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;

            if (!moveGripperLinear(BALL1_X,BALL1_Y,GRIP_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;
            closeGripper();

            if (!moveGripperLinear(BALL1_X,BALL1_Y,RELEASE_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;

            if (!moveGripperLinear(BALL1_X,BALL1_Y,RELEASE_BALL_HEIGHT,GRIPPER_XY2,GRIPPER_XZ2,GRIPPER_YZ2)) return;

            if (!moveGripperLinear(BALL1_X,BALL1_Y,RELEASE_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;
            openGripper();

            if (!moveGripper(BALL2_X,BALL2_Y,ABOVE_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;

            if (!moveGripperLinear(BALL2_X,BALL2_Y,GRIP_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;
            closeGripper();

            if (!moveGripperLinear(BALL2_X,BALL2_Y,RELEASE_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;

            if (!moveGripperLinear(BALL2_X,BALL2_Y,RELEASE_BALL_HEIGHT,GRIPPER_XY2,GRIPPER_XZ2,GRIPPER_YZ2)) return;

            if (!moveGripperLinear(BALL2_X,BALL2_Y,RELEASE_BALL_HEIGHT,GRIPPER_XY,GRIPPER_XZ,GRIPPER_YZ)) return;
            openGripper();


        }
    }


    private void openGripper() {
        gripper.openGripper();
        manipulator.waitCurrentMovementCompleted();
    }


    private void closeGripper() {
        gripper.closeGripper();
        manipulator.waitCurrentMovementCompleted();
    }


    // Linear zu Position, Gripper-Winkel bleibt erhalten
// Ergebnis=Erfolg
    private boolean moveGripperLinear(double x,double y,double z,double gripperAngXY,double gripperAngXZ,double gripperAngYZ) {

        drawDebugPainterLineTo(x,y,z);
        debugOut.println("moveWristLinearTo "+x+" "+y+" "+z+" "+Matrix.double2String(gripperAngXY*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngXZ*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngYZ*180/Math.PI,"0.0")+"\u00B0");

//        ManipulatorResult result=manipulator.moveWristLinearTo(x,y,z,gripperAngXY,gripperAngXZ,gripperAngYZ,0.9d,5.0d);
        ManipulatorResult result=manipulator.moveWristLinearToWithConstantSpeed(x,y,z,gripperAngXY,gripperAngXZ,gripperAngYZ,20d,0.8d,5.0d);

        if (!result.wasOK()) {
            debugOut.println("moveWristLinearTo result "+result.resultCode+", "+result.resultMessage);
            return false;
        }

        debugOut.println("Wait movement to complete");
        manipulator.waitOngoingMovementCompleted();
        debugOut.println("Movement completed");

        if (!manipulator.lastManipulatorResult().wasOK()) {
            debugOut.println("moveWristLinearTo final result "+manipulator.lastManipulatorResult().resultCode+", "+manipulator.lastManipulatorResult().resultMessage);
            return false;
        }
        return true;
    }



    // Servo-Interpolation zu Position:
// Ergebnis=Erfolg
    private boolean moveGripper(double x,double y,double z,double gripperAngXY,double gripperAngXZ,double gripperAngYZ) {

        drawDebugPainterLineTo(x,y,z);
        debugOut.println("moveWristTo "+Matrix.double2String(x,"0.0")+" "+Matrix.double2String(y,"0.0")+" "+Matrix.double2String(z,"0.0")+" "+Matrix.double2String(gripperAngXY*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngXZ*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngYZ*180/Math.PI,"0.0")+"\u00B0");

        ManipulatorResult result=manipulator.moveWristTo(x,y,z,gripperAngXY,gripperAngXZ,gripperAngYZ,1,Double.NaN,0.9d);

        if (!result.wasOK()) {
            debugOut.println("moveWristTo result "+result.resultCode+", "+result.resultMessage);
            if (result.exception!=null)
                debugOut.printStackTrace(result.exception);
            return false;
        }

        debugOut.println("Wait movement to complete TEST von Obadaaa");
        manipulator.waitOngoingMovementCompleted();
        debugOut.println("Movement completed");

        if (!manipulator.lastManipulatorResult().wasOK()) {
            debugOut.println("moveWristTo final result "+manipulator.lastManipulatorResult().resultCode+", "+manipulator.lastManipulatorResult().resultMessage);
            return false;
        }
        return true;
    }


    // Für die Visualisierung von...bis bei einer Arm-Bewegung
    private void drawDebugPainterLineTo(double x,double y,double z) {
        double[] pos=manipulator.localToWorld(new double[]{x,y,z});

        if (drawLineOld!=null) {
            DebugPainterOverlay3D ovlLineFromTo=debugPainter3D.getOverlay3D("Current movement");
            ovlLineFromTo.clear();
            ovlLineFromTo.drawArrow(drawLineOld[0],drawLineOld[1],drawLineOld[2],
                    pos[0],pos[1],pos[2],
                    10,0.3d,
                    255,0,0,255
            );
            ovlLineFromTo.paint();
        }
        drawLineOld=pos;
    }


    private void drawDebugPainterGripper() {
        DirectKinematics dk=manipulator.directKinematics();
        double[] jointValues=manipulator.jointValues();
        for (double jointvalue : jointValues) {
            System.out.println("Jointvalue: " + jointvalue);
        }
        RotoTranslation rt=dk.rotoTranslationForJoints(jointValues,DirectKinematics.REFERENCE_WORLD);
        double[] pos1=rt.rotoTranslate(new double[]{0,0,0});
        double[] pos2=rt.rotoTranslate(new double[]{30,0,0});

        DebugPainterOverlay3D ovlGripperPos=debugPainter3D.getOverlay3D("Gripper Position");
        ovlGripperPos.clear();
        ovlGripperPos.drawArrow(pos1[0],pos1[1],pos1[2],
                pos2[0],pos2[1],pos2[2],
                10,0.3d,
                0,255,0,255
        );
        ovlGripperPos.paint();
    }


    @Override
    public void pause() throws Exception{
        if (instanceNumber==0)
            commander.unregisterCommandListener(this);
    }


    @Override
    public void stop() throws Exception{
        if (instanceNumber==0)
            commander.unregisterCommandListener(this);
    }


    @Override
    public void interrupt() throws Exception {
    }


    @Override
    public void command(String command) throws Exception {
        if (command.equals("?")) {
            commander.println("  hello: ask for 'hello world'");
            commander.success();
        }
        else if (command.equals("hello")) {
            commander.println("hello world!");
            commander.success();
        }
        else {
            commander.println("Unknown command: "+command);
            commander.failure();
        }
    }
}