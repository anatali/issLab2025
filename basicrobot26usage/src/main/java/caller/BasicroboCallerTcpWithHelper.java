package main.java.caller;

import java.util.Scanner;
import main.java.BasicRobotMovesHelper;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.utils.CommUtils;


public class BasicroboCallerTcpWithHelper  {
   
 private BasicRobotMovesHelper brobothelper = new BasicRobotMovesHelper();

    
    protected void runWrongPlan() throws Exception   {
        //moveToHome();  //prudenziale per mettere down
    	/*
    	 * Build a safe plan from (0,0) to (1,4)
    	 */
    	String plan = brobothelper.buildPlan(0, 0, 1, 4);
 		/*
		 * Execute a wrong plan so that it fails
		 */
     	 String wrongPlan = plan + "w";
		 CommUtils.outblue("plan=" +  plan + " wrongPlan:" +  wrongPlan  );
		 boolean result = brobothelper.doPLan(wrongPlan);
		 
		 if( ! result ) {
            CommUtils.outred("Plan failed");
		 }
	}
 
     
	public void selectAndRunBehavior() throws Exception {
		Interaction conn = brobothelper.connectToService("localhost", "8020");
		if( conn != null ) {
		 Scanner scanner = new Scanner(System.in);
		 int scelta = 0;
		 while( scelta != 99 ) {
		 try {
		    System.out.println("Seleziona un comportamento:");
		    System.out.println("0. Exit");
		    System.out.println("1. Comandi base (doSomeCmd)");
		    System.out.println("2. Esegui piano che fallisce (runWrongPlan)");
		    System.out.println("3. Vai a Home (moveToHome)");
		    System.out.println("4. Tune aat Home (tuneAtHome)");
		    System.out.println("5. Direction Up (setDirectionUp)");
		    System.out.println("6. Direction Down (setDirectionDown)");
		    System.out.println("7. Esgui step (dostep)");
		    System.out.println("8. Da home a 3,5 (move35)");
		    System.out.println("9. Stato del brobothelper (getRobotState)");
		    System.out.println("10. Hide plan thinking (resesetplanbuildelay)");
		    System.out.println("11. Show plan thinking (setplanbuildelay)"); 
		    System.out.println("12. Temafinale25 (tf25)");
		    System.out.println("13. Do boundary (boundary)");
		    System.out.println("14. Set robot mind at home (setRobotAtHome)");
		    System.out.print("Scelta: ");
		    
		    scelta          = scanner.nextInt();		    
		    CommUtils.outblue("scelta=" + scelta);
	        
		    switch (scelta) { 
	        	case 0:
	        		CommUtils.outblue("BYE");
	        		scanner.close();
		            System.exit(0);
		            break;
		        case 1:
		        	brobothelper.doSomeCmd();
		            break;
		        case 2:
		            runWrongPlan();
		            break;
		        case 3:
		        	brobothelper.moveToHome();
		            break;
		        case 4:
		        	brobothelper.tuneAtHome();
		            break;
		        case 5:
		        	brobothelper.setDirectionUp();
		            break;
		        case 6:
		        	brobothelper.setDirectionDown();
		            break;
		        case 7:
		        	brobothelper.dostep();
		            break;
		        case 8:
		        	brobothelper.move35();
		            break;
		        case 9:
		        	brobothelper.getRobotState();  
		            break;
		        case 10:
		        	brobothelper.resetplanbuildelay();
		            break;
		        case 11:
		        	brobothelper.setplanbuildelay();
		            break;
		        case 12:
		        	brobothelper.tf25();
		            break;
		        case 13:
		        	brobothelper.boundary();
		            break;
		        case 14:
		        	brobothelper.setRobotAtHome();
		            break;
		        default:
		            System.out.println("Scelta non valida.");
		    }
		  }catch(Exception e) {
              CommUtils.outred("BasicroboCallerTcpWithHelper ERROR:" + e.getMessage());
              scelta          = scanner.nextInt();
		  }
		 }//while
		} else {
			CommUtils.outred("no connection");
		}	
		 
	}
 


	public static void main(String[] args) throws Exception  {
 		BasicroboCallerTcpWithHelper caller = new BasicroboCallerTcpWithHelper();
 		caller.selectAndRunBehavior();
	}
}

 
 