package modules;

import java.util.ArrayList;

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
//		callbackRegis.registerForCallback("tcp-server-pong", "tcpUtilities.PeersTable", "echoEntries", false, peersTable);
		callbackRegis.registerForCallback("tcp-server-pong", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-ping", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
//		callbackRegis.registerForCallback("tcp-server-pong-point", "modules.TCPServerModule", "", false, this);		
		callbackRegis.registerForCallback("ServerException-TimedOut", "baseServer.BaseNetworkEngine", "TimedOutHandler", false, networkEngine);
	}
	
	private void initSystemValues(){
		System.out.println("\nInitialising System variables... \n");
		System.out.println("User-Ip: " + utility.Utilities.getIpAddress(utility.Utilities.baseIp));
		System.out.println("User-Id: " + utility.Utilities.getSystemId());
		System.out.println("\n");
		
		System.out.println("Loading databases... \n");
		ArrayList<String> names= new ArrayList<String>();
		names.add("F:/");
		p2pApp.p2pIndexer.DirectoryReader.DR_init(names, true);
		System.out.println("\nFinished loading. \n");
	}
	
}
