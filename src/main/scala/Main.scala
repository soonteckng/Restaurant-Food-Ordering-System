import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import gui.LoginRegisterView

object Main extends JFXApp3:

  override def start(): Unit =
    stage = new PrimaryStage:
      title = "FoodLink App"
      maximized = true
      scene = new Scene(800, 600):
        root = LoginRegisterView.build(stage)
