package soonteck
import soonteck.view.{LoginOverviewController, RegisterOverviewController}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene as jfxs
import scalafx.Includes.*
import scalafx.stage.{Modality, Stage}

object Main extends JFXApp3:
  //Database.setupDB()
  var roots: Option[scalafx.scene.layout.BorderPane] = None

  override def start(): Unit =

    val rootResource= getClass.getResource("/soonteck/view/RootLayout.fxml")
    val loader = new FXMLLoader(rootResource)
    loader.load()

    roots = Option(loader.getRoot[jfxs.layout.BorderPane])

    stage = new PrimaryStage():
      title = "FridgeApp"
      scene = new Scene():
        root = roots.get

    showLoginOverview()

  def showLoginOverview(): Unit =
    val resource = getClass.getResource("view/LoginOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    val ctrl = loader.getController[LoginOverviewController]


    this.roots.get.center = roots

  def showRegisterOverview(): Unit =
    val resource = getClass.getResource("view/RegisterOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val registerRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val controller = loader.getController[RegisterOverviewController]

    this.roots.get.center = registerRoot


