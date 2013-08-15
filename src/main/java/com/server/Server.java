/*
 * Created by: William Daniels
 * 8 Feb 2012
 * Update version 1.1;
 */

package com.server;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.lang.StringBuilder;

/**
* This class acts as the basic port handler, and contains the 
* primary server loop. All Port handling, and handing off to threads,
* essentially happens here. 
*
*@author William Daniels
*@version 1.1
*/
public class Server {
    //initialize all of the ports this server will listen on.
    /** http upload and download port*/
    private static final int HTTP_PORT = 3962;
    /** UDP Upload port */
    private static final int UDP_UPLOAD_PORT = 6001;
    /** UDP Download port */
    private static final int UDP_DOWNLOAD_PORT = 9999;
    /** TCP Upload port */
    private static final int TCP_UPLOAD_PORT = 8080;
    /** TCP Downlaod port */
    private static final int TCP_DOWNLOAD_PORT = 8000;
    /**
    * This method acts as the main thread starter for the program. kicks off all the threads. 
    *
    */
    public void startServer() {
        new Thread(){ 
            public void run(){
                try{ 
                spinupServerSocket(new ServerSocket(HTTP_PORT), 0);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
                new Thread(){ 
            public void run(){
                try{ 
                spinupServerSocket(new ServerSocket(UDP_DOWNLOAD_PORT), UDP_DOWNLOAD_PORT);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
                new Thread(){ 
            public void run(){
                try{ 
                spinupServerSocket(new ServerSocket(UDP_UPLOAD_PORT), UDP_UPLOAD_PORT);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
                new Thread(){ 
            public void run(){
                try{ 
                spinupServerSocket(new ServerSocket(TCP_DOWNLOAD_PORT), 0);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
                new Thread(){ 
            public void run(){
                try{ 
                spinupServerSocket(new ServerSocket(TCP_UPLOAD_PORT), 0);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
    * method spinupServerSocket
    * This method starts all of the connetions, and listens in a loop to all of them. 
    *
    */
    public void spinupServerSocket(ServerSocket listen, int udpPort) {
        DatagramSocket udpClient = null;
        if (udpPort == 0){
            try {
                while(true) {
                    System.out.println("Listening for new TCP/HTTP connections... on port: " + listen.getLocalPort());
                    Socket client = listen.accept();
                    new ServerThread(client);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        else{
                try{
                    System.out.println("Listening for new UDP Data... on port: " + udpPort);
                    udpClient = new DatagramSocket(udpPort);
                    new ServerThread(udpClient);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            
        }      
    }


}

