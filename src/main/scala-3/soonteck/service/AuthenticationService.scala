package soonteck.service

import soonteck.model.User
import scala.util.{Try, Success, Failure}

class AuthenticationService:
  def authenticateUser(username: String, password: String): AuthResult =
    if (username.trim.isEmpty || password.isEmpty)
      return AuthResult.failure("Username and password are required.")

    User.getUserByUsername(username) match {
      case Some(user) if user.validateCredentials(password) => 
        AuthResult.success(user, "Login successful")
      case Some(_) =>
        AuthResult.failure("Incorrect password.")
      case None =>
        AuthResult.failure("Username not found.")
    }

  def registerUser(username: String, password: String, confirmPassword: String): Try[User] =
    Try {
      val newUser = User(username, password)
      newUser.validateForRegistration(confirmPassword) match {
        case Right(_) if !newUser.isExist =>
          newUser.save() match {
            case Success(_) => newUser
            case Failure(e) => throw e
          }
        case Right(_) =>
          throw new IllegalArgumentException("Username already exists.")
        case Left(error) =>
          throw new IllegalArgumentException(error)
      }
    }