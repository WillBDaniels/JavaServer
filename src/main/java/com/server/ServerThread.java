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

private boolean continueUDP = true;
private Socket client;
private DatagramSocket udpClient = null;

	//build the class with a client from Server.java
	public ServerThread(Socket client){
		this.client = client;
		run();
	}
	public ServerThread(DatagramSocket udpClient){
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
			try{
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				udpClient.receive(packet);
				System.out.println("Sending client to handler at port: " + udpClient.getLocalPort());
				switch(udpClient.getLocalPort()){
					case 6667:
						udpUpload();
						break;
					case 9999:
						udpDownload();
						break;
				}
			}catch(IOException e){
				e.printStackTrace();
			}
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
		HandleUDP myudpHandler = new HandleUDP(udpClient, 6667);
		while(continueUDP)
			continueUDP = myudpHandler.continueLooping();
		udpClient.close();
	}
	private void udpDownload(){
		System.out.println("Reached UDP Download");
		HandleUDP myudpHandler = new HandleUDP(udpClient, 9999);
		while(continueUDP)
			continueUDP = myudpHandler.continueLooping();
		udpClient.close();
	}
	private void httpHandler(){
		new HandleHttp(client);
	}
}