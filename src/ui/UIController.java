package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class UIController {

	@FXML private TextField searchField;
	@FXML private Button searchButton;
	@FXML private TableView<Records> resultsTable= new TableView<Records>();
	
	private final ObservableList<Records> data =
	        FXCollections.observableArrayList(
	        		new Records("172", "Taylor swift", "25Mb"));   
	
	@FXML protected void handleClickOnSearch(ActionEvent ae){
		String str= searchField.getText();
		System.out.println(str);
		
		if(str.equals("init"))
			init();
		else
			addRow();
	}
	
	@FXML protected void handleKeyOnSearch(KeyEvent ke){
		
	}
	
	private void init(){
		System.out.println("called");
		resultsTable.setEditable(true);
//		TableColumn firstNameCol = new TableColumn("Source");
//	    firstNameCol.setCellValueFactory(new PropertyValueFactory<Records,String>("Source"));
		resultsTable.setItems(data);
		

	}
	

	private void addRow(){
		data.add(new Records("192","Ronaldo", "400Mb"));
	}
	
	public static class Records {
        public SimpleStringProperty Source;
        public final SimpleStringProperty Filename;
        public final SimpleStringProperty Filesize;

        public Records(String source, String filename, String filesize) {
            this.Source= new SimpleStringProperty(source);
            this.Filename= new SimpleStringProperty(filename);
            this.Filesize= new SimpleStringProperty(filesize);
        }
    }
}