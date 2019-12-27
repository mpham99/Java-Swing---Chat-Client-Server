/*
Filename: Client.java
Author: Minh Duc Pham
Course: CST8221 - JAP, Lab Section: 313
Assignment #: 2 - Part 2
Date: 6th December 2019
Professor: Daniel Cormier
Purpose: Building and operating the calculator GUI
*/

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 * Purpose: This class called the method and launch the application window
 * @author Minh Duc Pham
 * @version 1.2
 * @see javax.swing java.awt
 * @since 1.8.0_221
 */
public class Client {

  /**
   * Purpose: The application main method
   * @param args Program command line argument
   */
  public static void main (String [] args) {
    String title;
    title = new String("Minh Duc's Client Chat UI");
    
    //Create and display the main application GUI
    EventQueue.invokeLater(new Runnable(){
      @Override
      public void run(){
        ClientChatUI mainPanel = new ClientChatUI(title);
        mainPanel.setResizable(false);
        mainPanel.setSize(new Dimension(588, 500));
        mainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setLocationByPlatform(true);
        mainPanel.setVisible(true);
      }
    });
  }
}