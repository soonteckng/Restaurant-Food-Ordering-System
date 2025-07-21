package foodapp.login

import foodapp.Main
import foodapp.model.User
import foodapp.service.AuthenticationService
import foodapp.register.RegisterOverviewController
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}
import javafx.event.ActionEvent
import foodapp.alert.Alerts

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

