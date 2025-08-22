package soonteck.view

import soonteck.model.FoodType
import soonteck.Main
import javafx.fxml.FXML
import javafx.scene.control.{Label, Button, Spinner}
import javafx.stage.Stage

class FoodDetailsOverviewController:
  @FXML 
  private var foodNameLabel: Label = null
  @FXML 
  private var categoryLabel: Label = null
  @FXML
  private var priceLabel: Label = null
  @FXML
  private var caloriesLabel: Label = null
  @FXML
  private var healthRatingLabel: Label = null
  @FXML 
  private var servingSizeLabel: Label = null
  @FXML 
  private var ingredientsLabel: Label = null
  @FXML
  private var descriptionLabel: Label = null
  @FXML 
  private var quickQuantitySpinner: Spinner[Int] = null
  @FXML 
  private var quickAddButton: Button = null
  @FXML
  private var closeButton: Button = null

  private var dialogStage: Stage = null
  private var currentFood: FoodType = null
  private var mainController: HomePageOverviewController = null

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setMainController(mainController: HomePageOverviewController): Unit =
    this.mainController = mainController

  def setFoodItem(foodItem: FoodType): Unit =
    currentFood = foodItem
    foodNameLabel.setText(foodItem.name.value)
    categoryLabel.setText(foodItem.category.value)
    priceLabel.setText(f"RM ${foodItem.price.value}%.2f")
    caloriesLabel.setText(s"${foodItem.calories.value} kcal")
    descriptionLabel.setText(foodItem.description.value)
    setHealthRating()
    setServingSize()
    setIngredients()

  private def setHealthRating(): Unit =
    healthRatingLabel.setText(currentFood.getHealthRating)

  private def setServingSize(): Unit =
    servingSizeLabel.setText(currentFood.getServingSize)

  private def setIngredients(): Unit =
    ingredientsLabel.setText(currentFood.getIngredients)

  @FXML
  def handleQuickAdd(): Unit =
    if currentFood != null && mainController != null then
      val quantity = quickQuantitySpinner.getValue
      mainController.addToCartFromDialog(currentFood, quantity)
      dialogStage.close()

  @FXML
  def handleClose(): Unit =
    dialogStage.close()