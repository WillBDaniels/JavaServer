/*
 * Created by: William Daniels
 * CS 460, 8 Feb 2012
 * Update version 1.0;
 */

package com.server;

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
                System.out.println("Listening for new connections...");
                Socket client = listen.accept();
                ServerThread cc = new ServerThread(client);
            }
        } catch(Exception e) {
        System.out.println("Exception: "+e.getMessage());
        }
    }


}

