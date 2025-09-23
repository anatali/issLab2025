package main.java;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

/**
 * Classe helper per inviare comandi di movimento al BasicRobot tramite messaggi.
 */
public class BasicRobotMovesHelper {
    // Identificativo del chiamante
	public static final String freecaller = "unibocaller";
    // Durata di uno step di movimento
	public static final int sT = 340;
    // Oggetto per la comunicazione con il robot
    private Interaction conn;
    // Ultima risposta ricevuta dal robot
    private IApplMessage answer;

    // Comandi di movimento base
    /** Comando: avanti (w) */
    public static final IApplMessage cmdw = CommUtils.buildDispatch(freecaller, "cmd", "cmd(w)", "basicrobot");
    /** Comando: gira a sinistra (l) */
    public static final IApplMessage cmdl = CommUtils.buildDispatch(freecaller, "cmd", "cmd(l)", "basicrobot");
    /** Comando: gira a destra (r) */
    public static final IApplMessage cmdr = CommUtils.buildDispatch(freecaller, "cmd", "cmd(r)", "basicrobot");
    /** Comando: esegui uno step */
    public static final IApplMessage dostep = CommUtils.buildRequest(freecaller, "step", "step(" + sT + ")", "basicrobot");
    /** Imposta direzione su (up) */
    public static final IApplMessage setDirectionUp = CommUtils.buildDispatch(freecaller, "setdirection", "dir(up)", "basicrobot");
    /** Imposta direzione giù (down) */
    public static final IApplMessage setDirectionDown = CommUtils.buildDispatch(freecaller, "setdirection", "dir(down)", "basicrobot");
    /** Richiesta stato robot */
    public static final IApplMessage robotstate = CommUtils.buildRequest(freecaller, "getrobotstate", "getrobotstate(now)", "basicrobot");
    /** Disabilita delay nel piano */
    public static final IApplMessage resetplanbuildelay = CommUtils.buildDispatch(freecaller, "setplanbuildelay", "setplanbuildelay(0)", "basicrobot");
    /** Abilita delay nel piano */
    public static final IApplMessage setplanbuildelay = CommUtils.buildDispatch(freecaller, "setplanbuildelay", "setplanbuildelay(50)", "basicrobot");
    /** Richiesta tuning posizione home */
    public static final IApplMessage tuneathome = CommUtils.buildRequest(freecaller, "tuneAtHome", "tuneAtHome(ok)", "basicrobot");
    /** Imposta robot a home */
    public static final IApplMessage setRobotAtHome = CommUtils.buildDispatch(freecaller, "setrobotstate", "setpos(0,0,null)", "basicrobot");

    /*
     * Comandi per muovere il robot in posizioni specifiche
     */
    private IApplMessage move35 = CommUtils.buildRequest(freecaller, "moverobot", "moverobot(3,5," + sT + ")", "basicrobot");
    private IApplMessage move14 = CommUtils.buildRequest(freecaller, "moverobot", "moverobot(1,4," + sT + ")", "basicrobot");
    private IApplMessage moveInPort = CommUtils.buildRequest(freecaller, "moverobot", "moverobot(4,0," + sT + ")", "basicrobot");

    //------------------------------------------------------------------------------------

    /**
     * Connette al servizio robot tramite TCP.
     */
    public Interaction connectToService(String host, String port) {
        try {
            CommUtils.outcyan("BasicRobotMovesHelper | connectService Hostname: " + host);
            CommUtils.outcyan("connectService Port:     " + port);
            conn = ConnectionFactory.createClientSupport23(ProtocolType.tcp, host, port);
            return conn;
        } catch (Exception e) {
            CommUtils.outred("BasicRobotMovesHelper | ERROR:" + e.getMessage());
            return null;
        }
    }

