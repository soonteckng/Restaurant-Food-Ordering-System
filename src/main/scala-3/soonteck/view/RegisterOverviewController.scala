package soonteck.view

import soonteck.alert.Alerts
import soonteck.model.User
import soonteck.service.AuthenticationService
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.Includes.*
import scalafx.stage.Stage
import soonteck.Main

@FXML
class RegisterOverviewController {

  @FXML
  private var usernameField: TextField = null

  @FXML
  private var passwordField: PasswordField = null

  @FXML
  private var confirmPasswordField: PasswordField = null

  @FXML
  private var fridgeNameField: TextField = null

  @FXML
  private var backToLoginButton: Button = null

  // Service for authentication
  private val authService = new AuthenticationService()
  var alerts = new Alerts()
  var isCreateAccountClicked = false
  
  // Handle registration
  @FXML
  private def handleRegister(action: ActionEvent): Unit = {
    val username = usernameField.getText.trim
    val password = passwordField.getText
    val confirmPassword = confirmPasswordField.getText
    val fridgeName = fridgeNameField.getText.trim

    // Stop if validation fails
    if (!validateInput(username, password, confirmPassword, fridgeName)) {
      return
    }

    // Create user
    val newUser = User(username, password)

    // Attempt registration
    if (authService.registerUser(newUser)) {
      alerts.showSuccessAlert("Registration Successful", s"Account created for $username! You can now login.")

      // Clear fields
      clearFields()

      // Navigate back to login after a brief delay
      // In a real app, you might want to do this automatically
      handleBackToLogin(action)
    } else {
      alerts.showErrorAlert("Registration Failed", s"Username '$username' already exists. Please choose a different username.")
    }
  }

  // Validate input fields
  private def validateInput(username: String, password: String, confirmPassword: String, fridgeName: String): Boolean = {
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

    if (fridgeName.isEmpty) {
      alerts.showErrorAlert("Validation Error", "Fridge name is required.")
      return false
    }
    true
  }
  
  // Handle back to login
  @FXML
  private def handleBackToLogin(action: ActionEvent): Unit =
    Main.showLoginOverview()


  // Clear all input fields
  private def clearFields(): Unit = {
    usernameField.clear()
    passwordField.clear()
    confirmPasswordField.clear()
    fridgeNameField.setText("My Smart Fridge")
  }
}