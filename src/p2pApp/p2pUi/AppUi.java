package p2pApp.p2pUi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppUi extends Application{

	public static void main(String []args){
		launch(args);	
	}
	
	@Override
	public void start(Stage primaryStage) {

		try{
			
			Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("p2p application");
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("img/File.png")));
			
			primaryStage.show();
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	
}
