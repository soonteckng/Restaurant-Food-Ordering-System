package foodapp.register

import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import foodapp.model.User
import foodapp.service.AuthenticationService

class RegisterOverviewController {

  @FXML
  private var usernameField: TextField = null

  @FXML
  private var emailField: TextField = null

  @FXML
  private var passwordField: PasswordField = null

  @FXML
  private var confirmPasswordField: PasswordField = null

  @FXML
  private var fridgeNameField: TextField = null

  @FXML
  private var registerButton: Button = null

  @FXML
  private var backToLoginButton: Button = null

  @FXML
  private var statusLabel: Label = null

  // Service for authentication
  private val authService = new AuthenticationService()

  // Reference to main app (for scene switching)
  private var mainApp: Option[foodapp.MainApp.type] = None

  def initialize(): Unit = {
    statusLabel.setText("Fill in the details to create your account")
  }

  // Handle registration
  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val email = emailField.getText.trim
    val password = passwordField.getText
    val confirmPassword = confirmPasswordField.getText
    val fridgeName = fridgeNameField.getText.trim

    // Validation
    if (!validateInput(username, password, confirmPassword, fridgeName)) {
      return
    }

    // Create user
    val emailOption = if (email.isEmpty) None else Some(email)
    val newUser = User(username, password, emailOption, fridgeName)

    // Attempt registration
    if (authService.registerUser(newUser)) {
      showSuccessAlert("Registration Successful", s"Account created for $username! You can now login.")
      statusLabel.setText("Registration successful! Redirecting to login...")

      // Clear fields
      clearFields()

      // Navigate back to login after a brief delay
      // In a real app, you might want to do this automatically
      handleBackToLogin(action)
    } else {
      showAlert("Registration Failed", s"Username '$username' already exists. Please choose a different username.")
      statusLabel.setText("Registration failed. Please try a different username.")
    }
  }

  // Validate input fields
  private def validateInput(username: String, password: String, confirmPassword: String, fridgeName: String): Boolean = {
    if (username.isEmpty) {
      showAlert("Validation Error", "Username is required.")
      return false
    }

    if (username.length < 3) {
      showAlert("Validation Error", "Username must be at least 3 characters long.")
      return false
    }

    if (password.isEmpty) {
      showAlert("Validation Error", "Password is required.")
      return false
    }

    if (password.length < 6) {
      showAlert("Validation Error", "Password must be at least 6 characters long.")
      return false
    }

    if (password != confirmPassword) {
      showAlert("Validation Error", "Passwords do not match.")
      return false
    }

    if (fridgeName.isEmpty) {
      showAlert("Validation Error", "Fridge name is required.")
      return false
    }

    // Check for valid email format if provided
    val email = emailField.getText.trim
    if (email.nonEmpty && !isValidEmail(email)) {
      showAlert("Validation Error", "Please enter a valid email address.")
      return false
    }

    true
  }

  // Simple email validation
  private def isValidEmail(email: String): Boolean = {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".r
    emailRegex.matches(email)
  }

  // Handle back to login
  @FXML
  private def handleBackToLogin(action: ActionEvent): Unit = {
    // This would typically involve switching scenes back to login
    // For now, we'll just show a message
    statusLabel.setText("Redirecting to login...")

    // In a real implementation, you would:
    // mainApp.foreach(_.showLoginView())
    println("Navigating back to login screen...")
  }

  // Clear all input fields
  private def clearFields(): Unit = {
    usernameField.clear()
    emailField.clear()
    passwordField.clear()
    confirmPasswordField.clear()
    fridgeNameField.setText("My Smart Fridge")
  }

  // Show error alert
  /*private def showAlert(title: String, message: String): Unit = {
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
  }*/

  // Set main app reference (for scene switching)
  def setMainApp(mainApp: foodapp.MainApp.type): Unit = {
    this.mainApp = Some(mainApp)
  }
}