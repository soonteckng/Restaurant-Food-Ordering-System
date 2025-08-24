package soonteck.model

import javafx.collections.transformation.FilteredList
import scalafx.beans.property.{DoubleProperty, IntegerProperty, StringProperty}
import scalikejdbc.*
import soonteck.util.Database

import scala.util.{Failure, Success, Try}
import javafx.collections.{FXCollections, ObservableList}

import scala.jdk.CollectionConverters.*

class FoodType(val nameS: String, val categoryS: String, val priceD: Double, val caloriesI: Int, val descriptionS: String) extends Database:
  var name        = new StringProperty(nameS)
  var category    = new StringProperty(categoryS)
  var price       = new DoubleProperty(this, "price", priceD)
  var calories    = new IntegerProperty(this, "calories", caloriesI)
  var description = new StringProperty(descriptionS)

  def save(): Try[Int] =
    Try(DB autoCommit { implicit session =>
      sql"""
        insert into foodtype (name, category, price, calories, description)
        values (${name.value}, ${category.value}, ${price.value}, ${calories.value}, ${description.value})
      """.update.apply()
    })

  def isHealthy: Boolean =
    calories.value < 500 &&
      !category.value.contains("Fast Food") &&
      !name.value.toLowerCase.contains("fried")

  def getHealthRating: String = calories.value match {
    case c if c < 300 => "Low Calorie"
    case c if c < 500 => "Moderate Calorie"
    case c if c < 700 => "High Calorie"
    case _ => "Very High Calorie"
  }

  def getServingSize: String = category.value match {
    case "Fast Food" => "1 large portion"
    case "Chinese" => "1 bowl / 1 plate"
    case _ => "1 regular serving"
  }

  def getIngredients: String = name.value.toLowerCase match {
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
    case _ => "Fresh ingredients prepared with care"
  }

