package foodapp.service

import foodapp.model.User
import scala.collection.mutable

class AuthenticationService {

  // In-memory storage for users (in a real app, this would be a database)
  private val users = mutable.Map[String, User]()
  private var currentUser: Option[User] = None

  // Initialize with default users for testing
  initializeDefaultUsers()

  private def initializeDefaultUsers(): Unit = {
    val defaultUser1 = new User("admin", "Admin123")
    users += (defaultUser1.getUsername -> defaultUser1)
   
    println("Default users initialized: admin, user, test")
  }

  /**
   * Register a new user
   * @param user The user to register
   * @return true if registration successful, false if username already exists
   */
  def registerUser(user: User): Boolean = {
    if (users.contains(user.getUsername)) {
      false // Username already exists
    } else {
      users += (user.getUsername -> user)
      println(s"User registered: ${user.getUsername}")
      true
    }
  }

  /**
   * Authenticate a user with username and password
   * @param username The username
   * @param password The password
   * @return Some(user) if authentication successful, None otherwise
   */
  def authenticateUser(username: String, password: String): Option[User] = {
    users.get(username) match {
      case Some(user) if user.isValidPassword(password) =>
        currentUser = Some(user)
        println(s"User authenticated: $username")
        Some(user)
      case _ =>
        println(s"Authentication failed for: $username")
        None
    }
  }

  /**
   * Login with username and password
   * @param username The username
   * @param password The password
   * @return true if login successful, false otherwise
   */
  def login(username: String, password: String): Boolean = {
    authenticateUser(username, password).isDefined
  }

  /**
   * Logout the current user
   */
  def logout(): Unit = {
    currentUser.foreach(user => println(s"You (${user.getUsername}) had successfully logged out."))
    currentUser = None
  }

  /**
   * Get the currently logged-in user
   * @return Some(user) if logged in, None otherwise
   */
  def getCurrentUser: Option[User] = currentUser

  /**
   * Check if a user is currently logged in
   * @return true if logged in, false otherwise
   */
  def isLoggedIn: Boolean = currentUser.isDefined

  /**
   * Check if a username is available
   * @param username The username to check
   * @return true if available, false if taken
   */
  def isUsernameAvailable(username: String): Boolean = {
    !users.contains(username)
  }

  /**
   * Get all registered users (for admin purposes)
   * @return List of all users
   */
  def getAllUsers: List[User] = users.values.toList

  /**
   * Update user information
   * @param updatedUser The updated user
   * @return true if update successful, false otherwise
   */
  def updateUser(updatedUser: User): Boolean = {
    users.get(updatedUser.getUsername) match {
      case Some(_) =>
        users += (updatedUser.getUsername -> updatedUser)
        if (currentUser.exists(_.getUsername == updatedUser.getUsername)) {
          currentUser = Some(updatedUser)
        }
        true
      case None => false
    }
  }

  /**
   * Delete a user
   * @param username The username to delete
   * @return true if deletion successful, false otherwise
   */
  def deleteUser(username: String): Boolean = {
    users.remove(username) match {
      case Some(_) =>
        if (currentUser.exists(_.getUsername == username)) {
          currentUser = None
        }
        true
      case None => false
    }
  }

  /**
   * Validate user input
   * @param username The username
   * @param password The password
   * @return List of validation errors (empty if valid)
   */
  def validateUserInput(username: String, password: String): List[String] = {
    val errors = mutable.ListBuffer[String]()

    if (username.trim.isEmpty) {
      errors += "Username cannot be empty"
    } else if (username.length < 3) {
      errors += "Username must be at least 3 characters long"
    } else if (username.length > 20) {
      errors += "Username must be less than 20 characters long"
    }

    if (password.isEmpty) {
      errors += "Password cannot be empty"
    } else if (password.length < 6) {
      errors += "Password must be at least 6 characters long"
    } else if (!password.matches(".*[A-Z].*")) {
      errors += "Password must contain at least one uppercase letter"
    } else if (!password.matches(".*[a-z].*")) {
      errors += "Password must contain at least one lowercase letter"
    } else if (!password.matches(".*\\d.*")) {
      errors += "Password must contain at least one number"
    }

    errors.toList
  }

  /**
   * Get user statistics
   * @return Map of statistics
   */
  def getUserStats: Map[String, Int] = {
    Map(
      "totalUsers" -> users.size,
    )
  }
}