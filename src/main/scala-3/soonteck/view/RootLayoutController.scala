package soonteck.view

import javafx.fxml.FXML
import javafx.event.ActionEvent
import soonteck.Main

@FXML
class RootLayoutController():
  @FXML
  def handleClose(action: ActionEvent): Unit =
    Main.stage.close()
    
  def handleAbout(action: ActionEvent): Unit =
    Main.showAbout()
