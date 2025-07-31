package foodapp.view

import javafx.fxml.FXML
import scalafx.event.ActionEvent

@FXML
class RootLayoutController():
  @FXML
  def handleClose(action: ActionEvent): Unit =
    System.exit(0)
