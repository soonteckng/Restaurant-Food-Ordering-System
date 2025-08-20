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
  var alerts = new Alerts()

  // Service for authentication
  private val authService = new AuthenticationService()

  def initialize(): Unit = {
    // Set default focus to username field
    if (usernameField != null) {
      usernameField.requestFocus()
    }

    // Print available test users for debugging
    println("=== Test Users Available ===")
    println("Username: abc, Password: 123456")
    println("Username: admin, Password: Admin123")
    }

  // Handle login
  @FXML
  private def handleLogin(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val password = passwordField.getText

    User.getUserByUsername(username) match {
      case Some(user) if user.password.value == password =>
        // Login successful
        alerts.showSuccessAlert("Login Successful", s"Welcome back, ${user.username.value}!")

        // Show home page and set current user by username
        Main.showHomePageOverview(user.username.value) // Pass username

      case Some(user) =>
        // Wrong password
        alerts.showErrorAlert("Login Failed", "Incorrect password. Please try again.")

      case None =>
        // User doesn't exist
        alerts.showErrorAlert("Login Failed", "Username not found. Please check your username or register a new account.")
    }
  }

  // Handle register button
  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    Main.showRegisterOverview()
  }


  // Clear all input fields
  def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
  }

  // Get current authentication service (for testing or external access)
  def getAuthService: AuthenticationService = authService
}

