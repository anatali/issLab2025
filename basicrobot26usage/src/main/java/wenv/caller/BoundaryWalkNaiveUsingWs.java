/**
 * BoundaryWalkNaiveUsingWs
 ============================================================================
 * Technology-dependent application
 * Le parti commentate sono communication details  
 * 
 * Il codice viene ora basato su Interaction (WsConnection)
 * che è osservabile, in modo da evitare i metodi come
 *   	public void onMessage(String message)  
 * previsti nel caso di uso di javax.websocket
 *  
 * Il metodo update viene invocato quando WEnv emette informazioni 
 * e include logica applicativa quando deve gestire 
 * l'informazione di una mossa (forward) che fallisce
 ============================================================================
 */

package main.java.wenv.caller;
//import javax.websocket.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.ws.WsConnection;
 

 
public class BoundaryWalkNaiveUsingWs extends ApplAbstractObserver{  
//    private Session userSession      = null;
    private  JSONParser simpleparser = new JSONParser();
    private long startTime;
    private long totalDuration = 0;
    private int count=0;
    
    protected Interaction conn;
    
	 private  String turnrightcmd  = "{\"robotmove\":\"turnRight\", \"time\": \"300\"}";
	 private  String turnleftcmd  = "{\"robotmove\":\"turnLeft\", \"time\": \"300\"}";
	 private  String forwardcmd   = "{\"robotmove\":\"moveForward\", \"time\": \"4000\"}";
	 private  String backwardcmd  = "{\"robotmove\":\"moveBackward\", \"time\": \"3000\"}";
	 private  String haltcmd      = "{\"robotmove\":\"alarm\", \"time\": \"10\"}";
	 private  String forwardlongcmd   = "{\"robotmove\":\"moveForward\" , \"time\":\"4000\"}";

	 
    public BoundaryWalkNaiveUsingWs(String addr) {
            CommUtils.outblue("ClientNaiveUsingWs |  CREATING ..." + addr);
            init(addr);
    }

    protected void init(String addr){
        try {
            //CommUtils.outblue("ClientNaiveUsingWs |  container=" + ContainerProvider.class.getName());
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            container.connectToServer(this, new URI("ws://"+addr));
        	conn = ConnectionFactory.createClientSupport(ProtocolType.ws,"localhost:8091","api/move");
        	CommUtils.outblue("BoundaryWalkNaiveUsingWs |  init conn=" + conn);
        	((WsConnection) conn).addObserver(this);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void halt(){
        callWS( haltcmd );CommUtils.delay(30);
    }



    protected void callWS(String msg )   {
        CommUtils.outcyan("BoundaryWalkNaiveUsingWs | callWS " + msg);
        if( ! msg.contains("alarm")) startTime = System.currentTimeMillis() ;
        try {
			conn.forward(msg);
		} catch (Exception e) {
 			e.printStackTrace();
		}    
    }
    /*
     * Since ApplAbstractObserver
     */

    //Deve includere parte della business logic alla collision e a endmove
	@Override
	public void update(String message) {
        long duration = System.currentTimeMillis() - startTime;
        try {
            //{"collision":"true ","move":"..."} or {"sonarName":"sonar2","distance":19,"axis":"x"}
        	CommUtils.outmagenta("BoundaryWalkNaiveUsingWs | onMessage:" + message + " duration="+duration);
            JSONObject jsonObj = (JSONObject) simpleparser.parse(message);
            //CommUtils.outblue("BoundaryWalkNaiveUsingWs | jsonObj:" + jsonObj);
            if (jsonObj.get("endmove") != null ) {
     
            //BUSINESS LOGIC -----------------------------------------------------------------------------------
              boolean endmove = jsonObj.get("endmove").toString().contains("true");
              String  move    = (String) jsonObj.get("move") ;
              //CommUtils.outyellow("onMessage | " + move + " endmove=" + endmove + " duration="+duration + " count=" + count);
              if( ! endmove ){ //forward failed since collisiom
                  count++;
                  callWS(  turnleftcmd  );//CommUtils.delay(350);
              }else //turnLeft completed
              if( count <= 4   ) callWS(  forwardlongcmd  );
              //------------------------------------------------------------------------------------------------
                
                //CommUtils.outgreen("BoundaryWalkNaiveUsingWs | endmove:" + endmove + " move="+move);
            } else if (jsonObj.get("collision") != null ) {
                String move   = (String) jsonObj.get("collision");
                String target = (String) jsonObj.get("target");
                halt(); //Forza l'emissione di {"endmove":"false","move":"moveForward-collision"}
                //senza halt il msg {"endmove":"false","move":"moveForward-collision"} arriva dopo 3000
             } else if (jsonObj.get("sonarName") != null ) { //JUST TO SHOW ...
                String sonarName = (String) jsonObj.get("sonarName") ;
                String distance  = jsonObj.get("distance").toString();
            }
        } catch (Exception e) {
        	CommUtils.outred("onMessage " + message + " " +e.getMessage());
        }
    }

/*
BUSINESS LOGIC
*/
 	/*
	 * Dopo il primo comando, opera onMessage
	 */
	
	public void walkAtBoundary() {
        CommUtils.waitTheUser("PUT ROBOT in HOME and hit");
		count   = 1;
		//callWS( haltcmd );CommUtils.delay(30); //TO avoid notallowed
		callWS(  forwardlongcmd  );  //deve terminare con una collisione anche se dura di più
		//CommUtils.waitTheUser("HIT TO END");
		CommUtils.delay(20000);
 	}

/*
MAIN
 */
    public static void main(String[] args) {
        try{
    		CommUtils.aboutThreads("Before start - ");
            BoundaryWalkNaiveUsingWs appl = new BoundaryWalkNaiveUsingWs("localhost:8091");
             appl.walkAtBoundary();
      		CommUtils.aboutThreads("At end - ");
        } catch( Exception ex ) {
            CommUtils.outred("ClientNaiveUsingWs | main ERROR: " + ex.getMessage());
        }
    }
}

