package ui;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static int tabCount = 0;

    @Override
    public void start(Stage primaryStage) {

        final TabPane tabPane = new TabPane();

        tabPane.getTabs().add(createTab());
        tabPane.getTabs().add(createTab());
        tabPane.getTabs().add(createTab());

        Button addTabButton = new Button( "Add Tab");
        addTabButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
			    tabPane.getTabs().add(createTab());
			}
		});

        Button logButton = new Button( "Log");
        logButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
			    for( Tab tab: tabPane.getTabs()) {
			        System.out.println( "Tab " + tab.getText() + " has content " + tab.getContent());
			    }
			}
		});

        HBox toolbar = new HBox();
        HBox.setMargin(addTabButton, new Insets(5,5,5,5));
        HBox.setMargin(logButton, new Insets(5,5,5,5));
        toolbar.getChildren().addAll( addTabButton, logButton);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 640, 480);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static Tab createTab() {

        tabCount++;

        Tab tab = new Tab();
        tab.setText("Tab " + tabCount);
        tab.setTooltip( new Tooltip( "Tooltip Tab " + tabCount));

        Node content = new Label( "Content Tab " + tabCount);
        tab.setContent(content);

        return tab;

    }

    public static void main(String[] args) {
        launch(args);
    }
}