package gui

import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.layout.VBox
import scalafx.scene.layout.HBox
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Stage

object LoginRegisterView:
  def build(stage: Stage): VBox =
    val usernameField = new TextField:
      promptText = "Username"


    val passwordField = new PasswordField:
      promptText = "Password"

    val loginButton = new Button("Login")
    val registerButton = new Button("Register")

    new VBox:
      spacing = 10
      alignment = Pos.Center
      padding = Insets(200)
      children = Seq(
        new Label("Username"),
        usernameField,
        new Label("Password"),
        passwordField,
        loginButton,
        registerButton,
       )

