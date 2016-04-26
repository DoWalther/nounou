package nounou.gui;

/**
 * Created by ktakagaki on 16/04/14.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class NounouFX extends Application {

    public static Stage primaryStage = null;
    public static Scene scene = null;


    public void launch() {
        //Platform.setImplicitExit(false);
        Application.launch( NounouFX.class );
    }

    public void launchStage() {
        if( primaryStage == null ) launch();
        else {
            final Stage stage = new Stage();
            stage.setScene( scene );
            Platform.runLater( new Runnable() {
                public void run(){
                    new NounouFX().start(stage);
                }
            });
        }
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void show(){
        if(primaryStage != null) primaryStage.show();
        else System.out.println( "primary stage not initialized, use launchGUI() instead.");
    }
}
