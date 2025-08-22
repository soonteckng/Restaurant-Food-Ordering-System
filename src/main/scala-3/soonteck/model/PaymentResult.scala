package soonteck.model

sealed trait PaymentResult:
  def isSuccess: Boolean
  def getMessage: String

object PaymentResult:
  case class Success(orderNumber: String, message: String) extends PaymentResult:
    def isSuccess: Boolean = true
    def getMessage: String = message

  case class Failure(message: String) extends PaymentResult:
    def isSuccess: Boolean = false
    def getMessage: String = message

  def success(orderNumber: String, message: String): PaymentResult = Success(orderNumber, message)
  def failure(message: String): PaymentResult = Failure(message)