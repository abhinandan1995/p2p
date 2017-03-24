package p2pApp.p2pUi.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import baseServer.BaseNetworkEngine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import modules.InitModule;
import p2pApp.SearchResults;
import p2pApp.SearchTable;
import p2pApp.p2pDownloader.DownloadEngine;
import p2pApp.p2pDownloader.DownloadRequest;
import p2pApp.p2pQueries.GetDirQuery;
import p2pApp.p2pQueries.SearchQuery;
import tcpQueries.PingQuery;
import tcpServer.BaseController;
import tcpUtilities.CallbackRegister;
import tcpUtilities.PeersTable;

public class UIController implements Initializable{

	@FXML private TextField searchField;
	@FXML private Button searchButton;
	//	@FXML private TableView<Records> resultsTable;
	//	@FXML private TableColumn<Records, String> Source;
	//    @FXML private TableColumn<Records, String> Filename;
	//    @FXML private TableColumn<Records, String> Filesize;
	//    @FXML private TableColumn<Records, Boolean> Download;
	@FXML public ListView<SearchResults> resultList;
	@FXML private Button startServer;
	@FXML private Button settings;
	@FXML private AnchorPane anchorPane;
	@FXML private Label totalResults;
	@FXML private TextArea consoleArea;
	@FXML private TextField textIp;
	@FXML private ListView<String> peersList;

	private PrintStream ps ;
	private final ObservableList<SearchResults> results =
			FXCollections.observableArrayList();   
	private final ObservableList<String> peers= 
			FXCollections.observableArrayList();

	@FXML protected void openPreferences(ActionEvent ae){
		showPreferencesDialog();
	}

	@FXML protected void initServer(ActionEvent ae){

		if(startServer.getText().equals("Stop")){
			totalResults.setText("Server Offline");
			try{
				BaseController.getInstance().stopServer();
				startServer.setText("Start");
			}
			catch(Exception e){

			}
		}
		else{
			totalResults.setText("Server Online");
			startServer();
		}
	}

	@FXML protected void pingIp(ActionEvent ae){
		String text= textIp.getText();
		textIp.setText("");
		if(utility.Utilities.validateIp(text))
			BaseController.getInstance().sendRequest(new PingQuery(), "tcp-server", "PingQuery", true, "", text);
		else
			System.out.println("\n** Error while pinging. Enter valid ip address!");
	}

	@FXML protected void handleClickOnSearch(ActionEvent ae){
		performSearch();
		//		if(str.equals("init"))
		//			init();
		//		else
		//			addRow();
	}

	@FXML protected void handleKeyOnSearch(KeyEvent ke){
		if(ke.getCode()== KeyCode.ENTER)
			performSearch();
	}

	private class ResultRowCell extends ListCell<SearchResults>{
		private ResultRow listRow= null;
		ResultRowCell(){
			listRow= new ResultRow(UIController.this);
		}
		@Override
		public void updateItem(SearchResults sr, boolean empty){
			super.updateItem(sr, empty);
			if(sr!=null){
				listRow.setDetails(sr);
				setGraphic(listRow.getRow());
			}
		}
	}

	private void performSearch(){
		String str= searchField.getText();
		//		System.out.println(str);
		totalResults.setText("Searching...");
		BaseNetworkEngine.getInstance().sendMultipleRequests(new SearchQuery(SearchTable.getInstance().getNewSearchId(), "search", str, null), "p2p-app", "SearchQuery", false);
	}

	@Override 
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		//        assert myButton != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

		// initialize your logic here: all @FXML variables will have been injected

		//totalResults.setText("hello");
		ps = new PrintStream(new Console(consoleArea)) ;
		System.setOut(ps);
		System.setErr(ps);
		System.out.println("System status area");

		peersList.setItems(peers);
		peersList.setTooltip(new Tooltip("Shows the list of peers with which you are connected"));
		consoleArea.setTooltip(new Tooltip("Shows the status of the system"));

