/*
Filename: ServerChatUI.java
Author: Minh Duc Pham
Course: CST8221 - JAP, Lab Section: 313
Assignment #: 2 - Part 2
Date: 6th December 2019
Professor: Daniel Cormier
Purpose: Building and operating the client chat GUI
Class list: ServerChatUI, WindowController, Controller
 */

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Purpose: This class set the GUI of the client chat
 * @author Minh Duc Pham
 * @version 1.0
 * @see javax.swing java.awt 
 * @since 1.8.0_221
 */
public class ServerChatUI extends JFrame implements Accessible{
  //Declare the global members for access

  /**Text field that holds the typed message*/
  private JTextField message;
  /**The send button*/
  private JButton sendButton; 
  /**The text area for the chat display*/
  private JTextArea display; 
  /**The socket object for the program*/
  private Socket socket; 
  /**The output stream*/
  private ObjectOutputStream outputStream;
  /**The object of ConnectionWrapper class*/
  private ConnectionWrapper connection;

  /**
   * Purpose: Initialize the socket member and call setFrame() and runClient function
   * @param s The object of type Socket
   */
  public ServerChatUI(Socket s) {
    this.socket = s;
    this.setFrame(this.createUI());
    this.runClient();
  }

  /**
   * Purpose: This private inner class override windowClosing method and call System.exit(0)
   * @author Minh Duc Pham
   * @version 1.0
   * @see javax.swing java.awt 
   * @since 1.8.0_221
   */
  private class WindowController extends WindowAdapter {

    /**
     * Purpose: This method try to close the chat connection, window frame and print to console 
     * message properly
     */
    @Override
    public void windowClosing(WindowEvent we) {
      System.out.println("ServerUI Window Closing");
      try {
        if(socket != null && !socket.isClosed())
          outputStream.writeObject(
              ChatProtocolConstants.DISPLACEMENT
              + ChatProtocolConstants.CHAT_TERMINATOR
              + ChatProtocolConstants.LINE_TERMINATOR);
      } catch (IOException ex) {}
      finally {
        dispose();
      }

      //Print message to the console
      System.out.println("Closing Chat!");
      
      //Close the chat connection and dispose the frame
      closeChat();

      //Dispose the frame
      dispose();
      System.out.println("Chat closed!");
    }

    @Override
    public void windowClosed(WindowEvent we) {
      System.out.println("Server UI closed");
    }
  }

  /**
   * Purpose: This class handle ActionEvent 
   * @author Minh Duc Pham
   * @version 1.0
   * @see java.awt.event.ActionListener
   * @since 1.8.0_221
   */
  private class Controller implements ActionListener {
    /**
     * Purpose: Handle all action event when a button is clicked
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
      if(ae.getActionCommand().equals("Send")) 
        this.send();
    }

    /**
     * Purpose: This method get the text from the message text box, append it to the chat display,
     * and use outputStream to write an output string.
     */
    private void send() {
      String sendMessage = message.getText();
      display.append(sendMessage + ChatProtocolConstants.LINE_TERMINATOR);
      try {
        outputStream.writeObject(
            ChatProtocolConstants.DISPLACEMENT
            + sendMessage
            + ChatProtocolConstants.LINE_TERMINATOR);
      }catch (IOException ex) {
        display.setText(ex.getMessage());
      }
    }

  }//end of Controller

  /**
   * Purpose: This method create a JPanel with the GUI and return it at the end
   * @return mainPanel The server chat JPanel main panel
   */
  private JPanel createUI() {
    /**The main panel that holds all components*/
    JPanel mainPanel = new JPanel(); 
    /**The Panel for Message*/
    JPanel messagePanel = new JPanel(); 
    /**The Panel for Chat Display*/
    JPanel chatDisplayPanel = new JPanel();
    /**The titled border for the chat display panel*/
    TitledBorder chatDisplayBorder;
    /**The scroll bar for the chat display*/
    JScrollPane scrollBar; 
    /**The handler for the buttons*/
    Controller handler = new Controller(); 

    //Split the mainPanel into two smaller panel
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(messagePanel, BorderLayout.NORTH);
    mainPanel.add(chatDisplayPanel, BorderLayout.CENTER);

    //Create the panel for Connection, Message and Chat Display
    messagePanel.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK, 10), "MESSAGE")
        );
    chatDisplayBorder = 
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 10), "CHAT DISPLAY");
    chatDisplayBorder.setTitleJustification(TitledBorder.CENTER);
    chatDisplayPanel.setBorder(chatDisplayBorder);


    //Set layout for the Panel
    chatDisplayPanel.setLayout(new BorderLayout());

    /** MESSAGE COMPONENTS **/
    messagePanel.setLayout(new FlowLayout(FlowLayout.LEADING));

    //For the message text field
    message = new JTextField(41);
    message.setText("Type mesage");
    message.requestFocus();
    message.setCaretPosition(0);

    //For the Send button
    sendButton = new JButton("Send");
    sendButton.setPreferredSize(new Dimension(81, 19));
    sendButton.setMnemonic('S');
    sendButton.addActionListener(handler);
    sendButton.setActionCommand("Send");

    //Add the button and text to message panel
    messagePanel.add(message);
    messagePanel.add(sendButton);

    /**CHAT DISPLAY COMPONENTS**/
    chatDisplayPanel.setLayout(new BorderLayout());

    //For the chat display text field
    display = new JTextArea(30, 45);
    display.setEditable(false);
    display.setBackground(Color.WHITE);

    //For the scroll bar
    scrollBar = new JScrollPane(display);

    //Add the scroll bar to the panel
    chatDisplayPanel.add(scrollBar);

    return mainPanel;
  }

  /**
   * Purpose: This method create an object of ConnectionWrapper and an object of output stream
   * and an object of Runnable. Then it start a new Thread with the newly created runnable object
   */
  private void runClient() {
    //Initialize an object of ConnectionWrapper
    connection = new ConnectionWrapper(socket);

    //Initialize outputStream field
    try {
      connection.createStreams();
      outputStream = connection.getOutputStream();
    } catch (IOException ex) {
      System.out.println("Failed to genereate an object output stream!");
    }

    //Create object of type Runnable
    Runnable run = new ChatRunnable<ServerChatUI>(this, connection);

    //Pass the runnable object to Thread and starts the thread
    Thread thread = new Thread(run);
    thread.start();
  }

  /**
   * Purpose: This method close the connection and disposes the frame 
   */
  @Override
  public void closeChat() {
    try {
      connection.closeConnection();
    } catch(IOException ex) {
      System.out.println("Failed to close the chat connection");
    }finally {
      //Dispose the frame
      this.dispose();
    }
  }

  /**
   * Purpose: Get the chat display object
   * @return The chat display JTWExtArea object
   */
  @Override
  public JTextArea getDisplay() {
    return display;
  }

  /**
   * Purpose: Take a parameter of an object of JPanel, add it to the frame content pane,
   *          set the size and the resizable properties of the frame. Add a WindowListener to 
   *          the Frame.
   * @param panel The panel to be added to the frame
   */
  private final void setFrame(JPanel panel) {
    this.setContentPane(panel);
    this.addWindowListener(new WindowController());
  }

}//end of ServerChatUI
