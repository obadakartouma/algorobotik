@echo off
if "%JAVA_BIN%"=="" echo Variable JAVA_BIN not set & exit /b 1
if "%JAVAFX_HOME%"=="" echo Variable JAVAFX_HOME not set & exit /b 1
@echo on

"%JAVA_BIN%"\javac -cp . -p .;"%JAVAFX_HOME%";jars\Basics.jar;jars\Manipulators.jar;jars\Platform.jar;jars\Robotinterface.jar;jars\Simulator.jar --add-modules Robotinterface,Manipulators Robotcontroller\robotcontroller\*.java
