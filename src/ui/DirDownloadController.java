package ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import p2pApp.p2pDownloader.DownloadNodes;
import p2pApp.p2pQueries.GetDirQuery;

public class DirDownloadController implements Initializable {
	
	@FXML private Label speedLabel;
	@FXML private Label dirsize;
	@FXML private Label dirname;
	@FXML private ProgressBar progressBar;
	@FXML private Label progressLabel;
	@FXML private ListView<DownloadNodes> listView;
	private Stage stage;
	
	private final ObservableList<DownloadNodes> files= 
			FXCollections.observableArrayList();
	
	@Override 
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		listView.setItems(files);
		//listView.setCellFactory(arg0);
	}
	
	public void setupStage(Stage stage){
		this.stage= stage;
	}
	
	//callback functions
	public void fileList(String action, Object obj){
		if(action.equals("p2p-app-dir-listfiles")){
			GetDirQuery gdq= (GetDirQuery)obj;
			for(int i=0; i<gdq.files.size();i++){
				files.add(new DownloadNodes(gdq.files.get(i)));
			}
		}
	}
}
