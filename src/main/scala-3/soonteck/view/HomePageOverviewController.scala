package soonteck.view

import soonteck.Main
import soonteck.model.{Cart, CartItem, FoodType, OrderHistory}
import soonteck.alert.Alerts
import scalafx.Includes.*
import javafx.scene.control.{ComboBox, Label, Spinner, TableColumn, TableRow, TableView, TextField, Tooltip}
import javafx.scene.input.MouseEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.stage.{Modality, Stage}
import javafx.scene.{Parent, Scene}
import javafx.collections.transformation.FilteredList
import javafx.util.Callback
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.scene.image.Image

import scala.util.{Failure, Success, Try}

@FXML
class HomePageOverviewController:

  @FXML private var foodTable: TableView[FoodType] = null
  @FXML private var nameColumn: TableColumn[FoodType, String] = null
  @FXML private var categoryColumn: TableColumn[FoodType, String] = null
  @FXML private var priceColumn: TableColumn[FoodType, java.lang.Double] = null
  @FXML private var caloriesColumn: TableColumn[FoodType, java.lang.Integer] = null
  @FXML private var descriptionColumn: TableColumn[FoodType, String] = null
  @FXML private var selectedItemLabel: Label = null
  @FXML private var quantitySpinner: Spinner[Int] = null
  @FXML private var addToCartButton: javafx.scene.control.Button = null
  @FXML private var categoryComboBox: ComboBox[String] = null
  @FXML private var searchField: TextField = null
  @FXML private var healthyFilterButton: javafx.scene.control.Button = null
  @FXML private var cartCountLabel: Label = null
  @FXML private var cartTable: TableView[CartItem] = null
  @FXML private var cartItemColumn: TableColumn[CartItem, String] = null
  @FXML private var cartQuantityColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML private var cartPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML private var clearCartButton: javafx.scene.control.Button = null
  @FXML private var cartUnitPriceColumn: TableColumn[CartItem, java.lang.Double] = null
  @FXML private var cartCaloriesColumn: TableColumn[CartItem, java.lang.Integer] = null
  @FXML private var cartSummaryItemsLabel: Label = null
  @FXML private var cartSummaryCaloriesLabel: Label = null
  @FXML private var cartSummaryPriceLabel: Label = null
  @FXML private var cartHealthStatusLabel: Label = null
  @FXML private var homeView: javafx.scene.layout.VBox = null
  @FXML private var cartView: javafx.scene.layout.VBox = null
  @FXML private var orderHistoryView: javafx.scene.layout.VBox = null
  @FXML private var homeNavButton: javafx.scene.control.Button = null
  @FXML private var cartNavButton: javafx.scene.control.Button = null
  @FXML private var orderHistoryNavButton: javafx.scene.control.Button = null
  @FXML private var orderHistoryTable: TableView[OrderHistory] = null
  @FXML private var orderDateColumn: TableColumn[OrderHistory, String] = null
  @FXML private var orderItemsColumn: TableColumn[OrderHistory, String] = null
  @FXML private var orderTotalColumn: TableColumn[OrderHistory, java.lang.Double] = null
  @FXML private var orderStatusColumn: TableColumn[OrderHistory, String] = null

  private val cart = new Cart()
  private val orderHistoryItems: javafx.collections.ObservableList[OrderHistory] = javafx.collections.FXCollections.observableArrayList()
  private var filteredFoodList: FilteredList[FoodType] = null
  private var selectedFood: FoodType = null
  private var isHealthyFilterActive = false
  private val alerts = new Alerts(Main.stage)
  private var currentUsername: String = ""

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

  def setCurrentUser(username: String): Unit = {
    currentUsername = username
    loadOrderHistory()
  }

  private def loadOrderHistory(): Unit = {
    try {
      val orders = if (currentUsername.nonEmpty) {
        OrderHistory.getOrdersForUser(currentUsername)
      } else {
        List.empty[OrderHistory]
      }
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
      if (currentUsername.isEmpty) {
        alerts.showErrorAlert("Error", "No user logged in.")
        return
      }
      val order = cart.createOrder(currentUsername)
      order.save() match {
        case Success(_) =>
          orderHistoryItems.add(0, order)
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

  def initialize(): Unit = {
    initializeMenuTab()
    initializeCartTab()
    initializeOrderHistoryTab()
    setupEventHandlers()
    setupTableTooltip()
    showHomeView()
  }

  private def initializeMenuTab(): Unit = {
    foodTable.items = Main.foodData
    filteredFoodList = new FilteredList[FoodType](Main.foodData)
    foodTable.setItems(filteredFoodList)

    nameColumn.cellValueFactory = { _.value.name }
    categoryColumn.cellValueFactory = { _.value.category }
    priceColumn.cellValueFactory = { c => c.value.price.delegate.asObject() }
    caloriesColumn.cellValueFactory = { c => c.value.calories.delegate.asObject() }
    descriptionColumn.cellValueFactory = { _.value.description }

    val categories = Main.foodData.map(_.category.value).distinct.sorted
    categoryComboBox.getItems.add("All")
    categories.foreach(category => categoryComboBox.getItems.add(category))
    categoryComboBox.setValue("All")

    updateCartCount()
  }

  private def initializeCartTab(): Unit = {
    cartTable.setItems(cart.getItems)
    cartItemColumn.cellValueFactory = { _.value.item }
    cartQuantityColumn.cellValueFactory = { c => c.value.quantity.delegate.asObject() }
    cartUnitPriceColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleDoubleProperty(c.value.foodItem.price.value).asObject()
    }
    cartCaloriesColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleIntegerProperty(c.value.getCalories).asObject()
    }
    cartPriceColumn.cellValueFactory = { c =>
      new javafx.beans.property.SimpleDoubleProperty(c.value.getTotalPrice).asObject()
    }
  }

  private def initializeOrderHistoryTab(): Unit = {
    orderHistoryTable.setItems(orderHistoryItems)
    orderDateColumn.cellValueFactory = { _.value.orderDate }
    orderItemsColumn.cellValueFactory = { _.value.items }
    orderTotalColumn.cellValueFactory = { c => c.value.total.delegate.asObject() }
    orderStatusColumn.cellValueFactory = { _.value.status }
    loadOrderHistory()
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

    searchField.textProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        applyFilters()
      }
    })

    categoryComboBox.valueProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        applyFilters()
      }
    })
  }

  private def setupTableTooltip(): Unit = {
    foodTable.setRowFactory(new Callback[TableView[FoodType], TableRow[FoodType]] {
      override def call(tableView: TableView[FoodType]): TableRow[FoodType] = {
        val row = new TableRow[FoodType]()
        row.itemProperty().addListener(new ChangeListener[FoodType] {
          override def changed(observable: ObservableValue[_ <: FoodType], oldValue: FoodType, newValue: FoodType): Unit = {
            row.setTooltip(if (newValue != null) new Tooltip("Double-click to view details") else null)
          }
        })
        row
      }
    })
  }

  @FXML
  def handleAddToCart(): Unit = {
    if (selectedFood != null) {
      try {
        cart.addItem(selectedFood, quantitySpinner.getValue)
        updateCartCount()
        alerts.showSuccessAlert("Success", s"Added ${selectedFood.name.value} to cart!")
      } catch {
        case e: IllegalArgumentException =>
          alerts.showWarningAlert("Invalid Quantity", e.getMessage)
      }
    } else {
      alerts.showWarningAlert("No Selection", "Please select a food item first.")
    }
  }

  @FXML
  def addToCartFromDialog(food: FoodType, quantity: Int): Unit = {
    try {
      cart.addItem(food, quantity)
      updateCartCount()
      alerts.showSuccessAlert("Success", s"Added ${selectedFood.name.value} to cart!")
    } catch {
      case e: IllegalArgumentException =>
        alerts.showWarningAlert("Invalid Quantity", e.getMessage)
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

  @FXML
  def handleClearCart(): Unit = {
    if (cart.getItems.isEmpty) {
      alerts.showInfoAlert("Nothing to Clear", "Your cart is already empty.")
    } else {
      val confirmed = alerts.showConfirmationAlert("Clear Cart", "Are you sure you want to clear all items from your cart?")
      if (confirmed) {
        cart.clear()
        updateCartCount()
        alerts.showSuccessAlert("Cart Cleared", "All items have been removed from your cart.")
      }
    }
  }

  @FXML
  def handleRemoveFromCart(): Unit = {
    val selectedItem = cartTable.getSelectionModel.getSelectedItem
    if (selectedItem != null) {
      cart.removeItem(selectedItem)
      updateCartCount()
      alerts.showSuccessAlert("Removed", "Item removed from cart.")
    } else {
      alerts.showWarningAlert("No Selection", "Please select an item to remove.")
    }
  }

  @FXML
  def handleCalculateCalories(): Unit = {
    if (cart.getItems.isEmpty) {
      alerts.showWarningAlert("Empty Cart", "Your cart is empty. Add some items first!")
    } else {
      showCaloriesSummary()
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
      caloriesController.setCartItems(cart) // Pass Cart instead of ObservableList
      dialogStage.showAndWait()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load cart summary dialog.")
    }
  }

  private def applyFilters(): Unit = {
    filteredFoodList = FoodType.filterFoods(Main.foodData, searchField.getText, categoryComboBox.getValue, isHealthyFilterActive)
    foodTable.setItems(filteredFoodList)
  }

  private def updateCartCount(): Unit = {
    cartCountLabel.setText(s"${cart.getTotalItems} item(s) in Cart")
    cartSummaryItemsLabel.setText(cart.getTotalItems.toString)
    cartSummaryCaloriesLabel.setText(s"${cart.getTotalCalories} kcal")
    cartSummaryPriceLabel.setText(f"RM ${cart.getTotalPrice}%.2f")
    cartHealthStatusLabel.setText(cart.getHealthStatus)
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

  @FXML
  def handleLogout(): Unit = {
    val confirmed = alerts.showConfirmationAlert("Logout Confirmation", "Are you sure you want to logout?")
    if (confirmed) {
      alerts.showSuccessAlert("Logout Successful", "You have successfully logged out!")
      Main.showLoginOverview()
    }
  }

  @FXML
  def handleCheckout(): Unit = {
    if (cart.getItems.isEmpty) {
      alerts.showWarningAlert("Empty Cart", "Your cart is empty. Add some items first!")
    } else {
      showCheckoutDialog()
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
      checkoutController.setCartItems(cart) // Pass Cart instead of ObservableList
      dialogStage.showAndWait()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        alerts.showErrorAlert("Error", "Could not load checkout dialog.")
    }
  }

  def clearCartAfterCheckout(): Unit = {
    saveOrderToHistory()
    cart.clear()
    updateCartCount()
    alerts.showInfoAlert("Order Completed", "You may view your order in order history.")
  }
