package foodapp.model

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}
import java.time.LocalDateTime

case class User(
                 username: String,
                 password: String,
                 email: Option[String] = None,
                 fridgeName: String = "My Smart Fridge",
                 createdAt: LocalDateTime = LocalDateTime.now(),
                 id: Option[Int] = None
               ) {

  // ScalaFX properties for UI binding if needed
  def usernameProperty: StringProperty = StringProperty(username)
  def emailProperty: StringProperty = StringProperty(email.getOrElse(""))
  def fridgeNameProperty: StringProperty = StringProperty(fridgeName)

  // Utility methods
  def hasEmail: Boolean = email.isDefined

  def displayName: String = username

  def isValidPassword(inputPassword: String): Boolean = {
    // In a real app, you'd use proper password hashing
    password == inputPassword
  }

  override def toString: String = s"User($username, email=${email.getOrElse("none")}, fridge=$fridgeName)"
}