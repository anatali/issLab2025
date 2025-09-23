package main.java.caller;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import main.java.BasicRobotMovesHelper;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.IApplMsgHandlerMqtt;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttConnection;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.msg.ApplMessage;

/**
 * BasicrobotCallerMqtt - MQTT Client for Basic Robot Communication
 * 
 * This class implements an MQTT client that can communicate with a basic robot system.
 * It provides functionality to send commands to the robot and receive responses/events
 * through the MQTT protocol using a callback-based approach.
 * 
 * Key features:
 * - MQTT connection management with automatic subscription
 * - Robot command sending and response handling
 * - Event-driven message processing via callbacks
 * - Integration with RobotCmds for robot operations
 * - Alarm event filtering and handling
 * 
 * The class implements IApplMsgHandlerMqtt interface to handle MQTT message callbacks,
 * providing a non-blocking approach to robot communication.
 * 
 * @author Unibo BasicRobot25 Team
 * @version 2025
 */
public class BasicrobotCallerMqtt implements IApplMsgHandlerMqtt {
	
	private final String hostAddr = BasicRobotMovesHelper.getLocalIp(); //"192.168.1.132"; //"localhost"; //TODO: myIPaddress ;
	/** MQTT broker URL - localhost for local development */
	private final String MqttBroker ="tcp://"+hostAddr+":1883";//"tcp://192.168.1.132:1883"; // "tcp://broker.hivemq.com"; // 
	
	/** MQTT topic for robot communication */
	private String topic = "basicrobot25topic";
    
    /** MQTT connection interface */ 
    private Interaction conn;
    
    /** Flag to ignore alarm events (set to true to suppress alarm messages) */
    private boolean ignorealarm = true;
 
    /** Robot commands handler for accessing predefined robot operations */
    //private RobotCmds robotcmds = new RobotCmds();

	/**
	 * Establishes connection to the MQTT service.
	 * Creates an MQTT connection and subscribes to the robot topic.
	 * The connection automatically receives all messages sent to the topic,
	 * including messages sent by this client itself.
	 * 
	 * @return Interaction object representing the MQTT connection, or null if connection fails
	 */
	protected Interaction connectToService() {
		try {			 
			CommUtils.outblue("connectToService ......... " + MqttBroker);
 			//Interaction mqttConnOut = MqttConnection.create(getName(), MqttBroker, topic, this);
 			
 			Interaction mqttConnOut = MqttConnection.create(getName(), MqttBroker, "unibo/qak/basicrobot", this);
 			
  		      	// MqttConnection also subscribes to topic =>
  		      	// receives all messages sent to the topic, including those sent by this client
  		      	// plus information (events) from wenv
//		      	mqttConnIn  = new MqttConnectionBaseInSynch(  MqttBroker, name+"_in", topic ) ;  
			return mqttConnOut;
 		} catch (Exception e) {
			CommUtils.outred("ERROR:" + e.getMessage());
			return null;
		}
	}

	/**
	 * Waits for events for a specified duration.
	 * Useful for receiving and processing robot events and responses.
	 * 
	 * @param time Duration to wait in milliseconds
	 * @throws Exception if waiting fails
	 */
	public void waitForEvents(int time) throws Exception   {
		CommUtils.outblue("waitForEvents " + time);
		Thread.sleep(time);
	}

	/**
	 * Sends a sequence of basic movement commands to the robot via MQTT.
	 * Demonstrates how to use the MQTT connection to control the robot
	 * with simple movement commands.
	 * 
	 * @throws Exception if communication fails
	 */
	public void doSomeCmd() throws Exception   {
		conn = connectToService();
		CommUtils.outcyan("doSomeCmd conn=" + conn);
		
		if( conn != null ) {
			// Send basic movement commands
 			conn.forward(BasicRobotMovesHelper.cmdl.toString());  // Turn left
 			CommUtils.delay(1000);
 			conn.forward(BasicRobotMovesHelper.cmdr.toString());  // Turn right
 			CommUtils.delay(1000);
 			conn.forward(BasicRobotMovesHelper.cmdw.toString());  // Move forward NON LO FA
		}else {
			CommUtils.outred("no connection mqtt");
		}
		CommUtils.delay(2500);
		CommUtils.outcyan("doSomeCmd BYE");
        System.exit(0);
	}

 

	/**
	 * Returns the name identifier for this MQTT client.
	 * Used by the MQTT connection for client identification.
	 * 
	 * @return The client name
	 */
	@Override
	public String getName() {
		return "mqttCaller";
	}

	/**
	 * Handles application messages received via MQTT.
	 * Processes robot responses and events, with special handling for alarm events.
	 * 
	 * @param message The received application message
	 * @param conn The connection that received the message
	 */
	@Override
	public void elaborate(IApplMessage message, Interaction conn) {
		CommUtils.outcyan("mqttCaller elaborate " + message);
		if( message.isEvent() && message.msgId().equals("alarm") && !ignorealarm ) {
			CommUtils.outred("ALARM: " + message.msgContent());
		}
	}

	/**
	 * Handles MQTT connection loss events.
	 * Logs the connection loss and can be extended for reconnection logic.
	 * 
	 * @param cause The cause of the connection loss
	 */
	@Override
	public void connectionLost(Throwable cause) {
		CommUtils.outred("mqttCaller connectionLost " + cause.getMessage());
	}

	/**
	 * Handles raw MQTT messages received on subscribed topics.
	 * Converts MQTT messages to application messages and processes them.
	 * 
	 * @param topic The MQTT topic that received the message
	 * @param message The raw MQTT message
	 * @throws Exception if message processing fails
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String content = message.toString();
		CommUtils.outcyan("mqttCaller messageArrived " + content);
		IApplMessage applMessage = new ApplMessage(content);
		elaborate(applMessage, conn);
	}

	/**
	 * Handles MQTT message delivery completion.
	 * Called when a message has been successfully delivered to the broker.
	 * 
	 * @param token The delivery token for the completed message
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		CommUtils.outblue("mqttCaller deliveryComplete");
	}

	/**
	 * Main entry point for the MQTT caller application.
	 * Creates an instance and executes robot commands via MQTT.
	 * 
	 * 
	 * @param args Command line arguments (not used)
	 * @throws Exception if any operation fails
	 */
	public static void main(String[] args) throws Exception  {
 		BasicrobotCallerMqtt caller = new BasicrobotCallerMqtt();
		caller.doSomeCmd();  // Uncomment for basic movement demo
 	}
}
