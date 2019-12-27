/*
Filename: ClientChatUI.java
Author: Minh Duc Pham
Course: CST8221 - JAP, Lab Section: 313
Assignment #: 2 - Part 2
Date: 6th December 2019
Professor: Daniel Cormier
Purpose: Building and operating the client chat GUI
Class list: ClientChatUI, WindowController, Controller
 */

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Purpose: This class set the GUI of the client chat
 * @author Minh Duc Pham
 * @version 1.0
 * @see javax.swing java.awt 
 * @since 1.8.0_221
 */
public class ClientChatUI extends JFrame implements Accessible {
  //Declare the global members for access

  /**Combo box for port selection*/
  private JComboBox<String> portBox;
  /**Text field for the host*/
  private JTextField hostText;  
  /**The connect button*/
  private JButton connectButton; 
  /**Text field that holds the typed message*/
  private JTextField message;
  /**The send button*/
  private JButton sendButton; 
  /**The text area for the chat display*/
  private JTextArea display; 
  /**The chat output stream*/
  private ObjectOutputStream outputStream;
  /**The chat socket*/
  private Socket socket;
  /**An object of ConnectionWrapper*/
  private ConnectionWrapper connection;

  /**
   * Purpose: The default constructor for the client chat UI
   * @param title The client chat frame title
   */
  public ClientChatUI(String title) {
    this.setTitle(title);
    this.runClient();
  }

  /**
   * Purpose: This private inner class override windowClosing method and call System.exit(0)
   * @author Minh Duc Pham
   * @version 1.0
   * @see java.io java.net
   * @since 1.8.0_221
   */
  private class WindowController extends WindowAdapter {
    /**
     * Purpose: This method is called when the Window is closed. It sends the chat
     * terminator to the output stream before close the program.
     */
    @Override
    public void windowClosing(WindowEvent we) {
      try {
        if(socket != null && !socket.isClosed())
          outputStream.writeObject(ChatProtocolConstants.CHAT_TERMINATOR);
      } catch (IOException ex) {
        System.exit(0);
      }
      System.exit(0);
    }//end of windowClosing

  }//end of WindowController

  /**
   * Purpose: This class handle ActionEvent and call the right function
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
      boolean connected = false;

      //If the connect button is clicked
      if(ae.getActionCommand().equals("Connect")) {
        String host = hostText.getText();

        try {
          //Get value from the combo box
          int port = Integer.parseInt(portBox.getSelectedItem().toString());
          //Connect to the server
          connected = connect(host, port);  
        }catch(NumberFormatException ex){
          display.setText(ex.getMessage() + "\n");
          return;
        }

        //If connected successfully
        if(connected == true) {
          connectButton.setEnabled(false);
          connectButton.setBackground(Color.BLUE);
          sendButton.setEnabled(true);
          message.requestFocus();
          Runnable run = new ChatRunnable<ClientChatUI>(ClientChatUI.this, connection);
          Thread thread = new Thread(run);
          thread.start();
        } else 
          return; 
      }

      //If the send button is clicked
      if(ae.getActionCommand().contentEquals("Send")) 
        send();

    }//end of actionPerformed method

    /**
     * Purpose: Connect to the server using a time-out socket then create the streams for the chat 
     * @param host The host to connect to
     * @param port The port to connect to
     * @return true if connect successfully, otherwise return false
     */
    private boolean connect(String host, int port) {
      //Create a time-out socket
      try {
        if (port < 0 || port > 65535) {
          System.out.println("The port number is out of range");
          return false;
        }
        //Create a new socket
        socket = new Socket();

        //The socket timeout is 60 seconds
        socket.connect(new InetSocketAddress(InetAddress.getByName(host), port), 60000);
        socket.setSoTimeout(60000);

        //Set the socket
        if(socket.getSoLinger() != -1)
          socket.setSoLinger(true, 5);
        if(!socket.getTcpNoDelay())
          socket.setTcpNoDelay(true);

        //Append the socket to the chat display area
        display.setText( display.getText() 
            + "Connected to Socket[addr=" + socket.getInetAddress() 
            + ",port=" + socket.getPort()
            + ",localport=" + socket.getLocalPort()
            + "]\n");

        //Create object of ConnectionWrapper, and set the streams
        connection = new ConnectionWrapper(socket);
        connection.createStreams();

        //Initialize outputStream field
        outputStream = connection.getOutputStream();
        return true;

      }catch (IOException ex) {
        display.setText("ERROR: Connection refused: server is not available. Check port or"
            + " restart server\n");
        return false;
      } 
    }//end of connect method

