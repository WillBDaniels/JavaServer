package com.server;

import java.net.*;
import java.io.*;

class HandleUDP extends Thread{
	private Socket client;
	private final int whichPort;

	public HandleUDP(Socket client, int whichPort){
		this.whichPort = whichPort;
		this.client = client;
		this.start();
	}

	public void run(){
		if (whichPort == 6667)
			handleUpload();
		else if (whichPort == 9999)
			handleDownload();
		try{
			client.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

	private void handleUpload(){
		//TODO: UDP Upload code
	}
	private void handleDownload(){
		//TODO: UDP Download code
	}
}
