package soonteck.alert

import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import scalafx.Includes._

class Alerts(primaryStage: Stage) {

  def showSuccessAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.getButtonTypes.setAll(ButtonType.OK)
    alert.delegate.getDialogPane.getStylesheets.add(getClass.getResource("/soonteck/view/FoodTheme.css").toExternalForm)
    alert.getDialogPane.getStyleClass.add("success")
    alert.showAndWait()
  }

  def showInfoAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.delegate.getDialogPane.getStylesheets.add(getClass.getResource("/soonteck/view/FoodTheme.css").toExternalForm)
    alert.getDialogPane.getStyleClass.add("information")
    alert.showAndWait()
  }

  def showWarningAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Warning) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.delegate.getDialogPane.getStylesheets.add(getClass.getResource("/soonteck/view/FoodTheme.css").toExternalForm)
    alert.getDialogPane.getStyleClass.add("warning")
    alert.showAndWait()
  }

  def showErrorAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Error) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.delegate.getDialogPane.getStylesheets.add(getClass.getResource("/soonteck/view/FoodTheme.css").toExternalForm)
    alert.getDialogPane.getStyleClass.add("error")
    alert.showAndWait()
  }

  def showConfirmationAlert(alertTitle: String, message: String): Boolean = {
    val alert = new Alert(AlertType.Confirmation) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.getButtonTypes.setAll(ButtonType.Yes, ButtonType.No)
    alert.delegate.getDialogPane.getStylesheets.add(getClass.getResource("/soonteck/view/FoodTheme.css").toExternalForm)
    alert.getDialogPane.getStyleClass.add("confirmation")
    val result = alert.showAndWait()
    result.contains(ButtonType.Yes)
  }
}