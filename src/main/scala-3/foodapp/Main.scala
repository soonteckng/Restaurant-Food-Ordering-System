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

  var roots: Option[BorderPane] = None

  override def start(): Unit =
    // 🔧 Create a BorderPane manually instead of loading from FXML
    val rootLayout = new BorderPane()
    roots = Option(rootLayout)

    stage = new PrimaryStage():
      title = "FridgeApp"
      scene = new Scene(rootLayout, 800, 600)

    showLoginOverview()

  def showLoginOverview(): Unit =
    val resource = getClass.getResource("/foodapp/login/LoginOverview.fxml")
    val loader = new FXMLLoader(resource)
    val root = loader.load[jfxs.layout.AnchorPane]

    val controller = loader.getController[LoginOverviewController]
    controller.setMainApp(this)

    stage = new PrimaryStage():
      title = "FridgeApp"
      scene = new Scene(root)

  def showRegisterOverview(): Unit =
    val resource = getClass.getResource("/foodapp/register/RegisterOverview.fxml")
    val loader = new FXMLLoader(resource)
    val root = loader.load[jfxs.layout.AnchorPane]

    val controller = loader.getController[RegisterOverviewController]
    controller.setMain(this)

    stage.scene = new Scene(root)


