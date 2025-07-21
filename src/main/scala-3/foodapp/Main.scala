package foodapp
import foodapp.login.LoginOverviewController
import foodapp.register.RegisterOverviewController
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene as jfxs
import scalafx.Includes.*
import scalafx.stage.{Modality, Stage}

object Main extends JFXApp3:

  var roots: Option[scalafx.scene.layout.BorderPane] = None

  override def start(): Unit =

    val rootResource = getClass.getResource("/foodapp/RootLayout.fxml")
    val loader = new FXMLLoader(rootResource)
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

    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.get.center = roots

  def showRegisterOverview(): Unit =
    val resource = getClass.getResource("/foodapp/register/RegisterOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[RegisterOverviewController]

    val register = new Stage():
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene:
        root = roots2
    register.showAndWait()



