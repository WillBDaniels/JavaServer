package com.server;

import java.net.*;
import java.io.*;
import java.util.*;

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
class HandleUDP{
	protected Map<String, Integer> addressTable;

	private final static int MAXIMUM_PACKET_SIZE = 1024 * 20;
	private DatagramSocket client;
	private final int whichPort;


	/**
	* constructor for the class, takes two parameters, as well as starts the thread. 
	*
	* @param client The DatagramSocket that is connected to.
	* @param whichPort an int that tells which port has been passed in.
	*/
	public HandleUDP(DatagramSocket client, int whichPort){
		this.whichPort = whichPort;
		this.client = client;
		run();
	}

	/**
	*simply chooses a port and runs it. 
	*/
	public void run(){
		if (whichPort == 6667)
			uploadBlackHole();
		else if (whichPort == 9999)
			downloadDataDump();

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

		addressTable = new HashMap<String, Integer>();
		try{
			//build a buffer for holding the packets. 
			byte[] buf = new byte[MAXIMUM_PACKET_SIZE];
			//make a new packet object
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			//wait until you receive a packet 
			//***WARNING*** this IS a blocking call, due to the threaded nature of the 
			//server, this is not the end of the world, but be aware. 
			while(true){
				client.receive(packet);
				byte[] data = packet.getData();
				String temp = packet.getSocketAddress().toString();

				if (addressTable.get(temp) == null){
					System.out.println("New Connection established");
					addressTable.put(temp, packet.getLength());
				}
				else{
					int tempValue = addressTable.get(temp) + packet.getLength();
					addressTable.put(temp, tempValue);

				}
				//tempBuf = packet.getData();
				//tempBufTwo = ("Well Hello back!").getBytes();
				//DatagramPacket reSend = new DatagramPacket(tempBufTwo, tempBufTwo.length, packet.getAddress(), packet.getPort());
				//client.send(reSend);
				if (((char)packet.getData()[0]) == '0'){
					System.out.println("About to kill connection");
					byte[] tempBuf = new byte[MAXIMUM_PACKET_SIZE];
					//Write back a packet containing how many total bytes were written. 
					tempBuf = ("You sent " + addressTable.get(temp) + " bytes of data via UDP").getBytes();
					DatagramPacket tempPacket = new DatagramPacket(tempBuf, tempBuf.length, packet.getAddress(), packet.getPort());
					//send the packet to the remote destination. 
					client.send(tempPacket);
					addressTable.remove(temp);
				}
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void downloadDataDump(){;
		try{
			//build a buffer for holding the packets. 
			byte[] buf = new byte[MAXIMUM_PACKET_SIZE];
			//make a new packet object
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while(true){
				client.receive(packet);
				byte[] tempBuf = ("Get ready for 100 MB of fun!").getBytes();
				System.out.println("I'm about to throw 100MB of the best data in the world at: " + packet.getAddress());
				DatagramPacket sendPacket = new DatagramPacket(tempBuf, tempBuf.length, packet.getAddress(), packet.getPort());
				client.send(sendPacket);
				Thread.sleep(100);
				(new DumpData(packet.getAddress(), packet.getPort(), client)).start();
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
class DumpData extends Thread{
	private final static int MAXIMUM_PACKET_SIZE = 1400;
	private final int BYTES_IN_MEGABYTES = 1048576;

	private InetAddress sendAddress;
	private int port;
	private DatagramSocket client;


	public DumpData(InetAddress sendAddress, int port, DatagramSocket client){
		this.sendAddress = sendAddress;
		this.port = port;
		this.client = client;
	}

	public void run(){
		try{
			double totalBytes = 0.0;
			byte[] b = new byte[MAXIMUM_PACKET_SIZE];
			DatagramPacket sendPacket = new DatagramPacket(b, b.length, sendAddress, port);
	        new Random().nextBytes(b);
	        while ( totalBytes < (BYTES_IN_MEGABYTES * 100)){
        		client.send(sendPacket);
        		totalBytes = totalBytes + MAXIMUM_PACKET_SIZE;

	        }
	        b = ("All done!  :) ").getBytes();
	        DatagramPacket sendPacketTwo = new DatagramPacket(b, b.length, sendAddress, port);
	        client.send(sendPacketTwo);

        }catch(IOException e){
    		e.printStackTrace();
    	}

	}
}
