package modules;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import baseServer.BaseNetworkEngine;
import p2pApp.SearchTable;
import p2pApp.p2pDownloader.DownloadEngine;
import p2pApp.p2pDownloader.DownloadNodes;
import p2pApp.p2pDownloader.SaveDownloadNodes;
import tcpServer.BaseController;
import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersEntries;
import tcpUtilities.PeersTable;
import utility.MySqlHandler;

public class InitModule {

	PeersTable peersTable;
	CallbackRegister callbackRegis;
	BaseNetworkEngine networkEngine;
	boolean shutDown= false;
	
	public InitModule(){
		
		peersTable= PeersTable.getInstance();
		callbackRegis= CallbackRegister.getInstance();
		networkEngine= BaseNetworkEngine.getInstance();
		MySqlHandler.getInstance();
		SearchTable.getInstance();
		
		initPingPongCallbacks();
		initUserValues();
		getPausedDownloads();
		
		initSystemValues();
		initPeersTable();
		
		
	}
	
	private void initPingPongCallbacks(){
//		callbackRegis.registerForCallback("tcp-server-pong", "tcpUtilities.PeersTable", "echoEntries", false, peersTable);
		callbackRegis.registerForCallback("tcp-server-pong", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-ping", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
//		callbackRegis.registerForCallback("tcp-server-pong-point", "modules.TCPServerModule", "", false, this);		
		callbackRegis.registerForCallback("ServerException-TimedOut", "baseServer.BaseNetworkEngine", "TimedOutHandler", false, networkEngine);
	}
	
	private void initSystemValues(){
		
		if(shutDown){
			return;
		}
		
		String ip;
		System.out.println("\nInitialising System variables... \n");
		System.out.println("User-Ip: " + (ip= utility.Utilities.getIpAddress(utility.Utilities.baseIp)));
		System.out.println("User-Id: " + utility.Utilities.getSystemId());
		System.out.println("\n");
		
		if(ip==null){
			System.out.println("\n *** Unable to find a network connection. Shutting down. *** ");
			stopServer();
		}
		
		if(shutDown)
			return;
		
		try{
			MySqlHandler.getInstance().TestDatabase();
		}
		catch(Exception e){
			System.out.println("Can't connect to database. : "+e.getMessage());
			stopServer();
			return;
		}
		
		System.out.println("Loading databases... \n");
		
		ArrayList<String> names= new ArrayList<String>();
		for(int i=0;i<utility.Utilities.inputFolders.length;i++){
			names.add(utility.Utilities.inputFolders[i].trim());
		}
		names.add(utility.Utilities.outputFolder.trim());
		
		p2pApp.p2pIndexer.DirectoryReader.DR_init(names, true);
		System.out.println("Finished loading databases. \n");
	}
	
	private void initPeersTable(){
		
		if(shutDown)
			return;
		
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
			stopServer();
		}
	}
	
	private void stopServer(){
		try{
			shutDown= true;
			BaseController.getInstance().stopServer();
		}
		catch(Exception e){
			System.out.println("Init Module: Can't stop server. "+e.getMessage());
		}
		
	}
	
	private void getPausedDownloads(){
		
		ArrayList<DownloadNodes> an= new ArrayList<DownloadNodes>();
		File aFile= new File("data/partials");
		File[] listOfFiles = aFile.listFiles();
		if(listOfFiles!=null) {
			for (int i = 0; i < listOfFiles.length; i++){
				try{
					String content= new String(Files.readAllBytes(Paths.get(listOfFiles[i].getAbsolutePath())));
					//listOfFiles[i].delete();
					DownloadNodes dn=new DownloadNodes((SaveDownloadNodes)utility.Utilities.getObjectFromJson(content, SaveDownloadNodes.class)); 
					if(new File(utility.Utilities.outputFolder+utility.Utilities.parseInvalidFilenames(dn.getSearchResults().getFilename())).exists())
						an.add(dn);
					else
						listOfFiles[i].delete();
				}
				catch(Exception e){
					listOfFiles[i].delete();
					System.out.println("Error while loading paused downloads: "+e.getMessage());
				}
			}
			DownloadEngine.getInstance().AddPausedDownloads(an);
		}
	}
	
}
