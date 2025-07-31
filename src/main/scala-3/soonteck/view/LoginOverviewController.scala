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
    println("Username: admin, Password: Admin123")
    }

  // Handle login
  @FXML
  private def handleLogin(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val password = passwordField.getText

    // Validate input
    if (username.isEmpty || password.isEmpty) {
      alerts.showErrorAlert("Login Error", "Please enter both username and password.")
      return
    }

    // Attempt login
    if (authService.login(username, password)) {
      val user = authService.getCurrentUser.get
      alerts.showSuccessAlert("Login Successful", s"Welcome back, ${user.getUsername}!")

      // Clear fields
      clearFields()

    } else {
      alerts.showErrorAlert("Login Failed", "Invalid username or password. Please try again.")
      passwordField.clear()
    }
  }
/*
  // Handle register button
  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    Main.showRegisterOverview()
  }*/


  // Clear all input fields
  def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
  }

  // Get current authentication service (for testing or external access)
  def getAuthService: AuthenticationService = authService
}