object FoodType extends Database:
  def getAllFood: List[FoodType] =
    DB readOnly { implicit session =>
      sql"select * from foodtype"
        .map(rs => new FoodType(
          rs.string("name"),
          rs.string("category"),
          rs.double("price"),
          rs.int("calories"),
          rs.string("description")
        ))
        .list.apply()
    }

  def initializeTable(): Unit =
    DB autoCommit { implicit session =>
      // Check if table exists using Derby syntax
      val tableExists = Try {
        sql"SELECT COUNT(*) FROM SYS.SYSTABLES WHERE TABLENAME = 'FOODTYPE'"
          .map(_.int(1)).single.apply().getOrElse(0) > 0
      }.getOrElse(false)

      if (!tableExists) {
        sql"""
          create table foodtype (
            id integer GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
            name varchar(200) not null,
            category varchar(100) not null,
            price double not null,
            calories integer not null,
            description varchar(500) not null
          )
        """.execute.apply()
      }
    }

  def resetDefaultFoods(): Unit =
    // Clear existing data
    DB autoCommit { implicit session =>
      sql"delete from foodtype".update.apply()
    }

    val defaultFoods = List(
      new FoodType("Margherita Pizza", "Italian", 25.00, 720, "Classic pizza with fresh tomatoes, mozzarella, and basil"),
      new FoodType("Pepperoni Pizza", "Italian", 28.00, 850, "Traditional pizza topped with pepperoni slices"),
      new FoodType("Carbonara Pasta", "Italian", 23.00, 680, "Creamy pasta with bacon, eggs, and parmesan cheese"),
      new FoodType("Lasagna", "Italian", 27.00, 820, "Layered pasta dish with meat sauce and cheese"),
      new FoodType("Chicken Parmigiana", "Italian", 30.00, 780, "Breaded chicken with marinara sauce and melted cheese"),
      new FoodType("Fettuccine Alfredo", "Italian", 25.00, 900, "Rich and creamy pasta dish with parmesan sauce"),
      new FoodType("Risotto Mushroom", "Italian", 24.00, 620, "Creamy rice dish cooked with wild mushrooms"),

      new FoodType("Classic Cheeseburger", "Fast Food", 19.00, 750, "Juicy beef patty with cheese, lettuce, and tomato"),
      new FoodType("Chicken Burger", "Fast Food", 17.00, 680, "Crispy chicken breast with mayo and fresh vegetables"),
      new FoodType("Fish Burger", "Fast Food", 18.00, 620, "Battered fish fillet with tartar sauce and lettuce"),
      new FoodType("Double Bacon Burger", "Fast Food", 25.00, 1150, "Two beef patties with crispy bacon and cheese"),
      new FoodType("Chicken Wings", "Fast Food", 20.00, 600, "Spicy buffalo wings served with ranch dressing"),
      new FoodType("French Fries", "Fast Food", 10.00, 450, "Golden crispy potato fries with sea salt"),
      new FoodType("Onion Rings", "Fast Food", 10.00, 520, "Beer-battered onion rings, crispy and golden"),
      new FoodType("Hot Dog", "Fast Food", 13.00, 420, "Classic beef frankfurter with mustard and ketchup"),

      new FoodType("Salmon Sashimi", "Japanese", 33.00, 250, "Fresh raw salmon slices served with wasabi and soy sauce"),
      new FoodType("Tuna Sashimi", "Japanese", 36.00, 220, "Premium bluefin tuna, expertly sliced"),
      new FoodType("California Roll", "Japanese", 27.00, 330, "Sushi roll with crab, avocado, and cucumber"),
      new FoodType("Tempura Prawns", "Japanese", 29.00, 520, "Lightly battered and fried prawns with tentsuyu sauce"),
      new FoodType("Chicken Teriyaki", "Japanese", 25.00, 580, "Grilled chicken glazed with sweet teriyaki sauce"),
      new FoodType("Beef Ramen", "Japanese", 23.00, 650, "Rich beef broth with ramen noodles and tender beef slices"),
      new FoodType("Miso Soup", "Japanese", 9.00, 120, "Traditional soybean paste soup with tofu and seaweed"),
      new FoodType("Gyoza", "Japanese", 17.00, 380, "Pan-fried pork dumplings with soy dipping sauce"),

      new FoodType("Sweet and Sour Pork", "Chinese", 22.00, 620, "Crispy pork pieces in tangy sweet and sour sauce"),
      new FoodType("Kung Pao Chicken", "Chinese", 21.00, 540, "Spicy stir-fried chicken with peanuts and chili peppers"),
      new FoodType("Beef Black Bean", "Chinese", 24.00, 560, "Tender beef strips in savory black bean sauce"),
      new FoodType("Fried Rice", "Chinese", 16.00, 520, "Wok-fried rice with eggs and mixed vegetables"),
      new FoodType("Chow Mein", "Chinese", 19.00, 580, "Stir-fried noodles with vegetables and soy sauce"),
      new FoodType("Peking Duck", "Chinese", 46.00, 980, "Roasted duck served with pancakes and hoisin sauce"),
      new FoodType("Dim Sum Platter", "Chinese", 29.00, 480, "Assorted steamed dumplings and buns"),

      new FoodType("Chicken Tacos", "Mexican", 20.00, 480, "Soft tacos filled with grilled chicken and fresh salsa"),
      new FoodType("Beef Burrito", "Mexican", 23.00, 820, "Large flour tortilla filled with seasoned beef and beans"),
      new FoodType("Chicken Quesadilla", "Mexican", 19.00, 650, "Grilled tortilla filled with chicken and melted cheese"),
      new FoodType("Nachos Supreme", "Mexican", 17.00, 720, "Tortilla chips topped with cheese, jalapeños, and sour cream"),
      new FoodType("Guacamole & Chips", "Mexican", 13.00, 420, "Fresh avocado dip served with crispy tortilla chips"),
      new FoodType("Fish Tacos", "Mexican", 22.00, 530, "Grilled fish with cabbage slaw and chipotle mayo"),

      new FoodType("Chicken Curry", "Indian", 24.00, 620, "Aromatic chicken curry with traditional spices"),
      new FoodType("Lamb Biryani", "Indian", 29.00, 850, "Fragrant basmati rice cooked with tender lamb and spices"),
      new FoodType("Butter Chicken", "Indian", 26.00, 720, "Creamy tomato-based chicken curry"),
      new FoodType("Vegetable Curry", "Indian", 20.00, 500, "Mixed vegetables in coconut curry sauce"),
      new FoodType("Naan Bread", "Indian", 9.00, 320, "Traditional Indian flatbread baked in tandoor"),
      new FoodType("Samosa", "Indian", 13.00, 300, "Crispy pastry filled with spiced potatoes and peas"),

      new FoodType("Pad Thai", "Thai", 21.00, 640, "Stir-fried rice noodles with tamarind and peanuts"),
      new FoodType("Green Curry", "Thai", 23.00, 600, "Spicy coconut curry with thai basil and eggplant"),
      new FoodType("Tom Yum Soup", "Thai", 19.00, 220, "Hot and sour soup with prawns and lemongrass"),
      new FoodType("Massaman Curry", "Thai", 25.00, 720, "Rich and mild curry with beef and potatoes"),
      new FoodType("Mango Sticky Rice", "Thai", 15.00, 430, "Sweet dessert with coconut milk and fresh mango"),

      new FoodType("BBQ Ribs", "American", 33.00, 1100, "Slow-cooked pork ribs with smoky BBQ sauce"),
      new FoodType("Mac and Cheese", "American", 17.00, 750, "Creamy macaroni pasta with melted cheddar cheese"),
      new FoodType("Buffalo Chicken Salad", "American", 22.00, 480, "Mixed greens with spicy chicken strips and ranch"),
      new FoodType("Pancakes", "American", 16.00, 650, "Fluffy pancakes served with maple syrup and butter"),

      new FoodType("Caesar Salad", "Mediterranean", 19.00, 440, "Crisp romaine lettuce with parmesan and croutons"),
      new FoodType("Greek Salad", "Mediterranean", 18.00, 360, "Fresh vegetables with feta cheese and olive oil"),
      new FoodType("Lamb Souvlaki", "Mediterranean", 27.00, 620, "Grilled lamb skewers with tzatziki sauce"),
      new FoodType("Hummus Platter", "Mediterranean", 15.00, 400, "Creamy chickpea dip with pita bread and vegetables"),
      new FoodType("Falafel Wrap", "Mediterranean", 20.00, 520, "Crispy chickpea balls in pita with tahini sauce"),

      new FoodType("Quinoa Bowl", "Healthy", 23.00, 450, "Superfood bowl with quinoa, avocado, and fresh vegetables"),
      new FoodType("Grilled Salmon", "Healthy", 30.00, 500, "Fresh salmon fillet grilled with herbs and lemon"),
      new FoodType("Chicken Salad Wrap", "Healthy", 19.00, 420, "Whole wheat wrap with grilled chicken and vegetables"),
      new FoodType("Veggie Smoothie Bowl", "Healthy", 17.00, 350, "Acai berry bowl topped with granola and fresh fruits"),
      new FoodType("Steamed Vegetables", "Healthy", 13.00, 180, "Seasonal vegetables steamed to perfection"),
      new FoodType("Grilled Chicken Breast", "Healthy", 25.00, 380, "Lean protein served with herbs and spices")
    )

    defaultFoods.foreach { food =>
      try {
        food.save() match {
          case Success(_) => 
          case Failure(e) => println(s"Failed to add ${food.name.value}: ${e.getMessage}")
        }
      } catch {
        case e: Exception => println(s"Exception adding ${food.name.value}: ${e.getMessage}")
      }
    }