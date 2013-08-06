package com.server;

class ServerHandler {
//main method, simply endorses the run() method
   public static void main(String argv[]){
      Server httpserver = new Server();
      httpserver.startServer();
   }
}
