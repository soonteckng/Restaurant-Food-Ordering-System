package soonteck.service

import soonteck.model.FoodType
import javafx.collections.{FXCollections, ObservableList}
import javafx.collections.transformation.FilteredList
import scala.jdk.CollectionConverters.*

class FoodService:
  private val allFoods: ObservableList[FoodType] = FXCollections.observableArrayList()

  def loadAllFoods(): Unit =
    allFoods.clear()
    val foods = FoodType.getAllFood
    foods.foreach(allFoods.add)

  def getAllFoods: ObservableList[FoodType] = allFoods

  def getDistinctCategories: List[String] =
    allFoods.asScala.map(_.category.value).distinct.sorted.toList

  def filterFoods(searchText: String, category: String, healthyOnly: Boolean): FilteredList[FoodType] =
    val filteredList = new FilteredList[FoodType](allFoods)
    filteredList.setPredicate { food =>
      val matchesSearch = searchText == null || searchText.isEmpty ||
        food.name.value.toLowerCase.contains(searchText.toLowerCase) ||
        food.description.value.toLowerCase.contains(searchText.toLowerCase)
      val matchesCategory = category == "All" || food.category.value == category
      val matchesHealthy = !healthyOnly || food.isHealthy
      matchesSearch && matchesCategory && matchesHealthy
    }
    filteredList