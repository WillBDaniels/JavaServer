package com.server;

import java.net.*;
import java.io.*;

/**
* This class handles most the primary UDP Upload and Download functionality for 
* the server. All of the various handling of packets, whether it be dumping or
* recieving is handled inside. A side note: ALL of the methods used for building
* the 'willdp' aka: the baby protocol on top of UDP the server uses to specifically
* consider '0' (that is, the char value of 0), to be the finishing bit for the connection.
* once that is reached, the respective sender is expected to send, in this order: a packet
* containing the ipAddress to bind to, as well as a port number. 
*
*@author William Daniels
*@version 1.1
*/
class HandleUDP extends Thread{

	private final static int BYTES_IN_MEGABYTES = 1048576;
	private DatagramSocket client;
	private final int whichPort;
	private boolean continueLooping = true;

	/**
	* constructor for the class, takes two parameters, as well as starts the thread. 
	*
	* @param client The DatagramSocket that is connected to.
	* @param whichPort an int that tells which port has been passed in.
	*/
	public HandleUDP(DatagramSocket client, int whichPort){
		this.whichPort = whichPort;
		this.client = client;
		this.start();
	}

	/**
	*simply chooses a port and runs it. 
	*/
	@Override
	public void run(){
		if (whichPort == 6667)
			uploadBlackHole();
		else if (whichPort == 9999)
			downloadDataDump();
		try{
			client.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	* This method handles all of the upload logic for the UDP protocol. When a connection
	* is made to it, it 'black holes' all of the given information, and disposes it. It waits
	* until the exit character (0) is sent to it, and then tries to connect to a remote host with
	* the next two packets. 
	*
	*/
	private void uploadBlackHole(){
		double length = 0.0;
		try{
			//build a buffer for holding the packets. 
			byte[] buf = new byte[BYTES_IN_MEGABYTES];
			//make a new packet object
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			//wait until you receive a packet 
			//***WARNING*** this IS a blocking call, due to the threaded nature of the 
			//server, this is not the end of the world, but be aware. 
			client.receive(packet);
			//loop until you've either received 100 MB, or received the exit bit. 
			//This makes it so there is at least a little pause between bursts of 100 MB. 
			while((((char)packet.getData()[0]) != '0') && (length < (BYTES_IN_MEGABYTES *100))){
				byte[] temp = new byte[packet.getLength()];
				temp = packet.getData();
				client.receive(packet);
				//This length variable for holding the aggregate amount of bytes thus far. 
				length += packet.getLength();
			}
			//receive the last two packets.
			client.receive(packet);

			byte[] tempTwo = packet.getData();
			//ip address byte array conversion
			String ipAddress = new String(tempTwo);
			ipAddress = ipAddress.trim();
			ipAddress = ipAddress.substring(0, ipAddress.length());
			client.receive(packet);
			//port byte array converstion.
			byte[] tempThree = packet.getData();
			String port = new String(tempThree);
			port = port.trim();
			port = port.substring(0, port.length());
			//try and make a new connection to the given port and IP address. 
			System.out.println("ipaddress length: " + ipAddress.length() + "port length: " + port.length());
			Thread.sleep(2000);
			DatagramSocket returnSocket = new DatagramSocket(new InetSocketAddress(ipAddress, Integer.parseInt(port)));
			byte[] tempBuf = new byte[256];
			//Write back a packet containing how many total bytes were written. 
			tempBuf = ("You sent " + length + "bytes of data via UDP").getBytes();
			DatagramPacket tempPacket = new DatagramPacket(tempBuf, tempBuf.length);
			//send the packet to the remote destination. 
			returnSocket.send(tempPacket);
			//kill the loop, wait for more connections. 
			continueLooping = false;
		}catch(Exception e){
			continueLooping = false;
			e.printStackTrace();
		}
	}
	private void downloadDataDump(){
		continueLooping = false;
		//TODO: UDP Download code
	}
	/**
	* This method simply returns our private internal looping boolean
	* used in ServerThead.java for checking completion state.
	*
	* @return this.continueLooping a boolean containing a state of the class. 
	*/
	public boolean continueLooping(){
		return this.continueLooping;
	}
}
