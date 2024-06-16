del jars\*.jar

cd Basics
del ..\jars\Basics.jar 2>nul
\jdk13\bin\jar -c --file=..\jars\Basics.jar license.txt module-info.class basics\math\*.class basics\math\solids\*.class basics\math\downhill\*.class basics\math\rot\*.class basics\util\*.class basics\obj\*.class 
cd ..

cd Robotinterface
del ..\jars\Robotinterface.jar 2>nul
\jdk13\bin\jar -c --file=..\jars\Robotinterface.jar license.txt module-info.class robotinterface\*.class robotinterface\manipulator\*.class robotinterface\dirkin\*.class robotinterface\invkin\*.class robotinterface\cmd\*.class robotinterface\trigger\*.class robotinterface\debug\*.class robotinterface\motors\*.class robotinterface\passives\*.class
cd ..

cd Platform
del ..\jars\Platform.jar 2>nul
\jdk13\bin\jar -c --file=..\jars\Platform.jar license.txt module-info.class platform\*.class 
cd ..

cd Manipulators
del ..\jars\Manipulators.jar 2>nul
\jdk13\bin\jar -c --file=..\jars\Manipulators.jar license.txt module-info.class manipulators\*.class 
cd ..

cd Simulator
del ..\jars\Simulator.jar 2>nul
\jdk13\bin\jar -c --file=..\jars\Simulator.jar --main-class=kin.sim.ArmBot license.txt module-info.class kin\invkin\closed\*.class kin\invkin\downhill\*.class kin\invkin\jacobian\*.class kin\dirkin\*.class kin\dyn\*.class kin\dyn\closed\*.class kin\sim\*.class kin\execmodels\*.class kin\sim\debug\*.class kin\sim\cmd\*.class kin\trigger\*.class kin\env\*.class kin\env\geom\*.class kin\env\obstacles\*.class kin\env\obstaclesdyn\*.class kin\env\trigger\*.class kin\model\*.class kin\model\closed\*.class kin\phy\*.class kin\renderfx\*.class kin\renderfx\renderobj\*.class kin\render\*.class kin\render\renderobj\*.class kin\sim\trid\*.class kin\sim\trid\renderobj\*.class
cd ..
\jdk13\bin\jar -u --file=jars\Simulator.jar resources\*.*

rem ROBOTCONTROLLER UEBER VERZEICHNIS EINBINDEN
@rem cd Robotcontroller
@rem del ..\jars\Robotcontroller.jar 2>nul
@rem \jdk13\bin\jar -c --file=..\jars\Robotcontroller.jar license.txt module-info.class robotcontroller\*.class 
@rem cd ..