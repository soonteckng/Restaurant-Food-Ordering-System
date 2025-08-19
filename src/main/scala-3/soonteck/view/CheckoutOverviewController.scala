package soonteck.view

import soonteck.model.CartItem
import soonteck.alert.Alerts
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.scene.control.{Label, Button, TableView, TableColumn, ComboBox, TextField}
import javafx.stage.Stage
import javafx.collections.ObservableList

class CheckoutOverviewController:
  // Order Summary Table
  @FXML private var orderItemsTable: TableView[CartItem] = null
  @FXML private var itemNameColumn: TableColumn[CartItem, String] = null
  @FXML private var itemQuantityColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML private var itemUnitPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML private var itemTotalPriceColumn: TableColumn[CartItem, java.lang.Double] = null

  // Summary Labels
  @FXML private var totalItemsLabel: Label = null
  @FXML private var totalCaloriesLabel: Label = null
  @FXML private var subtotalLabel: Label = null
  @FXML private var taxLabel: Label = null
  @FXML private var totalAmountLabel: Label = null

  // Payment Fields
  @FXML private var paymentMethodComboBox: ComboBox[String] = null
  @FXML private var cardNumberField: TextField = null
  @FXML private var cardHolderField: TextField = null
  @FXML private var expiryField: TextField = null
  @FXML private var cvvField: TextField = null
  @FXML private var processPaymentButton: Button = null

  private var dialogStage: Stage = null
  private var mainController: HomePageOverviewController = null
  private val alerts = new Alerts()

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setMainController(controller: HomePageOverviewController): Unit =
    this.mainController = controller

  def setCartItems(cartItems: ObservableList[CartItem]): Unit =
    // Set up the table
    orderItemsTable.setItems(cartItems)
    itemNameColumn.cellValueFactory = { _.value.item }
    itemQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    itemUnitPriceColumn.cellValueFactory = { c =>
      val unitPrice = c.value.foodItem.price.value
      new javafx.beans.property.SimpleDoubleProperty(unitPrice).asObject()
    }
    itemTotalPriceColumn.cellValueFactory = { c =>
      val totalPrice = c.value.getTotalPrice
      new javafx.beans.property.SimpleDoubleProperty(totalPrice).asObject()
    }

    // Setup payment methods
    if paymentMethodComboBox.getItems.isEmpty then
      paymentMethodComboBox.getItems.addAll("Credit Card", "Debit Card", "Cash", "E-Wallet")
      paymentMethodComboBox.setValue("Cash")

    // Calculate totals
    var totalCalories = 0
    var subtotal = 0.0
    var totalItems = 0

    for i <- 0 until cartItems.size() do
      val item = cartItems.get(i)
      totalCalories += item.getCalories
      subtotal += item.getTotalPrice
      totalItems += item.quantity.value

    val tax = subtotal * 0.06 // 6% tax
    val totalAmount = subtotal + tax

    // Update labels
    totalItemsLabel.setText(s"$totalItems")
    totalCaloriesLabel.setText(s"$totalCalories kcal")
    subtotalLabel.setText(f"RM $subtotal%.2f")
    taxLabel.setText(f"RM $tax%.2f")
    totalAmountLabel.setText(f"RM $totalAmount%.2f")

  @FXML
  def handleProcessPayment(): Unit =
    val paymentMethod = paymentMethodComboBox.getValue

    if paymentMethod == null || paymentMethod.isEmpty then
      alerts.showWarningAlert("Payment Method Required", "Please select a payment method.")
      return

    // For non-cash payments, validate card details
    if paymentMethod != "Cash" && paymentMethod != "E-Wallet" then
      if cardNumberField.getText == null || cardNumberField.getText.trim.isEmpty then
        alerts.showWarningAlert("Card Details Required", "Please enter your card number.")
        return
      if cardHolderField.getText == null || cardHolderField.getText.trim.isEmpty then
        alerts.showWarningAlert("Card Details Required", "Please enter the card holder name.")
        return

    val orderNumber = s"QD${System.currentTimeMillis()}"
    val totalAmount = totalAmountLabel.getText

    val confirmationMessage = s"""Order Number: $orderNumber
                                 |Payment Method: $paymentMethod
                                 |Total Amount: $totalAmount
                                 |
                                 |Thank you for your order!""".stripMargin

    alerts.showSuccessAlert("Payment Successful", confirmationMessage)

    // Clear the cart through main controller
    if mainController != null then
      mainController.clearCartAfterCheckout()

    dialogStage.close()

  @FXML
  def handleCancel(): Unit =
    dialogStage.close()