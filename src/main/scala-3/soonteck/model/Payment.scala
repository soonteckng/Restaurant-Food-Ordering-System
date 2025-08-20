package soonteck.model

import java.time.YearMonth

case class PaymentValidationError(message: String) extends Exception(message)

class Payment(
               paymentMethod: String,
               cardNumber: String = "",
               cardHolder: String = "",
               expiry: String = "",
               cvv: String = ""
             ) {
  def validate(): Either[PaymentValidationError, Unit] = {
    if (paymentMethod == null || paymentMethod.isEmpty) {
      return Left(PaymentValidationError("Payment method required"))
    }
    if (paymentMethod != "E-Wallet") {
      if (cardNumber.trim.isEmpty) {
        return Left(PaymentValidationError("Card number cannot be empty"))
      }
      if (cardHolder.trim.isEmpty) {
        return Left(PaymentValidationError("Card holder name cannot be empty"))
      }
      if (expiry.trim.isEmpty) {
        return Left(PaymentValidationError("Expiry date cannot be empty"))
      }
      if (cvv.trim.isEmpty) {
        return Left(PaymentValidationError("CVV cannot be empty"))
      }
      if (!cardNumber.matches("\\d{16}")) {
        return Left(PaymentValidationError("Card number must be exactly 16 digits"))
      }
      if (!cardHolder.matches("^[A-Za-z ]+$")) {
        return Left(PaymentValidationError("Card holder name must only contain letters and spaces"))
      }
      if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
        return Left(PaymentValidationError("Expiry date must be in MM/YY format"))
      }
      val parts = expiry.split("/")
      val expMonth = parts(0).toInt
      val expYear = 2000 + parts(1).toInt
      val expiryDate = YearMonth.of(expYear, expMonth)
      val currentDate = YearMonth.now()
      val maxFutureDate = currentDate.plusYears(10)
      if (expiryDate.isBefore(currentDate)) {
        return Left(PaymentValidationError("The card expiry date has already passed"))
      }
      if (expiryDate.isAfter(maxFutureDate)) {
        return Left(PaymentValidationError("Expiry date cannot be more than 10 years in the future"))
      }
      if (!cvv.matches("\\d{3}")) {
        return Left(PaymentValidationError("CVV must be exactly 3 digits"))
      }
    }
    Right(())
  }
}
