package soonteck.model

import scalafx.beans.property.{IntegerProperty, DoubleProperty, StringProperty}
import javafx.collections.{FXCollections, ObservableList}
import scala.jdk.CollectionConverters._

class Cart {
  private val items: ObservableList[CartItem] = FXCollections.observableArrayList()

  // Add item to cart
  def addItem(food: FoodType, quantity: Int): Unit = {
    require(quantity > 0, "Quantity must be positive")
    items.asScala.find(_.item.value == food.name.value) match {
      case Some(existingItem) =>
        existingItem.quantity.value += quantity
      case None =>
        items.add(new CartItem(food, quantity))
    }
  }

  // Remove item from cart
  def removeItem(item: CartItem): Unit = {
    items.remove(item)
  }

  // Clear cart
  def clear(): Unit = {
    items.clear()
  }

  // Get total items
  def getTotalItems: Int = {
    items.asScala.map(_.quantity.value).sum
  }

  // Get total calories
  def getTotalCalories: Int = {
    items.asScala.map(_.getCalories).sum
  }

  // Get total price
  def getTotalPrice: Double = {
    items.asScala.map(_.getTotalPrice).sum
  }

  // Get average calories
  def getAverageCalories: Double = {
    val totalItems = getTotalItems
    if (totalItems > 0) getTotalCalories.toDouble / totalItems else 0.0
  }

  // Get health status
  def getHealthStatus: String = {
    val avgCalories = getAverageCalories
    avgCalories match {
      case avg if avg == 0 => "No Items"
      case avg if avg < 250 => "🟢 Very Healthy"
      case avg if avg < 400 => "🟡 Moderately Healthy"
      case avg if avg < 600 => "🟠 High Calorie"
      case _ => "🔴 Very High Calorie"
    }
  }

  // Get cart items for UI binding
  def getItems: ObservableList[CartItem] = items

  // Create order from cart
  def createOrder(username: String): OrderHistory = {
    OrderHistory.createOrderFromCart(items, username)
  }
}