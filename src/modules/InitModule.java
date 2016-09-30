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
		
		initPingPointCallback();
		initSystemValues();
		//ModuleLoader.getInstance();
	}
	
	public void initPingPointCallback(){
		callbackRegis.registerForCallback("tcp-server-pong", "tcpUtilities.PeersTable", "echoEntries", false, peersTable);
		callbackRegis.registerForCallback("tcp-server-pong", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-ping", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-pong-point", "modules.TCPServerModule", "", false, this);			
	}
	
	public void initSystemValues(){
		System.out.println("User ip:" + utility.Utilities.getIpAddress(utility.Utilities.baseIp));
		System.out.println("User id:" + utility.Utilities.getSystemId());	
	}
	
}
