package soonteck.view

import soonteck.model.CartItem
import soonteck.alert.Alerts
import scalafx.Includes.*
import javafx.fxml.FXML
import javafx.scene.control.{Label, Button, TableView, TableColumn, ComboBox, TextField}
import javafx.stage.Stage
import javafx.collections.ObservableList
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.layout.VBox
import java.time.YearMonth

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
  private val alerts = new Alerts()

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setMainController(controller: HomePageOverviewController): Unit =
    this.mainController = controller

  def initialize(): Unit =
    // Setup payment methods
    paymentMethodComboBox.getItems.addAll("E-Wallet", "Credit Card", "Debit Card")
    paymentMethodComboBox.setValue("E-Wallet")
    paymentMethodComboBox.valueProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit =
        updateCardFieldsVisibility(newValue)
    })

    updateCardFieldsVisibility("E-Wallet")

  private def updateCardFieldsVisibility(paymentMethod: String): Unit =
    val showCardFields = paymentMethod == "Credit Card" || paymentMethod == "Debit Card"
    if cardDetailsSection != null then
      cardDetailsSection.setVisible(showCardFields)
      cardDetailsSection.setManaged(showCardFields) // Prevents empty space when hidden

  def setCartItems(cartItems: ObservableList[CartItem]): Unit =
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
    orderItemsTable.setItems(cartItems)

    var totalCalories = 0
    var subtotal = 0.0
    var totalItems = 0

    for i <- 0 until cartItems.size() do
      val item = cartItems.get(i)
      totalCalories += item.getCalories
      subtotal += item.getTotalPrice
      totalItems += item.quantity.value

    val tax = subtotal * 0.06 // tax = 6%
    val totalAmount = subtotal + tax

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

    if paymentMethod != "E-Wallet" then
      val cardNumber = Option(cardNumberField.getText).getOrElse("").trim
      val cardHolder = Option(cardHolderField.getText).getOrElse("").trim
      val expiry = Option(expiryField.getText).getOrElse("").trim
      val cvv = Option(cvvField.getText).getOrElse("").trim

      if cardNumber.isEmpty then
        alerts.showWarningAlert("Missing Information", "Card number cannot be empty.")
        return
      if cardHolder.isEmpty then
        alerts.showWarningAlert("Missing Information", "Card holder name cannot be empty.")
        return
      if expiry.isEmpty then
        alerts.showWarningAlert("Missing Information", "Expiry date cannot be empty.")
        return
      if cvv.isEmpty then
        alerts.showWarningAlert("Missing Information", "CVV cannot be empty.")
        return
      if !cardNumber.matches("\\d{16}") then
        alerts.showWarningAlert("Invalid Card Number", "Card number must be exactly 16 digits (numbers only).")
        return
      if !cardHolder.matches("^[A-Za-z ]+$") then
        alerts.showWarningAlert("Invalid Card Holder", "Card holder name must only contain letters and spaces.")
        return
      if !expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$") then
        alerts.showWarningAlert("Invalid Expiry Date", "Expiry date must be in MM/YY format (e.g., 05/27).")
        return
      else
        val parts = expiry.split("/")
        val expMonth = parts(0).toInt
        val expYear = 2000 + parts(1).toInt
        val expiryDate = YearMonth.of(expYear, expMonth)
        val currentDate = YearMonth.now()
        val maxFutureDate = currentDate.plusYears(10)

        if expiryDate.isBefore(currentDate) then
          alerts.showWarningAlert("Card Expired", "The card expiry date has already passed.")
          return
        if expiryDate.isAfter(maxFutureDate) then
          alerts.showWarningAlert("Invalid Expiry Date", "Expiry date cannot be more than 10 years in the future.")
          return

      // 🔒 Validate CVV: 3 digits
      if !cvv.matches("\\d{3}") then
        alerts.showWarningAlert("Invalid CVV", "CVV must be exactly 3 digits.")
        return

    // --- ✅ If validation passes, process order ---
    val orderNumber = s"QD${System.currentTimeMillis()}"
    val totalAmount = totalAmountLabel.getText

    val confirmationMessage = s"""Order Number: $orderNumber
                                 |Payment Method: $paymentMethod
                                 |Total Amount: $totalAmount
                                 |
                                 |Thank you for your order!""".stripMargin

    alerts.showSuccessAlert("Payment Successful", confirmationMessage)

    if mainController != null then
      mainController.clearCartAfterCheckout()

    dialogStage.close()

  @FXML
  def handleCancel(): Unit =
    dialogStage.close()
