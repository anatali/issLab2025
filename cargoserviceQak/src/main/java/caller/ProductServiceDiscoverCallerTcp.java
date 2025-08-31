package main.java.caller;

/*
 * ---------------------------------------------
 * WARNING:
 *  	ATTIVARE con gradlew runCaller
 *      configurare eureka-client.properties
 * ---------------------------------------------
 */
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
 import main.java.domain.Product;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

public class ProductServiceDiscoverCallerTcp  {
	
//	private String p = "product( '{\"productId\":23,\"name\":\"p23\",\"weight\":230}' )";
	private EurekaClient eurekaClient;
//	private String serviceName = "ctxcargoservice";
	private Interaction connSupport;
	
    
	protected String[] discoverProductService(String serviceName) {
 		boolean result = CommUtils.ckeckEureka( );
    	//eurekaClient = CommUtils.createEurekaClient( new EurekaServiceConfig()  ); 
		//DISCOVER
    	CommUtils.outyellow(" result = " + result);
    	if( ! result ) return null;
 		
    	CommUtils.outyellow(" ---------------------------- discoverService " + result);
    	String[]  hostPort = CommUtils.discoverService(  "ctxcargoservice" ); 
		CommUtils.outyellow(" ---------------------------- discoverService hostPort=" + hostPort);
 		if( hostPort != null ) {
			CommUtils.outcyan("connectService hostPort:     " + hostPort[0] +":"+hostPort[1]);
			return hostPort;
		}else {
			CommUtils.outred("Discoverable " + serviceName + " not found");
			return null;
		}			

	}
	
	
	protected boolean connectToService(String[]  hostPort) {
		try {
			 
			if( hostPort != null ) {
				String host = hostPort[0];
				String port = hostPort[1];
				CommUtils.outcyan("connectService Hostname: " + host);
				CommUtils.outcyan("connectService Port:     " + port);
 				connSupport = //new TcpConnection(host, Integer.parseInt(port));
				 ConnectionFactory.createClientSupport23(ProtocolType.tcp, host, port);
 				return true;
			}else {
				CommUtils.outred("no service available");
			}
		} catch (Exception e) {
			CommUtils.outred("ERROR:" + e.getMessage());
		}
		return false;
	}

	protected void useService( String[]  hostPort ) {
		try {
			connectToService(hostPort);
			
			Product p1 = new Product(21, "p21", 210); 
			String p1Json = CommUtils.toPrologStr(p1.toString(),true);
 			CommUtils.outcyan("doJob p1Json:" + p1Json);
			String pp = "product( JSON )".replace("JSON", p1Json);
			IApplMessage msg = CommUtils.buildRequest("caller", "createProduct", pp, "productservice");
			CommUtils.outblue("SEND:" + msg);
			IApplMessage answer = connSupport.request(msg);
			CommUtils.outblue("ANSWER:" + answer);			
		} catch (Exception e) {
			CommUtils.outred("ERROR:" + e.getMessage());
		}
		
	}
	
    
    protected void getProduct(int n) throws Exception {
        IApplMessage req = CommUtils.buildRequest("tcpcaller", "getProduct", "product(" + n + ")", "productservice");
        //CommUtils.outblue(name + " | sends request on " + connSupport);
        IApplMessage answer = connSupport.request(req);  //raise exception
        CommUtils.outmagenta("tcpcaller | getProduct answer=" + answer);
		
	}

	
	public void doJob() throws Exception   {
		CommUtils.outcyan("connectService doJob"  );
		String[]  hostPort = discoverProductService("ctxcargoservice");
		if( hostPort != null ) {
            useService(hostPort);
            getProduct(17);
            shutDownTheClient();
//    		getAllProducts();
		}

        System.exit(0);
	}
	
    protected void shutDownTheClient() throws Exception {
        // Chiudi il client Eureka
 		CommUtils.delay(1000); 
 		//deregister();
 		CommUtils.delay(1000); 
    	CommUtils.outblue("ServiceUsage shutting down the EurekaClient after 1 sec delay");
		connSupport.close();
        if(eurekaClient != null) eurekaClient.shutdown();   	
    }
	
    protected void deregister() {  //NON FUNZIONA
        InstanceInfo instanceInfo = eurekaClient.getApplicationInfoManager().getInfo();
        eurekaClient.shutdown(); // Questo cancella la registrazione dell'istanza corrente
        CommUtils.outblue("deregister done per l'istanza: " + instanceInfo.getInstanceId());
        
        //curl -X DELETE "http://localhost:8761/eureka/apps/productservice/79.22.134.219"
     }

	public static void main(String[] args) throws Exception  {
		//TCP call requires knowledge at system level!
 		ProductServiceDiscoverCallerTcp caller = new ProductServiceDiscoverCallerTcp( );
		caller.doJob();
	}

}
