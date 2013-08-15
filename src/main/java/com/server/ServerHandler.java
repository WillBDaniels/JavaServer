/*
* Silly test
*/

package com.server;

/**
* This class is the entry point into the program where the main method lives
*
*@author William Daniels
*/
public class ServerHandler {
//main method, simply endorses the run() method
   public static void main(String argv[]){
      Server httpserver = new Server();
      httpserver.startServer();
   }
}