    /**
     * Purpose: Get the text from the message text field to append to the display and put to 
     * OutputStream
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
        enableConnectButton();
        display.setText(ex.getMessage());
      }
    }//end of send method

  }//end of Controller inner class

  /**
   * Purpose: This method create a JPanel as the GUI and return it at the end
   * @return mainPanel The client chat JPanel main panel
   */
  private JPanel createClientUI() {
    /**The main panel that holds all components*/
    JPanel mainPanel = new JPanel(); 
    /**The panel on the north of the mainPanel*/
    JPanel northPanel = new JPanel(); 
    /**The Panel for Connection*/
    JPanel connectionPanel = new JPanel();
    /**The Panel for the host label + text field*/
    JPanel hostPanel = new JPanel(); 
    /**The Panel for the Port combo box + connect button*/
    JPanel portPanel = new JPanel(); 
    /**The Panel for Message*/
    JPanel messagePanel = new JPanel(); 
    /**The Panel for Chat Display*/
    JPanel chatDisplayPanel = new JPanel();
    /**The titled border for the chat display panel*/
    TitledBorder chatDisplayBorder; 
    /**The host label*/
    JLabel hostLabel = new JLabel("Host: "); 
    /**The port label*/
    JLabel portLabel = new JLabel("Port: "); 
    /**Array of string for combo box options*/
    String[] portString = 
        new String[] {"", "8089", "65000", "65535"};
    /**The scroll bar for the chat display*/
    JScrollPane scrollBar; 
    /**The handler for the buttons and combo box*/
    Controller handler = new Controller(); 

    //Split the mainPanel into two smaller panel
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(northPanel, BorderLayout.NORTH);
    mainPanel.add(chatDisplayPanel, BorderLayout.CENTER);

    //Create the panel for Connection, Message and Chat Display
    connectionPanel.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 10), "CONNECTION")
        );
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
    northPanel.setLayout(new BorderLayout());
    chatDisplayPanel.setLayout(new BorderLayout());

    //Add sub-panels to the north panel
    northPanel.add(connectionPanel, BorderLayout.NORTH);
    northPanel.add(messagePanel, BorderLayout.SOUTH);

    /**CONNECTION COMPONENTS**/
    connectionPanel.setLayout(new BorderLayout());

    //For the host label
    hostLabel.setPreferredSize(new Dimension(35, 30));
    hostLabel.setDisplayedMnemonic('H');

    //For the port label
    portLabel.setPreferredSize(new Dimension(35, 30));
    portLabel.setDisplayedMnemonic('P');

    //For the host text field
    hostText = new JTextField(45);
    hostLabel.setLabelFor(hostText);
    hostText.setText("localhost");
    hostText.requestFocus();
    hostText.setBackground(Color.WHITE);
    hostText.setBorder(
        BorderFactory.createCompoundBorder(hostText.getBorder(), 
            BorderFactory.createEmptyBorder(0, 5, 0, 0)));
    hostText.setCaretPosition(0);

    //Add the label + text field to the host panel
    hostPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
    hostPanel.add(hostLabel);
    hostPanel.add(hostText);

    //For the combo box
    portBox = new JComboBox<String>(portString);
    portLabel.setLabelFor(portBox);
    portBox.setPreferredSize(new Dimension(90, 20));
    portBox.setBackground(Color.WHITE);
    portBox.setEditable(true);
    portBox.addActionListener(handler);

    //For the connection button
    connectButton = new JButton("Connect");
    connectButton.setPreferredSize(new Dimension(90, 20));
    connectButton.setBackground(Color.red);
    connectButton.setMnemonic('C');
    connectButton.addActionListener(handler);
    connectButton.setActionCommand("Connect");
    //Add the label + combo box + connect button to the port panel
    portPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
    portPanel.add(portLabel);
    portPanel.add(portBox);
    portPanel.add(connectButton);

    //Add components to the connection pane
    connectionPanel.add(hostPanel, BorderLayout.NORTH);
    connectionPanel.add(portPanel, BorderLayout.SOUTH);

    /** MESSAGE COMPONENTS **/
    messagePanel.setLayout(new FlowLayout(FlowLayout.LEADING));

    //For the message text field
    message = new JTextField(41);
    message.setText("Type mesage");

    //For the Send button
    sendButton = new JButton("Send");
    sendButton.setPreferredSize(new Dimension(81, 19));
    sendButton.setMnemonic('S');
    sendButton.setEnabled(false);
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
   * Purpose: This method called the createClientUI() method and sets the content pane and
   * add window listener to the frame using an object of WindowController
   */
  private void runClient() {
    this.setContentPane(createClientUI());
    this.addWindowListener(new WindowController());
  }

  /**
   * Purpose: return the chat display JTextArea object
   * @return an object of JTextArea for the chat display
   */
  @Override
  public JTextArea getDisplay() {
    return display;
  }

  /**
   * Purpose: Tries to close the connection if not closed, then enable the connect button
   * by calling enableConnectButton()
   */
  @Override
  public void closeChat() {
    //Close the socket
    if(!socket.isClosed()) {
      try {
        socket.close();  
      } catch (IOException ex) {}
    }

    //Enable the connect button
    this.enableConnectButton();
  }

  /**
   * Purpose: Enable the Connect button, set the background of connect button to Red,
   * disable the send button and request focust to the host text field 
   */
  private void enableConnectButton() {
    connectButton.setEnabled(true);
    sendButton.setEnabled(false);
    connectButton.setBackground(Color.RED);
    hostText.requestFocus();
  }

}//end of ClientChatUI
