/*
  Filename: ConnectionWrapper.java
  Author: Minh Duc Pham
  Course: CST8221 - JAP, Lab Section: 313
  Assignment #: 2 - Part 2
  Date: 6th December 2019
  Professor: Daniel Cormier
  Purpose: To hold some fields, constructor and method to create connection streams
  Class list: ConnectionWrapper
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Purpose: This class create stream object and initialize socket object with some methods related
 * to the stream
 * @author Minh Duc Pham
 * @version 1.0
 * @see javax.swing java.awt 
 * @since 1.8.0_221
 */
public class ConnectionWrapper {
  /**The output stream*/
  private ObjectOutputStream outputStream;
  /**The input stream*/
  private ObjectInputStream inputStream;
  /**The socket object*/
  private Socket socket;
  
  /**
   * Purpose: Initialize the class member socket
   * @param socket The Socket object used to initialized
   */
  public ConnectionWrapper(Socket socket) {
    this.socket = socket;
  }
  
  /**
   * Purpose: This method used to return the socket object
   * @return socket The socket object
   */
  Socket getSocket() {
    return socket;
  }
  
  /**
   * Purpose: This method used to return the ObjectOutputStream object
   * @return An object of ObjectOutputStream 
   */
  public ObjectOutputStream getOutputStream() {
    return outputStream;
  }
  
  /**
   * Purpose: This method used to return the ObjectInputStream object
   * @return An object of ObjectInputStream
   */
  public ObjectInputStream getInputStream() {
    return inputStream;
  }
  
  /**
   * Purpose: This method instantiates object of ObjectInputStreams, assigns it to inputStream and
   * return the object to the stream
   * @return inputStream An object of ObjectInputStream
   * @throws IOException To handle all exception that may occur
   */
  public ObjectInputStream createObjectIStream() throws IOException {
    inputStream = new ObjectInputStream(socket.getInputStream());
    return inputStream;
  }
  
  /**
   * Purpose: This method instantiates object of ObjectOutputStreams, assigns it to outputStream and
   * return the object to the stream
   * @return outputStream An object of ObjectOutputStream
   * @throws IOException To handle all exception that may occur
   */
  public ObjectOutputStream createObjectOStream() throws IOException {
    outputStream = new ObjectOutputStream(socket.getOutputStream());
    return outputStream;
  }
  
  /**
   * Purpose: This method used to initialize outputStream and inputStream by calling the 
   * above createObjectStream method and createInputStream method
   * @throws IOException To handle all exception that may occur
   */
  public void createStreams() throws IOException {
    this.createObjectOStream();
    this.createObjectIStream();
  }
  
  /**
   * Purpose: This method close the output stream the input stream and the socket
   * @throws IOException To handle all exception that may occur
   */
  public void closeConnection() throws IOException {
    if(inputStream != null)
      inputStream.close();
    if(outputStream != null)
      outputStream.close();
    if(socket != null) 
      if (!socket.isClosed())
        socket.close();
  } 
}
