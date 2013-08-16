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
public class ServerThread {

private boolean continueUDP = true;
private Socket client;
private DatagramSocket udpClient = null;

	//build the class with a client from Server.java
	public ServerThread(Socket client){
		this.client = client;
		run();
	}
	public ServerThread(DatagramSocket udpClient){
		continueUDP = true;
		this.udpClient = udpClient;
		run();
	}

	//These values are defined in Server.java if confusion exists. 
	public void run(){
			if (udpClient == null){
				switch(client.getLocalPort()){
					case 8080: 
						tcpUpload();
						break;
					case 8000:
						tcpDownload();
						break;
					case 3962:
						httpHandler();
						break;
				}
			}
			else{
				switch(udpClient.getLocalPort()){
					case 6001:
						udpUpload();
						break;
					case 9999:
						udpDownload();
						break;
				}
	
			}
	
	}

	//Initialize the various threads that are needed.
	private void tcpUpload(){
		new HandleTCP(client, 8080);
	}
	private void tcpDownload(){	
		new HandleTCP(client, 8000);
	}
	private void udpUpload(){
		new HandleUDP(udpClient, 6001);
	}
	private void udpDownload(){
		HandleUDP myudpHandler = new HandleUDP(udpClient, 9999);
	}
	private void httpHandler(){
		new HandleHTTP(client);
	}
}