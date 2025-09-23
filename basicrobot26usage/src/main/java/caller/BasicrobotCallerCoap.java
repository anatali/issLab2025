package main.java.caller;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import it.unibo.kactor.sysUtil;
import main.java.BasicRobotMovesHelper;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

/**
 * BasicrobotCallerCoap - COAP Client for Basic Robot Communication
 * 
 * This class implements a COAP client that can communicate with a basic robot system.
 * It provides functionality to send commands to the robot and observe robot state
 * changes through the COAP protocol using the Californium framework.
 * 
 * Key features:
 * - COAP connection management for robot communication
 * - Robot command sending and response handling
 * - Observer pattern for monitoring robot state changes
 * - Sonar sensor data monitoring with alarm detection
 * - Integration with RobotCmds for robot operations
 * 
 * The class uses COAP observe functionality to monitor robot state changes
 * and can detect sonar alarms for obstacle detection.
 * 
 * @author Unibo BasicRobot25 Team
 * @version 2025
 */
public class BasicrobotCallerCoap {
	/** Logger for this class */
//	private static final Logger logger = LoggerFactory.getLogger("BasicRobotCallerCoap");
 	
    /** Client name identifier */
    protected String name;
    
    /** Communication channel for COAP interactions */
    protected Interaction commChannel;
    
    /** Protocol type (COAP) */
    protected ProtocolType protocol;
    
    /** Host address for robot service */
    protected String hostAddr;
    
    /** Entry point for robot service */
    protected String entry;
    
    /** Connection status flag */
    protected boolean connected = false;
    
    /** COAP observe relation for monitoring robot state */
    protected CoapObserveRelation relation;
    
	/**
	 * Constructor initializes the COAP caller and clears log files.
	 * Sets up logging and prepares the client for robot communication.
	 */
	public BasicrobotCallerCoap() {
//		sysUtil.clearlog("./logs/basicrobot24.log");
//		logger.info("avviato correttamente.");		
	}

	/**
	 * Establishes a COAP connection to the robot service.
	 * Creates a client connection using the ConnectionFactory with COAP protocol.
	 * 
	 * @param host The hostname or IP address of the robot service
	 * @param entry The entry point for the robot service
	 * @return An Interaction object for communication, or null if connection fails
	 */
	protected Interaction connectToService(String host, String entry) {
		try {			 
  				CommUtils.outcyan("connectService Hostname: " + host);
				CommUtils.outcyan("connectService Port:     " + entry);
 				Interaction connSupport = 
 						ConnectionFactory.createClientSupport23(ProtocolType.coap, host, entry);
// 				logger.info("connected");
 				return connSupport;
 		} catch (Exception e) {
			CommUtils.outred("ERROR:" + e.getMessage());
			return null;
		}
	}
 
  	/**
  	 * Sets up observation of the basicrobot actor via COAP.
  	 * Creates an observer to monitor robot state changes and sonar data.
  	 * Detects sonar alarms and provides audio feedback for obstacles.
  	 */
	protected void observeBasicrobot() {
 		// Observe basicrobot
 		Interaction conn = ConnectionFactory.createClientSupport23(ProtocolType.coap,"localhost:8020", "ctxbasicrobot/basicrobot");
		CoapClient client = ((CoapConnection)conn).getClient();
	    //CommUtils.outblue("callerCoap addObservation client");
		relation = client.observe(
				new CoapHandler() {
					@Override public void onLoad(CoapResponse response) {
						String content = response.getResponseText();
						CommUtils.outmagenta("basicrobot observer | " + content);
						if( content.contains("sonar")) {
							CommUtils.outred(content);
							java.awt.Toolkit.getDefaultToolkit().beep();  // Audio alarm for sonar detection
						}
//						else CommUtils.outgreen(content);
					}					
					@Override public void onError() {
						CommUtils.outred("basicrobot OBSERVING FAILED");
					}
				});	
	}
	
	/**
	 * Sets up observation of the robotpos actor via COAP.
	 * Creates an observer to monitor robot position and state changes.
	 * Provides real-time updates on robot position and direction.
	 */
	protected void observeRobotpos() {
	    // Observe robotPos
			Interaction conn = ConnectionFactory.createClientSupport23(ProtocolType.coap,"localhost:8020", "ctxbasicrobot/robotpos");
			CoapClient client = ((CoapConnection)conn).getClient();
		    //CommUtils.outblue("callerCoap addObservation client");
			relation = client.observe(
					new CoapHandler() {
						@Override public void onLoad(CoapResponse response) {
							String content = response.getResponseText();
							CommUtils.outcyan("robotpos observer | " + content);
							//CommUtils.outmagenta(content);
						}					
						@Override public void onError() {
							CommUtils.outred("robotpos OBSERVING FAILED");
						}
					});	
	}

	/**
     * Sends a sequence of basic movement commands to the robot.
     * Demonstrates how to use the Interaction interface to control the robot
     * with simple movement commands (left, right, forward).
     * 
     * @throws Exception if communication fails
     */
	public void doSomeCmd() throws Exception   {
		CommUtils.outcyan(" doSomeCmd");
		Interaction conn = connectToService("localhost:8020", "ctxbasicrobot/basicrobot");
		if( conn != null ) {
			// Send basic movement commands
			CommUtils.outcyan(" connected ");
			conn.forward(BasicRobotMovesHelper.cmdl.toString());  // Turn left
			conn.forward(BasicRobotMovesHelper.cmdr.toString());  // Turn right
			conn.forward(BasicRobotMovesHelper.cmdw.toString());  // Move forward
		}else {
			CommUtils.outred("no connection");
		}
	       System.exit(0);
		}

	/**
	 * Main job method that performs robot operations via COAP.
	 * Connects to the robot service and can optionally set up observers
	 * for monitoring robot state changes.
	 * 
	 * @throws Exception if connection or robot operations fail
	 */
	public void doJob() throws Exception   {
		CommUtils.outcyan("BasicRobotCallerCoap doJob");
		new BasicRobotMovesHelper().tf25();  // Execute predefined path sequence
        System.exit(0);
	}

	/**
	 * Main entry point for the COAP caller application.
	 * Creates an instance and executes robot commands via COAP.
	 * 
	 * @param args Command line arguments (not used)
	 * @throws Exception if any operation fails
	 */
	public static void main(String[] args) throws Exception{
		BasicrobotCallerCoap caller = new BasicrobotCallerCoap();
		caller.doSomeCmd();
	}
}
