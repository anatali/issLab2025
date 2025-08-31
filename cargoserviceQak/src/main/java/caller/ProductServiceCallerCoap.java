package main.java.caller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.kactor.sysUtil;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
 
public class ProductServiceCallerCoap extends AbstractCaller{
	private static final Logger logger = LoggerFactory.getLogger("ProductServiceCallerCoap");
	private String pstr =  "{\"productId\":59,\"name\":\"p59\",\"weight\":593}";
	private String pstr1 =  "productId_53_name_p53_weight_533";
	private String p    = "product("+ sysUtil.toPrologStr( pstr, true ) + ")";
	
	public ProductServiceCallerCoap(String name, ProtocolType protocol, String hostAddr, String entry) {
		super(name, protocol, hostAddr, entry);
		sysUtil.clearlog("./logs/app_cargoproduct.log");
//		connect();  //lo fa nel Thread di AbstractCaller
		logger.info("avviato correttamente.");
//		logger.debug("Questo è un messaggio di debug.");
	}

	@Override
	protected void body() throws Exception {
//         createAProduct("product(1,p1,10)");
        //createAProduct( "product('a_b_c_d')" ); 
		CommUtils.outcyan("p = " + p);
//		String encodedMessage = URLEncoder.encode(p, StandardCharsets.UTF_8.toString());
//		CommUtils.outcyan("encodedMessage=" + encodedMessage);
        //createAProduct(encodedMessage); 
		createAProduct(p);
//         createAProduct("product(3,p3,30)");
         getProduct(59);
//        if( getProduct(1) ) deleteProduct(1);
//        else CommUtils.outred("Product not found" + 1);  
        System.exit(0);
	}
	
//	protected void createAProduct(String p) throws Exception {
//		IApplMessage req = CommUtils.buildRequest(name, "createProduct", p, "productservice");
//		//CommUtils.outblue(name + " | sends request on " + connSupport);
//		IApplMessage answer = commChannel.request(req); // raise exception
//		CommUtils.outblue(name + " | createAProduct answer=" + answer);
//	}
//	protected boolean getProduct(int n) throws Exception {
//        IApplMessage req = CommUtils.buildRequest(name, "getProduct", "product(" + n + ")", "productservice");
//        //CommUtils.outblue(name + " | sends request on " + connSupport);
//        IApplMessage answer = commChannel.request(req);  //raise exception
////        String outs         = answer.msgContent();
//        CommUtils.outblue(name + " | getProduct answer=" + answer);
//        return ! answer.toString().contains("error");
//		
//	}
	protected void deleteProduct(int n) throws Exception {
		IApplMessage req = CommUtils.buildRequest(name, "deleteProduct", "product(" + n + ")", "productservice");
        //CommUtils.outblue(name + " | sends request on " + connSupport);
        IApplMessage answer = commChannel.request(req);  //raise exception
        CommUtils.outblue(name + " | deleteProduct answer=" + answer );
	}
	
	/*
	protected void addObservation(Interaction conn) {
		//CoapClient client = new CoapClient("coap://localhost:8919/ctxqakms025/ms0" );  
		
		CoapConnection coapConn = (CoapConnection)conn;
		CoapClient client = coapConn.getClient();
		
	    CommUtils.outblue("callerCoap addObservation client"  );
		CoapObserveRelation relation = client.observe(
				new CoapHandler() {
					@Override public void onLoad(CoapResponse response) {
						String content = response.getResponseText();
						CommUtils.outgreen("ActorObserver | value=" + content );
					}					
					@Override public void onError() {
						CommUtils.outred("OBSERVING FAILED  ");
					}
				});	
		
		CommUtils.delay(3000);
		relation.proactiveCancel();
		CommUtils.outblue("ActorObserver | ENDS"   );
		System.exit(0);
	}
*/
	 
	public static void main(String[] args) {
		ProductServiceCallerCoap caller = 
			new ProductServiceCallerCoap(
					"callercoap", ProtocolType.coap,"localhost:8111", "ctxcargoservice/productservice"); //productservice
		caller.activate();
	}

}