		resultList.setItems(results);
		resultList.setCellFactory(new Callback<ListView<SearchResults>, ListCell<SearchResults>>()
		{
			@Override
			public ListCell<SearchResults> call(ListView<SearchResults> listView)
			{
				return new ResultRowCell();
			}
		});

		//		Source.setCellValueFactory(new PropertyValueFactory<Records, String>("Source"));
		//		Filename.setCellValueFactory(new PropertyValueFactory<Records, String>("Filename"));
		//		Filesize.setCellValueFactory(new PropertyValueFactory<Records, String>("Filesize"));
		//		//Download.setCellValueFactory(new PropertyValueFactory<Records, Boolean>("Download"));
		//		
		//		Download.setSortable(false);
		//		// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
		//	    Download.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Records, Boolean>, ObservableValue<Boolean>>() {
		//	      @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Records, Boolean> features) {
		//	        return new SimpleBooleanProperty(features.getValue() != null);
		//	      }
		//	    });
		//
		//	 // create a cell value factory with an add button for each row in the table.
		//	    Download.setCellFactory(new Callback<TableColumn<Records, Boolean>, TableCell<Records, Boolean>>() {
		//	      @Override public TableCell<Records, Boolean> call(TableColumn<Records, Boolean> personBooleanTableColumn) {
		//	        return new DownloadFile(resultsTable);
		//	      }
		//	    });

		/*
		//resultsTable.setEditable(true);

//	    resultsTable.setRowFactory(new Callback<TableView<Records>, TableRow<Records>>() {
//			@Override
//			public TableRow<Records> call(TableView<Records> tv) {
//				return new TableRow<Records>() {
//				    private Tooltip tooltip = new Tooltip();
//				    @Override
//				    public void updateItem(Records record, boolean empty) {
//				        super.updateItem(record, empty);
//				        if (record == null) {
//				            setTooltip(null);
//				        } else {
//				           // tooltip.setText(person.getFirstName()+" "+person.getLastName());
//				            tooltip.setText(record.getFilename());
//				        	setTooltip(tooltip);
//				        }
//				    }
//				};
//			}
//		});
		 */

