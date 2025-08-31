package main.java;
import java.util.UUID;

import com.netflix.appinfo.MyDataCenterInstanceConfig;

import unibo.basicomm23.utils.CommUtils;

/**
 * EurekaServiceConfig - Eureka Service Registration Configuration
 * 
 * This class extends MyDataCenterInstanceConfig to provide custom configuration
 * for registering the BasicRobot25 service with Eureka service discovery.
 * It defines how the robot service appears in the Eureka registry and how
 * it communicates with the Eureka server for service discovery.
 * 
 * Key responsibilities:
 * - Service name and identification configuration
 * - Host and IP address management
 * - Port configuration for service communication
 * - Heartbeat and lease management for service health
 * - Instance ID generation for unique service identification
 * 
 * The class supports both environment variable configuration and fallback
 * default values for flexible deployment scenarios.
 * 
 * Used by ServiceRegistered.java for automatic service registration.
 * 
 * @author Unibo BasicRobot25 Team
 * @version 2025
 */
public class EurekaServiceConfig extends MyDataCenterInstanceConfig {
	
	/**
	 * Returns the application name for Eureka service registration.
	 * This name is used to identify the service in the Eureka registry
	 * and is used by clients for service discovery.
	 * 
	 * @return The application name "ctxbasicrobot"
	 */
	@Override
	public String getAppname() {
		return "ctxcargoservice";
	}
	
	/**
	 * Returns the hostname for service registration.
	 * Checks for environment variable SERVICE_HOST first, then falls back
	 * to a default IP address if not configured.
	 * 
	 * @param refresh Whether to refresh the hostname (not used in this implementation)
	 * @return The hostname/IP address for service registration
	 */
	@Override
	public String getHostName(boolean refresh) {
//	   	CommUtils.outgreen("		EurekaServiceConfig getHostName="  + System.getenv("SERVICE_HOST") );
		String ip = "";
		String serviceshost = System.getenv("SERVICE_HOST");
 		if( serviceshost != null) {
 			ip = serviceshost;
 		} else {
 			ip = "192.168.1.132"; // Default fallback IP
 		}
 		return ip;
//		return "192.168.1.132";
	}
	
	/**
	 * Returns the IP address for service registration.
	 * Checks for environment variable SERVICE_IP first, then falls back
	 * to a default IP address if not configured.
	 * 
	 * This IP address is used by Eureka clients to connect to this service
	 * after discovering it through the Eureka registry.
	 * 
	 * @return The IP address for service registration
	 */
    @Override
    public String getIpAddress() {
//    	CommUtils.outmagenta("		EurekaServiceConfig getIpAddress=" + System.getenv("SERVICE_IP") );
        String ipAddress = System.getenv("SERVICE_IP");
        if (ipAddress != null ) {
        	CommUtils.outmagenta("		EurekaServiceConfig getIpAddress=" + ipAddress);
            return ipAddress;
         } 
        return "192.168.1.132"; // Default fallback IP
//    	return "192.168.1.132";
    }

	/**
	 * Returns the non-secure port for service communication.
	 * This is the port on which the robot service accepts connections
	 * from clients after they discover the service through Eureka.
	 * 
	 * @return The service port (8020)
	 */
	@Override
	public int getNonSecurePort() {
		return 8111;
	}

    /**
     * Returns the lease expiration duration in seconds.
     * 
     * This indicates the time in seconds that the Eureka server waits since
     * it received the last heartbeat before it can remove this instance from
     * its view and thereby disallowing traffic to this instance.
     * 
     * Default Eureka value: 90 seconds
     * This implementation: 3600 seconds (1 hour)
     * 
     * @return The lease expiration duration in seconds
     */
	@Override
	public int getLeaseExpirationDurationInSeconds(){
		return 60*10*6; // 3600 seconds = 1 hour
	}
    
    /**
     * Returns the lease renewal interval in seconds.
     * 
     * This indicates how often (in seconds) the Eureka client needs to send
     * heartbeats to the Eureka server to indicate that it is still alive.
     * 
     * Default Eureka value: 30 seconds
     * This implementation: 3600 seconds (1 hour)
     * 
     * @return The lease renewal interval in seconds
     */ 
	@Override
	public int getLeaseRenewalIntervalInSeconds() {
		return 60*10*6; // 3600 seconds = 1 hour
	}
	
	/**
	 * Returns a unique instance ID for this service.
	 * 
	 * Generates a unique identifier using the application name and a random UUID.
	 * This ensures that each service instance has a unique identifier in the
	 * Eureka registry, even if multiple instances of the same service are running.
	 * 
	 * Alternative formats could be:
	 * - host:app:port (Spring default style)
	 * - IP:port combination
	 * 
	 * @return A unique instance ID for Eureka registration
	 */
	@Override 
	public String getInstanceId() {
	    // Generate a unique ID using app name and UUID
	    return getAppname() + ":" + UUID.randomUUID().toString();
	    
	    // Alternative: host:app:port format (Spring default style)
	    // return super.getHostName(false) + ":" + getAppname() + ":" + getNonSecurePort();
	}
}


//ELIMINZIONE
//curl -X DELETE http://localhost:8761/eureka/apps/CTXCARGOSERVICE/192.168.1.132