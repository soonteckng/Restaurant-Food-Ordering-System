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

    if (!validateInput(username, password, confirmPassword)) return

    val newUser = User(username, password)

    if (authService.registerUser(newUser)) {
      val confirmRegister = s"""Account created for $username!
                               |You may login now.""".stripMargin
    
      alerts.showSuccessAlert("Registration Successful", confirmRegister)
      clearFields()
      handleBackToLogin(action)
    } else {
      alerts.showErrorAlert("Registration Failed", s"Username '$username' already exists.")
    }
  }

  private def validateInput(username: String, password: String, confirmPassword: String): Boolean = {
    if (username.isEmpty) {
      alerts.showErrorAlert("Validation Error", "Username is required.")
      return false
    }

    if (username.length < 3) {
      alerts.showErrorAlert("Validation Error", "Username must be at least 3 characters long.")
      return false
    }

    if (password.isEmpty) {
      alerts.showErrorAlert("Validation Error", "Password is required.")
      return false
    }

    if (password.length < 6) {
      alerts.showErrorAlert("Validation Error", "Password must be at least 6 characters long.")
      return false
    }

    if (password != confirmPassword) {
      alerts.showErrorAlert("Validation Error", "Passwords do not match.")
      return false
    }
    true
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