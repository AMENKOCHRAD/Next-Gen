<?xml version="1.0" encoding="UTF-8"?>

        <?import javafx.scene.control.Button?>
        <?import javafx.scene.image.Image?>
        <?import javafx.scene.image.ImageView?>
        <?import javafx.scene.layout.AnchorPane?>
        <?import javafx.scene.layout.HBox?>

<AnchorPane prefHeight="50.0" prefWidth="875.0" style="-fx-background-color: #4843e4;" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="controllers.NavbarController">
    <HBox alignment="CENTER_LEFT" spacing="20.0" AnchorPane.leftAnchor="10.0" AnchorPane.rightAnchor="10.0">
        <Button styleClass="navbar-button" text="User" />
        <Button styleClass="navbar-button" text="Cours" />
        <Button fx:id="coursButton" onAction="#handleCoursButtonAction" styleClass="navbar-button" text="Event" />
        <Button styleClass="navbar-button" text="Nutrition" />
        <Button styleClass="navbar-button" text="Vente" />
        <Button styleClass="navbar-button" text="Reclamation" />
    </HBox>
    <ImageView fitHeight="50.0" fitWidth="116.0" layoutX="10.0" layoutY="0.0" pickOnBounds="true" preserveRatio="true">
        <image>
            <Image url="@images/logosportify.png" />
        </image>
    </ImageView>
</AnchorPane>
