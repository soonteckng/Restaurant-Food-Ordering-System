package soonteck.model

import java.time.YearMonth
import scala.util.{Try, Success, Failure}

case class PaymentValidationError(message: String) extends Exception(message)

class Payment(val paymentMethod: String, val cardNumber: String = "", val cardHolder: String = "",
               val expiry: String = "", val cvv: String = ""):
  
  def validate(): Either[PaymentValidationError, Unit] =
    validatePaymentMethod()
      .flatMap(_ => validateCardDetails())
      .flatMap(_ => validateExpiryDate())

  private def validatePaymentMethod(): Either[PaymentValidationError, Unit] =
    if (paymentMethod == null || paymentMethod.isEmpty)
      Left(PaymentValidationError("Payment method required."))
    else
      Right(())

  private def validateCardDetails(): Either[PaymentValidationError, Unit] =
    if (paymentMethod == "E-Wallet") return Right(())
    for {
      _ <- validateCardNumber()
      _ <- validateCardHolder()
      _ <- validateExpiryDate()
      _ <- validateCVV()
    } yield ()

  private def validateCardNumber(): Either[PaymentValidationError, Unit] =
    if (cardNumber.trim.isEmpty)
      Left(PaymentValidationError("Card number cannot be empty."))
    else if (!cardNumber.matches("\\d{16}"))
      Left(PaymentValidationError("Card number must be exactly 16 digits."))
    else
      Right(())

  private def validateCardHolder(): Either[PaymentValidationError, Unit] =
    if (cardHolder.trim.isEmpty)
      Left(PaymentValidationError("Card holder name cannot be empty."))
    else if (!cardHolder.matches("^[A-Za-z ]+$")) {
      val cardHolderError =s"""Card holder name must only contain 
                              |letters and spaces.""".stripMargin
      Left(PaymentValidationError(cardHolderError))
    } else
      Right(())
  
  private def validateExpiryDate(): Either[PaymentValidationError, Unit] =
    if (paymentMethod == "E-Wallet") return Right(())

    if (expiry.trim.isEmpty)
      return Left(PaymentValidationError("Expiry date cannot be empty."))

    if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$"))
      return Left(PaymentValidationError("Expiry date must be in MM/YY format."))

    Try {
      val parts = expiry.split("/")
      val expMonth = parts(0).toInt
      val expYear = 2000 + parts(1).toInt
      val expiryDate = YearMonth.of(expYear, expMonth)
      val currentDate = YearMonth.now()
      val maxFutureDate = currentDate.plusYears(10)

      if (expiryDate.isBefore(currentDate))
        Left(PaymentValidationError("The card expiry date has already passed."))
      else if (expiryDate.isAfter(maxFutureDate))
        val cardExpiryDateError = s"""Expiry date cannot be more than 10 years
                                  |in the future.""".stripMargin
        Left(PaymentValidationError(cardExpiryDateError))
      else
        Right(())
    }.getOrElse(Left(PaymentValidationError("Invalid expiry date format.")))

  private def validateCVV(): Either[PaymentValidationError, Unit] =
    if (cvv.trim.isEmpty)
      Left(PaymentValidationError("CVV cannot be empty."))
    else if (!cvv.matches("\\d{3}"))
      Left(PaymentValidationError("CVV must be exactly 3 digits."))
    else
      Right(())

  def processPayment(amount: Double): PaymentResult =
    validate() match
      case Left(error) => PaymentResult.failure(error.message)
      case Right(_) =>
        val orderNumber = s"QD${System.currentTimeMillis()}"
        PaymentResult.success(orderNumber, s"Payment of RM ${amount}%.2f processed successfully")