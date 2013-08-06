package com.server;

import java.net.*;
import java.io.*;

class HandleUDP extends Thread{
	private DatagramSocket client;
	private final int whichPort;
	private boolean continueLooping = true;

	public HandleUDP(DatagramSocket client, int whichPort){
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
		catch(Exception e){
			e.printStackTrace();
		}

	}

	private void handleUpload(){
		//TODO: UDP Upload code
		continueLooping = false;
	}
	private void handleDownload(){
		continueLooping = false;
		//TODO: UDP Download code
	}
	public boolean continueLooping(){
		return this.continueLooping;
	}
}
