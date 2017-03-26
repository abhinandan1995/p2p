package p2pApp.p2pUi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import p2pApp.p2pUi.controller.UIController;

public class AppUi extends Application{

	public static void main(String []args){
		launch(args);	
	}
	
	@Override
	public void start(Stage primaryStage) {

		try{
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));
			Parent root = (Parent)loader.load();
			UIController controller = (UIController)loader.getController();
			controller.setupStage(primaryStage);
			Scene scene = new Scene(root);
			primaryStage.setTitle("p2p application");
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("img/File.png")));
			primaryStage.initStyle(StageStyle.UNDECORATED);
			UIController.makeDraggable(primaryStage);
			primaryStage.show();
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}	
}
