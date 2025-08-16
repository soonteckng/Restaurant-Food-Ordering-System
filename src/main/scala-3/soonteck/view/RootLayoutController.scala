package soonteck.view

import soonteck.Main
import javafx.fxml.FXML
import javafx.event.ActionEvent

@FXML
class RootLayoutController():
  @FXML
  def handleClose(action: ActionEvent): Unit =
    Main.stage.close()
    
  def handleAbout(action: ActionEvent): Unit =
    Main.showAbout()
