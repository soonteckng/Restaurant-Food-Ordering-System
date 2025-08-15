package soonteck.view

import scalafx.Includes.*
import javafx.scene.control.{TableView, TableColumn, Label, Spinner, Button}
import javafx.fxml.FXML
import soonteck.Main
import soonteck.model.FoodType

@FXML
class HomePageOverviewController():

  @FXML private var foodTable: TableView[FoodType] = null
  @FXML private var nameColumn: TableColumn[FoodType, String] = null
  @FXML private var categoryColumn: TableColumn[FoodType, String] = null
  @FXML private var priceColumn: TableColumn[FoodType, java.lang.Double] = null
  @FXML private var caloriesColumn: TableColumn[FoodType, java.lang.Integer] = null
  @FXML private var descriptionColumn: TableColumn[FoodType, String] = null
  @FXML private var selectedItemLabel: Label = null
  @FXML private var quantitySpinner: Spinner[Int] = null
  @FXML private var addToCartButton: Button = null

  def initialize(): Unit =
    foodTable.items = Main.foodData

    nameColumn.cellValueFactory        = { _.value.name }
    categoryColumn.cellValueFactory    = { _.value.category }
    priceColumn.cellValueFactory       = { c => c.value.price.delegate.asObject() }
    caloriesColumn.cellValueFactory    = { c => c.value.calories.delegate.asObject() }
    descriptionColumn.cellValueFactory = { _.value.description }

    foodTable.selectionModel().selectedItem.onChange {
      (_, _, newValue) =>
        if newValue != null then selectedItemLabel.text = newValue.name.value
        else selectedItemLabel.text = "None selected"
    }
