package soonteck.view

import soonteck.model.{Cart, CartItem}
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, TableColumn, TableView}
import javafx.stage.Stage

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

  def setDialogStage(dialogStage: Stage): Unit = {
    this.dialogStage = dialogStage
  }

  def setCartItems(cart: Cart): Unit = {
    summaryTable.setItems(cart.getItems)
    summaryItemColumn.cellValueFactory = { _.value.item }
    summaryQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    summaryCaloriesColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleIntegerProperty(c.value.getCalories).asObject()
    }
    summaryPriceColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleDoubleProperty(c.value.getTotalPrice).asObject()
    }

    itemCountLabel.setText(s"${cart.getTotalItems}")
    totalCaloriesLabel.setText(s"${cart.getTotalCalories} kcal")
    totalPriceLabel.setText(f"RM ${cart.getTotalPrice}%.2f")
    val averageCalories = cart.getAverageCalories
    averageCaloriesLabel.setText(f"$averageCalories%.1f kcal/item")
    averageCaloriesLabel.setStyle(cart.getHealthStatus match {
      case "🟢 Very Healthy" => "-fx-text-fill: #28a745; -fx-font-weight: bold;"
      case "🟡 Moderately Healthy" => "-fx-text-fill: #ffc107; -fx-font-weight: bold;"
      case "🟠 High Calorie" => "-fx-text-fill: #fd7e14; -fx-font-weight: bold;"
      case _ => "-fx-text-fill: #dc3545; -fx-font-weight: bold;"
    })
  }

  @FXML
  def handleClose(): Unit = {
    dialogStage.close()
  }
