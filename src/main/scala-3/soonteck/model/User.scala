package soonteck.model

import soonteck.util.Database
import scalafx.beans.property.StringProperty

class User(usernameS: String, passwordS: String) extends Database:
  def this()= this(null, null)
  var username = new StringProperty(usernameS)
  var password = new StringProperty(passwordS)

  // Method required by AuthenticationService
  def isValidPassword(inputPassword: String): Boolean = {
    password.value == inputPassword
  }
  
  // Getter methods for easier access
  def getUsername: String = username.value
  def getPassword: String = password.value

  // Override toString for debugging
  override def toString: String = s"User(username=${getUsername})"