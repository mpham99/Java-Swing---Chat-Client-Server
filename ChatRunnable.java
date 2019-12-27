/*
Filename: ChatRunnable.java
Author: Minh Duc Pham
Course: CST8221 - JAP, Lab Section: 313
Assignment #: 2 - Part 2
Date: 6th December 2019
Professor: Daniel Cormier
Purpose: This method monitor the chat activities
Class list: ChatRunnable
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * Purpose: This class initialize members and monitor the chat activities
 * to the stream
 * @author Minh Duc Pham
 * @version 1.0
 * @see javax.swing java.net.Socket java.io
 * @since 1.8.0_221
 */
public class ChatRunnable<T extends JFrame & Accessible> implements Runnable {
  /**The chat UI*/
  private final T ui;
  /**The chat socket object*/
  private final Socket socket;
  /**The chat input stream*/
  private final ObjectInputStream inputStream;
  /**The chat output stream*/
  private final ObjectOutputStream outputStream;
  /**The chat display*/
  private final JTextArea display;

  /**
   * Purpose: Initialize the class fields
   * @param ui Object of generic type T extends JFrame and Accessible
   * @param connection Object of ConnectionWrapper
   */
  public ChatRunnable (T ui, ConnectionWrapper connection) {
    this.ui = ui;
    this.display = ui.getDisplay();
    this.socket = connection.getSocket();
    this.inputStream = connection.getInputStream();
    this.outputStream = connection.getOutputStream();
  }

  /**
   * Purpose: This method used to monitor the chat activities
   * to the stream
   */
  @Override
  public void run() {
    /**The string from the input stream*/
    String strin = "";
    /**The format for the date time object*/
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, hh:mm a");
    /**The date time object*/
    LocalDateTime time;

    //Create an endless loop
    while(true) {
      //Check if the socket is closed or not
      if(socket.isClosed())
        break;
      
      //Read object from the input stream and display it to the chat message display
      try {
        strin = (String)inputStream.readObject();
        time = LocalDateTime.now();

        //Compare the string to chat terminator constant
        if(strin.trim().equals(ChatProtocolConstants.CHAT_TERMINATOR)) {
          final String terminate;
          terminate = ChatProtocolConstants.DISPLACEMENT
              + time.format(formatter)
              + ChatProtocolConstants.LINE_TERMINATOR
              + strin;
          //Append to the display field
          display.append(terminate);
          break;
        }

        //If the chat is not terminated
        final String append;
        append = ChatProtocolConstants.DISPLACEMENT
            + time.format(formatter)
            + ChatProtocolConstants.LINE_TERMINATOR
            + strin;

        //Append to the display field
        display.append(append);

      }catch (ClassNotFoundException | IOException ex) {
        break;
      }
    }//end of endless for loop
    

    //Check if the socket is close or not when the loop is broken
    if(!socket.isClosed()) {
      try {
        outputStream.writeObject(
            ChatProtocolConstants.DISPLACEMENT
            + ChatProtocolConstants.CHAT_TERMINATOR
            + ChatProtocolConstants.LINE_TERMINATOR);
      } catch (IOException ex) {}
    }
    
    ui.closeChat();
  }//end of run()
  
}//end of ChatRunnable
