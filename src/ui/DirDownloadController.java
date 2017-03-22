package ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import p2pApp.p2pDownloader.DownloadEngine;
import p2pApp.p2pDownloader.DownloadNodes;
import p2pApp.p2pQueries.GetDirQuery;

public class DirDownloadController implements Initializable {
	
	@FXML private Label speedLabel;
	@FXML private Label dirsize;
	@FXML private Label dirname;
	@FXML private ProgressBar progressBar;
	@FXML private Label progressLabel, filesCount;
	@FXML public ListView<DownloadNodes> listView;
	@FXML private Button openButton, pauseButton, stopButton;
	private Stage stage;
	public long totalSize=0;
	private boolean isComplete;
	private long speed;
	private double percent=0;
	private long prevDone=0;
	public Image runImage;
	public Image pauseImage;
	public Image cancelImage;
//	private int last= -1;
	
	private final ObservableList<DownloadNodes> files= 
			FXCollections.observableArrayList();
	
	@FXML protected void onOpenPressed(ActionEvent ae){
		openFileLocation(utility.Utilities.outputFolder + dirname);
	}
	
	@FXML protected void onPausePressed(ActionEvent ae){
		if(pauseButton.getText().equals("Pause")){
			pauseButton.setText("Resume");
			for(int i=0; i< files.size();i++){
				files.get(i).pauseDownload();
			}
			return;
		}
		
		if(pauseButton.getText().equals("Resume")){
			pauseButton.setText("Pause");
			for(int i=0; i< files.size();i++){
				files.get(i).resumeDownload();
			}
			return;
		}
	}
	
	@FXML protected void onStopPressed(ActionEvent ae){
		if(stopButton.getText().equals("Stop")){
		
		for(int i=0;i<files.size();i++){
			files.get(i).stopDownload();
		}
		}
		stage.hide();
	}
	
	@Override 
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		listView.setItems(files);
		listView.setCellFactory(new Callback<ListView<DownloadNodes>, ListCell<DownloadNodes>>()
        {
            @Override
            public ListCell<DownloadNodes> call(ListView<DownloadNodes> listView)
            {
                return new ListViewCell();
            }
        });
		
		runImage= new Image(getClass().getResourceAsStream("img/image_pl.png"));
		pauseImage= new Image(getClass().getResourceAsStream("img/images_ps.png"));
		cancelImage= new Image(getClass().getResourceAsStream("img/images_cn.png"));
	}
	
	public void setupStage(Stage stage){
		this.stage= stage;
	}
	
	public void setDetails(String name, String size){
		dirname.setText(name);
		dirsize.setText(utility.Utilities.humanReadableByteCount(size, false)+"\nSize");
		totalSize= Long.parseLong(size);
	}
	
	//callback functions
	public void fileList(String action, Object obj){
		if(action.equals("p2p-app-dir-listfiles")){
			GetDirQuery gdq= (GetDirQuery)obj;
			
//			for(int i=0; i<gdq.files.size();i++){
//				if(gdq.files.get(i).getType().equals("1"))
//					files.add(new DownloadNodes(gdq.files.get(i)));
//			}
			files.addAll(DownloadEngine.getInstance().addMultiple(gdq.name, gdq.files, false));
			Platform.runLater(new Runnable(){
				public void run(){		
					filesCount.setText("Total files: "+ files.size());
				}
			});
			DownloadEngine.getInstance().startDownloading();
			setUpdates();
		}
	}
	
	private class ListViewCell extends ListCell<DownloadNodes>{
		
		private ListRow listRow= null;
		ListViewCell(){
			listRow= new ListRow(DirDownloadController.this);
		}
		@Override
		public void updateItem(DownloadNodes node, boolean empty){
			super.updateItem(node, empty);
			if(node!=null && node.getSearchResults().getType().equals("1")){
				listRow.setDetails(node);
				setGraphic(listRow.getRow());
			}
		}
	}
	
	private void setUpdates(){
		
		Thread timer= new Thread(){
			
			public void run(){
				try{
					while(true){
					Thread.sleep(100);
					setDetails();
					Platform.runLater(new Runnable(){
						public void run(){	
							listView.refresh();
							speedLabel.setText(""+speed+ " Mbps");
							progressBar.setProgress(percent);
							progressLabel.setText((long)(percent*100)+"%");
							if(isComplete){
								stage.setTitle("Download Completed");
								pauseButton.setDisable(true);
								stopButton.setText("Close");
							}
						}
					});
					if(isComplete)
						break;
					else
						Thread.sleep(900);
					}
				}
				catch(Exception e){
					
				}
	}
		};
		timer.setDaemon(true);
		timer.start();
	}
	
	public void setDetails(){
		speed= 0;
		isComplete = true;
		long sizeDone = 0;
		for(int i=0; i<files.size();i++){
			//speed= speed + files.get(i).getSpeed(8);
			DownloadNodes node= files.get(i);
			if(!node.isStopped && !node.isComplete)
				isComplete= false;
			sizeDone= sizeDone + node.getSizeDone();
		}
		speed= sizeDone- prevDone;
		speed= speed * 8 / (1024 * 1024);
		if(speed<0)
			speed= 0;
		prevDone= sizeDone;
		if(isComplete)
			percent= 1;
		else
			percent= (double)sizeDone/totalSize;
	}
	
//	public double getPercent(){
//		for(int i=0;i<files.size();i++){
//			
//		}
//		return 0;
//	}
//	public boolean isComplete(){
//		for(int i=0; i<files.size();i++){
//			if(!files.get(i).isStopped && !files.get(i).isComplete)
//				return false;
//		}
//		return true;
//	}
	
//	public void cancelDownload(){
//		int index= listView.getSelectionModel().getSelectedIndex();
//		if(last==index)
//			return;
//		System.out.println("index "+ index);
//		last= index;
//		files.remove(index);
//	}
	
	 	public void openFileLocation(String path) {
		 try{
			 Runtime.getRuntime().exec("explorer.exe /select," + path.replace("/", "\\"));
		 }
		 catch(Exception e){
			 System.out.println(e.getMessage());
		 }
	    }
}

