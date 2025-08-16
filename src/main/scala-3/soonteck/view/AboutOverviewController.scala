package soonteck.view

import soonteck.Main
import javafx.fxml.FXML
import javafx.event.ActionEvent
import scalafx.stage.Stage

@FXML
class AboutOverviewController():
  var stage: Option[Stage] = None
  var okClicked = false

  @FXML
  def handleClose(action: ActionEvent): Unit =
    okClicked = true
    stage.foreach(_.close())