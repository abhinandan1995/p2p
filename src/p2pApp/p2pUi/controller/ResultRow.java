package p2pApp.p2pUi.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import p2pApp.SearchResults;

public class ResultRow {

	@FXML private VBox vBox;
	@FXML private Label filename;
	@FXML private Label filesize;
	@FXML private ImageView image;
	@FXML private Label sourceip;
	@FXML private Label moreip;
	@FXML private Label dirlabel;
	@FXML private Button downloadbtn;
	
	UIController controller;
		
	public ResultRow(UIController controller){
		this.controller= controller;
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/results_row.fxml"));
		fxmlLoader.setController(this);
		try{
			fxmlLoader.load();
		}
		catch(Exception e){
			System.out.println("Failed to load the list row: "+e.getMessage());
		}
	}
	
	public void setDetails(final SearchResults sr){
		filename.setText(sr.getFilename());
		filesize.setText(utility.Utilities.humanReadableByteCount(sr.getFileSize(), false));
		filename.setTooltip(new Tooltip(sr.getFilename()));
		
		if(sr.getType().equals("1")){
			dirlabel.setVisible(false);
		}
		else{
			dirlabel.setVisible(true);
			dirlabel.setTooltip(new Tooltip("its a directory containing multiple files"));
		}
		
		sourceip.setText(sr.getIp());
		if(sr.getAlternateIps().size()>0){
			moreip.setVisible(true);
			moreip.setText(""+sr.getAlternateIps().size());
		}
		else
			moreip.setVisible(false);
		
		downloadbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(sr.getType().equals("1")){
                	controller.showFileDownloadDialog(sr);
                }
                if(sr.getType().equals("2")){
                	controller.showDirDownloadDialog(sr);
                }
            }
        });
	}
	
	public VBox getRow(){
		return vBox;
	}
}
