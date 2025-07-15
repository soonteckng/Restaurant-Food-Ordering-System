package foodapp
import foodapp.login.LoginOverviewController
import foodapp.register.RegisterOverviewController
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import javafx.fxml.FXMLLoader
import javafx.scene as jfxs
import scalafx.Includes.*

object Main extends JFXApp3:

  var roots: Option[scalafx.scene.layout.BorderPane] = None

    override def start(): Unit =

      val rootResource = getClass.getResource("/foodapp/RootLayout.fxml")
      // initialize the loader object.
      val loader = new FXMLLoader(rootResource)
      // Load root layout from fxml file.
      loader.load()

      roots = Option(loader.getRoot[jfxs.layout.BorderPane])

      stage = new PrimaryStage():
        title = "FridgeApp"
        scene = new Scene():
          root = roots.get

      showLoginOverview()

    def showLoginOverview(): Unit =
      val resource = getClass.getResource("/foodapp/login/LoginOverview.fxml")
      val loader = new FXMLLoader(resource)
      loader.load()
      val loginRoot = loader.getRoot[jfxs.layout.AnchorPane]
      this.roots.get.center = loginRoot

    def showRegisterOverview(): Unit =
      val resource = getClass.getResource("/foodapp/register/RegisterOverview.fxml")
      val loader = new FXMLLoader(resource)
      loader.load()
      val registerRoot = loader.getRoot[jfxs.layout.AnchorPane]
  
      // Set controller reference to Main
      val controller = loader.getController[RegisterOverviewController]
      controller.setMain(this)
  
      this.roots.get.center = registerRoot

