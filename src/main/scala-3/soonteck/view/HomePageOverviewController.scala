package soonteck.view

import soonteck.Main
import soonteck.model.{Cart, CartItem, FoodType, OrderHistory}
import soonteck.service.{FoodService, OrderService}
import soonteck.alert.Alerts
import scalafx.Includes.*
import javafx.scene.control.{Button, ComboBox, Label, Spinner, TableColumn, TableRow, TableView, TextField, Tooltip}
import javafx.scene.input.MouseEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.stage.{Modality, Stage}
import javafx.scene.{Parent, Scene}
import javafx.collections.transformation.FilteredList
import javafx.util.Callback
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.scene.layout.VBox
import scalafx.scene.image.Image
import java.lang.{Integer, Double}
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}

import scala.util.{Failure, Success, Try}

@FXML
class HomePageOverviewController:
  @FXML 
  private var foodTable: TableView[FoodType] = null
  @FXML 
  private var nameColumn: TableColumn[FoodType, String] = null
  @FXML 
  private var categoryColumn: TableColumn[FoodType, String] = null
  @FXML 
  private var priceColumn: TableColumn[FoodType, Double] = null
  @FXML 
  private var caloriesColumn: TableColumn[FoodType,Integer] = null
  @FXML 
  private var descriptionColumn: TableColumn[FoodType, String] = null
  @FXML 
  private var selectedItemLabel: Label = null
  @FXML 
  private var quantitySpinner: Spinner[Int] = null
  @FXML 
  private var addToCartButton: Button = null
  @FXML
  private var categoryComboBox: ComboBox[String] = null
  @FXML 
  private var searchField: TextField = null
  @FXML 
  private var healthyFilterButton: Button = null
  @FXML
  private var cartCountLabel: Label = null
  @FXML
  private var cartTable: TableView[CartItem] = null
  @FXML
  private var cartItemColumn: TableColumn[CartItem, String] = null
  @FXML 
  private var cartQuantityColumn: TableColumn[CartItem, Integer] = null
  @FXML
  private var cartPriceColumn: TableColumn[CartItem, Double] = null
  @FXML
  private var clearCartButton: Button = null
  @FXML
  private var cartUnitPriceColumn: TableColumn[CartItem,Double] = null
  @FXML 
  private var cartCaloriesColumn: TableColumn[CartItem,Integer] = null
  @FXML 
  private var cartSummaryItemsLabel: Label = null
  @FXML 
  private var cartSummaryCaloriesLabel: Label = null
  @FXML 
  private var cartSummaryPriceLabel: Label = null
  @FXML
  private var cartHealthStatusLabel: Label = null
  @FXML 
  private var homeView: VBox = null
  @FXML
  private var cartView: VBox = null
  @FXML 
  private var orderHistoryView: VBox = null
  @FXML 
  private var homeNavButton:Button = null
  @FXML 
  private var cartNavButton: Button = null
  @FXML 
  private var orderHistoryNavButton:Button = null
  @FXML
  private var orderHistoryTable: TableView[OrderHistory] = null
  @FXML 
  private var orderDateColumn: TableColumn[OrderHistory, String] = null
  @FXML 
  private var orderItemsColumn: TableColumn[OrderHistory, String] = null
  @FXML 
  private var orderTotalColumn: TableColumn[OrderHistory, Double] = null
  @FXML 
  private var orderStatusColumn: TableColumn[OrderHistory, String] = null

  private val foodService = new FoodService()
  private val orderService = new OrderService()
  private val cart = new Cart()

  private val orderHistoryItems: ObservableList[OrderHistory] = observableArrayList()
  private var selectedFood: FoodType = null
  private var isHealthyFilterActive = false
  private var currentUsername: String = ""
  private val alerts = new Alerts(Main.stage)

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

  def initialize(): Unit = {
    initializeServices()
    initializeMenuTab()
    initializeCartTab()
    initializeOrderHistoryTab()
    setupEventHandlers()
    setupTableTooltip()
    showHomeView()
  }

  private def initializeServices(): Unit = {
    foodService.loadAllFoods()
  }

  def setCurrentUser(username: String): Unit = {
    currentUsername = username
    loadOrderHistory()
  }

  private def initializeMenuTab(): Unit = {
    foodTable.setItems(foodService.getAllFoods)
    nameColumn.cellValueFactory = { _.value.name }
    categoryColumn.cellValueFactory = { _.value.category }
    priceColumn.cellValueFactory = { c => c.value.price.delegate.asObject() }
    caloriesColumn.cellValueFactory = { c => c.value.calories.delegate.asObject() }
    descriptionColumn.cellValueFactory = { _.value.description }
    setupCategoryComboBox()
    updateCartDisplay()
  }

  private def setupCategoryComboBox(): Unit = {
    val categories = foodService.getDistinctCategories
    categoryComboBox.getItems.add("All")
    categories.foreach(category => categoryComboBox.getItems.add(category))
    categoryComboBox.setValue("All")
  }

  private def initializeCartTab(): Unit = {
    cartTable.setItems(cart.getItems)
    cartItemColumn.cellValueFactory = { _.value.item }
    cartQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    cartUnitPriceColumn.cellValueFactory = { c =>
      new SimpleDoubleProperty(c.value.foodItem.price.value).asObject()
    }
    cartCaloriesColumn.cellValueFactory = { c =>
      new SimpleIntegerProperty(c.value.getCalories).asObject()
    }
    cartPriceColumn.cellValueFactory = { c =>
      new SimpleDoubleProperty(c.value.getTotalPrice).asObject()
    }
  }

  private def initializeOrderHistoryTab(): Unit = {
    orderHistoryTable.setItems(orderHistoryItems)
    orderDateColumn.cellValueFactory = { _.value.orderDate }
    orderItemsColumn.cellValueFactory = { _.value.items }
    orderTotalColumn.cellValueFactory = { c => c.value.total.delegate.asObject() }
    orderStatusColumn.cellValueFactory = { _.value.status }
  }

  private def setupEventHandlers(): Unit = {
    foodTable.selectionModel().selectedItem.onChange { (_, _, newValue) =>
      selectedFood = newValue
      selectedItemLabel.text = if (newValue != null) newValue.name.value else "None selected"
    }
    foodTable.setOnMouseClicked { (event: MouseEvent) =>
      if (event.getClickCount == 2 && selectedFood != null) {
        showFoodDetails(selectedFood)
      }
    }
    searchField.textProperty().addListener { (_, _, newValue) =>
      applyFilters()
    }
    categoryComboBox.valueProperty().addListener { (_, _, newValue) =>
      applyFilters()
    }
  }

  private def setupTableTooltip(): Unit = {
    foodTable.setRowFactory { _ =>
      val row = new TableRow[FoodType]()
      row.itemProperty().addListener { (_, _, newValue) =>
        row.setTooltip(if (newValue != null) new Tooltip("Double-click to view details") else null)
      }
      row
    }
  }

  @FXML
  def handleAddToCart(): Unit = {
    if (selectedFood == null) {
      alerts.showWarningAlert("No Selection", "Please select a food item first.")
      return
    }

    cart.addItem(selectedFood, quantitySpinner.getValue) match {
      case result if result.isSuccess =>
        updateCartDisplay()
        alerts.showSuccessAlert("Success", result.getMessage)
      case result =>
        alerts.showWarningAlert("Error", result.getMessage)
    }
  }

  def addToCartFromDialog(food: FoodType, quantity: Int): Unit = {
    cart.addItem(food, quantity) match {
      case result if result.isSuccess =>
        updateCartDisplay()
        alerts.showSuccessAlert("Success", result.getMessage)
      case result =>
        alerts.showWarningAlert("Error", result.getMessage)
    }
  }

  @FXML
  def handleShowAll(): Unit = {
    categoryComboBox.setValue("All")
    searchField.clear()
    isHealthyFilterActive = false
    healthyFilterButton.setText("Healthy Options")
    applyFilters()
  }

  @FXML
  def handleHealthyFilter(): Unit = {
    isHealthyFilterActive = !isHealthyFilterActive
    healthyFilterButton.setText(if (isHealthyFilterActive) "Show All" else "Healthy Options")
    applyFilters()
  }

  private def applyFilters(): Unit = {
    val filteredList = foodService.filterFoods(
      searchField.getText,
      categoryComboBox.getValue,
      isHealthyFilterActive
    )
    foodTable.setItems(filteredList)
  }

  @FXML
  def handleClearCart(): Unit = {
    if (cart.isEmpty) {
      alerts.showInfoAlert("Nothing to Clear", "Your cart is already empty.")
      return
    }

    val confirmed = alerts.showConfirmationAlert("Clear Cart", "Are you sure you want to clear all items from your cart?")
    if (confirmed) {
      cart.clear()
      updateCartDisplay()
      alerts.showSuccessAlert("Cart Cleared", "All items have been removed from your cart.")
    }
  }

  @FXML
  def handleRemoveFromCart(): Unit = {
    val selectedItem = cartTable.getSelectionModel.getSelectedItem
    if (selectedItem == null) {
      alerts.showWarningAlert("No Selection", "Please select an item to remove.")
      return
    }

    cart.removeItem(selectedItem) match {
      case result if result.isSuccess =>
        updateCartDisplay()
        alerts.showSuccessAlert("Removed", result.getMessage)
      case result =>
        alerts.showErrorAlert("Error", result.getMessage)
    }
  }

  @FXML
  def handleCalculateCalories(): Unit = {
    if (cart.isEmpty) {
      alerts.showWarningAlert("Empty Cart", "Your cart is empty. Add some items first!")
    } else {
      showCaloriesSummary()
    }
  }

  @FXML
  def handleCheckout(): Unit = {
    if (cart.isEmpty) {
      alerts.showWarningAlert("Empty Cart", "Your cart is empty. Add some items first!")
    } else {
      showCheckoutDialog()
    }
  }

  private def updateCartDisplay(): Unit = {
    val summary = cart.getCartSummary
    cartCountLabel.setText(s"${summary.totalItems} item(s) in Cart")
    cartSummaryItemsLabel.setText(summary.totalItems.toString)
    cartSummaryCaloriesLabel.setText(s"${summary.totalCalories} kcal")
    cartSummaryPriceLabel.setText(f"RM ${summary.totalPrice}%.2f")
    cartHealthStatusLabel.setText(summary.healthStatus)
  }

  private def loadOrderHistory(): Unit = {
    orderService.getOrderHistoryForUser(currentUsername) match {
      case Success(orders) =>
        orderHistoryItems.clear()
        orders.foreach(order => orderHistoryItems.add(order))
      case Failure(e) =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", s"Could not load order history: ${e.getMessage}")
    }
  }

  private def showCaloriesSummary(): Unit = {
    try {
      val loader = new FXMLLoader()
      loader.setLocation(getClass.getResource("/soonteck/view/TotalCaloriesOverview.fxml"))
      val page: Parent = loader.load()
      val dialogStage = new Stage()
      dialogStage.setTitle("Calories Summary")
      dialogStage.initModality(Modality.WINDOW_MODAL)
      dialogStage.getIcons.add(new Image(getClass.getResource("/images/calories.png").toExternalForm))
      dialogStage.setScene(new Scene(page))
      val caloriesController = loader.getController[TotalCaloriesOverviewController]
      caloriesController.setDialogStage(dialogStage)
      caloriesController.setCartItems(cart)
      dialogStage.showAndWait()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load cart summary dialog.")
    }
  }

  private def showCheckoutDialog(): Unit = {
    try {
      val loader = new FXMLLoader()
      loader.setLocation(getClass.getResource("/soonteck/view/CheckoutOverview.fxml"))
      val page: Parent = loader.load()
      val dialogStage = new Stage()
      dialogStage.setTitle("Checkout")
      dialogStage.initModality(Modality.WINDOW_MODAL)
      dialogStage.getIcons.add(new Image(getClass.getResource("/images/checkout.webp").toExternalForm))
      dialogStage.setScene(new Scene(page))
      val checkoutController = loader.getController[CheckoutOverviewController]
      checkoutController.setDialogStage(dialogStage)
      checkoutController.setMainController(this)
      checkoutController.setCartItems(cart)
      dialogStage.showAndWait()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load checkout dialog.")
    }
  }

  private def showFoodDetails(food: FoodType): Unit = {
    try {
      val loader = new FXMLLoader()
      loader.setLocation(getClass.getResource("/soonteck/view/FoodDetailsOverview.fxml"))
      val page: Parent = loader.load()
      val dialogStage = new Stage()
      dialogStage.setTitle("Food Details")
      dialogStage.initModality(Modality.WINDOW_MODAL)
      dialogStage.getIcons.add(new Image(getClass.getResource("/images/fooddetails.png").toExternalForm))
      dialogStage.setScene(new Scene(page))
      val foodDetailsController = loader.getController[FoodDetailsOverviewController]
      foodDetailsController.setDialogStage(dialogStage)
      foodDetailsController.setMainController(this)
      foodDetailsController.setFoodItem(food)
      dialogStage.showAndWait()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load food details dialog.")
    }
  }

  def clearCartAfterCheckout(): Unit = {
    orderService.createOrderFromCart(cart, currentUsername) match {
      case Success(order) =>
        orderHistoryItems.add(0, order)
        cart.clear()
        updateCartDisplay()
        alerts.showInfoAlert("Order Completed", "You may view your order in order history.")
      case Failure(e) =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", s"Could not save order to history: ${e.getMessage}")
    }
  }

  @FXML
  def handleLogout(): Unit = {
    val confirmed = alerts.showConfirmationAlert("Logout Confirmation", "Are you sure you want to logout?")
    if (confirmed) {
      alerts.showSuccessAlert("Logout Successful", "You have successfully logged out!")
      Main.showLoginOverview()
    }
  }