		//	    Source.setCellFactory
		//	    (
		//	      new Callback<TableColumn<Records, String>, TableCell<Records, String>>() {
		//			@Override
		//			public TableCell<Records, String> call(TableColumn<Records, String> column) {
		//			     return new TableCell<Records, String>()
		//			      {
		//			        @Override
		//			        protected void updateItem(String item, boolean empty)
		//			         {
		//			            super.updateItem(item, empty);
		//			            
		//			            String txt= "Source: "+ item;
		//			            try{
		//			            ArrayList<AlternateIps> as= SearchTable.getInstance().getFromSearchTable(getTableRow().getIndex()).getAlternateIps();
		//			            	if(as.size()>0){
		//			            		txt= "Multiple sources available";
		//			            		txt= txt+"\n"+item;
		//			            		for(int i=0;i<as.size();i++)
		//			            			txt= txt+"\n"+as.get(i).getIp();
		//			            		
		//			            		setText(item + "   +"+ as.size());
		//			            	}
		//			            	else
		//			            	setText(item);
		//			            }
		//			            catch(Exception e){
		//			            	setText( item );
		//			            }
		//			            
		//			            setTooltip(new Tooltip(txt));
		//			         }
		//			      };
		//			   }
		//		});
		//	    
		//	    Filename.setCellFactory
		//	    (
		//	      new Callback<TableColumn<Records, String>, TableCell<Records, String>>() {
		//			@Override
		//			public TableCell<Records, String> call(TableColumn<Records, String> column) {
		//			     return new TableCell<Records, String>()
		//			      {
		//			        @Override
		//			        protected void updateItem(String item, boolean empty)
		//			         {
		//			            super.updateItem(item, empty);
		//			            setText( item );
		//			            if(item!= null && item.contains("(DIR)")){
		//			            	setTooltip(new Tooltip("This is a folder consisting of multiple files"));
		//			            }
		//			            else
		//			            setTooltip(new Tooltip(item));
		//			         }
		//			      };
		//			   }
		//		});
		//	    
		//	    
		//		resultsTable.setItems(records);
		//		//records.add(new Records("349", "Ronaldo", "67mb"));
	}
	/*
//	private void init(){
//		System.out.println("called");
//		resultsTable.setEditable(true);
////		TableColumn firstNameCol = new TableColumn("Source");
////	    firstNameCol.setCellValueFactory(new PropertyValueFactory<Records,String>("Source"));
//		resultsTable.setItems(data);
//	}
//	
//
//	private void addRow(){
//		data.add(new Records("192","Ronaldo", "400Mb"));
//	}
	 */

	//	public class Records {
	//        private final SimpleStringProperty Source = new SimpleStringProperty("");
	//        private final SimpleStringProperty Filename = new SimpleStringProperty("");
	//        private final SimpleStringProperty Filesize = new SimpleStringProperty("");
	//        //private final SimpleStringProperty Download = new SimpleStringProperty("");
	//       // public String type= "2";
	//        
	//        public Records(SearchResults sr){
	//        	setSource(sr.getIp());
	//        	if(sr.getType().equals("2"))
	//        		setFilename(" (DIR) "+ sr.getFilename());
	//        	else
	//        		setFilename( sr.getFilename());
	//        	setFilesize( utility.Utilities.humanReadableByteCount(sr.getFileSize(), false));
	//        }
	//        public Records(String source, String filename, String filesize){
	//        	setSource(source);
	//        	setFilename(filename);
	//        	setFilesize(filesize);
	//        	//setDownload("download");
	//        	
	//        }
	//        
	//        public void setSource(String s){
	//        	Source.set(s);
	//        }
	//        public void setFilename(String s){
	//        	Filename.set(s);
	//        }
	//        public void setFilesize(String s){
	//        	Filesize.set(s);
	//        }
	////        public void setDownload(String s){
	////        	Download.set(s);
	////        }
	////        
	//        public String getSource(){
	//        	return Source.get();
	//        }
	//        
	//        public String getFilename(){
	//        	return Filename.get();
	//        }
	//        
	//        public String getFilesize(){
	//        	return Filesize.get();
	//        }
	//        
	////        public String getDownload(){
	////        	return Download.get();
	////        }
	//    }

	public void addResults(final ArrayList<SearchResults> sr){
		results.clear();		
		for(int i=0; i < sr.size();i++){
			results.add(sr.get(i));
		}

		Platform.runLater(new Runnable(){
			public void run(){		
				totalResults.setText("Total Results: ".toString()+sr.size());
			}
		});

	}

	public static void appendResults(ArrayList<SearchResults> sr){

	}

	//	private class DownloadFile extends TableCell<Records, Boolean> {
	//	    // a button for adding a new person.
	//		
	//	    final Button addButton       = new Button("Download");
	//	    // pads and centers the add button in the cell.
	//	    final StackPane paddedButton = new StackPane();
	//	    // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
	//	    final DoubleProperty buttonY = new SimpleDoubleProperty();
	//
	//	    /**
	//	     * AddPersonCell constructor
	//	     * @param stage the stage in which the table is placed.
	//	     * @param table the table to which a new person can be added.
	//	     */
	//	    DownloadFile(final TableView<Records> table) {
	//	      paddedButton.setPadding(new Insets(3));
	//	      paddedButton.getChildren().add(addButton);
	//	     
	//	      addButton.setOnMousePressed(new EventHandler<MouseEvent>() {
	//	        @Override public void handle(MouseEvent mouseEvent) {
	//	          buttonY.set(mouseEvent.getScreenY());
	//	        }
	//	      });
	//	      addButton.setOnAction(new EventHandler<ActionEvent>() {
	//	        @Override public void handle(ActionEvent actionEvent) {
	//	        //  showAddPersonDialog(stage, table, buttonY.get());
	//	        	SearchResults sr= SearchTable.getInstance().getFromSearchTable(
	//	        			getTableRow().getIndex());
	//	        	
	//	        	if(sr.getType().equals("1")){
	//	        		//DownloadEngine.getInstance().addDownload(sr);
	//	        		showFileDownloadDialog(sr);
	//	        	}
	//	        	if(sr.getType().equals("2")){
	//	        			//GetDirQuery.getDirQuery(sr);
	//	        		showDirDownloadDialog(sr);
	//	        	}
	//	          table.getSelectionModel().select(getTableRow().getIndex());
	//	        }
	//	      });
	//	    }
	//
	//	    /** places an add button in the row only if the row is not empty. */
	//	    @Override protected void updateItem(Boolean item, boolean empty) {
	//	      super.updateItem(item, empty);
	//	      if (!empty) {
	//	        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	//	        setGraphic(paddedButton);
	//	        setTooltip(new Tooltip("Download the file"));
	//	      } else {
	//	        setGraphic(null);
	//	      }
	//	    }
	//	  }
	//

	public void consoleSupport(){
		BaseController baseController= BaseController.getInstance();
		String input="";
		Scanner sc=new Scanner(System.in);

		while(true){

			input= sc.nextLine();
			if(input.contains("close")){

				try{
					baseController.stopServer();
					sc.close();
					break;
				}
				catch(Exception e){
					System.out.println("Failed to stop the server: "+e.getMessage());
				}

			}
			else if(input.contains("ping-message-all")){
				String data= input.substring(17);
				BaseNetworkEngine.getInstance().sendMultipleRequests(new PingQuery("ping-message-all", null, null, data), "tcp-server", "PingQuery", false);
			}
			else if(input.contains("ping-message")){
				String data= input.substring(12);
				baseController.sendRequest(new PingQuery("ping-message", null, null, data), "tcp-server", "PingQuery", false, "", utility.Utilities.getIpAddress());
			}
			else if(input.contains("ping")){
				String data= input.substring(input.indexOf("1"));
				baseController.sendRequest(new PingQuery(input.substring(0, input.indexOf("1")-1),null,null), "tcp-server", "PingQuery", true, "", data);
				//baseController.sendRequest(new PingQuery("ping",null,null), "tcp-server","PingQuery", true, "", ip));
			}
			else if(input.contains("show-peers")){
				PeersTable.getInstance().echoEntries();
			}
			else if(input.contains("show-npeers")){
				PeersTable.getInstance().echoNeighbours();
			}
			else if(input.contains("query")){

				BaseNetworkEngine.getInstance().sendMultipleRequests(new SearchQuery(SearchTable.getInstance().getNewSearchId(), "search", input.substring(6, input.length()), null), "p2p-app", "SearchQuery", false);
				//baseController.sendRequest(input.substring(6, input.length()), "p2p-app", "string", false, "", utility.Utilities.getIpAddress());
			}
			else if(input.contains("download")){
				String []arr= input.split(" ");
				new DownloadRequest(arr[2],arr[1],arr[3]);
			}
			else if(input.contains("getfile")){
				String []arr= input.split(" ");
				int index= Integer.parseInt(arr[1]);
				SearchResults sr= SearchTable.getInstance().getFromSearchTable(index);
				new DownloadRequest(sr.getFileId(), sr.getIp(), sr.getFilename(), sr.getUserid());
			}
			else
				baseController.sendRequest(input, "tcp-server", "string", true, "", utility.Utilities.getIpAddress());
		}

	}

	private void startServer(){

		Thread serv= new Thread(){
			public void run(){
				BaseController.getInstance().startServer();
				new InitModule();
				CallbackRegister.getInstance().registerForCallback("p2p-app-results", "p2pApp.p2pUi.controller.UIController", "handleResults", false, UIController.this);
				CallbackRegister.getInstance().registerForCallback("tcp-server-neighbours", "p2pApp.p2pUi.controller.UIController", "modifyPeersList", false, UIController.this);
				//returns true if peers table has some entries (some possibility for connections).
				//returns false if peers table is empty.
				BaseNetworkEngine.getInstance().connectToNetwork();


				//infinite loop. Perform all actions before it.
				consoleSupport();
			}
		};
		serv.setDaemon(true);
		serv.start();
		startServer.setText("Stop");
	}

	public void showPreferencesDialog(){
		try{

			Stage dialog = new Stage();
			dialog.setTitle("Preferences");
			dialog.getIcons().add(new Image(getClass().getResourceAsStream("../img/File.png")));
			dialog.initOwner(anchorPane.getScene().getWindow());
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/settings.fxml"));
			Parent root = (Parent)loader.load();
			PreferencesDialogController controller = (PreferencesDialogController)loader.getController();
			controller.setupStage(dialog);
			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.show();
		}
		catch(Exception e){
			System.out.println("Unable to open the preferences "+e.getMessage());
		}
	}

	public void showFileDownloadDialog(SearchResults sr){
		try{
			Stage dialog = new Stage();
			dialog.setTitle("Downloading: "+sr.getFilename());
			dialog.initOwner(anchorPane.getScene().getWindow());
			dialog.setY(400+ 40*Math.random());
			dialog.setY(200+ 20*Math.random());
			dialog.getIcons().add(new Image(getClass().getResourceAsStream("../img/File.png")));

			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/download_file.fxml"));
			Parent root = (Parent)loader.load();
			FileDownloadController controller = (FileDownloadController)loader.getController();
			controller.setupStage(dialog);
			//			controller.setTitle(sr);
			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.show();

			controller.setDownloadNode(DownloadEngine.getInstance().addDownload(sr));
			CallbackRegister.getInstance().registerForCallback(
					"p2p-app-download-file-"+sr.getIp()+"-"+sr.getFileId(), "p2pApp.p2pUi.controller.FileDownloadController", "completeDownload", true, controller);
		}

		catch(Exception e){
			System.out.println("Unable to open the file download: "+e.getMessage());
		}
	}

	public void showDirDownloadDialog(SearchResults sr){
		try{

			Stage dialog = new Stage();
			dialog.setTitle("Downloading: "+sr.getFilename());
			dialog.initOwner(anchorPane.getScene().getWindow());
			//			    dialog.setY(400+ 40*Math.random());
			//			    dialog.setY(200+ 20*Math.random());
			dialog.getIcons().add(new Image(getClass().getResourceAsStream("../img/File.png")));

			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/download_dir.fxml"));
			Parent root = (Parent)loader.load();
			DirDownloadController controller = (DirDownloadController)loader.getController();
			controller.setupStage(dialog);
			//				controller.setTitle(sr);
			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.show();

			//				controller.setDownloadNode(DownloadEngine.getInstance().addDownload(sr));
			controller.setDetails(sr.getFilename(), sr.getFileSize());

			GetDirQuery.sendDirQuery(sr);
			CallbackRegister.getInstance().registerForCallback(
					"p2p-app-dir-listfiles", "p2pApp.p2pUi.controller.DirDownloadController", "fileList", true, controller);
		}

		catch(Exception e){
			System.out.println("Unable to open the file download: "+e.getMessage());
		}
	}

	public class Console extends OutputStream {
		private TextArea console;

		public Console(TextArea console) {
			this.console = console;
		}

		public void appendText(final String valueOf) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					console.appendText(valueOf);
				}
			});
		}

		public void write(int b) throws IOException {
			appendText(String.valueOf((char)b));
		}
	}
	// Callback functions

	public void handleResults(String action, Object obj){
		if(action.equals("p2p-app-results")){
			addResults(SearchTable.getInstance().getSearchTable());
		}
	}

	public synchronized void modifyPeersList(String action, Object obj){
		if(action.equals("tcp-server-neighbours")){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					peers.clear();
					peers.addAll(PeersTable.getInstance().getNeighbourIps());
				}
			});
		}
	}

}