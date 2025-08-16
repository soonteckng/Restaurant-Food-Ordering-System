package soonteck.view

import soonteck.Main
import soonteck.model.{CartItem, FoodType}
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

  // Data
  private val cartItems: ObservableList[CartItem] = FXCollections.observableArrayList()
  private var filteredFoodList: FilteredList[FoodType] = null
  private var selectedFood: FoodType = null
  private var isHealthyFilterActive = false

  // Alert instance
  private val alerts = new Alerts()

  def initialize(): Unit =
    initializeMenuTab()
    initializeCartTab()
    setupEventHandlers()
    setupTableTooltip()

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
              tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 4px;")
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
    healthyFilterButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;")
    applyFilters()

  @FXML
  def handleHealthyFilter(): Unit =
    isHealthyFilterActive = !isHealthyFilterActive
    if isHealthyFilterActive then
      healthyFilterButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;")
      healthyFilterButton.setText("Show All")
    else
      healthyFilterButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;")
      healthyFilterButton.setText("Healthier Options")
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
      case avg if avg == 0 => ("No Items", "-fx-text-fill: #6c757d; -fx-font-weight: bold;")
      case avg if avg < 250 => ("🟢 Very Healthy", "-fx-text-fill: #28a745; -fx-font-weight: bold;")
      case avg if avg < 400 => ("🟡 Moderately Healthy", "-fx-text-fill: #ffc107; -fx-font-weight: bold;")
      case avg if avg < 600 => ("🟠 High Calorie", "-fx-text-fill: #fd7e14; -fx-font-weight: bold;")
      case _ => ("🔴 Very High Calorie", "-fx-text-fill: #dc3545; -fx-font-weight: bold;")

    cartHealthStatusLabel.setText(status)
    cartHealthStatusLabel.setStyle(style)

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