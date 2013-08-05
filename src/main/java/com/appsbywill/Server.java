/*
 * Created by: William Daniels
 * CS 460, 8 Feb 2012
 * Update version 1.0;
 */

package com.appsbywill;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.lang.StringBuilder;

public class Server {

   public static final int HTTP_PORT = 6666;
   //initializes the ServerSocket
   public ServerSocket getServer() throws Exception {
      return new ServerSocket(HTTP_PORT);
   }

   //Accepts the Connection, processes the user
   public void run() {
      ServerSocket listen;
      try {
         listen = getServer();
         while(true) {
            Socket client = listen.accept();
            ProcessConnection cc = new
              ProcessConnection(client);
         }
      } catch(Exception e) {
         System.out.println("Exception: "+e.getMessage());
      }
   }

  //main method, simply endorses the run() method
   public static void main(String argv[]) throws
     Exception {
      Server httpserver = new Server();
      httpserver.run();
   }
}

//Inner Process Connection Class, processes the connection that the server makes
class ProcessConnection extends Thread {
   Socket client;
       BufferedReader is;
       DataOutputStream os;

   //Constructor
   public ProcessConnection(Socket s) { 
      client = s;
      try {
         is = new BufferedReader(new InputStreamReader
           (client.getInputStream()));
         os = new DataOutputStream(client.getOutputStream());
      } catch (IOException e) {
         System.out.println("Exception: "+e.getMessage());
      }
      this.start();
   }
   //The main method that breaks down all of the client input string (GET request, etc.)
   public void run() {
      try {

         String request = is.readLine();
         System.out.println( "Request: "+request );
         StringTokenizer st = new StringTokenizer( request );
            if ( (st.countTokens() >= 2) &&
              st.nextToken().equals("GET") ) {
               if ( (request =
                 st.nextToken()).startsWith("/") )
                  request = request.substring( 1 );
               if ( request.equals("") )
                  request = request + "index.html";
               File f = new File(request);
               shipDocument(os, f);
            } else {
              // BufferedReader rd = new BufferedReader(is));
               //System.out.println("here...?");

               String line;
              // System.out.println(" first line length: " + is.readLine().length());
               File newFile = new File("test.txt");
               byte[] myBuffer = null;
               FileOutputStream fos = new FileOutputStream(newFile);
               while (((line = is.readLine()) != null)&&(line.length() >0)) {
                      myBuffer = line.getBytes();
                      fos.write(myBuffer);
                }
               fos.close();
                System.out.println("after the loop");
                is.close();
            }
            client.close();
      } catch (Exception e) {
            e.printStackTrace();
      }
   }

//Sends the Selected document to the Client
   public static void shipDocument(DataOutputStream out,
     File f) throws Exception {
       try {
            //System.out.println((int)f.length());
            FileInputStream myFile= new FileInputStream(f);
          DataInputStream in = new
          DataInputStream(myFile);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String strLine = null;
          StringBuilder myBuilder = new StringBuilder();
          
          while (br.ready() == true){
              strLine = br.readLine();
              myBuilder.append(strLine);
             
          }
  
          in.close();
          out.writeBytes("HTTP/1.0 200 OK\r\n");
          out.writeBytes("Content-Length: " +
          f.length() +"\r\n");
          out.writeBytes("File Contents: " + myBuilder.toString() + "\r\n");

          out.writeBytes("Content-Type: text/html\r\n\r\n");

          out.flush();
       } catch (Exception e) {
          out.writeBytes("<html><head><title>error</title></head><body>\r\n\r\n");
          out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
          out.writeBytes("Content-Type: text/html\r\n\r\n");
          out.writeBytes("</body></html>");
          out.flush();
       } finally {
          out.close();
       }
   }
}