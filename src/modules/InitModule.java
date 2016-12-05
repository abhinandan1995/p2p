package modules;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersEntries;
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
		initUserValues();
		
		initSystemValues();
		initPeersTable();
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
		for(int i=0;i<utility.Utilities.inputFolders.length;i++){
			
			names.add(utility.Utilities.inputFolders[i].trim());
		}
		p2pApp.p2pIndexer.DirectoryReader.DR_init(names, true);
		System.out.println("\nFinished loading. \n");
	}
	
	private void initPeersTable(){
		String s= utility.Utilities.readFromIpFile("data/ips.dat");
		try{
			
		String [] arr= s.split("\n");
		for(int i=0;i <arr.length;i++){
			String []temp= arr[i].split(" ");
			PeersTable.getInstance().addEntry(temp[0], temp[1], "connected");
		}
		
		List<PeersEntries> pe= PeersTable.getInstance().getAll();
		for(int i=0;i<pe.size();i++){
			utility.Utilities.writeToFile("data/ips.dat", pe.get(i).ip+" "+pe.get(i).systemId, false);
		}
		System.out.println(s);
		}
		catch(Exception e){
			
		}
	}
	
	private void initUserValues(){

		try{
			Properties props = new Properties();
			FileInputStream fis = null;
			fis = new FileInputStream("data/config.properties");
			props.load(fis);
			
			utility.Utilities.baseIp= props.getProperty("p2p.baseIp");
			utility.Utilities.outputFolder= props.getProperty("p2p.outputFolder");
			String str= props.getProperty("p2p.inputFolder");
			utility.Utilities.inputFolders= str.split(",");
		}
		catch(Exception e){
			System.out.println("Exception: Config file not found!");
		}

	}
	
}
