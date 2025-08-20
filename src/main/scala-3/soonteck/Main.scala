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

  // Root layout container
  var roots: Option[scalafx.scene.layout.BorderPane] = None

  // CSS resource - following lecturer's approach
  var cssResource = getClass.getResource("view/FoodTheme.css")

  // Food data observable buffer
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

    // Load root layout - following lecturer's pattern
    val rootResource = getClass.getResource("view/RootLayout.fxml")
    val loader = new FXMLLoader(rootResource)
    loader.load()

    roots = Option(loader.getRoot[jfxs.layout.BorderPane])

    // Create primary stage with CSS styling
    stage = new PrimaryStage():
      title = "LeanBites - Healthy Food Ordering"
      // Optional: Add application icon
      // icons += new Image(getClass.getResource("/images/app-icon.png").toExternalForm)
      scene = new Scene():
        root = roots.get
        // Apply CSS theme - following lecturer's approach
        stylesheets = Seq(cssResource.toExternalForm)

    // Show login screen on startup
    showLoginOverview()

  // Display login overview - following lecturer's pattern
  def showLoginOverview(): Unit =
    val resource = getClass.getResource("view/LoginOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val loginRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val loginController = loader.getController[LoginOverviewController]

    // Set center of root layout to login screen
    this.roots.get.center = loginRoot

  // Display about dialog - following lecturer's modal dialog pattern
  def showAbout(): Boolean =
    val about = getClass.getResource("view/AboutOverview.fxml")
    val loader = new FXMLLoader(about)
    loader.load()
    val pane = loader.getRoot[jfxs.layout.AnchorPane]

    // Create modal dialog stage
    val aboutDialog = new Stage():
      initOwner(stage)
      initModality(ApplicationModal)
      title = "About LeanBites"
      scene = new Scene():
        root = pane
        // Apply same CSS theme to dialog
        stylesheets = Seq(cssResource.toExternalForm)

    val aboutController = loader.getController[AboutOverviewController]
    aboutController.stage = Option(aboutDialog)
    aboutDialog.showAndWait()
    aboutController.okClicked

  // Display register overview - following lecturer's pattern
  def showRegisterOverview(): Unit =
    val resource = getClass.getResource("view/RegisterOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val registerRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val registerController = loader.getController[RegisterOverviewController]

    // Set center of root layout to register screen
    this.roots.get.center = registerRoot

  // Display home page overview - following lecturer's pattern
  def showHomePageOverview(username: String = ""): Unit =
    val resource = getClass.getResource("view/HomePageOverview.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val homeRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val homeController = loader.getController[HomePageOverviewController]

    // Set current user if provided
    if (username.nonEmpty) {
      homeController.setCurrentUser(username)
    }

    // Set center of root layout to home page
    this.roots.get.center = homeRoot