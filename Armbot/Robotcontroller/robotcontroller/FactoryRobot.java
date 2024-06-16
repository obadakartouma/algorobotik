package robotcontroller;

import robotinterface.RobotController;
import robotinterface.Time;
import robotinterface.manipulator.ManipulatorResult;
import basics.math.Matrix;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/**
* A robot controller that works in the factory environment.
* @author J&ouml;rg Roth (<a href="mailto:Joerg.Roth@Wireless-earth.org">Joerg.Roth@Wireless-earth.org</a>)
*/
public class FactoryRobot extends RobotController {

    private double[] neutralPose=null;          // Pose mit 6 Angaben
    private String[] triggerStr=null;           // Welcher Trigger löst ein Kommando aus
    private double[][] srcPose=null;            // Pose mit 6 Angaben
    private double[][] srcGripDirection=null;   // Richtung des Grippers (x,y,z)
    private double[] srcLift=null;              // Um wieviel Anheben (Quelle)
    private double[][] targPose=null;           // Pose mit 6 Angaben
    private double[][] targGripDirection=null;  // Richtung des Grippers (x,y,z)
    private double[] targLift=null;             // Um wieviel Anheben (Ziel)

    private BlockingQueue<Integer> queuedCommands=null;  // Kommandos (als Nummern), evtl. gespeichert


@Override
    public void init() {
    }
    

@Override
    public String getName() {
        return "Factory Robot";
    }


@Override
    public String getDescription() {
        return getName()+":\n"+
               "Factory Robot for the factory environment\n"+
               "Configuration <neutral>|<cmd2>|<cmd3>|...\n"+
               "where <neutral> is\n"+
               " xyz=<x>;<y>;<z>;ang=<angXY>;<srcAngXZ>;<srcAngYZ> the neutral pose when waiting for new object (ang in degrees)\n"+
               "and <cmdi> is\n"+
               "<trigger>;xyz=<srcX>;<srcY>;<srcZ>;ang=<srcAngXY>;<srcAngXZ>;<srcAngYZ>;xyz=<targX>;<targY>;<targZ>;ang=<targAngXY>;<targAngXZ>;<targAngYZ>\n"+
               "  <trigger>: the trigger string to execute the command\n"+
               "  <src...>: the pose to grip the object (ang in degrees)\n"+
               "  <targ...>: the pose to put the object (ang in degrees)";
    }


@Override
    public boolean requiresConfiguration() {
        return true;
    }


@Override
    public void configure(String params) throws IllegalArgumentException {
        String[] topSplit=params.split("\\|");

        if (topSplit.length<2)
             throw new IllegalArgumentException("Wrong configuration: '"+params+"': at least <neutral> and one <cmd> expected");

        String[] split=topSplit[0].split(";");
        if (split.length!=6)
            throw new IllegalArgumentException("Wrong configuration: '"+topSplit[0]+"' does not have 6 sub-params");

        try {
            if (split[0].startsWith("xyz="))
                split[0]=split[0].substring(4);
            if (split[3].startsWith("ang="))
                split[3]=split[3].substring(4);

            neutralPose=new double[]{
                   Double.parseDouble(split[0]),
                   Double.parseDouble(split[1]),
                   Double.parseDouble(split[2]),
                   Double.parseDouble(split[3])*Math.PI/180,
                   Double.parseDouble(split[4])*Math.PI/180,
                   Double.parseDouble(split[5])*Math.PI/180
                 };
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wrong configuration: neutral '"+split[0]+";"+split[1]+";"+split[2]+";"+split[3]+";"+split[4]+";"+split[5]+"' contains non-numbers");
        }

        triggerStr=new String[topSplit.length-1];
        srcPose=new double[topSplit.length-1][];
        srcGripDirection=new double[topSplit.length-1][];
        srcLift=new double[topSplit.length-1];
        targPose=new double[topSplit.length-1][];
        targGripDirection=new double[topSplit.length-1][];
        targLift=new double[topSplit.length-1];
        for (int i=0;i<topSplit.length-1;i++) {
            split=topSplit[i+1].split(";");
            if (split.length!=15)
                throw new IllegalArgumentException("Wrong configuration: '"+topSplit[i+1]+"' does not have 15 sub-params");

            triggerStr[i]=split[0];
            try {
                if (split[1].startsWith("xyz="))
                    split[1]=split[1].substring(4);
                if (split[4].startsWith("ang="))
                    split[4]=split[4].substring(4);

                srcPose[i]=new double[]{
                   Double.parseDouble(split[1]),
                   Double.parseDouble(split[2]),
                   Double.parseDouble(split[3]),
                   Double.parseDouble(split[4])*Math.PI/180,
                   Double.parseDouble(split[5])*Math.PI/180,
                   Double.parseDouble(split[6])*Math.PI/180
                 };

                 srcGripDirection[i]=Matrix.multiply(Matrix.rotationMatrix3DReverse(Math.sin(srcPose[i][3]),Math.cos(srcPose[i][3]),
                                                                                    Math.sin(srcPose[i][4]),Math.cos(srcPose[i][4]),
                                                                                    Math.sin(srcPose[i][5]),Math.cos(srcPose[i][5])),new double[]{1,0,0});
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Wrong configuration: src '"+split[1]+";"+split[2]+";"+split[3]+";"+split[4]+";"+split[5]+";"+split[6]+"' contains non-numbers");
            }

            try {
                if (split[7].startsWith("lift="))
                    split[7]=split[7].substring(5);
                srcLift[i]=Double.parseDouble(split[7]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Wrong configuration: srclift '"+split[7]+"' contains non-numbers");
            }

            try {
                if (split[8].startsWith("xyz="))
                    split[8]=split[8].substring(4);
                if (split[11].startsWith("ang="))
                    split[11]=split[11].substring(4);

                targPose[i]=new double[]{
                   Double.parseDouble(split[8]),
                   Double.parseDouble(split[9]),
                   Double.parseDouble(split[10]),
                   Double.parseDouble(split[11])*Math.PI/180,
                   Double.parseDouble(split[12])*Math.PI/180,
                   Double.parseDouble(split[13])*Math.PI/180
                 };

                 targGripDirection[i]=Matrix.multiply(Matrix.rotationMatrix3DReverse(Math.sin(targPose[i][3]),Math.cos(targPose[i][3]),
                                                                                     Math.sin(targPose[i][4]),Math.cos(targPose[i][4]),
                                                                                     Math.sin(targPose[i][5]),Math.cos(targPose[i][5])),new double[]{1,0,0});
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Wrong configuration: targ '"+split[8]+";"+split[9]+";"+split[10]+";"+split[11]+";"+split[12]+";"+split[13]+"' contains non-numbers");
            }

            try {
                if (split[14].startsWith("lift="))
                    split[14]=split[14].substring(5);
                targLift[i]=Double.parseDouble(split[14]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Wrong configuration: targlift '"+split[14]+"' contains non-numbers");
            }
        }
    }


@Override
    public void run() throws Exception {

        queuedCommands=new ArrayBlockingQueue<>(10);

        // In die neutrale Position gehen
        openGripper();
        if (!moveGripperLinear(neutralPose[0],neutralPose[1],neutralPose[2],neutralPose[3],neutralPose[4],neutralPose[5])) return;

        while (isRunning()) {
            Time.sleep(200);

            Integer command=queuedCommands.poll();
            if (command!=null) {
                executeCommand(command.intValue());
            }
        }
    }


@Override
    public void pause() throws Exception{
    }


@Override
    public void stop() throws Exception{
    }


@Override
    public void receiveTrigger(String triggerString) {


        if (triggerString.equals("gripgreen"))
            debugOut.sayToast("Grip green box!");
        else if (triggerString.equals("gripred"))
            debugOut.sayToast("Grip red box!");
        else if (triggerString.equals("grip"))
            debugOut.sayToast("Grip box!");


        for (int i=0;i<triggerStr.length;i++) {
            if (triggerStr[i].equalsIgnoreCase(triggerString)) {
                queuedCommands.add(i);
                return;
            }
        }
        debugOut.println("Ignoring trigger '"+triggerString+"' - no command found");
    }


// Es gab einen Trigger für Kommando #i - dieses ausführen
    public void executeCommand(int i) {
        debugOut.println("Open gripper");
        openGripper();

        debugOut.println("Move gripper to source (higher)");
        if (!moveGripper(srcPose[i][0]-srcGripDirection[i][0]*srcLift[i],srcPose[i][1]-srcGripDirection[i][1]*srcLift[i],srcPose[i][2]-srcGripDirection[i][2]*srcLift[i],
                         srcPose[i][3],srcPose[i][4],srcPose[i][5])) return;

        debugOut.println("Down gripper");
        if (!moveGripperLinear(srcPose[i][0],srcPose[i][1],srcPose[i][2],
                               srcPose[i][3],srcPose[i][4],srcPose[i][5])) return;

        debugOut.println("Close gripper");
        closeGripper();

        debugOut.println("Lift gripper");
        if (!moveGripperLinear(srcPose[i][0]-srcGripDirection[i][0]*srcLift[i],srcPose[i][1]-srcGripDirection[i][1]*srcLift[i],srcPose[i][2]-srcGripDirection[i][2]*srcLift[i],
                               srcPose[i][3],srcPose[i][4],srcPose[i][5])) return;

        debugOut.println("Move gripper to target (higher)");
        if (!moveGripper(targPose[i][0]-targGripDirection[i][0]*targLift[i],targPose[i][1]-targGripDirection[i][1]*targLift[i],targPose[i][2]-targGripDirection[i][2]*targLift[i],
                         targPose[i][3],targPose[i][4],targPose[i][5])) return;

        debugOut.println("Down gripper");
        if (!moveGripper(targPose[i][0],targPose[i][1],targPose[i][2],
                         targPose[i][3],targPose[i][4],targPose[i][5])) return;

        debugOut.println("Open gripper");
        openGripper();

        debugOut.println("Lift gripper");
        if (!moveGripper(targPose[i][0]-targGripDirection[i][0]*targLift[i],targPose[i][1]-targGripDirection[i][1]*targLift[i],targPose[i][2]-targGripDirection[i][2]*targLift[i],
                         targPose[i][3],targPose[i][4],targPose[i][5])) return;

        debugOut.println("Move gripper to neutral position");
        if (!moveGripper(neutralPose[0],neutralPose[1],neutralPose[2],
                         neutralPose[3],neutralPose[4],neutralPose[5])) return;
    }



// Linear zu Position, Gripper-Winkel bleibt erhalten
// Ergebnis=Erfolg
    private boolean moveGripperLinear(double x,double y,double z,double gripperAngXY,double gripperAngXZ,double gripperAngYZ) {

        debugOut.println("moveWristLinearTo "+Matrix.double2String(x,"0.0")+" "+Matrix.double2String(y,"0.0")+" "+Matrix.double2String(z,"0.0")+" "+Matrix.double2String(gripperAngXY*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngXZ*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngYZ*180/Math.PI,"0.0")+"\u00B0");

        ManipulatorResult result=manipulator.moveWristLinearTo(x,y,z,gripperAngXY,gripperAngXZ,gripperAngYZ,0.9d,5.0d);

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

        debugOut.println("moveWristTo "+Matrix.double2String(x,"0.0")+" "+Matrix.double2String(y,"0.0")+" "+Matrix.double2String(z,"0.0")+" "+Matrix.double2String(gripperAngXY*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngXZ*180/Math.PI,"0.0")+"\u00B0 "+Matrix.double2String(gripperAngYZ*180/Math.PI,"0.0")+"\u00B0");
        

        ManipulatorResult result=manipulator.moveWristTo(x,y,z,gripperAngXY,gripperAngXZ,gripperAngYZ,1,Double.NaN,0.9d);

        if (!result.wasOK()) {
           debugOut.println("moveWristTo result "+result.resultCode+", "+result.resultMessage);
           if (result.exception!=null)
               debugOut.printStackTrace(result.exception);
           return false;
        }

        debugOut.println("Wait movement to complete");
        manipulator.waitOngoingMovementCompleted();
        debugOut.println("Movement completed");

        if (!manipulator.lastManipulatorResult().wasOK()) {
            debugOut.println("moveWristTo final result "+manipulator.lastManipulatorResult().resultCode+", "+manipulator.lastManipulatorResult().resultMessage);
            return false;
        }
        return true;
    }



    private void openGripper() {
        gripper.openGripper();
        manipulator.waitCurrentMovementCompleted();
    }


    private void closeGripper() {
        gripper.closeGripper();
        manipulator.waitCurrentMovementCompleted();
    }

}

