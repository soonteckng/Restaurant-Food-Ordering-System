package soonteck.model

import scalafx.beans.property.{StringProperty, DoubleProperty, IntegerProperty}
import scalikejdbc.*
import soonteck.util.Database
import scala.util.{Try, Success, Failure}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderHistory(val orderIdS: String, val orderDateS: String, val itemsS: String,
                   val totalD: Double, val statusS: String, val usernameS: String = "") extends Database:
  var orderId    = new StringProperty(orderIdS)
  var orderDate  = new StringProperty(orderDateS)
  var items      = new StringProperty(itemsS)
  var total      = new DoubleProperty(this, "total", totalD)
  var status     = new StringProperty(statusS)
  var username   = new StringProperty(usernameS)

  def save(): Try[Int] =
    Try(DB autoCommit { implicit session =>
      sql"""
        insert into order_history (order_id, order_date, items, total, status, username)
        values (${orderId.value}, ${orderDate.value}, ${items.value}, ${total.value}, ${status.value}, ${username.value})
      """.update.apply()
    })

object OrderHistory extends Database:

  def initializeTable(): Unit =
    DB autoCommit { implicit session =>
      // Check if table exists
      val tableExists = sql"""
        SELECT COUNT(*)
        FROM sys.systables
        WHERE tablename = 'ORDER_HISTORY'
      """.map(_.int(1)).single.apply().getOrElse(0) > 0

      if (!tableExists) {
        sql"""
          create table order_history (
            id int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
            order_id varchar(50) NOT NULL,
            order_date varchar(50) NOT NULL,
            items varchar(500),
            total double,
            status varchar(20),
            username varchar(64) NOT NULL,
            FOREIGN KEY (username) REFERENCES users(username)
          )
        """.execute.apply()
      } else {
        val hasUsernameColumn = Try {
          sql"SELECT username FROM order_history WHERE 1=0".map(_.string("username")).single.apply()
          true
        }.getOrElse(false)

        if (!hasUsernameColumn) {
          sql"ALTER TABLE order_history ADD COLUMN username varchar(64) DEFAULT 'unknown'".execute.apply()

          val userCount = sql"SELECT COUNT(*) FROM users".map(_.int(1)).single.apply().getOrElse(0)
          if (userCount == 0) {
            sql"INSERT INTO users (username, password) VALUES ('unknown', 'temp')".update.apply()
          }
          sql"UPDATE order_history SET username = 'unknown' WHERE username IS NULL OR username = ''".update.apply()

          Try {
            sql"ALTER TABLE order_history ADD CONSTRAINT fk_order_username FOREIGN KEY (username) REFERENCES users(username)".execute.apply()
          }.recover {
            case e => println(s"Warning: Could not add foreign key constraint: ${e.getMessage}")
          }
        }
      }
    }

  def getOrdersForUser(username: String): List[OrderHistory] =
    DB readOnly { implicit session =>
      sql"select * from order_history WHERE username = ${username} ORDER BY id DESC"
        .map(rs => new OrderHistory(
          rs.string("order_id"),
          rs.string("order_date"),
          rs.string("items"),
          rs.double("total"),
          rs.string("status"),
          rs.string("username")
        ))
        .list.apply()
    }

  @deprecated("Use getOrdersForUser instead", "1.0")
  def getAllOrders: List[OrderHistory] =
    DB readOnly { implicit session =>
      sql"select * from order_history ORDER BY id DESC"
        .map(rs => new OrderHistory(
          rs.string("order_id"),
          rs.string("order_date"),
          rs.string("items"),
          rs.double("total"),
          rs.string("status"),
          rs.stringOpt("username").getOrElse("")
        ))
        .list.apply()
    }

  def createOrderFromCart(cartItems: java.util.List[CartItem], username: String): OrderHistory = {
    // Generate order ID
    val orderId = s"ORD-${System.currentTimeMillis()}"

    // Format current date
    val orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    // Build items string
    val itemsBuilder = new StringBuilder()
    var totalPrice = 0.0

    for (i <- 0 until cartItems.size()) {
      val item = cartItems.get(i)
      val itemTotal = item.getTotalPrice
      totalPrice += itemTotal

      if (i > 0) itemsBuilder.append(", ")
      itemsBuilder.append(s"${item.item.value} x${item.quantity.value}")
    }

    new OrderHistory(orderId, orderDate, itemsBuilder.toString(), totalPrice, "Completed", username)
  }