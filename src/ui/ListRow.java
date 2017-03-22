package ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import p2pApp.SearchResults;
import p2pApp.p2pDownloader.DownloadNodes;

public class ListRow {

	@FXML private VBox vBox;
	@FXML private Label filename;
	@FXML private Label filesize;
	@FXML private ImageView image;
	@FXML private ProgressIndicator progress;
	@FXML private ImageView startImage, pauseImage, cancelImage;
	DirDownloadController dc;
	
	public VBox getRow(){
		return vBox;
	}
	
	public ListRow(DirDownloadController dc){
		this.dc= dc;
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("file_row.fxml"));
		fxmlLoader.setController(this);
		try{
			fxmlLoader.load();
		}
		catch(Exception e){
			System.out.println("Failed to load the list row: "+e.getMessage());
		}
	}
		
	public void setDetails(DownloadNodes node){
		SearchResults sr= node.getSearchResults();
		filename.setText(getFilename(sr.getFilename()));
		filename.setTooltip(new Tooltip("Path: "+sr.getFilename()));
		filesize.setText(utility.Utilities.humanReadableByteCount(sr.getFileSize(), false));
		progress.setProgress(node.getPercent()/ 100.0);
		
//		final ImageView startImage= this.startImage;
//		final ImageView pauseImage= this.pauseImage;
//		final ImageView cancelImage= this.cancelImage;
		
		startImage.setVisible(true);
		pauseImage.setVisible(true);
		cancelImage.setVisible(true);
		
		if(node.isPaused){
			image.setImage(dc.pauseImage);
		}
		else if(node.isStopped){
			image.setImage(dc.cancelImage);
			startImage.setVisible(false);
			pauseImage.setVisible(false);
		}
		else{
			image.setImage(dc.runImage);
		}
		
		startImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		    	 System.out.println("Selected item: " + dc.listView.getSelectionModel().getSelectedItem());
		    	 dc.listView.getSelectionModel().getSelectedItem().resumeDownload();
		        // node.resumeDownload();
		         event.consume();
		     }
		});
		
		pauseImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		        // node.pauseDownload();
		         dc.listView.getSelectionModel().getSelectedItem().pauseDownload();
		         event.consume();
		     }
		});
		
		cancelImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event){
				dc.listView.getSelectionModel().getSelectedItem().stopDownload();
				event.consume();
			}
		});
		
	}
	
	public String getFilename(String f){
		int index= f.lastIndexOf("/");
		if(index==-1)
			return f;
		return f.substring(index+1, f.length());
	}
	
	
}
