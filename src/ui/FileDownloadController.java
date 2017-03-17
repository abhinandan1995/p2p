package ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import p2pApp.SearchResults;
import p2pApp.p2pDownloader.DownloadNodes;

public class FileDownloadController implements Initializable {
	
	@FXML private Label title;
	@FXML private Button pauseButton, closeButton, openExplorer;
	@FXML private ProgressBar progressBar;
	@FXML private ImageView image;
	@FXML private AnchorPane anchorPane;
	@FXML private Label speedLabel;
	
	private Desktop desktop = Desktop.getDesktop();
	private SearchResults sr;
	private DownloadNodes node;
	private Stage stage;
	
	@Override 
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		
	}
	
	@FXML protected void openInExplorer(ActionEvent ae){
		openFileLocation(utility.Utilities.outputFolder+ utility.Utilities.parseInvalidFilenames(sr.getFilename()));
	}
	
	@FXML protected void controlAction(ActionEvent ae){
		if(pauseButton.getText().contains("Open")){
			openFile(new File(utility.Utilities.outputFolder+ utility.Utilities.parseInvalidFilenames(sr.getFilename())));
		}
		
		if(pauseButton.getText().contains("Pause")){
			node.pauseDownload();
			Platform.runLater(new Runnable(){
				public void run(){		
					pauseButton.setText("Resume");
				}
			});
		}
		
		if(pauseButton.getText().contains("Resume")){
			node.resumeDownload();
			Platform.runLater(new Runnable(){
				public void run(){		
					pauseButton.setText("Pause");
				}
			});
		}
	}
	
	@FXML protected void stopAction(ActionEvent ae){
		if(closeButton.getText().equals("Close")){
			stage.close();
			return;
		}
		node.stopDownload();
		stage.close();
	}
//	public void setTitle(final SearchResults sr){
//		this.sr= sr;
//		title.setText(sr.getFilename());
//	}
	
	public void setDownloadNode(final DownloadNodes node){
		this.node= node;
		this.sr= node.getSearchResults();
		title.setText(sr.getFilename());
		
		setProgress();
	}
	
	public void setProgress(){
		
		Thread timer= new Thread(){
		
		public void run(){
			try{
				while(true){
				Thread.sleep(500);
			
				Platform.runLater(new Runnable(){
					public void run(){	
						try{	
							System.out.println("Percent from "+node.getSearchResults().getFilename()+ " "+node.getPercent()+ " ");
							progressBar.setProgress(node.getPercent()/100.0);
							if(node.isComplete)
								progressBar.setProgress(1);
							speedLabel.setText(node.getSpeed(8)+"\nMbps");
						}
						catch(Exception e){
							System.out.println("Error" + e.getMessage());
						}
					}
				});
					if(node.getPercent()>=100 || node.isComplete)
						break;
				}
				
			}
			catch(Exception e){
				System.out.println("Error #1" + e.getMessage());
			}
		}
	};
	timer.setDaemon(true);
	timer.start();
//		final PercentKeeper pk= sr.getLinkedPercentKeeper();
//		Platform.runLater(new Runnable(){
//			public void run(){	
//				try{
//					
//				while(pk.getPercent()< 100)
//				progressBar.setProgress(pk.getPercent());
//				Thread.sleep(500);
//				}
//				catch(Exception e){
//					System.out.println("Error" + e.getMessage());
//				}
//			}
//		});
	}
	
	public void setupStage(Stage stage){
		this.stage= stage;
	}
	
	//callback functions
	
	public void completeDownload(String action, Object o){
		SearchResults sr= (SearchResults)o;
		
		try{
		
		if(this.sr.getFileId().equals(sr.getFileId())&&
				this.sr.getFileSize().equals(sr.getFileSize())){

			Platform.runLater(new Runnable(){
				public void run(){		
					pauseButton.setText("Open file");
					stage.setTitle("Download Completed");
					closeButton.setText("Close");
					openExplorer.setVisible(true);
					//speedLabel.setText("");
				}
			});
			
		}
		}
		catch(Exception e){
		}
	}
	
	 private void openFile(File file) {
	        try {
	            desktop.open(file);
	        } catch (IOException ex) {  
	        }
	    }
//	 
	 public static void openFileLocation(String path) {
		 try{
			 Runtime.getRuntime().exec("explorer.exe /select," + path.replace("/", "\\"));
		 }
		 catch(Exception e){
			 System.out.println(e.getMessage());
		 }
	    }
}
