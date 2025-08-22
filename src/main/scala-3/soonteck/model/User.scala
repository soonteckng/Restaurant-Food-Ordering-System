package soonteck.model

import soonteck.util.Database
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalikejdbc.*
import scala.util.{Try, Success, Failure}

class User(val usernameS: String, val passwordS: String, val userIdI: Int = 0) extends Database:
  def this() = this(null, null)

  var username = new StringProperty(usernameS)
  var password = new StringProperty(passwordS)
  var id = new IntegerProperty(this, "id", userIdI)
  
  def validateCredentials(inputPassword: String): Boolean =
    password.value == inputPassword

  def validateForRegistration(confirmPassword: String): Either[String, Unit] =
    if (username.value == null || username.value.trim.isEmpty)
      Left("Username is required.")
    else if (username.value.length < 3)
      Left("Username must be at least 3 characters long.")
    else if (password.value == null || password.value.isEmpty)
      Left("Password is required.")
    else if (password.value.length < 6)
      Left("Password must be at least 6 characters long.")
    else if (password.value != confirmPassword)
      Left("Passwords do not match.")
    else
      Right(())

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

  def isExist: Boolean =
    DB readOnly { implicit session =>
      sql"""
          select * from users where username = ${username.value}
        """.map(rs => rs.string("username")).single.apply()
    } match
      case Some(x) => true
      case None => false

object User extends Database:
  def initializeTable() =
    DB autoCommit { implicit session =>
      sql"""
          create table users (
            id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
            username varchar(64) unique not null,
            password varchar(64) not null
          )
        """.execute.apply()
    }

  def findByUsername(username: String): Option[User] =
    DB readOnly { implicit session =>
      sql"select * from users where username = $username"
        .map(rs => new User(rs.string("username"), rs.string("password"), rs.int("id")))
        .single.apply()
    }

  def getUserByUsername(username: String): Option[User] = findByUsername(username)

  def getAllUsers: List[User] =
    DB readOnly { implicit session =>
      sql"select * from users"
        .map(rs => new User(rs.string("username"), rs.string("password"), rs.int("id")))
        .list.apply()
    }