    /**
     * Disabilita il delay nel piano di movimento.
     */
    public void resetplanbuildelay() throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | Hide plan thinking");
        conn.forward(resetplanbuildelay);
    }

    /**
     * Abilita il delay nel piano di movimento.
     */
    public void setplanbuildelay() throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | Show plan thinking");
        conn.forward(setplanbuildelay);
    }

    /**
     * Richiede lo stato attuale del robot.
     */
    public void getRobotState() throws Exception {
        answer = conn.request(robotstate);
        CommUtils.outblue("BasicRobotMovesHelper | robotstate answer=" + answer);
    }

    /**
     * Esegue uno step di movimento.
     */
    public void dostep() throws Exception {
        answer = conn.request(dostep);
        CommUtils.outblue("BasicRobotMovesHelper | dostep answer=" + answer);
    }

    /**
     * Esegue un piano di movimento.
     * @param plan stringa con il piano
     * @return true se il piano è stato eseguito con successo
     */
    public boolean doPLan(String plan) throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | doplan plan=" + plan);
        IApplMessage doplan = CommUtils.buildRequest(freecaller, "doplan", "doplan(P,340)".replace("P", plan), "basicrobot");
        answer = conn.request(doplan);
        CommUtils.outblue("BasicRobotMovesHelper | doplan answer=" + answer);
        if (answer.msgContent().startsWith("doplanfailed")) {
            CommUtils.outred("BasicRobotMovesHelper | Plan failed");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Imposta la direzione del robot verso l'alto.
     */
    public void setDirectionUp() throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | setDirectionUp");
        conn.forward(setDirectionUp);
    }

    /**
     * Imposta la direzione del robot verso il basso.
     */
    public void setDirectionDown() throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | setDirectionDown");
        conn.forward(setDirectionDown);
    }

    /**
     * Restituisce la posizione attuale del robot come Struct pos(X,Y).
     */
    public Struct getrobotPos() throws Exception {
        answer = conn.request(robotstate);
        String stateTermStr = answer.msgContent();   // robotstate(pos(X,Y),DIR)
        CommUtils.outgreen("BasicRobotMovesHelper | robotstate state=" + stateTermStr);

        Struct state = (Struct) Term.parse(stateTermStr);
        Struct pos = (Struct) state.getArg(0);  // pos(X,Y)
        return pos;
    }

    /**
     * Mostra la posizione attuale del robot.
     */
    public void showtrobotPos() throws Exception {
        Struct pos = getrobotPos();
        int x = Integer.parseInt(pos.getArg(0).toString());
        int y = Integer.parseInt(pos.getArg(1).toString());
        CommUtils.outgreen("BasicRobotMovesHelper | Current robot position: (" + x + "," + y + ")");
    }

    /**
     * Verifica se il robot è nella posizione home (0,0).
     */
    public boolean checkRobotAtHome() throws Exception {
        Struct pos = getrobotPos(); // pos(X,Y)
        int x = Integer.parseInt(pos.getArg(0).toString());
        int y = Integer.parseInt(pos.getArg(1).toString());

        if (x == 0 && y == 0) {
            CommUtils.outgreen("BasicRobotMovesHelper | checkRobotAtHome : Robot is at home");
            return true;
        } else {
            CommUtils.outgreen("BasicRobotMovesHelper | checkRobotAtHome : Robot is NOT at home");
            return false;
        }
    }

    /**
     * Verifica se il robot è vicino alla posizione home.
     */
    public boolean nearToHome() throws Exception {
        Struct pos = getrobotPos(); // pos(X,Y)
        int x = Integer.parseInt(pos.getArg(0).toString());
        int y = Integer.parseInt(pos.getArg(1).toString());
        CommUtils.outgreen("BasicRobotMovesHelper | moveToHome nearToHome=" + (x <= 1 && y <= 1));
        return (x <= 1 && y <= 1);
    }

    /**
     * Esegue il tuning della posizione home.
     */
    public void tuneAtHome() throws Exception {
        CommUtils.outcyan("BasicRobotMovesHelper | tuneAtHome ");
        answer = conn.request(tuneathome);
        CommUtils.outcyan("BasicRobotMovesHelper | tuneathome answer=" + answer);
        setDirectionDown();
    }
       /**
     * Muove il robot verso la posizione home (0,0).
     */
    public void moveToHome() throws Exception {
        String paylod = "moverobot(0,0,ST)".replace("ST", "" + sT);
        //CommUtils.outmagenta("BasicRobotMovesHelper | moveToHome payload=" + paylod);

        IApplMessage gotoHome = CommUtils.buildRequest(freecaller, "moverobot", paylod, "basicrobot");
        answer = conn.request(gotoHome);
        CommUtils.outblue("gotoHome  answer=" + answer);
        String result = answer.msgContent();

        if (result.startsWith("moverobotfailed")) {
            String plantodo = ((Struct) Term.parse(result)).getArg(1).toString();
            CommUtils.outred("BasicRobotMovesHelper | gotoHome  moverobotfailed plantodo=" + plantodo);

            // Se vicino a home e il piano è breve, esegue tuning
            if (nearToHome() && plantodo.length() == 1) {
                tuneAtHome();
                conn.forward(setRobotAtHome);
            } else {
                tryToReachHome(plantodo);
            }
        } else {
            CommUtils.outcyan("BasicRobotMovesHelper | moveToHome TUNING");
            tuneAtHome();
        }
    }

    /**
     * Prova a raggiungere la posizione home eseguendo il piano rimanente.
     */
    protected void tryToReachHome(String plantodo) throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | tryToReachHome  plan to do:" + plantodo);
        plantodo = plantodo.substring(1); // Rimuove la prima mossa fallita
        CommUtils.outred("BasicRobotMovesHelper | tryToReachHome unexpected plan to do:" + plantodo);
        if (doPLan(plantodo)) {
            if (checkRobotAtHome()) {
                CommUtils.outgreen("BasicRobotMovesHelper | tryToReachHome arrived at home");
                tuneAtHome();
                conn.forward(setRobotAtHome);
            } else {
                if (nearToHome()) {
                    CommUtils.outgreen("BasicRobotMovesHelper | tryToReachHome near at home !!! => tuning");
                    tuneAtHome();
                } else {
                    CommUtils.outgreen("BasicRobotMovesHelper | tryToReachHome not arrived at home !!! => no tuning");
                }
            }
        } else {
            CommUtils.outred("BasicRobotMovesHelper | tryToReachHome recovery plan failed");
            showtrobotPos();
        }
    }

    /**
     * Muove il robot alla posizione (3,5).
     */
    public void move35() throws Exception {
    	CommUtils.outblue("BasicRobotMovesHelper | move35  "  );
        answer = conn.request(move35);
        CommUtils.outblue("BasicRobotMovesHelper | move35 answer=" + answer);
    }

    /**
     * Esegue una sequenza di test di movimento ispirate al temaFinale25.
     */
    public void tf25() {
        try {
          
			if (! checkRobotAtHome()  ) {
				CommUtils.outmagenta("BasicRobotMovesHelper tf25 | moveToHome ");
				moveToHome();
			}
            
			//DELAY BEFORE START
			CommUtils.delay(2000); 			 
            CommUtils.outmagenta("BasicRobotMovesHelper tf25 | moveInPort ......... ");
            answer = conn.request(moveInPort);
            CommUtils.outblue("BasicRobotMovesHelper tf25 | moveInPort answer " + answer.toString());
            
            
            if (answer.msgContent().toString().contains("moverobotdone")) {
                //DEALY TO LOAD
                CommUtils.delay(2000); 
                CommUtils.outmagenta("BasicRobotMovesHelper tf25 | moveTarget move14 ");
                answer = conn.request(move14);
                CommUtils.outblue("BasicRobotMovesHelper tf25 | moveTarget answer " + answer.toString());
            
	            
	            if (answer.toString().contains("moverobotdone")) {
		            //DELAY FOR DOWNLOAD
		            CommUtils.delay(2000);
		            CommUtils.outmagenta("BasicRobotMovesHelper tf25 | moveToHome AGAIN");
	            	moveToHome();
	            } else {
	            	CommUtils.outred("BasicRobotMovesHelper tf25 | moveTarget FAILED ");
	            }
            }else {
            	CommUtils.outred("BasicRobotMovesHelper tf25 | moveInPort FAILED ");
            }
        } catch (Exception e) {
            CommUtils.outred("BasicRobotMovesHelper tf25 | ERROR:" + e.getMessage());
        }
    }

    /**
     * Costruisce un piano di movimento dal punto (x1,y1) a (x2,y2).
     */
    public String buildPlan(int x1, int y1, int x2, int y2) throws Exception {
        IApplMessage buildPlan = CommUtils.buildRequest(freecaller, "buildPlan", "buildPlan(" + x1 + "," + y1 + "," + x2 + "," + y2 + ")", "basicrobot");
        IApplMessage answer = conn.request(buildPlan);
        String result = answer.msgContent();
        //CommUtils.outyellow("buildPlan result:" + result);
		if (result.startsWith("buildPlanDone")) {
			Struct struct = (Struct) Term.parse(result);
			String plan = struct.getArg(0).toString();
			CommUtils.outyellow("BasicRobotMovesHelper | buildPlan plan=" + plan);
			return plan;
		}else throw new Exception("BasicRobotMovesHelper |buildPlan fatal error");
    }

    /**
     * Comandi di rotazione.
     */
    public void tunrleft( ) throws Exception {
         conn.forward( cmdl.toString( ) );
     }
    public void tunrright( ) throws Exception {
        conn.forward( cmdr.toString( ) );
    }
    public void turnup( ) throws Exception {
         setDirectionUp();
    }
    public void turndown( ) throws Exception {
         setDirectionDown();
   }
    /**
     * Posiziona il robot a home (0,0).
     */
    public void setRobotAtHome( ) throws Exception {
        CommUtils.outblue("BasicRobotMovesHelper | setRobotAtHome");
        conn.forward( setRobotAtHome );
   }

    
    public void boundary() {
        try {
        	CommUtils.outmagenta("BasicRobotMovesHelper bouandary |  gowalldown ");
        	
        	IApplMessage move40 = 
        		CommUtils.buildRequest(freecaller, "moverobot", "moverobot(4,0," + sT + ")", "basicrobot");
            answer = conn.request(move40);
            CommUtils.outblue("BasicRobotMovesHelper bouandary | moveInPort answer " + answer.toString());
            if ( ! answer.msgContent().toString().contains("moverobotdone") ) return;
        	
            IApplMessage move46 = 
            		CommUtils.buildRequest(freecaller, "moverobot", "moverobot(4,6," + sT + ")", "basicrobot");
            answer = conn.request(move46);
            CommUtils.outblue("BasicRobotMovesHelper bouandary | moveInPort answer " + answer.toString());
            if ( ! answer.msgContent().toString().contains("moverobotdone") ) return;
           	
            IApplMessage move06 = 
            		CommUtils.buildRequest(freecaller, "moverobot", "moverobot(0,6," + sT + ")", "basicrobot");
            answer = conn.request(move06);
            CommUtils.outblue("BasicRobotMovesHelper bouandary | moveInPort answer " + answer.toString());
            if ( ! answer.msgContent().toString().contains("moverobotdone") ) return;
            
            moveToHome();
 
        	
        }catch(Exception e) {
            CommUtils.outred("BasicRobotMovesHelper tf25 | ERROR:" + e.getMessage());
       }
    }
    
    /**
     * Esegue alcuni comandi base di movimento.
     */
    public void doSomeCmd() throws Exception {
        // Gira a sinistra, gira a destra, avanza
        conn.forward(cmdl.toString());
        conn.forward(cmdr.toString());
        conn.forward(cmdw.toString());
    }
    
    public static String getLocalIp() {		
        try {
            Enumeration<NetworkInterface> interfacce = NetworkInterface.getNetworkInterfaces();
            while (interfacce.hasMoreElements()) {
                NetworkInterface interfaccia = interfacce.nextElement();
                Enumeration<InetAddress> indirizzi = interfaccia.getInetAddresses();
                while (indirizzi.hasMoreElements()) {
                    InetAddress indirizzo = indirizzi.nextElement();
                    if (!indirizzo.isLoopbackAddress()) { // Esclude l'indirizzo loopback (127.0.0.1)
                    	//CommUtils.outgreen(interfaccia.getDisplayName() + ": " + indirizzo.getHostAddress());                        
                        if( indirizzo.getHostAddress().startsWith("192.168")) {
                        	CommUtils.outgreen("ActorGuiControllerMqtt ==== " + indirizzo.getHostAddress());
                        	return indirizzo.getHostAddress();
                        }
                    }
                }
            }
            return null;
        } catch (SocketException e) {
            CommUtils.outred("Errore ricerca degli indirizzi IP: " + e.getMessage());
            return null;
        }			
	}
}
