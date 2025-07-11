package ch.makery.login

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3

object MainApp extends JFXApp3:

  override def start(): Unit ={
    val loginResource = getClass.getResource("view/LoginOverview.fxml")
    val loader = new FXMLLoader(loginResource)
    loader.load()
  }
  
