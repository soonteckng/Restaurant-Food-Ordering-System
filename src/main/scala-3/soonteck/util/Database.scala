package soonteck.util

import scalikejdbc.*
import soonteck.model.User

trait Database:
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:userDB;create=true;" // different DB name

  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine")

  given AutoSession = AutoSession

