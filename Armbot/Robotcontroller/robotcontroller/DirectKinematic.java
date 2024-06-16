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

import basics.math.Matrix;

public class DirectKinematic {

    public DirectKinematic() {
    }

    // Funktion zur Erstellung von DH-Matrix
    public static double[][] createDHMatrix(double theta, double d, double a, double alpha) {
        double[][] dhMatrix = new double[4][4];
        dhMatrix[0][0] = Math.cos(theta);
        dhMatrix[0][1] = -Math.sin(theta) * Math.cos(alpha);
        dhMatrix[0][2] = Math.sin(theta) * Math.sin(alpha);
        dhMatrix[0][3] = a * Math.cos(theta);

        dhMatrix[1][0] = Math.sin(theta);
        dhMatrix[1][1] = Math.cos(theta) * Math.cos(alpha);
        dhMatrix[1][2] = -Math.cos(theta) * Math.sin(alpha);
        dhMatrix[1][3] = a * Math.sin(theta);

        dhMatrix[2][0] = 0;
        dhMatrix[2][1] = Math.sin(alpha);
        dhMatrix[2][2] = Math.cos(alpha);
        dhMatrix[2][3] = d;

        dhMatrix[3][0] = 0;
        dhMatrix[3][1] = 0;
        dhMatrix[3][2] = 0;
        dhMatrix[3][3] = 1;

        return dhMatrix;
    }

    // Funktion zur Berechnung der Position des Endeffektors
    public static double[] calculateEndEffectorPosition(double[] jointValues, double dStand, double femurLength, double tibiaLength1, double tibiaLength2, double tibiaUpShift, double standRightShift) {
        // Define the DH parameters for each joint
        double[][] dhParameters = {
                {jointValues[0], dStand, 0, -Math.PI / 2}, // stand servo
                {-jointValues[1], standRightShift, femurLength, Math.PI/2}, // femur servo
                {jointValues[2], 0, 0, -Math.PI / 2}, // 90-degree femur servo
                {-jointValues[3], 0, tibiaLength1, Math.PI / 2}, // tibia servo
                {jointValues[4], tibiaUpShift, tibiaLength2, 0}, // 90-degree tibia servo
                {-jointValues[5], 0, 0, 0},
                {jointValues[6], 0, 0, -Math.PI/2},
                {jointValues[7], 0, 0, 0}
        };

        // Initialize transformation matrix as identity matrix
        double[][] transformationMatrix = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };

        // Compute the overall transformation matrix
        for (int i = 0; i < dhParameters.length; i++) {
            double[][] dhMatrix = createDHMatrix(dhParameters[i][0], dhParameters[i][1], dhParameters[i][2], dhParameters[i][3]);
            transformationMatrix = Matrix.multiply(transformationMatrix, dhMatrix);
        }

        // Extract the position of the end effector
        double[] endEffectorPosition = {
                transformationMatrix[0][3],
                transformationMatrix[1][3],
                transformationMatrix[2][3]
        };

        return endEffectorPosition;
    }

}
