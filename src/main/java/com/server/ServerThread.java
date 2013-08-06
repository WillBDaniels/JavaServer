package com.server;

import java.util.*;
import java.io.*;
import java.net.*;

/**
* This class is the class that checks the clients local port,
* and hands off threads accordingly. 
*
*@author William Daniels
*@version 1.1
*/
class ServerThread {

private Socket client;

	//build the class with a client from Server.java
	public ServerThread(Socket client){
		this.client = client;
		run();
	}

	//These values are defined in Server.java if confusion exists. 
	public void run(){
		System.out.println("Checking port...." + client.getLocalPort());
		switch(client.getLocalPort()){
			case 8080: 
				tcpUpload();
				break;
			case 8000:
				tcpDownload();
				break;
			case 6667:
				udpUpload();
				break;
			case 9999:
				udpDownload();
				break;
			case 3962:
				httpHandler();
				break;
		}
	}

	//Initialize the various threads that are needed.
	private void tcpUpload(){
		System.out.println("Reached TCP Upload");
		new HandleTCP(client, 8080);
	}
	private void tcpDownload(){	
		System.out.println("Reached TCP Download");
		new HandleTCP(client, 8000);
	}
	private void udpUpload(){
		System.out.println("Reached UDP Upload");
		new HandleUDP(client, 6667);
	}
	private void udpDownload(){
		System.out.println("Reached UDP Download");
		new HandleUDP(client, 9999);
	}
	private void httpHandler(){
		new HandleHttp(client);
	}
}