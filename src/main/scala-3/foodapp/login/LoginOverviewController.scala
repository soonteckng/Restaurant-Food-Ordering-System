package foodapp.login

import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, TextField, PasswordField}
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import foodapp.service.AuthenticationService

class LoginOverviewController {

  @FXML
  private var usernameField: TextField = null

  @FXML
  private var passwordField: PasswordField = null

  @FXML
  private var loginButton: Button = null

  @FXML
  private var registerButton: Button = null

  @FXML
  private var statusLabel: Label = null

  // Service for authentication
  private val authService = new AuthenticationService()

  // Reference to main app (for scene switching)
  private var mainApp: Option[foodapp.MainApp.type] = None

  def initialize(): Unit = {
    statusLabel.setText("Please enter your credentials to login")

    // Set default focus to username field
    if (usernameField != null) {
      usernameField.requestFocus()
    }
  }

  // Handle login
  @FXML
  private def handleLogin(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val password = passwordField.getText

    // Validate input
    if (username.isEmpty || password.isEmpty) {
      showAlert("Login Error", "Please enter both username and password.")
      statusLabel.setText("Please fill in all fields")
      return
    }

    // Attempt login
    if (authService.login(username, password)) {
      val user = authService.getCurrentUser.get
      showSuccessAlert("Login Successful", s"Welcome back, ${user.displayName}!")
      statusLabel.setText(s"Login successful! Welcome ${user.displayName}")

      // Clear fields
      clearFields()

      // Navigate to main app
      // In a real app, you would switch to the main application scene
      println(s"Navigating to main app for user: ${user.username}")
      // mainApp.foreach(_.showMainView())

    } else {
      showAlert("Login Failed", "Invalid username or password. Please try again.")
      statusLabel.setText("Login failed. Please check your credentials.")
      passwordField.clear()
    }
  }

  // Handle register button
  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    statusLabel.setText("Redirecting to registration...")
    // In a real implementation, you would switch to the registration scene
    // mainApp.foreach(_.showRegisterView())
    println("Navigating to registration screen...")
  }

  // Clear all input fields
  private def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
  }

  // Show error alert
  private def showAlert(title: String, message: String): Unit = {
    val alert = new Alert(AlertType.Error) {
      this.title = title
      headerText = None
      contentText = message
    }
    alert.showAndWait()
  }

  // Show success alert
  private def showSuccessAlert(title: String, message: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      this.title = title
      headerText = None
      contentText = message
    }
    alert.showAndWait()
  }

  // Set main app reference (for scene switching)
  def setMainApp(mainApp: foodapp.MainApp.type): Unit = {
    this.mainApp = Some(mainApp)
  }

  // Get current authentication service (for testing or external access)
  def getAuthService: AuthenticationService = authService
}