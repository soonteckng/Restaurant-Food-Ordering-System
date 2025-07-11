package foodapp

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane
import javafx.fxml.FXMLLoader

object MainApp extends JFXApp3:
  override def start(): Unit = {

    val resource = getClass.getResource("/foodapp/login/LoginOverview.fxml")

    if (resource == null) {
      println("LoginOverview.fxml not found!")
      return
    }

    try {
      val loader = new FXMLLoader(resource)
      loader.load()
      val root = loader.getRoot[javafx.scene.layout.AnchorPane]

      stage = new PrimaryStage:
        title = "FoodApp - Login"
        scene = new Scene(new AnchorPane(root))

      stage.show()

    } catch {
      case e: Exception =>
        println(s"Error loading FXML: ${e.getMessage}")
        e.printStackTrace()
    }
  }