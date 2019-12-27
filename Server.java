/*
Filename: Server.java
Author: Minh Duc Pham
Course: CST8221 - JAP, Lab Section: 313
Assignment #: 2 - Part 2
Date: 6th December 2019
Professor: Daniel Cormier
Purpose: Building and operating the calculator GUI
Class List: Server
 */

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 * Purpose: This class called the method and launch the application window
 * @author Minh Duc Pham
 * @version 1.4
 * @see javax.swing java.awt
 * @since 1.8.0_221
 */
public class Server {

  /**
   * Purpose: The application main method
   * @param args Program command line argument
   */
  public static void main (String [] args) {
    /**Default port number 65535*/
    int port = 65535;
    /**Socket object*/
    Socket socket = null;
    /**Server socket object*/
    ServerSocket serverSocket = null;

    //Check command line argument
    if(args.length > 0) {

      //Check if the conversion from string to integer is successful or not
      try {
        port = Integer.parseInt(args[0]);  
      }catch(NumberFormatException ex) {
        System.out.println("Please enter a integer for port number!");
        return;
      }

      //Check the range of the port number
      if(port >= 0 && port <= 65535) 
        System.out.println("Using port number: " + port);
      else {
        System.out.println("Port number out of range");
        return;
      }

    }else
      //If no command line argument is given, use default port
      System.out.println("Using default port: 65535");

    //Create a TCP/IP server socket
    try {
      serverSocket = new ServerSocket(port);

      //Friend's name
      int friend = 0;

      //Infinite while loop to call accept() on server socket
      while(true) {
        socket = serverSocket.accept();

        //Set the socket
        if(socket.getSoLinger() != -1)
          socket.setSoLinger(true, 5);
        if(!socket.getTcpNoDelay()) 
          socket.setTcpNoDelay(true);

        //Print socket information on the console screen
        System.out.println( "Connecting to a client Socket [addr=" + socket.getInetAddress() 
                + ",port=" + socket.getPort() 
                + ",localport=" + socket.getLocalPort() 
                + "]");

        //Increment friend value
        friend++;

        //Change the title for the window
        final String title = "Minh Duc's Friend " + friend;

        //Launch the client window
        launchClient(socket, title);
      }//end of for loop

    } catch (IOException ex) {
      System.out.println("Failed to create a server socket!");
    }
  }

  /**
   * Purpose: The launch client method is for setting up the server GUI
   * @param socket The socket object to pass to the GUI.
   * @param title The window title 
   */
  public static void launchClient(Socket socket, String title) {
    //Create and display the main application GUI
    EventQueue.invokeLater(new Runnable(){
      @Override
      public void run(){
        ServerChatUI mainPanel = new ServerChatUI(socket);
        mainPanel.setTitle(title);
        mainPanel.setResizable(false);
        mainPanel.setSize(new Dimension(588, 500));
        mainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setLocationByPlatform(true);
        mainPanel.setVisible(true);
      }
    });
  }

}//end of Server