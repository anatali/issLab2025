/*
BoundaryWalkNaiveUsingHttp.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package main.java.wenv.caller;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.net.URI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

public class BoundaryWalkNaiveUsingHttp {
	private  final String URL              = "http://localhost:8090/api/move";
//	private  CloseableHttpClient httpclient ;
	private  JSONParser simpleparser = new JSONParser();
	private  long startTime ;
	 protected Interaction conn;
	
	 private String turnrightcmd  = "{\"robotmove\":\"turnRight\"    , \"time\": \"300\"}";
	 private  String turnleftcmd  = "{\"robotmove\":\"turnLeft\"     , \"time\": \"300\"}";
	 private  String forwardcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"2500\"}";  //long ...
	 private  String backwardcmd  = "{\"robotmove\":\"moveBackward\" , \"time\": \"2500\"}";
	 private  String haltcmd      = "{\"robotmove\":\"alarm\" ,        \"time\": \"10\"}";
	
	public BoundaryWalkNaiveUsingHttp() {
//		httpclient = HttpClients.createDefault();
		conn = ConnectionFactory.createClientSupport(ProtocolType.http,"localhost:8090/api/move","/api/move");
		CommUtils.outblue("BoundaryWalkNaiveUsingHttp | CREATED" );
	}




/*
	BUSINESS LOGIC
*/ 	

	public void walkAtBoundary() throws Exception {
		CommUtils.waitTheUser("PUT ROBOT in HOME and hit");
		boolean answer;
		startTime = System.currentTimeMillis();
		for( int i=1; i<=4;i++) {
			answer = walkAheadUntilCollision();
			long duration  = System.currentTimeMillis() - startTime;
			CommUtils.outgreen("walkAtBoundary done edge:" + i + " duration=" + duration);
			if( !answer ) break;
		}
		long duration  = System.currentTimeMillis() - startTime;
		CommUtils.outblue("walkAtBoundary DONE  total duration=" + duration);
 	}
	
	public boolean walkAheadUntilCollision() throws Exception {

		//JSONObject result = callHTTP(  forwardcmd  );
		String result = conn.request(forwardcmd);
		CommUtils.outblue("walkAheadUntilCollision forwardcmd result=" + result);
		if( result.toString().contains("collision")) {
			//callHTTP(  turnleftcmd  );
			conn.request( turnleftcmd );
			return true;
		}else{
			CommUtils.outred("fatal error: no collision");
			return false;
		}
	}
	
/*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
		BoundaryWalkNaiveUsingHttp appl = new BoundaryWalkNaiveUsingHttp();
		appl.walkAtBoundary();
		CommUtils.aboutThreads("At end - ");
	}
	
 }
