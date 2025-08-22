package soonteck.view

import soonteck.alert.Alerts
import soonteck.model.User
import soonteck.service.AuthenticationService
import soonteck.Main
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.Includes.*
import scalafx.stage.Stage
import scala.util.{Try, Success, Failure}

@FXML
class RegisterOverviewController {
  @FXML
  private var usernameField: TextField = null
  @FXML
  private var passwordField: PasswordField = null
  @FXML
  private var confirmPasswordField: PasswordField = null
  @FXML
  private var backToLoginButton: Button = null

  private val authService = new AuthenticationService()
  var alerts = new Alerts(Main.stage)
  var isCreateAccountClicked = false

  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val password = passwordField.getText
    val confirmPassword = confirmPasswordField.getText

    authService.registerUser(username, password, confirmPassword) match {
      case Success(user) =>
        val confirmRegister = s"""Account created for ${user.username.value}!
                                 |You may login now.""".stripMargin
        alerts.showSuccessAlert("Registration Successful", confirmRegister)
        clearFields()
        handleBackToLogin(action)
      case Failure(e) =>
        alerts.showErrorAlert("Registration Failed", e.getMessage)
    }
  }

  @FXML
  private def handleBackToLogin(action: ActionEvent): Unit =
    Main.showLoginOverview()

  private def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
    confirmPasswordField.clear()
  }
}