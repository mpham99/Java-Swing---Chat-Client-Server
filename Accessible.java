/*
Filename: Accessible.java
Author: Minh Duc Pham
Course: CST8221 - JAP, Lab Section: 313
Assignment #: 2 - Part 2
Date: 6th December 2019
Professor: Daniel Cormier
Purpose: An inteface with two methods
Class list: None
*/

import javax.swing.JTextArea;

/**
 * Purpose: An interface to get the chat display object and close the chat window
 * @author Minh Duc Pham
 * @version 1.0
 * @see javax.swing.JTextArea
 * @since 1.8.0_221
 */
public interface Accessible {
  /**
   * Purpose: Get the value of the display JTextArea
   * @return an object of JText Area
   */
  public JTextArea getDisplay();
  
  /**
   * Purpose: To close the chat window
   */
  public void closeChat();
  
}//end of Accessible
