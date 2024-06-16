package robotcontroller;

import robotinterface.Time;
import robotinterface.trigger.TriggerProcessor;


public class FactoryTriggerProcessor extends TriggerProcessor {

    private int onBelt1=8;                  // Wieviele Objekte liegen auf Förderband 1 - Initialisieren mit der richtigen Anzahl gemäß Umgebungsdatei
    private boolean targetArea1=false;      // Liegt was am Ende von Förderband 1
    private boolean conveyor1running=true;  // Läuft das  Förderband 1

    private int onBelt2=0;                  // Wieviele Objekte liegen auf Förderband 2
    private boolean targetArea2=false;      // Liegt was am Ende von Förderband 2
    private boolean conveyor2running=false; // Läuft das  Förderband 2

    private int onBelt3=0;                  // Wieviele Objekte liegen auf Förderband 3
    private boolean targetArea3=false;      // Liegt was am Ende von Förderband 3
    private boolean conveyor3running=false; // Läuft das  Förderband 3

    private int onBelt4=0;                  // Wieviele Objekte liegen auf Förderband 4
    private boolean targetArea4=false;      // Liegt was am Ende von Förderband 4
    private boolean conveyor4running=false; // Läuft das  Förderband 4

    private int onBelt5=0;                  // Wieviele Objekte liegen auf Förderband 5
    private boolean conveyor5running=false; // Läuft das  Förderband 5



@Override
    public String getDescription() {
        return "Trigger processor "+getClass().getName()+" (no configuration)";
    }


@Override
    public boolean requiresConfiguration() {
        return false;
    }


@Override
    public void init() throws Exception {
    }


@Override
    public void receiveTrigger(String triggerString) {
        if (triggerString.equalsIgnoreCase("SOURCEENTR1")) {
            onBelt1++;
            if (!targetArea1 && !conveyor1running) {
                conveyor1running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR1_INSTNR"),"run");
            }
        }
        else if (triggerString.equalsIgnoreCase("TARGETBARR1GREEN")) {
            targetArea1=true;
            conveyor1running=false;
            Time.runDelayed(4000,()->sendTrigger(0,"gripgreen"));
            Time.runDelayed((int)(31/eval.evalDoubleExpression("CONVERYOR1SPEED")*1000),
                            ()->sendTrigger(eval.evalIntExpression("CONVEYOR1_INSTNR"),"stop"));
        }
        else if (triggerString.equalsIgnoreCase("TARGETBARR1RED")) {
            targetArea1=true;
            conveyor1running=false;
            Time.runDelayed(4000,()->sendTrigger(0,"gripred"));
            Time.runDelayed((int)(31/eval.evalDoubleExpression("CONVERYOR1SPEED")*1000),
                            ()->sendTrigger(eval.evalIntExpression("CONVEYOR1_INSTNR"),"stop"));
        }
        else if (triggerString.equalsIgnoreCase("TARGETLEAV1")) {
            targetArea1=false;
            onBelt1--;
            if (onBelt1>0 && !conveyor1running) {
                conveyor1running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR1_INSTNR"),"run");
            }
        }


        else if (triggerString.equalsIgnoreCase("SOURCEENTR2")) {
            onBelt2++;
            if (!targetArea2 && !conveyor2running) {
                conveyor2running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR2_INSTNR"),"run");
            }
        }
        else if (triggerString.equalsIgnoreCase("TARGETBARR2")) {
            targetArea2=true;
            conveyor2running=false;
            Time.runDelayed(4000,()->sendTrigger(1,"gripred"));
            Time.runDelayed((int)(31/eval.evalDoubleExpression("CONVERYOR2SPEED")*1000),
                            ()->sendTrigger(eval.evalIntExpression("CONVEYOR2_INSTNR"),"stop"));
        }
        else if (triggerString.equalsIgnoreCase("TARGETLEAV2")) {
            targetArea2=false;
            onBelt2--;
            if (onBelt2>0 && !conveyor2running) {
                conveyor2running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR2_INSTNR"),"run");
            }
        }

        else if (triggerString.equalsIgnoreCase("SOURCEENTR3")) {
            onBelt3++;
            if (!targetArea3 && !conveyor3running) {
                conveyor3running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR3_INSTNR"),"run");
            }
        }
        else if (triggerString.equalsIgnoreCase("TARGETBARR3")) {
            targetArea3=true;
            conveyor3running=false;
            Time.runDelayed(4000,()->sendTrigger(1,"gripgreen"));
            Time.runDelayed((int)(31/eval.evalDoubleExpression("CONVERYOR3SPEED")*1000),
                            ()->sendTrigger(eval.evalIntExpression("CONVEYOR3_INSTNR"),"stop"));
        }
        else if (triggerString.equalsIgnoreCase("TARGETLEAV3")) {
            targetArea3=false;
            onBelt3--;
            if (onBelt3>0 && !conveyor3running) {
                conveyor3running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR3_INSTNR"),"run");
            }
        }

        else if (triggerString.equalsIgnoreCase("SOURCEENTR4")) {
            onBelt4++;
            if (!targetArea4 && !conveyor4running) {
                conveyor4running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR4_INSTNR"),"run");
            }
        }
        else if (triggerString.equalsIgnoreCase("TARGETBARR4")) {
            targetArea4=true;
            conveyor4running=false;
            Time.runDelayed(4000,()->sendTrigger(2,"grip"));
            Time.runDelayed((int)(31/eval.evalDoubleExpression("CONVERYOR4SPEED")*1000),
                            ()->sendTrigger(eval.evalIntExpression("CONVEYOR4_INSTNR"),"stop"));
        }
        else if (triggerString.equalsIgnoreCase("TARGETLEAV4")) {
            targetArea4=false;
            onBelt4--;
            if (onBelt4>0 && !conveyor4running) {
                conveyor4running=true;
                sendTrigger(eval.evalIntExpression("CONVEYOR4_INSTNR"),"run");
            }
        }

        else if (triggerString.equalsIgnoreCase("SOURCEENTR5")) {
            onBelt5++;
            if (!conveyor5running) {
                conveyor5running=true;
                Time.runDelayed(3000,()->sendTrigger(eval.evalIntExpression("CONVEYOR5_INSTNR"),"run"));  // Etwas verzögert, damit der Roboter den Greifer wegnehmen kann
            }
        }
        else if (triggerString.equalsIgnoreCase("TARGETBARR5")) {
            onBelt5--;

            if (onBelt5==0 && conveyor5running) {
                conveyor5running=false;
                Time.runDelayed((int)(40/eval.evalDoubleExpression("CONVERYOR5SPEED")*1000),
                                ()->{if (!conveyor5running) sendTrigger(eval.evalIntExpression("CONVEYOR5_INSTNR"),"stop");});
            }


        }

        else {
            debugOut.println("Ignoring trigger '"+triggerString+"'");
            return;
        }
    }

}