package com.server;

import java.io.*;
import java.net.*;

class HandleTCP extends Thread{
	private Socket client;
	private final int whichPort;

	public HandleTCP(Socket client, int whichPort){
		this.client = client;
		this.whichPort = whichPort;
		this.start();
	}

	public void run(){
		if (whichPort == 8080)
			handleUpload();
		else if (whichPort == 8000)
			handleDownload();
		try{
			client.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private void handleUpload(){
		//TODO: TCP Upload Logic. 
	}
	private void handleDownload(){
		//TODO: TCP Download Logic. 
	}
}