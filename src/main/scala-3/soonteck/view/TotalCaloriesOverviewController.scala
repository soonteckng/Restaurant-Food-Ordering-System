package soonteck.view

import soonteck.model.{Cart, CartItem}
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label, TableColumn, TableView}
import javafx.stage.Stage
import javafx.beans.property.{SimpleIntegerProperty, SimpleDoubleProperty}

class TotalCaloriesOverviewController:
  @FXML 
  private var itemCountLabel: Label = null
  @FXML 
  private var totalCaloriesLabel: Label = null
  @FXML 
  private var totalPriceLabel: Label = null
  @FXML 
  private var averageCaloriesLabel: Label = null
  @FXML 
  private var summaryTable: TableView[CartItem] = null
  @FXML
  private var summaryItemColumn: TableColumn[CartItem, String] = null
  @FXML
  private var summaryQuantityColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML 
  private var summaryCaloriesColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML
  private var summaryPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML
  private var closeButton: Button = null

  private var dialogStage: Stage = null

  def setDialogStage(dialogStage: Stage): Unit = {
    this.dialogStage = dialogStage
  }

  def setCartItems(cart: Cart): Unit = {
    summaryTable.setItems(cart.getItems)
    summaryItemColumn.cellValueFactory = { _.value.item }
    summaryQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    summaryCaloriesColumn.cellValueFactory = { c =>
      new SimpleIntegerProperty(c.value.getCalories).asObject()
    }
    summaryPriceColumn.cellValueFactory = { c =>
      new SimpleDoubleProperty(c.value.getTotalPrice).asObject()
    }

    itemCountLabel.setText(s"${cart.getTotalItems}")
    totalCaloriesLabel.setText(s"${cart.getTotalCalories} kcal")
    totalPriceLabel.setText(f"RM ${cart.getTotalPrice}%.2f")
    val averageCalories = cart.getAverageCalories
    averageCaloriesLabel.setText(f"$averageCalories%.1f kcal/item")
  }

  @FXML
  def handleClose(): Unit = {
    dialogStage.close()
  }
