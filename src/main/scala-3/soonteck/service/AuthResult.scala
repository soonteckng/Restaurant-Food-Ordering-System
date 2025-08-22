package soonteck.service

import soonteck.model.User

sealed trait AuthResult:
  def isSuccess: Boolean
  def getMessage: String

object AuthResult:
  case class Success(user: User, message: String) extends AuthResult:
    def isSuccess: Boolean = true
    def getMessage: String = message

  case class Failure(message: String) extends AuthResult:
    def isSuccess: Boolean = false
    def getMessage: String = message

  def success(user: User, message: String): AuthResult = Success(user, message)
  def failure(message: String): AuthResult = Failure(message)

sealed trait ValidationResult:
  def isSuccess: Boolean
  def getMessage: String

object ValidationResult:
  case class Success(message: String) extends ValidationResult:
    def isSuccess: Boolean = true
    def getMessage: String = message

  case class Failure(message: String) extends ValidationResult:
    def isSuccess: Boolean = false
    def getMessage: String = message

  def success(message: String): ValidationResult = Success(message)
  def failure(message: String): ValidationResult = Failure(message)