//package nounou.gui
//
//import javafx.application.Application
//import javafx.event.{ActionEvent, EventHandler}
//import javafx.scene.Scene
//import javafx.scene.control.Button
//import javafx.scene.layout.StackPane
//import javafx.stage.Stage
//
//object Nounou {
//
//  def main(args: Array[String]): Unit = {
//    Nounou.launch(args)
//  }
//
//}
//
//class Nounou extends Application {
//
//  def main(args: Array[String]): Unit = {
//    Nounou.launch(args)
//  }
//
//  var pStage: Stage = null
//
//  def start(primaryStage: Stage) {
//    pStage = primaryStage
//    val btn: Button = new Button
//    btn.setText("Say 'Hello World'")
//    btn.setOnAction(new EventHandler[ActionEvent]() {
//      def handle(event: ActionEvent) {
//        System.out.println("Hello World!")
//      }
//    })
//    val root: StackPane = new StackPane
//    root.getChildren.add(btn)
//    val scene: Scene = new Scene(root, 300, 250)
//    primaryStage.setTitle("Hello World!")
//    primaryStage.setScene(scene)
//    primaryStage.show
//  }
//
//}
