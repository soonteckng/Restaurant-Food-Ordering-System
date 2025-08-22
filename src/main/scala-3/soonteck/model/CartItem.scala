package soonteck.model

import scalafx.beans.property.{StringProperty, DoubleProperty, IntegerProperty}

class CartItem(val foodItem: FoodType, val quantityI: Int):
  var item = new StringProperty(foodItem.name.value)
  var quantity = new IntegerProperty(this, "quantity", quantityI)
  var price = new DoubleProperty(this, "price", foodItem.price.value * quantityI)

  quantity.onChange { (_, _, newValue) =>
    price.value = foodItem.price.value * newValue.intValue()
  }

  def getTotalPrice: Double = foodItem.price.value * quantity.value
  def getCalories: Int = foodItem.calories.value * quantity.value