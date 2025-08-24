package soonteck.service

import soonteck.model.{Cart, OrderHistory, CartItem}
import scala.util.{Try, Success, Failure}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.*

class OrderService:
  def createOrderFromCart(cart: Cart, username: String): Try[OrderHistory] =
    if (cart.isEmpty)
      return Failure(new IllegalStateException("Cannot create order from empty cart."))

    if (username.trim.isEmpty)
      return Failure(new IllegalArgumentException("Username is required."))

    Try {
      val orderId = s"ORD-${System.currentTimeMillis()}"
      val orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
      val itemsBuilder = new StringBuilder()
      val cartItems = cart.getItems.asScala.toList
      cartItems.zipWithIndex.foreach { case (item, index) =>
        if (index > 0) itemsBuilder.append(", ")
        itemsBuilder.append(s"${item.item.value} x${item.quantity.value}")
      }
      val order = new OrderHistory(
        orderId,
        orderDate,
        itemsBuilder.toString(),
        cart.getFinalTotal,
        "Completed",
        username
      )
      order.save() match {
        case Success(_) => order
        case Failure(e) => throw e
      }
    }

  def getOrderHistoryForUser(username: String): Try[List[OrderHistory]] =
    Try {
      OrderHistory.getOrdersForUser(username)
    }