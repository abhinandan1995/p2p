package modules;

import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersTable;
import utility.MySqlHandler;
import baseServer.BaseNetworkEngine;

public class InitModule {

	PeersTable peersTable;
	CallbackRegister callbackRegis;
	BaseNetworkEngine networkEngine;
	
	public InitModule(){
		
		peersTable= PeersTable.getInstance();
		callbackRegis= CallbackRegister.getInstance();
		networkEngine= BaseNetworkEngine.getInstance();
		MySqlHandler.getInstance();
		
		initPingPongCallbacks();
		initSystemValues();
		//ModuleLoader.getInstance();
	}
	
	private void initPingPongCallbacks(){
		callbackRegis.registerForCallback("tcp-server-pong", "tcpUtilities.PeersTable", "echoEntries", false, peersTable);
		callbackRegis.registerForCallback("tcp-server-pong", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-ping", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-pong-point", "modules.TCPServerModule", "", false, this);			
	}
	
	private void initSystemValues(){
		System.out.println("\nInitialising System variables... \n");
		System.out.println("User-Ip: " + utility.Utilities.getIpAddress(utility.Utilities.baseIp));
		System.out.println("User-Id: " + utility.Utilities.getSystemId());	
	}
	
}
