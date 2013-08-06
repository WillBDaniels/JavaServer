package com.server;

class ServerHandler {
//main method, simply endorses the run() method
   public static void main(String argv[]) throws Exception {
      Server httpserver = new Server();
      httpserver.run();
   }
}
