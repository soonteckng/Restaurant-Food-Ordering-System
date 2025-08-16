package soonteck.view

import soonteck.model.CartItem
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.scene.control.{Label, Button, TableView, TableColumn}
import javafx.stage.Stage
import javafx.collections.ObservableList

class TotalCaloriesOverviewController:
  @FXML private var itemCountLabel: Label = null
  @FXML private var totalCaloriesLabel: Label = null
  @FXML private var totalPriceLabel: Label = null
  @FXML private var averageCaloriesLabel: Label = null
  @FXML private var summaryTable: TableView[CartItem] = null
  @FXML private var summaryItemColumn: TableColumn[CartItem, String] = null
  @FXML private var summaryQuantityColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML private var summaryCaloriesColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML private var summaryPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML private var closeButton: Button = null

  private var dialogStage: Stage = null

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setCartItems(cartItems: ObservableList[CartItem]): Unit =
    // Set up the table
    summaryTable.setItems(cartItems)
    summaryItemColumn.cellValueFactory = { _.value.item }
    summaryQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    summaryCaloriesColumn.cellValueFactory = { c =>
      val totalItemCalories = c.value.getCalories
      new javafx.beans.property.SimpleIntegerProperty(totalItemCalories).asObject()
    }
    summaryPriceColumn.cellValueFactory = { c =>
      val totalItemPrice = c.value.getTotalPrice
      new javafx.beans.property.SimpleDoubleProperty(totalItemPrice).asObject()
    }

    // Calculate totals
    var totalCalories = 0
    var totalPrice = 0.0
    var totalItems = 0

    for i <- 0 until cartItems.size() do
      val item = cartItems.get(i)
      totalCalories += item.getCalories
      totalPrice += item.getTotalPrice
      totalItems += item.quantity.value

    // Calculate average calories per item
    val averageCalories = if totalItems > 0 then totalCalories.toDouble / totalItems else 0.0

    // Update labels
    itemCountLabel.setText(s"$totalItems")
    totalCaloriesLabel.setText(s"$totalCalories kcal")
    totalPriceLabel.setText(f"RM $totalPrice%.2f")
    averageCaloriesLabel.setText(f"$averageCalories%.1f kcal/item")

    // Set health status style based on average calories
    setHealthStatusStyle(averageCalories)

  private def setHealthStatusStyle(averageCalories: Double): Unit =
    val (style, message) = averageCalories match
      case avg if avg < 250 => ("-fx-text-fill: #28a745; -fx-font-weight: bold;", "🟢 Healthy Choice")
      case avg if avg < 400 => ("-fx-text-fill: #ffc107; -fx-font-weight: bold;", "🟡 Moderate")
      case avg if avg < 600 => ("-fx-text-fill: #fd7e14; -fx-font-weight: bold;", "🟠 High Calorie")
      case _ => ("-fx-text-fill: #dc3545; -fx-font-weight: bold;", "🔴 Very High Calorie")

    averageCaloriesLabel.setStyle(style)

  @FXML
  def handleClose(): Unit =
    dialogStage.close()