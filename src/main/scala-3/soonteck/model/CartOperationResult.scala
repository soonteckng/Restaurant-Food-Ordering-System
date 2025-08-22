package soonteck.model

sealed trait CartOperationResult:
  def isSuccess: Boolean
  def getMessage: String

object CartOperationResult:
  case class Success(message: String) extends CartOperationResult:
    def isSuccess: Boolean = true
    def getMessage: String = message

  case class Failure(message: String) extends CartOperationResult:
    def isSuccess: Boolean = false
    def getMessage: String = message

  def success(message: String): CartOperationResult = Success(message)
  def failure(message: String): CartOperationResult = Failure(message)