package soonteck.model

import scalafx.beans.property.{IntegerProperty, DoubleProperty, StringProperty}
import javafx.collections.{FXCollections, ObservableList}
import scala.jdk.CollectionConverters._

class Cart {
  private val items: ObservableList[CartItem] = FXCollections.observableArrayList()

  def addItem(food: FoodType, quantity: Int): Unit = {
    require(quantity > 0, "Quantity must be positive")
    items.asScala.find(_.item.value == food.name.value) match {
      case Some(existingItem) =>
        existingItem.quantity.value += quantity
      case None =>
        items.add(new CartItem(food, quantity))
    }
  }

  def removeItem(item: CartItem): Unit = {
    items.remove(item)
  }

  def clear(): Unit = {
    items.clear()
  }

  def getTotalItems: Int = {
    items.asScala.map(_.quantity.value).sum
  }

  def getTotalCalories: Int = {
    items.asScala.map(_.getCalories).sum
  }

  def getTotalPrice: Double = {
    items.asScala.map(_.getTotalPrice).sum
  }

  def getAverageCalories: Double = {
    val totalItems = getTotalItems
    if (totalItems > 0) getTotalCalories.toDouble / totalItems else 0.0
  }

  def getHealthStatus: String = {
    val avgCalories = getAverageCalories
    avgCalories match {
      case avg if avg == 0 => "No Items"
      case avg if avg < 300 => "🟢 Very Healthy"
      case avg if avg < 500 => "🟡 Moderately Healthy"
      case avg if avg < 650 => "🟠 High Calorie"
      case _ => "🔴 Very High Calorie"
    }
  }

  def getItems: ObservableList[CartItem] = items

  def createOrder(username: String): OrderHistory = {
    OrderHistory.createOrderFromCart(items, username)
  }
}