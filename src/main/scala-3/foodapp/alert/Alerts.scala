import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

private def showErrorAlert(alertTitle: String, message: String): Unit = {
  val alert = new Alert(AlertType.Error) {
    this.title = alertTitle
    headerText = None
    contentText = message
  }
  alert.showAndWait()
}

// Show success alert
private def showSuccessAlert(alertTitle: String, message: String): Unit = {
  val alert = new Alert(AlertType.Information) {
    this.title = alertTitle
    headerText = None
    contentText = message
  }
  alert.showAndWait()
}