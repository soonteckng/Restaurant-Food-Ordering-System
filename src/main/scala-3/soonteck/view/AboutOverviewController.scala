package soonteck.view

import javafx.fxml.FXML
import javafx.event.ActionEvent
import scalafx.stage.Stage
import soonteck.Main

@FXML
class AboutOverviewController():
  var stage: Option[Stage] = None
  var okClicked = false

  @FXML
  def handleClose(action: ActionEvent): Unit =
    okClicked = true
    stage.foreach(_.close())