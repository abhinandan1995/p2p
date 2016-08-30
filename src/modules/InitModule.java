package modules;

import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersTable;

public class InitModule {

	PeersTable peersTable;
	CallbackRegister callbackRegis;
	public InitModule(){
		peersTable= PeersTable.getInstance();
		callbackRegis= CallbackRegister.getInstance();
		
		initPingPointCallback();
	}
	
	public void initPingPointCallback(){
		callbackRegis.registerForCallback("tcp-server-pong", "tcpUtilities.PeersTable", "echoEntries", false, peersTable);
		callbackRegis.registerForCallback("tcp-server-pong-point", "modules.TCPServerModule", "", false, this);
	}
}
