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

  // Moved isHealthy to the class (instance method)
  def isHealthy: Boolean =
    calories.value < 500 &&
      !category.value.contains("Fast Food") &&
      !name.value.toLowerCase.contains("fried")

object FoodType extends Database:

  def initializeTable(): Unit =
    DB autoCommit { implicit session =>
      // Check if table exists
      val tableExists = sql"""
        SELECT COUNT(*)
        FROM sys.systables
        WHERE tablename = 'FOODTYPE'
      """.map(_.int(1)).single.apply().getOrElse(0) > 0

      // Only create table if it doesn't exist
      if (!tableExists) {
        sql"""
          create table foodtype (
            id int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
            name varchar(64) NOT NULL UNIQUE,
            category varchar(64),
            price double,
            calories int,
            description varchar(255)
          )
        """.execute.apply()
      }
    }

  /** Completely reset food table to your default list */
  def resetDefaultFoods(): Unit =
    val defaults = Seq(
      new FoodType("Margherita Pizza", "Italian", 28.00, 780, "Classic tomato sauce, mozzarella, and fresh basil"),
      new FoodType("Pepperoni Pizza", "Italian", 32.00, 850, "Spicy pepperoni with mozzarella cheese"),
      new FoodType("Carbonara Pasta", "Italian", 24.00, 620, "Creamy pasta with bacon, egg, and parmesan"),
      new FoodType("Lasagna", "Italian", 26.00, 710, "Layered pasta with meat sauce and cheese"),
      new FoodType("Chicken Parmigiana", "Italian", 29.00, 690, "Breaded chicken with marinara and mozzarella"),
      new FoodType("Fettuccine Alfredo", "Italian", 22.00, 580, "Rich cream sauce with parmesan cheese"),
      new FoodType("Risotto Mushroom", "Italian", 25.00, 520, "Creamy arborio rice with wild mushrooms"),
      new FoodType("Classic Cheeseburger", "Fast Food", 15.00, 540, "Beef patty with cheese, lettuce, and tomato"),
      new FoodType("Chicken Burger", "Fast Food", 16.00, 480, "Grilled chicken breast with mayo and lettuce"),
      new FoodType("Fish Burger", "Fast Food", 17.00, 420, "Crispy fish fillet with tartar sauce"),
      new FoodType("Double Bacon Burger", "Fast Food", 22.00, 780, "Two beef patties with bacon and cheese"),
      new FoodType("Chicken Wings", "Fast Food", 18.00, 650, "Spicy buffalo wings with ranch dip"),
      new FoodType("French Fries", "Fast Food", 8.00, 320, "Golden crispy potato fries"),
      new FoodType("Onion Rings", "Fast Food", 9.00, 380, "Beer-battered onion rings"),
      new FoodType("Hot Dog", "Fast Food", 12.00, 450, "All-beef frankfurter with mustard and ketchup"),
      new FoodType("Salmon Sashimi", "Japanese", 24.00, 180, "Fresh raw salmon slices"),
      new FoodType("Tuna Sashimi", "Japanese", 26.00, 165, "Premium bluefin tuna slices"),
      new FoodType("California Roll", "Japanese", 18.00, 290, "Crab, avocado, and cucumber roll"),
      new FoodType("Tempura Prawns", "Japanese", 22.00, 340, "Lightly battered and fried prawns"),
      new FoodType("Chicken Teriyaki", "Japanese", 20.00, 420, "Grilled chicken with sweet teriyaki glaze"),
      new FoodType("Beef Ramen", "Japanese", 19.00, 580, "Rich broth with tender beef slices"),
      new FoodType("Miso Soup", "Japanese", 6.00, 85, "Traditional soybean paste soup"),
      new FoodType("Gyoza", "Japanese", 14.00, 280, "Pan-fried pork and vegetable dumplings"),
      new FoodType("Sweet and Sour Pork", "Chinese", 21.00, 520, "Battered pork with pineapple in tangy sauce"),
      new FoodType("Kung Pao Chicken", "Chinese", 19.00, 480, "Spicy chicken with peanuts and vegetables"),
      new FoodType("Beef Black Bean", "Chinese", 23.00, 450, "Tender beef in savory black bean sauce"),
      new FoodType("Fried Rice", "Chinese", 16.00, 420, "Wok-fried rice with egg and vegetables"),
      new FoodType("Chow Mein", "Chinese", 17.00, 390, "Stir-fried noodles with mixed vegetables"),
      new FoodType("Peking Duck", "Chinese", 35.00, 650, "Crispy duck with pancakes and hoisin sauce"),
      new FoodType("Dim Sum Platter", "Chinese", 25.00, 380, "Assorted steamed dumplings and buns"),
      new FoodType("Chicken Tacos", "Mexican", 18.00, 420, "Soft tacos with grilled chicken and salsa"),
      new FoodType("Beef Burrito", "Mexican", 20.00, 680, "Large tortilla with seasoned beef and beans"),
      new FoodType("Chicken Quesadilla", "Mexican", 19.00, 540, "Grilled tortilla with chicken and cheese"),
      new FoodType("Nachos Supreme", "Mexican", 17.00, 620, "Tortilla chips with cheese, jalapeños, and sour cream"),
      new FoodType("Guacamole & Chips", "Mexican", 12.00, 280, "Fresh avocado dip with corn tortilla chips"),
      new FoodType("Fish Tacos", "Mexican", 21.00, 380, "Grilled fish with cabbage slaw and lime"),
      new FoodType("Chicken Curry", "Indian", 22.00, 510, "Tender chicken in aromatic spice gravy"),
      new FoodType("Lamb Biryani", "Indian", 28.00, 620, "Fragrant basmati rice with spiced lamb"),
      new FoodType("Butter Chicken", "Indian", 24.00, 580, "Creamy tomato-based chicken curry"),
      new FoodType("Vegetable Curry", "Indian", 18.00, 320, "Mixed vegetables in coconut curry sauce"),
      new FoodType("Naan Bread", "Indian", 8.00, 290, "Soft leavened flatbread baked in tandoor"),
      new FoodType("Samosa", "Indian", 10.00, 250, "Crispy pastry filled with spiced potatoes"),
      new FoodType("Pad Thai", "Thai", 19.00, 450, "Stir-fried rice noodles with tamarind sauce"),
      new FoodType("Green Curry", "Thai", 21.00, 380, "Spicy coconut curry with vegetables"),
      new FoodType("Tom Yum Soup", "Thai", 16.00, 180, "Hot and sour soup with prawns"),
      new FoodType("Massaman Curry", "Thai", 23.00, 520, "Rich curry with beef and potatoes"),
      new FoodType("Mango Sticky Rice", "Thai", 12.00, 320, "Sweet dessert with coconut milk"),
      new FoodType("BBQ Ribs", "American", 32.00, 780, "Slow-cooked pork ribs with barbecue sauce"),
      new FoodType("Mac and Cheese", "American", 14.00, 520, "Creamy macaroni pasta with cheese sauce"),
      new FoodType("Buffalo Chicken Salad", "American", 18.00, 380, "Mixed greens with spicy chicken strips"),
      new FoodType("Pancakes", "American", 13.00, 480, "Fluffy pancakes with maple syrup"),
      new FoodType("Caesar Salad", "American", 16.00, 290, "Romaine lettuce with parmesan and croutons"),
      new FoodType("Greek Salad", "Mediterranean", 15.00, 250, "Fresh vegetables with feta and olives"),
      new FoodType("Lamb Souvlaki", "Mediterranean", 26.00, 460, "Grilled lamb skewers with tzatziki"),
      new FoodType("Hummus Platter", "Mediterranean", 14.00, 220, "Chickpea dip with pita bread and vegetables"),
      new FoodType("Falafel Wrap", "Mediterranean", 16.00, 380, "Fried chickpea balls in pita with salad"),
      new FoodType("Quinoa Bowl", "Healthy", 19.00, 340, "Superfood bowl with quinoa, vegetables, and avocado"),
      new FoodType("Grilled Salmon", "Healthy", 28.00, 350, "Fresh Atlantic salmon with lemon and herbs"),
      new FoodType("Chicken Salad Wrap", "Healthy", 17.00, 320, "Whole wheat wrap with grilled chicken and greens"),
      new FoodType("Veggie Smoothie Bowl", "Healthy", 16.00, 280, "Acai bowl topped with fresh fruits and granola"),
      new FoodType("Steamed Vegetables", "Healthy", 12.00, 120, "Seasonal vegetables lightly steamed"),
      new FoodType("Grilled Chicken Breast", "Healthy", 22.00, 290, "Lean protein with herbs and spices")
    )

    DB autoCommit { implicit session =>
      sql"delete from foodtype".update.apply()  // Clear table
    }

    defaults.foreach(_.save())  // Insert new defaults

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

  // Static method to filter foods, adjusted for FilteredList compatibility
  def filterFoods(foods: ObservableList[FoodType], search: String, category: String, healthyOnly: Boolean): FilteredList[FoodType] =
    val filteredList = new FilteredList[FoodType](foods)
    filteredList.setPredicate { food =>
      val matchesSearch = search == null || search.isEmpty ||
        food.name.value.toLowerCase.contains(search.toLowerCase) ||
        food.description.value.toLowerCase.contains(search.toLowerCase)
      val matchesCategory = category == "All" || food.category.value == category
      val matchesHealthy = !healthyOnly || food.isHealthy
      matchesSearch && matchesCategory && matchesHealthy
    }
    filteredList