package soonteck.model

import scalafx.beans.property.{IntegerProperty, DoubleProperty, StringProperty}
import javafx.collections.{FXCollections, ObservableList}
import scala.jdk.CollectionConverters.*

class Cart:
  private val items: ObservableList[CartItem] = FXCollections.observableArrayList()
  private val maxItemsPerFood = 10
  private val maxTotalItems = 50

  def addItem(food: FoodType, quantity: Int): CartOperationResult =
    if (quantity <= 0)
      return CartOperationResult.failure("Quantity must be positive.")

    if (getTotalItems + quantity > maxTotalItems)
      return CartOperationResult.failure(s"Cart limit of $maxTotalItems items exceeded.")

    items.asScala.find(_.item.value == food.name.value) match
      case Some(existingItem) =>
        val newQuantity = existingItem.quantity.value + quantity
        if (newQuantity > maxItemsPerFood)
          return CartOperationResult.failure(s"Maximum $maxItemsPerFood items allowed per food.")
        existingItem.quantity.value = newQuantity
        CartOperationResult.success(s"Updated ${food.name.value} quantity to $newQuantity.")
      case None =>
        if (quantity > maxItemsPerFood)
          return CartOperationResult.failure(s"Maximum $maxItemsPerFood items allowed per food item.")
        items.add(new CartItem(food, quantity))
        CartOperationResult.success(s"Added ${food.name.value} to cart!")

  def removeItem(item: CartItem): CartOperationResult =
    if (items.remove(item))
      CartOperationResult.success("Item removed from cart.")
    else
      CartOperationResult.failure("Item not found in cart.")

  def updateQuantity(item: CartItem, newQuantity: Int): CartOperationResult =
    if (newQuantity <= 0)
      removeItem(item)
    else if (newQuantity > maxItemsPerFood)
      CartOperationResult.failure(s"Maximum $maxItemsPerFood items allowed.")
    else
      item.quantity.value = newQuantity
      CartOperationResult.success("Quantity updated.")

  def clear(): Unit =
    items.clear()

  def isEmpty: Boolean = items.isEmpty

  def getTotalItems: Int =
    items.asScala.map(_.quantity.value).sum

  def getTotalCalories: Int =
    items.asScala.map(_.getCalories).sum

  def getTotalPrice: Double =
    items.asScala.map(_.getTotalPrice).sum

  def getSubtotal: Double = getTotalPrice

  def getTax: Double = getTotalPrice * 0.06

  def getFinalTotal: Double = getSubtotal + getTax

  def getAverageCalories: Double =
    val totalItems = getTotalItems
    if (totalItems > 0) getTotalCalories.toDouble / totalItems else 0.0

  def getHealthStatus: String =
    val avgCalories = getAverageCalories
    avgCalories match
      case avg if avg == 0 => "-"
      case avg if avg < 300 => "🟢 Low Calorie"
      case avg if avg < 500 => "🟡 Moderate Calorie"
      case avg if avg < 700 => "🟠 High Calorie"
      case _ => "🔴 Very High Calorie"

  def getItems: ObservableList[CartItem] = items

  def getCartSummary: CartSummary =
    CartSummary(
      totalItems = getTotalItems,
      totalCalories = getTotalCalories,
      totalPrice = getTotalPrice,
      averageCalories = getAverageCalories,
      healthStatus = getHealthStatus,
      subtotal = getSubtotal,
      tax = getTax,
      finalTotal = getFinalTotal
    )