package soonteck.util

import scalikejdbc.*
import soonteck.model.User

trait Database:
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true"

  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine")

  given AutoSession = AutoSession

object Database extends Database:
  def setupDB() =
    if (!hasDBInitialize)
      User.initializeTable()

  def hasDBInitialize: Boolean =
    DB getTable "users" match
      case Some(x) => true
      case None => false