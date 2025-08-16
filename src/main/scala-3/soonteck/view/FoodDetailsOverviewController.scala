package soonteck.view

import javafx.fxml.FXML
import javafx.scene.control.{Label, Button, Spinner}
import javafx.stage.Stage
import soonteck.model.FoodType
import soonteck.Main

class FoodDetailsOverviewController:
  @FXML private var foodNameLabel: Label = null
  @FXML private var categoryLabel: Label = null
  @FXML private var priceLabel: Label = null
  @FXML private var caloriesLabel: Label = null
  @FXML private var healthRatingLabel: Label = null
  @FXML private var servingSizeLabel: Label = null
  @FXML private var ingredientsLabel: Label = null
  @FXML private var descriptionLabel: Label = null
  @FXML private var quickQuantitySpinner: Spinner[Int] = null
  @FXML private var quickAddButton: Button = null
  @FXML private var closeButton: Button = null

  private var dialogStage: Stage = null
  private var currentFood: FoodType = null
  private var mainController: HomePageOverviewController = null

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setMainController(mainController: HomePageOverviewController): Unit =
    this.mainController = mainController

  def setFoodItem(foodItem: FoodType): Unit =
    currentFood = foodItem

    // Basic info
    foodNameLabel.setText(foodItem.name.value)
    categoryLabel.setText(foodItem.category.value)
    priceLabel.setText(f"RM ${foodItem.price.value}%.2f")
    caloriesLabel.setText(s"${foodItem.calories.value} kcal")
    descriptionLabel.setText(foodItem.description.value)

    // Enhanced info based on food properties
    setHealthRating(foodItem.calories.value)
    setServingSize(foodItem.category.value)
    setIngredients(foodItem.name.value, foodItem.category.value)

  private def setHealthRating(calories: Int): Unit =
    val (rating, style) = calories match
      case c if c < 250 => ("🟢 Very Healthy", "-fx-text-fill: #28a745; -fx-font-weight: bold;")
      case c if c < 400 => ("🟡 Moderately Healthy", "-fx-text-fill: #ffc107; -fx-font-weight: bold;")
      case c if c < 600 => ("🟠 High Calorie", "-fx-text-fill: #fd7e14; -fx-font-weight: bold;")
      case _ => ("🔴 Very High Calorie", "-fx-text-fill: #dc3545; -fx-font-weight: bold;")

    healthRatingLabel.setText(rating)
    healthRatingLabel.setStyle(style)

  private def setServingSize(category: String): Unit =
    val servingSize = category match
      case "Fast Food" => "1 large portion"
      case "Italian Food" => "1 slice (for pizza) / 1 plate"
      case "Japanese" => "6-8 pieces"
      case "Chinese" => "1 bowl / 1 plate"
      case "Malaysian" => "1 plate"
      case _ => "1 regular serving"

    servingSizeLabel.setText(servingSize)

  private def setIngredients(foodName: String, category: String): Unit =
    val ingredients = (foodName.toLowerCase, category) match
      case (name, _) if name.contains("pizza") => "Dough, tomato sauce, cheese, toppings"
      case (name, _) if name.contains("burger") => "Beef patty, bun, lettuce, tomato, onions"
      case (name, _) if name.contains("sushi") => "Rice, fresh fish, seaweed, wasabi"
      case (name, _) if name.contains("noodle") => "Noodles, vegetables, protein, sauce"
      case (name, _) if name.contains("rice") => "Rice, vegetables, meat/seafood, spices"
      case (_, "Italian Food") => "Fresh herbs, olive oil, tomatoes, cheese"
      case (_, "Japanese") => "Fresh ingredients, rice, soy sauce, miso"
      case (_, "Fast Food") => "Processed ingredients, sauces, seasonings"
      case (_, "Chinese") => "Soy sauce, ginger, garlic, fresh vegetables"
      case _ => "Fresh ingredients, spices, seasonings"

    ingredientsLabel.setText(ingredients)

  @FXML
  def handleQuickAdd(): Unit =
    if currentFood != null && mainController != null then
      val quantity = quickQuantitySpinner.getValue
      mainController.addToCartFromDialog(currentFood, quantity)
      dialogStage.close()

  @FXML
  def handleClose(): Unit =
    dialogStage.close()