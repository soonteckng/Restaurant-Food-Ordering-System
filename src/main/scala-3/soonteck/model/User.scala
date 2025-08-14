package soonteck.model

import scalafx.beans.property.StringProperty
import scalikejdbc.*
import scala.util.{Try, Success, Failure}
import soonteck.util.Database

class User(val usernameS: String, val passwordS: String) extends Database:
  def this() = this(null, null)

  var username = new StringProperty(usernameS)
  var password = new StringProperty(passwordS)

  // Save user to database
  def save(): Try[Int] =
    if (!isExist) then
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into users (username, password) values
          (${username.value}, ${password.value})
        """.update.apply()
      })
    else
      Try(DB autoCommit { implicit session =>
        sql"""
          update users set password = ${password.value}
          where username = ${username.value}
        """.update.apply()
      })

  // Check if this user exists in the database  
  def isExist: Boolean =
    DB readOnly { implicit session =>
      sql"""
        select * from users where username = ${username.value}
      """.map(rs => rs.string("username")).single.apply()
    } match
      case Some(x) => true
      case None => false

object User extends Database:

  // Initialize users table if not exists
  def initializeTable() =
    DB autoCommit { implicit session =>
      sql"""
        create table users (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          username varchar(64) unique not null,
          password varchar(64) not null
        )
      """.execute.apply()
    }

  // Find a user by username
  def findByUsername(username: String): Option[User] =
    DB readOnly { implicit session =>
      sql"select * from users where username = $username"
        .map(rs => new User(rs.string("username"), rs.string("password")))
        .single.apply()
    }

  // Get all users
  def getAllUsers: List[User] =
    DB readOnly { implicit session =>
      sql"select * from users"
        .map(rs => new User(rs.string("username"), rs.string("password")))
        .list.apply()
    }
