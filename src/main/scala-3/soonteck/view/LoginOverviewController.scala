package soonteck.view

import soonteck.Main
import soonteck.alert.Alerts
import soonteck.model.User
import soonteck.service.AuthenticationService
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}

@FXML
class LoginOverviewController {
  @FXML
  private var usernameField: TextField = null
  @FXML
  private var passwordField: PasswordField = null

  var isRegisterClicked = false
  var alerts = new Alerts(Main.stage)

  private val authService = new AuthenticationService()

  def initialize(): Unit = {
    if (usernameField != null) {
      usernameField.requestFocus()
    }
  }

  @FXML
  private def handleLogin(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val password = passwordField.getText

    User.getUserByUsername(username) match {
      case Some(user) if user.password.value == password =>
        Main.showHomePageOverview(user.username.value) 

      case Some(user) =>
        alerts.showErrorAlert("Login Failed", "Incorrect password. Please try again.")

      case None =>
        val failedLogin = s"""Username not found.
                         |Please check your username or password.""".stripMargin

        alerts.showErrorAlert("Login Failed", failedLogin)
    }
  }

  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    Main.showRegisterOverview()
  }

  def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
  }

  def getAuthService: AuthenticationService = authService
}

