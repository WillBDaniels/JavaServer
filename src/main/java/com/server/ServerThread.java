package com.server;

import java.util.*;
import java.io.*;
import java.net.*;

class ServerThread {
private Socket client;

	public ServerThread(Socket client){
		this.client = client;
	}

	public void run(){
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
			case 3962:
				httpHandler();
		}
	}

	private void tcpUpload(){

	}
	private void tcpDownload(){

	}
	private void udpUpload(){

	}
	private void udpDownload(){

	}
	private void httpHandler(){
		new HandleHttp(client);
	}
}