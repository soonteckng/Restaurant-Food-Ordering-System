package soonteck.view

import soonteck.Main
import soonteck.model.{CartItem, FoodType, OrderHistory}
import soonteck.alert.Alerts
import scalafx.Includes.*
import javafx.scene.control.{ComboBox, Label, Spinner, TableColumn, TableRow, TableView, TextField, Tooltip}
import javafx.scene.input.MouseEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.stage.{Modality, Stage}
import javafx.scene.{Parent, Scene}
import javafx.collections.{FXCollections, ObservableList}
import javafx.collections.transformation.FilteredList
import javafx.util.Callback
import javafx.beans.value.{ChangeListener, ObservableValue}
import scala.util.{Try, Success, Failure}

@FXML
class HomePageOverviewController():

  // Menu Tab Controls
  @FXML
  private var foodTable: TableView[FoodType] = null
  @FXML
  private var nameColumn: TableColumn[FoodType, String] = null
  @FXML
  private var categoryColumn: TableColumn[FoodType, String] = null
  @FXML
  private var priceColumn: TableColumn[FoodType, java.lang.Double] = null
  @FXML
  private var caloriesColumn: TableColumn[FoodType, java.lang.Integer] = null
  @FXML
  private var descriptionColumn: TableColumn[FoodType, String] = null
  @FXML
  private var selectedItemLabel: Label = null
  @FXML
  private var quantitySpinner: Spinner[Int] = null
  @FXML
  private var addToCartButton: javafx.scene.control.Button = null
  @FXML
  private var categoryComboBox: ComboBox[String] = null
  @FXML
  private var searchField: TextField = null
  @FXML
  private var healthyFilterButton: javafx.scene.control.Button = null
  @FXML
  private var cartCountLabel: Label = null

  // Cart Tab Controls
  @FXML
  private var cartTable: TableView[CartItem] = null
  @FXML
  private var cartItemColumn: TableColumn[CartItem, String] = null
  @FXML
  private var cartQuantityColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML
  private var cartPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML
  private var clearCartButton: javafx.scene.control.Button = null
  @FXML
  private var cartUnitPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML
  private var cartCaloriesColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML
  private var cartSummaryItemsLabel: Label = null
  @FXML
  private var cartSummaryCaloriesLabel: Label = null
  @FXML
  private var cartSummaryPriceLabel: Label = null
  @FXML
  private var cartHealthStatusLabel: Label = null

  // View Controls (Fixed - removed duplicates)
  @FXML
  private var homeView: javafx.scene.layout.VBox = null
  @FXML
  private var cartView: javafx.scene.layout.VBox = null
  @FXML
  private var orderHistoryView: javafx.scene.layout.VBox = null
  @FXML
  private var homeNavButton: javafx.scene.control.Button = null
  @FXML
  private var cartNavButton: javafx.scene.control.Button = null
  @FXML
  private var orderHistoryNavButton: javafx.scene.control.Button = null

  // Order History Table Components
  @FXML
  private var orderHistoryTable: TableView[OrderHistory] = null
  @FXML
  private var orderDateColumn: TableColumn[OrderHistory, String] = null
  @FXML
  private var orderItemsColumn: TableColumn[OrderHistory, String] = null
  @FXML
  private var orderTotalColumn: TableColumn[OrderHistory, java.lang.Double] = null
  @FXML
  private var orderStatusColumn: TableColumn[OrderHistory, String] = null

  // Data
  private val cartItems: ObservableList[CartItem] = FXCollections.observableArrayList()
  private val orderHistoryItems: ObservableList[OrderHistory] = FXCollections.observableArrayList()
  private var filteredFoodList: FilteredList[FoodType] = null
  private var selectedFood: FoodType = null
  private var isHealthyFilterActive = false
  // Alert instance
  private val alerts = new Alerts()

  // Navigation Methods
  @FXML
  def showHomeView(): Unit = {
    homeView.setVisible(true)
    cartView.setVisible(false)
    orderHistoryView.setVisible(false)
  }

  @FXML
  def showCartView(): Unit = {
    homeView.setVisible(false)
    cartView.setVisible(true)
    orderHistoryView.setVisible(false)
  }

  @FXML
  def showOrderHistoryView(): Unit = {
    homeView.setVisible(false)
    cartView.setVisible(false)
    orderHistoryView.setVisible(true)
  }

  // Order History Action Methods
  @FXML
  def handleRefreshOrderHistory(): Unit = {
    loadOrderHistory()
    alerts.showInfoAlert("Refresh", "Order history refreshed!")
  }

  private def loadOrderHistory(): Unit = {
    try {
      val orders = OrderHistory.getAllOrders
      orderHistoryItems.clear()
      orders.foreach(order => orderHistoryItems.add(order))
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load order history.")
    }
  }

  private def saveOrderToHistory(): Unit = {
    try {
      // Create order from current cart
      val order = OrderHistory.createOrderFromCart(cartItems)

      // Save to database
      order.save() match {
        case Success(_) =>
          // Add to current display list
          orderHistoryItems.add(0, order) // Add at beginning for newest first
        case Failure(e) =>
          e.printStackTrace()
          alerts.showErrorAlert("Error", "Could not save order to history.")
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not create order record.")
    }
  }

  @FXML
  def handleViewOrderDetails(): Unit = {
    // TODO: Implement view order details logic
    val selectedOrder = orderHistoryTable.getSelectionModel.getSelectedItem
    if (selectedOrder != null) {
      alerts.showInfoAlert("Order Details", "Feature coming soon!")
    } else {
      alerts.showWarningAlert("No Selection", "Please select an order to view details.")
    }
  }

  @FXML
  def handleReorder(): Unit = {
    // TODO: Implement reorder logic
    val selectedOrder = orderHistoryTable.getSelectionModel.getSelectedItem
    if (selectedOrder != null) {
      alerts.showInfoAlert("Reorder", "Reorder feature coming soon!")
    } else {
      alerts.showWarningAlert("No Selection", "Please select an order to reorder.")
    }
  }

  def initialize(): Unit =
    initializeMenuTab()
    initializeCartTab()
    initializeOrderHistoryTab()
    setupEventHandlers()
    setupTableTooltip()
    // Initialize with Home view visible
    showHomeView()

  private def initializeMenuTab(): Unit =
    // Setup food table
    foodTable.items = Main.foodData
    filteredFoodList = new FilteredList[FoodType](Main.foodData)
    foodTable.setItems(filteredFoodList)

    nameColumn.cellValueFactory = { _.value.name }
    categoryColumn.cellValueFactory = { _.value.category }
    priceColumn.cellValueFactory = { c => c.value.price.delegate.asObject() }
    caloriesColumn.cellValueFactory = { c => c.value.calories.delegate.asObject() }
    descriptionColumn.cellValueFactory = { _.value.description }

    // Setup category combo box
    val categories = Main.foodData.map(_.category.value).distinct.sorted
    categoryComboBox.getItems.add("All")
    categories.foreach(category => categoryComboBox.getItems.add(category))
    categoryComboBox.setValue("All")

    // Initialize cart count
    updateCartCount()

  private def initializeCartTab(): Unit =
    cartTable.setItems(cartItems)
    cartItemColumn.cellValueFactory = { _.value.item }
    cartQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    cartUnitPriceColumn.cellValueFactory = { c =>
      val unitPrice = c.value.foodItem.price.value
      new javafx.beans.property.SimpleDoubleProperty(unitPrice).asObject()
    }
    cartCaloriesColumn.cellValueFactory = { c =>
      val totalCalories = c.value.getCalories
      new javafx.beans.property.SimpleIntegerProperty(totalCalories).asObject()
    }
    cartPriceColumn.cellValueFactory = { c =>
      val totalPrice = c.value.getTotalPrice
      new javafx.beans.property.SimpleDoubleProperty(totalPrice).asObject()
    }

  private def initializeOrderHistoryTab(): Unit =
    // Setup order history table
    orderHistoryTable.setItems(orderHistoryItems)
    orderDateColumn.cellValueFactory = { _.value.orderDate }
    orderItemsColumn.cellValueFactory = { _.value.items }
    orderTotalColumn.cellValueFactory = { c => c.value.total.delegate.asObject() }
    orderStatusColumn.cellValueFactory = { _.value.status }

    // Load existing order history
    loadOrderHistory()

  private def setupEventHandlers(): Unit =
    // Food table selection
    foodTable.selectionModel().selectedItem.onChange { (_, _, newValue) =>
      selectedFood = newValue
      if newValue != null then
        selectedItemLabel.text = newValue.name.value
      else
        selectedItemLabel.text = "None selected"
    }

    // Double-click to show food details
    foodTable.setOnMouseClicked { (event: MouseEvent) =>
      if event.getClickCount == 2 && selectedFood != null then
        showFoodDetails(selectedFood)
    }

    // Search functionality
    searchField.textProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit =
        applyFilters()
    })

    // Category filter
    categoryComboBox.valueProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit =
        applyFilters()
    })

  private def setupTableTooltip(): Unit =
    foodTable.setRowFactory(new Callback[TableView[FoodType], TableRow[FoodType]] {
      override def call(tableView: TableView[FoodType]): TableRow[FoodType] = {
        val row = new TableRow[FoodType]()

        row.itemProperty().addListener(new ChangeListener[FoodType] {
          override def changed(observable: ObservableValue[_ <: FoodType], oldValue: FoodType, newValue: FoodType): Unit = {
            if (newValue != null) {
              val tooltip = new Tooltip()
              tooltip.setText("Double-click to view details")
              // Removed CSS styling since you want to handle it separately
              row.setTooltip(tooltip)
            } else {
              row.setTooltip(null)
            }
          }
        })

        row
      }
    })

  @FXML
  def handleAddToCart(): Unit =
    if selectedFood != null then
      val quantity = quantitySpinner.getValue
      var existingItem: CartItem = null

      // Find existing item manually
      for i <- 0 until cartItems.size() do
        if cartItems.get(i).item.value == selectedFood.name.value then
          existingItem = cartItems.get(i)

      if existingItem != null then
        existingItem.quantity.value = existingItem.quantity.value + quantity
      else
        cartItems.add(new CartItem(selectedFood, quantity))

      updateCartCount()
      alerts.showSuccessAlert("Success", s"Added ${selectedFood.name.value} to cart!")
    else
      alerts.showWarningAlert("No Selection", "Please select a food item first.")

  @FXML
  def addToCartFromDialog(food: FoodType, quantity: Int): Unit =
    var existingItem: CartItem = null

    // Find existing item manually
    for i <- 0 until cartItems.size() do
      if cartItems.get(i).item.value == food.name.value then
        existingItem = cartItems.get(i)

    if existingItem != null then
      existingItem.quantity.value = existingItem.quantity.value + quantity
    else
      cartItems.add(new CartItem(food, quantity))

    updateCartCount()

  @FXML
  def handleShowAll(): Unit =
    categoryComboBox.setValue("All")
    searchField.clear()
    isHealthyFilterActive = false
    // Removed CSS styling since you want to handle it separately
    applyFilters()

  @FXML
  def handleHealthyFilter(): Unit =
    isHealthyFilterActive = !isHealthyFilterActive
    if isHealthyFilterActive then
      healthyFilterButton.setText("Show All")
    else
      healthyFilterButton.setText("Healthy Options")
    applyFilters()

  @FXML
  def handleClearCart(): Unit =
    if cartItems.isEmpty then
      alerts.showInfoAlert("Nothing to Clear", "Your cart is already empty.")
    else
      val confirmed = alerts.showConfirmationAlert(
        "Clear Cart",
        "Are you sure you want to clear all items from your cart?",
      )
      if confirmed then
        cartItems.clear()
        updateCartCount()
        alerts.showSuccessAlert("Cart Cleared", "All items have been removed from your cart.")

  @FXML
  def handleRemoveFromCart(): Unit =
    val selectedItem = cartTable.getSelectionModel.getSelectedItem
    if selectedItem != null then
      cartItems.remove(selectedItem)
      updateCartCount()
      alerts.showSuccessAlert("Removed", "Item removed from cart.")
    else
      alerts.showWarningAlert("No Selection", "Please select an item to remove.")

  @FXML
  def handleCalculateCalories(): Unit =
    if cartItems.isEmpty then
      alerts.showWarningAlert("Empty Cart", "Your cart is empty. Add some items first!")
    else
      showCaloriesSummary()

  private def showCaloriesSummary(): Unit =
    try
      val loader = new FXMLLoader()
      loader.setLocation(getClass.getResource("/soonteck/view/TotalCaloriesOverview.fxml"))
      val page: Parent = loader.load()

      val dialogStage = new Stage()
      dialogStage.setTitle("Cart Summary")
      dialogStage.initModality(Modality.WINDOW_MODAL)
      dialogStage.setScene(new Scene(page))

      val caloriesController = loader.getController[TotalCaloriesOverviewController]
      caloriesController.setDialogStage(dialogStage)
      caloriesController.setCartItems(cartItems)

      dialogStage.showAndWait()
    catch
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load cart summary dialog.")

  private def applyFilters(): Unit =
    filteredFoodList.setPredicate { food =>
      val matchesSearch = searchField.getText == null ||
        searchField.getText.isEmpty ||
        food.name.value.toLowerCase.contains(searchField.getText.toLowerCase) ||
        food.description.value.toLowerCase.contains(searchField.getText.toLowerCase)

      val matchesCategory = categoryComboBox.getValue == "All" ||
        food.category.value == categoryComboBox.getValue

      val matchesHealthy = !isHealthyFilterActive || food.calories.value < 400

      matchesSearch && matchesCategory && matchesHealthy
    }

  private def updateCartCount(): Unit =
    var itemCount = 0
    var totalCalories = 0
    var totalPrice = 0.0

    for i <- 0 until cartItems.size() do
      val item = cartItems.get(i)
      itemCount += cartItems.get(i).quantity.value
      totalCalories += item.getCalories
      totalPrice += item.getTotalPrice
    cartCountLabel.setText(s"$itemCount item(s) in Cart")
    cartSummaryItemsLabel.setText(itemCount.toString)
    cartSummaryCaloriesLabel.setText(s"$totalCalories kcal")
    cartSummaryPriceLabel.setText(f"RM $totalPrice%.2f")

    val averageCalories = if itemCount > 0 then totalCalories.toDouble / itemCount else 0.0
    updateHealthStatus(averageCalories)

  private def updateHealthStatus(averageCalories: Double): Unit =
    val (status, style) = averageCalories match
      case avg if avg == 0 => ("No Items", "")
      case avg if avg < 250 => ("🟢 Very Healthy", "")
      case avg if avg < 400 => ("🟡 Moderately Healthy", "")
      case avg if avg < 600 => ("🟠 High Calorie", "")
      case _ => ("🔴 Very High Calorie", "")

    cartHealthStatusLabel.setText(status)
  // Removed setStyle since you want to handle CSS separately

  private def showFoodDetails(food: FoodType): Unit =
    try
      val loader = new FXMLLoader()
      loader.setLocation(getClass.getResource("/soonteck/view/FoodDetailsOverview.fxml"))
      val page: Parent = loader.load()

      val dialogStage = new Stage()
      dialogStage.setTitle("Food Details")
      dialogStage.initModality(Modality.WINDOW_MODAL)
      dialogStage.setScene(new Scene(page))

      val foodDetailsController = loader.getController[FoodDetailsOverviewController]
      foodDetailsController.setDialogStage(dialogStage)
      foodDetailsController.setMainController(this)
      foodDetailsController.setFoodItem(food)

      dialogStage.showAndWait()
    catch
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load food details dialog.")

  @FXML
  def handleLogout(): Unit =
    val confirmed = alerts.showConfirmationAlert(
      "Logout Confirmation",
      "Are you sure you want to logout?"
    )

    if confirmed then
      alerts.showSuccessAlert("Logout Successful", "You have successfully logged out!")
      Main.showLoginOverview()

  @FXML
  def handleCheckout(): Unit =
    if cartItems.isEmpty then
      alerts.showWarningAlert("Empty Cart", "Your cart is empty. Add some items first!")
    else
      showCheckoutDialog()

  private def showCheckoutDialog(): Unit =
    try
      val loader = new FXMLLoader()
      loader.setLocation(getClass.getResource("/soonteck/view/CheckoutOverview.fxml"))
      val page: Parent = loader.load()

      val dialogStage = new Stage()
      dialogStage.setTitle("Checkout")
      dialogStage.initModality(Modality.WINDOW_MODAL)
      dialogStage.setScene(new Scene(page))

      val checkoutController = loader.getController[CheckoutOverviewController]
      checkoutController.setDialogStage(dialogStage)
      checkoutController.setMainController(this)
      checkoutController.setCartItems(cartItems)

      dialogStage.showAndWait()
    catch
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load checkout dialog.")

  def clearCartAfterCheckout(): Unit =
    // Save current cart to order history before clearing
    saveOrderToHistory()

    cartItems.clear()
    updateCartCount()
    alerts.showSuccessAlert("Order Completed", "Your order has been saved to history and cart cleared.")