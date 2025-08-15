package soonteck.model

import scalafx.beans.property.{StringProperty, DoubleProperty, IntegerProperty}
import scalikejdbc.*
import soonteck.util.Database
import scala.util.{Try, Success, Failure}

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
      new FoodType("Pizza", "Italian Food", 30.0, 800, "Cheesy pizza"),
      new FoodType("Burger", "Fast Food", 12.0, 500, "Juicy beef burger"),
      new FoodType("Sushi", "Japanese", 15.0, 300, "Fresh salmon sushi")
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
