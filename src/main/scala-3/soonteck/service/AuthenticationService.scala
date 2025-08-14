package soonteck.service

import soonteck.model.User
import scala.util.{Success, Failure}

class AuthenticationService:

  def registerUser(user: User): Boolean =
    if (user.isExist) then
      false // Username taken
    else
      user.save() match
        case Success(_) => true
        case Failure(ex) =>
          ex.printStackTrace()
          false

  def validateLogin(username: String, password: String): Boolean =
    User.findByUsername(username) match
      case Some(dbUser) => dbUser.password.value == password
      case None => false