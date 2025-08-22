package soonteck.model

case class CartSummary(
                        totalItems: Int,
                        totalCalories: Int,
                        totalPrice: Double,
                        averageCalories: Double,
                        healthStatus: String,
                        subtotal: Double,
                        tax: Double,
                        finalTotal: Double
                      )