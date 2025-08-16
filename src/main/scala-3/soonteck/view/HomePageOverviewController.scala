package soonteck.view

import scalafx.Includes.*
import javafx.scene.control.{TableView, TableColumn, Label, Spinner, Button, ComboBox, TextField, Alert}
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.MouseEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.stage.{Stage, Modality}
import javafx.scene.{Scene, Parent}
import javafx.collections.{ObservableList, FXCollections}
import javafx.collections.transformation.FilteredList
import soonteck.Main
import soonteck.model.{FoodType, CartItem}
import scala.collection.mutable.ListBuffer
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
  private var addToCartButton: Button = null
  @FXML
  private var categoryComboBox: ComboBox[String] = null
  @FXML
  private var searchField: TextField = null
  @FXML
  private var healthyFilterButton: Button = null
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
  private var clearCartButton: Button = null

  // Data
  private val cartItems: ObservableList[CartItem] = FXCollections.observableArrayList()
  private var filteredFoodList: FilteredList[FoodType] = null
  private var selectedFood: FoodType = null
  private var isHealthyFilterActive = false

  def initialize(): Unit =
    initializeMenuTab()
    initializeCartTab()
    setupEventHandlers()

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
    cartPriceColumn.cellValueFactory = { c => c.value.price.delegate.asObject() }

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
      showAlert("Success", s"Added ${selectedFood.name.value} to cart!", AlertType.INFORMATION)
    else
      showAlert("No Selection", "Please select a food item first.", AlertType.WARNING)

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
      healthyFilterButton.setText("Remove Filter")
    else
      healthyFilterButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;")
      healthyFilterButton.setText("Healthy Options")
    applyFilters()

  @FXML
  def handleClearCart(): Unit =
    cartItems.clear()
    updateCartCount()
    showAlert("Cart Cleared", "All items have been removed from your cart.", AlertType.INFORMATION)

  @FXML
  def handleRemoveFromCart(): Unit =
    val selectedItem = cartTable.getSelectionModel.getSelectedItem
    if selectedItem != null then
      cartItems.remove(selectedItem)
      updateCartCount()
      showAlert("Removed", "Item removed from cart.", AlertType.INFORMATION)
    else
      showAlert("No Selection", "Please select an item to remove.", AlertType.WARNING)

  @FXML
  def handleCalculateCalories(): Unit =
    var totalCalories = 0
    var totalPrice = 0.0
    val itemCount = cartItems.size()

    // Calculate totals manually
    for i <- 0 until cartItems.size() do
      totalCalories += cartItems.get(i).getCalories
      totalPrice += cartItems.get(i).getTotalPrice

    val message = s"""
                     |Cart Summary:
                     |Items: $itemCount
                     |Total Calories: $totalCalories kcal
                     |Total Price: RM ${"%.2f".format(totalPrice)}
    """.stripMargin

    showAlert("Cart Summary", message, AlertType.INFORMATION)

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
    for i <- 0 until cartItems.size() do
      itemCount += cartItems.get(i).quantity.value
    cartCountLabel.setText(s"$itemCount item(s) in Cart")

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
      foodDetailsController.setFoodItem(food)

      dialogStage.showAndWait()
    catch
      case e: Exception =>
        e.printStackTrace()
        showAlert("Error", "Could not load food details dialog.", AlertType.ERROR)

  private def showAlert(title: String, message: String, alertType: AlertType): Unit =
    val alert = new Alert(alertType)
    alert.setTitle(title)
    alert.setHeaderText(null)
    alert.setContentText(message)
    alert.showAndWait()