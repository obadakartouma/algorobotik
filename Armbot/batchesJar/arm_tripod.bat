@echo off
if "%JAVA_BIN%"=="" echo Variable JAVA_BIN not set & exit /b 1
if "%JAVAFX_HOME%"=="" echo Variable JAVAFX_HOME not set & exit /b 1
@echo on

"%JAVA_BIN%"\java -cp . -p .;"%JAVAFX_HOME%";jars\Basics.jar;jars\Manipulators.jar;jars\Platform.jar;jars\Robotinterface.jar;jars\Simulator.jar --add-modules Manipulators,Robotcontroller -m Simulator/kin.sim.ArmBot -logdir logs -logall -renderflags FFT -man manipulators.Tripod -gri manipulators.LinearGripper -c robotcontroller.TripodTest -e environments\environment_gripball.txt
