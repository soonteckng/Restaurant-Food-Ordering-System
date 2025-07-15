package foodapp.login

import foodapp.model.User
import foodapp.service.AuthenticationService
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class LoginOverviewController {

  @FXML
  private var usernameField: TextField = null

  @FXML
  private var passwordField: PasswordField = null

  // Service for authentication
  private val authService = new AuthenticationService()

  // Reference to main app (for scene switching)
  private var mainApp: Option[foodapp.Main.type] = None

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
      showAlert("Login Error", "Please enter both username and password.")
      return
    }

    // Attempt login
    if (authService.login(username, password)) {
      val user = authService.getCurrentUser.get
      showSuccessAlert("Login Successful", s"Welcome back, ${user.getUsername}!")

      // Clear fields
      clearFields()

      // Navigate to main app
      println(s"Navigating to main app for user: ${user.getUsername}")
    

    } else {
      showAlert("Login Failed", "Invalid username or password. Please try again.")
      passwordField.clear()
    }
  }

  // Handle register button
  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    mainApp.foreach(_.showRegisterOverview())
  }

  // Clear all input fields
  private def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
  }

  // Show error alert
  private def showAlert(alertTitle: String, message: String): Unit = {
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

  // Set main app reference (for scene switching)
  def setMainApp(mainApp: foodapp.Main.type): Unit = {
    this.mainApp = Some(mainApp)
  }

  // Get current authentication service (for testing or external access)
  def getAuthService: AuthenticationService = authService
}