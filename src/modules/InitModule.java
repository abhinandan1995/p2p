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
import p2pApp.p2pIndexer.TableHandler;
import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersEntries;
import tcpUtilities.PeersTable;
import utility.MySqlHandler;

public class InitModule {

	PeersTable peersTable;
	CallbackRegister callbackRegis;
	BaseNetworkEngine networkEngine;

	public InitModule(){

		peersTable= PeersTable.getInstance();
		callbackRegis= CallbackRegister.getInstance();
		networkEngine= BaseNetworkEngine.getInstance();
		SearchTable.getInstance();
	}

	public void initSystem() throws Exception{
		initPingPongCallbacks();
		initDirs();
		initUserValues();
		initSystemValues();

		getPausedDownloads();
		initPeersTable();
	}

	private void initPingPongCallbacks(){
		callbackRegis.registerForCallback("tcp-server-pong", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("tcp-server-ping", "baseServer.BaseNetworkEngine", "manageNeighboursList", false, networkEngine);
		callbackRegis.registerForCallback("ServerException-TimedOut", "baseServer.BaseNetworkEngine", "TimedOutHandler", false, networkEngine);
	}

	private void initSystemValues() throws Exception{

		String ip;
		System.out.println("\nInitialising System variables... \n");
		System.out.println("User-Ip: " + (ip= utility.Utilities.getIpAddress(utility.Utilities.baseIp)));
		System.out.println("User-Id: " + utility.Utilities.getSystemId()+"\n");

		if(ip==null){
			throw new Exception("** Unable to find a network connection. Connect to network. **");
		}

		ArrayList<String> names= new ArrayList<String>();
		for(int i=0;i<utility.Utilities.inputFolders.length;i++){
			if(utility.Utilities.inputFolders[i].trim().length()>2)
				names.add(utility.Utilities.inputFolders[i].trim().replace("\\", "/"));
		}
		names.add(utility.Utilities.outputFolder.trim().replace("\\","/"));

		if(!new File(utility.Utilities.outputFolder).exists()){
			new File(utility.Utilities.outputFolder).mkdirs();
		}

		System.out.println("Loading databases... \n");

		try{
			if(TableHandler.tableType.equals("mysql")){
				MySqlHandler.getInstance().TestDatabase();
				p2pApp.p2pIndexer.DirectoryReader.DR_init(names, true);
			}
			else{
				p2pApp.p2pIndexer.DirectoryReader.getInstance().indexDirectories(names);
			}
		}
		catch(Exception e){
			throw e;
		}

		System.out.println("Finished Loading databases... \n");

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
		}
		catch(Exception e){

		}
	}

	private void initUserValues() throws Exception{

		try{
			Properties props = new Properties();
			FileInputStream fis = null;
			fis = new FileInputStream("data/config.properties");
			props.load(fis);

			utility.Utilities.baseIp= props.getProperty("p2p.baseIp");
			utility.Utilities.outputFolder= props.getProperty("p2p.outputFolder");
			String str= props.getProperty("p2p.inputFolder");
			utility.Utilities.inputFolders= str.split(",");
			utility.Utilities.setSystemId(props.getProperty("p2p.systemId"));
		}
		catch(Exception e){
			throw new Exception("Error: Config file not found. "+e.getMessage()+" Go to settings to create a new file.");
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

	private void initDirs(){

		if(!new File("data/partials").exists())
			new File("data/partials").mkdirs();

		if(!new File("data/db-ms.properties").exists()){
			String data= "# mysql properties\n"+
					"# complete url= url+':'+port+'/'+database\n\n"+
					"mysql.driver=com.mysql.jdbc.Driver\n"+
					"mysql.url=jdbc:mysql://localhost\n"+
					"mysql.port=3306\n"+
					"mysql.database=test\n"+
					"mysql.username=root\n"+
					"mysql.password=\n"+
					"mysql.unicode=useUnicode=yes&characterEncoding=UTF-8\n";

			utility.Utilities.writeToFile("data/db-ms.properties", data, false);
		}
	}

}
