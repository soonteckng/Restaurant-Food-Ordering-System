package soonteck.alert

import scalafx.scene.control.{Alert, ButtonType, Label}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.layout.Region

class Alerts(primaryStage: Stage) {

  private def configureDialog(alert: Alert, styleClass: String): Unit = {
    alert.delegate.getDialogPane.getStylesheets.add(getClass.getResource("/soonteck/view/FoodTheme.css").toExternalForm)

    alert.getDialogPane.getStyleClass.add(styleClass)

    alert.getDialogPane.setMinWidth(350)
    alert.getDialogPane.setPrefWidth(Region.USE_COMPUTED_SIZE)
    alert.getDialogPane.setMaxWidth(600)
    alert.getDialogPane.setMinHeight(120)
    alert.getDialogPane.setPrefHeight(Region.USE_COMPUTED_SIZE)

    alert.delegate.setResizable(true)
  }

  def showSuccessAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    alert.getButtonTypes.setAll(ButtonType.OK)
    configureDialog(alert, "success")
    alert.showAndWait()
  }

  def showInfoAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    configureDialog(alert, "information")
    alert.showAndWait()
  }

  def showWarningAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Warning) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    configureDialog(alert, "warning")
    alert.showAndWait()
  }

  def showErrorAlert(alertTitle: String, message: String): Unit = {
    val alert = new Alert(AlertType.Error) {
      initOwner(primaryStage)
      this.title = alertTitle
      headerText = None
      contentText = message
    }
    configureDialog(alert, "error")
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
    configureDialog(alert, "confirmation")
    val result = alert.showAndWait()
    result.contains(ButtonType.Yes)
  }
}