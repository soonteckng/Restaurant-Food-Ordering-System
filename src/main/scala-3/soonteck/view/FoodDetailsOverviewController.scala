package soonteck.view

import javafx.fxml.FXML
import javafx.scene.control.{Label, Button}
import javafx.stage.Stage
import soonteck.model.FoodType

class FoodDetailsOverviewController():
  @FXML
  private var foodNameLabel: Label = null
  @FXML
  private var categoryLabel: Label = null
  @FXML
  private var priceLabel: Label = null
  @FXML
  private var caloriesLabel: Label = null
  @FXML
  private var descriptionLabel: Label = null
  @FXML
  private var closeButton: Button = null

  private var dialogStage: Stage = null

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setFoodItem(foodItem: FoodType): Unit =
    foodNameLabel.setText(foodItem.name.value)
    categoryLabel.setText(foodItem.category.value)
    priceLabel.setText(f"RM ${foodItem.price.value}%.2f")
    caloriesLabel.setText(s"${foodItem.calories.value} kcal")
    descriptionLabel.setText(foodItem.description.value)

  @FXML
  def handleClose(): Unit =
    dialogStage.close()