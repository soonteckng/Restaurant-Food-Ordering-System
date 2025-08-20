package soonteck.view

import soonteck.model.FoodType
import soonteck.Main
import javafx.fxml.FXML
import javafx.scene.control.{Label, Button, Spinner}
import javafx.stage.Stage

class FoodDetailsOverviewController:
  @FXML private var foodNameLabel: Label = null
  @FXML private var categoryLabel: Label = null
  @FXML private var priceLabel: Label = null
  @FXML private var caloriesLabel: Label = null
  @FXML private var healthRatingLabel: Label = null
  @FXML private var servingSizeLabel: Label = null
  @FXML private var ingredientsLabel: Label = null
  @FXML private var descriptionLabel: Label = null
  @FXML private var quickQuantitySpinner: Spinner[Int] = null
  @FXML private var quickAddButton: Button = null
  @FXML private var closeButton: Button = null

  private var dialogStage: Stage = null
  private var currentFood: FoodType = null
  private var mainController: HomePageOverviewController = null

  def setDialogStage(dialogStage: Stage): Unit =
    this.dialogStage = dialogStage

  def setMainController(mainController: HomePageOverviewController): Unit =
    this.mainController = mainController

  def setFoodItem(foodItem: FoodType): Unit =
    currentFood = foodItem

    foodNameLabel.setText(foodItem.name.value)
    categoryLabel.setText(foodItem.category.value)
    priceLabel.setText(f"RM ${foodItem.price.value}%.2f")
    caloriesLabel.setText(s"${foodItem.calories.value} kcal")
    descriptionLabel.setText(foodItem.description.value)

    setHealthRating(foodItem.calories.value)
    setServingSize(foodItem.category.value)
    setIngredients(foodItem.name.value, foodItem.category.value)

  private def setHealthRating(calories: Int): Unit =
    val (rating, style) = calories match
      case c if c < 250 => ("Very Healthy", "-fx-text-fill: #28a745; -fx-font-weight: bold;")
      case c if c < 400 => ("Moderately Healthy", "-fx-text-fill: #ffc107; -fx-font-weight: bold;")
      case c if c < 600 => ("High Calorie", "-fx-text-fill: #fd7e14; -fx-font-weight: bold;")
      case _ => ("Very High Calorie", "-fx-text-fill: #dc3545; -fx-font-weight: bold;")

    healthRatingLabel.setText(rating)
    healthRatingLabel.setStyle(style)

  private def setServingSize(category: String): Unit =
    val servingSize = category match
      case "Fast Food" => "1 large portion"
      case "Italian Food" => "1 slice (for pizza) / 1 plate"
      case "Japanese" => "6-8 pieces"
      case "Chinese" => "1 bowl / 1 plate"
      case "Malaysian" => "1 plate"
      case _ => "1 regular serving"

    servingSizeLabel.setText(servingSize)

  private def setIngredients(foodName: String, category: String): Unit =
    val ingredients = foodName.toLowerCase match
      // Italian Food
      case "margherita pizza" => "Pizza dough, tomato sauce, mozzarella cheese, fresh basil, olive oil"
      case "pepperoni pizza" => "Pizza dough, tomato sauce, mozzarella cheese, pepperoni, oregano"
      case "carbonara pasta" => "Spaghetti, eggs, bacon, parmesan cheese, black pepper, cream"
      case "lasagna" => "Lasagna sheets, ground beef, ricotta cheese, mozzarella, tomato sauce, herbs"
      case "chicken parmigiana" => "Chicken breast, breadcrumbs, marinara sauce, mozzarella, parmesan"
      case "fettuccine alfredo" => "Fettuccine pasta, butter, heavy cream, parmesan cheese, garlic"
      case "risotto mushroom" => "Arborio rice, wild mushrooms, vegetable stock, white wine, parmesan"
      case "classic cheeseburger" => "Beef patty, burger bun, cheddar cheese, lettuce, tomato, pickles"
      case "chicken burger" => "Chicken breast, burger bun, mayonnaise, lettuce, tomato"
      case "fish burger" => "Fish fillet, burger bun, tartar sauce, lettuce, pickles"
      case "double bacon burger" => "Two beef patties, bacon, cheese, burger bun, lettuce, tomato"
      case "chicken wings" => "Chicken wings, buffalo sauce, celery, ranch dressing"
      case "french fries" => "Potatoes, vegetable oil, salt"
      case "onion rings" => "Onions, flour, beer batter, breadcrumbs, oil"
      case "hot dog" => "Beef frankfurter, hot dog bun, mustard, ketchup, onions"
      case "salmon sashimi" => "Fresh salmon, soy sauce, wasabi, pickled ginger"
      case "tuna sashimi" => "Fresh bluefin tuna, soy sauce, wasabi, pickled ginger"
      case "california roll" => "Sushi rice, nori, crab meat, avocado, cucumber, sesame seeds"
      case "tempura prawns" => "Prawns, tempura batter, vegetable oil, tentsuyu sauce"
      case "chicken teriyaki" => "Chicken thigh, teriyaki sauce, mirin, sake, ginger, garlic"
      case "beef ramen" => "Ramen noodles, beef broth, beef slices, green onions, nori, egg"
      case "miso soup" => "Miso paste, dashi stock, tofu, wakame seaweed, green onions"
      case "gyoza" => "Pork mince, cabbage, garlic, ginger, dumpling wrappers, soy sauce"
      case "sweet and sour pork" => "Pork, pineapple, bell peppers, sweet and sour sauce, cornstarch"
      case "kung pao chicken" => "Chicken, peanuts, chili peppers, soy sauce, garlic, ginger"
      case "beef black bean" => "Beef strips, black bean sauce, bell peppers, onions, garlic"
      case "fried rice" => "Jasmine rice, eggs, mixed vegetables, soy sauce, sesame oil"
      case "chow mein" => "Egg noodles, cabbage, carrots, bean sprouts, soy sauce, oyster sauce"
      case "peking duck" => "Duck, hoisin sauce, pancakes, cucumber, spring onions"
      case "dim sum platter" => "Various dumpling wrappers, pork, shrimp, vegetables, soy sauce"
      case "chicken tacos" => "Corn tortillas, grilled chicken, onions, cilantro, salsa, lime"
      case "beef burrito" => "Flour tortilla, seasoned beef, black beans, rice, cheese, salsa"
      case "chicken quesadilla" => "Flour tortilla, grilled chicken, cheese, bell peppers, onions"
      case "nachos supreme" => "Tortilla chips, cheese sauce, jalapeños, sour cream, guacamole"
      case "guacamole & chips" => "Avocados, lime juice, onions, cilantro, tortilla chips"
      case "fish tacos" => "Corn tortillas, grilled fish, cabbage slaw, lime, chipotle mayo"
      case "chicken curry" => "Chicken, onions, tomatoes, garam masala, turmeric, coconut milk"
      case "lamb biryani" => "Basmati rice, lamb, yogurt, saffron, cardamom, cinnamon, onions"
      case "butter chicken" => "Chicken, tomatoes, butter, cream, garam masala, fenugreek"
      case "vegetable curry" => "Mixed vegetables, coconut milk, curry leaves, mustard seeds, turmeric"
      case "naan bread" => "Flour, yogurt, yeast, butter, garlic, cilantro"
      case "samosa" => "Pastry dough, potatoes, peas, cumin, coriander, turmeric"
      case "pad thai" => "Rice noodles, tamarind paste, fish sauce, bean sprouts, peanuts, lime"
      case "green curry" => "Green curry paste, coconut milk, thai basil, eggplant, chicken"
      case "tom yum soup" => "Prawns, lemongrass, kaffir lime leaves, chili, fish sauce, lime juice"
      case "massaman curry" => "Massaman curry paste, beef, potatoes, coconut milk, peanuts"
      case "mango sticky rice" => "Glutinous rice, coconut milk, mango, sugar, salt"
      case "bbq ribs" => "Pork ribs, BBQ sauce, brown sugar, paprika, garlic powder"
      case "mac and cheese" => "Macaroni pasta, cheddar cheese, milk, butter, flour"
      case "buffalo chicken salad" => "Mixed greens, chicken strips, buffalo sauce, ranch dressing, celery"
      case "pancakes" => "Flour, eggs, milk, butter, baking powder, maple syrup"
      case "caesar salad" => "Romaine lettuce, parmesan cheese, croutons, caesar dressing, anchovies"
      case "greek salad" => "Tomatoes, cucumbers, olives, feta cheese, red onions, olive oil"
      case "lamb souvlaki" => "Lamb chunks, olive oil, lemon juice, oregano, tzatziki sauce"
      case "hummus platter" => "Chickpeas, tahini, lemon juice, garlic, pita bread, olive oil"
      case "falafel wrap" => "Chickpeas, herbs, spices, pita bread, tahini sauce, vegetables"
      case "quinoa bowl" => "Quinoa, avocado, cherry tomatoes, cucumber, lemon vinaigrette"
      case "grilled salmon" => "Salmon fillet, lemon, herbs, olive oil, garlic, black pepper"
      case "chicken salad wrap" => "Whole wheat tortilla, grilled chicken, mixed greens, vegetables"
      case "veggie smoothie bowl" => "Acai berries, banana, granola, fresh berries, coconut flakes"
      case "steamed vegetables" => "Broccoli, carrots, snap peas, cauliflower, herbs, olive oil"
      case "grilled chicken breast" => "Chicken breast, herbs, spices, olive oil, garlic, lemon"
      case name if category == "Italian" => "Fresh herbs, olive oil, tomatoes, cheese"
      case name if category == "Japanese" => "Fresh ingredients, rice, soy sauce, miso"
      case name if category == "Fast Food" => "Processed ingredients, sauces, seasonings"
      case name if category == "Chinese" => "Soy sauce, ginger, garlic, fresh vegetables"
      case name if category == "Mexican" => "Spices, peppers, lime, cilantro, beans"
      case name if category == "Indian" => "Aromatic spices, herbs, yogurt, rice"
      case name if category == "Thai" => "Coconut milk, lemongrass, chili, fish sauce"
      case name if category == "American" => "Fresh ingredients, herbs, seasonings"
      case name if category == "Mediterranean" => "Olive oil, herbs, vegetables, cheese"
      case name if category == "Healthy" => "Fresh vegetables, lean proteins, whole grains"
      case _ => "Fresh ingredients, spices, seasonings"
  
      ingredientsLabel.setText(ingredients)

  @FXML
  def handleQuickAdd(): Unit =
    if currentFood != null && mainController != null then
      val quantity = quickQuantitySpinner.getValue
      mainController.addToCartFromDialog(currentFood, quantity)
      dialogStage.close()

  @FXML
  def handleClose(): Unit =
    dialogStage.close()