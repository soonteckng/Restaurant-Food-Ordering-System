package soonteck.view

import soonteck.Main
import soonteck.model.{Cart, CartItem}
import soonteck.alert.Alerts
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.scene.control.{Button, ComboBox, Label, TableColumn, TableView, TextField}
import javafx.stage.Stage
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.layout.VBox

class CheckoutOverviewController:
  @FXML
  private var orderItemsTable: TableView[CartItem] = null
  @FXML
  private var itemNameColumn: TableColumn[CartItem, String] = null
  @FXML
  private var itemQuantityColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML
  private var itemUnitPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML
  private var itemTotalPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML
  private var totalItemsLabel: Label = null
  @FXML
  private var totalCaloriesLabel: Label = null
  @FXML
  private var subtotalLabel: Label = null
  @FXML
  private var taxLabel: Label = null
  @FXML
  private var totalAmountLabel: Label = null
  @FXML
  private var paymentMethodComboBox: ComboBox[String] = null
  @FXML
  private var cardNumberField: TextField = null
  @FXML
  private var cardHolderField: TextField = null
  @FXML
  private var expiryField: TextField = null
  @FXML
  private var cvvField: TextField = null
  @FXML
  private var processPaymentButton: Button = null
  @FXML
  private var cardDetailsSection: VBox = null

  private var dialogStage: Stage = null
  private var mainController: HomePageOverviewController = null
  private var alerts: Alerts = null

  def setDialogStage(dialogStage: Stage): Unit = {
    this.dialogStage = dialogStage
    this.alerts = new Alerts(dialogStage)
  }

  def setMainController(controller: HomePageOverviewController): Unit = {
    this.mainController = controller
  }

  def initialize(): Unit = {
    paymentMethodComboBox.getItems.addAll("E-Wallet", "Credit Card", "Debit Card")
    paymentMethodComboBox.setValue("E-Wallet")
    paymentMethodComboBox.valueProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        updateCardFieldsVisibility(newValue)
      }
    })
    updateCardFieldsVisibility("E-Wallet")
  }

  private def updateCardFieldsVisibility(paymentMethod: String): Unit = {
    val showCardFields = paymentMethod == "Credit Card" || paymentMethod == "Debit Card"
    if (cardDetailsSection != null) {
      cardDetailsSection.setVisible(showCardFields)
      cardDetailsSection.setManaged(showCardFields)
    }
  }

  def setCartItems(cart: Cart): Unit = {
    itemNameColumn.cellValueFactory = { _.value.item }
    itemQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    itemUnitPriceColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleDoubleProperty(c.value.foodItem.price.value).asObject()
    }
    itemTotalPriceColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleDoubleProperty(c.value.getTotalPrice).asObject()
    }
    orderItemsTable.setItems(cart.getItems)

    val subtotal = cart.getTotalPrice
    val tax = subtotal * 0.06
    val totalAmount = subtotal + tax

    totalItemsLabel.setText(s"${cart.getTotalItems}")
    totalCaloriesLabel.setText(s"${cart.getTotalCalories} kcal")
    subtotalLabel.setText(f"RM $subtotal%.2f")
    taxLabel.setText(f"RM $tax%.2f")
    totalAmountLabel.setText(f"RM $totalAmount%.2f")
  }

  @FXML
  def handleProcessPayment(): Unit = {
    val payment = new soonteck.model.Payment(
      paymentMethodComboBox.getValue,
      cardNumberField.getText,
      cardHolderField.getText,
      expiryField.getText,
      cvvField.getText
    )
    val totalAmountText = totalAmountLabel.getText.replace("RM ", "")
    val totalAmount = try {
      totalAmountText.toDouble
    } catch {
      case _: NumberFormatException => 0.0
    }
    payment.processPayment(totalAmount) match {
      case result if result.isSuccess =>
        val confirmationMessage = s"""Order Number: ${result.asInstanceOf[soonteck.model.PaymentResult.Success].orderNumber}
                                     |Payment Method: ${paymentMethodComboBox.getValue}
                                     |Total Amount: ${totalAmountLabel.getText}
                                     |
                                     |Thank you for your order!""".stripMargin
        alerts.showSuccessAlert("Payment Successful", confirmationMessage)
        if (mainController != null) {
          mainController.clearCartAfterCheckout()
        }
        dialogStage.close()
      case result =>
        alerts.showWarningAlert("Payment Failed", result.getMessage)
    }
  }

  @FXML
  def handleCancel(): Unit = {
    dialogStage.close()
  }
