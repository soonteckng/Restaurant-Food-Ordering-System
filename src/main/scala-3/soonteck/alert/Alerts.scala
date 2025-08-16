package soonteck.alert

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class Alerts{
  
  def showErrorAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Error) {
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.showAndWait()
  }
  
  def showSuccessAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.showAndWait()
  }

  def showWarningAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Warning) {
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.showAndWait()
  }
}