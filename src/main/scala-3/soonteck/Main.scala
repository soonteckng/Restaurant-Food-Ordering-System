package soonteck
import soonteck.view.{AboutOverviewController, HomePageOverviewController, LoginOverviewController, RegisterOverviewController}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene as jfxs
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.stage.Modality.ApplicationModal
import scalafx.stage.{Modality, Stage}
import soonteck.model.{FoodType, User, OrderHistory}
import soonteck.util.Database

object Main extends JFXApp3:
  Database.setupDB()
  FoodType.initializeTable()
  OrderHistory.initializeTable()
  FoodType.resetDefaultFoods()
  
  var roots: Option[scalafx.scene.layout.BorderPane] = None

  val foodData = new ObservableBuffer[FoodType]()
  foodData ++= FoodType.getAllFood

  override def start(): Unit =
    // Initialize database table FIRST
    try {
      println("Database initialized successfully")
    } catch {
      case ex: Exception =>
        println(s"Database initialization failed: ${ex.getMessage}")
        ex.printStackTrace()
    }

    val rootResource= getClass.getResource("view/RootLayout.fxml")
    val loader = new FXMLLoader(rootResource)
    loader.load()

    roots = Option(loader.getRoot[jfxs.layout.BorderPane])

    stage = new PrimaryStage():
      title = "LeanBites"
      scene = new Scene():
        root = roots.get

    showLoginOverview()

  def showLoginOverview(): Unit =
    val resource = getClass.getResource("view/LoginOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    val loginController = loader.getController[LoginOverviewController]
    this.roots.get.center = roots

  def showAbout(): Boolean =
    val about = getClass.getResource("view/AboutOverview.fxml")
    val loader = new FXMLLoader(about)
    loader.load()
    val pane = loader.getRoot[jfxs.layout.AnchorPane]
    val mywindow = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "About"
      scene = new Scene():
        root = pane
    val aboutController = loader.getController[AboutOverviewController]
    aboutController.stage = Option(mywindow)
    mywindow.showAndWait()
    aboutController.okClicked

  def showRegisterOverview(): Unit =
    val resource = getClass.getResource("view/RegisterOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val registerRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val registerController = loader.getController[RegisterOverviewController]

    this.roots.get.center = registerRoot

  def showHomePageOverview(username: String = ""): Unit =
    val resource = getClass.getResource("view/HomePageOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val homeRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val homeController = loader.getController[HomePageOverviewController]
    
    if (username.nonEmpty) {
      homeController.setCurrentUser(username)
    }
    // Set the center of root layout to Home Page
    this.roots.get.center = homeRoot

    
