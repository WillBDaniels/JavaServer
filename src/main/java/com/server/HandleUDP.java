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

	private final static int ONE_MINUTE_IN_MILLISECONDS = 60000;

	private final static int MAXIMUM_PACKET_SIZE = 30 * 1024;
	
  private final Map<String, TimeStampValue> addressTable;
	private final DatagramSocket client;
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
    addressTable = new HashMap<String, TimeStampValue>();
    
		run();
	}

	/**
	*simply chooses a port and runs it. 
	*/
	public void run(){
		if (whichPort == 6001)
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

				TimeStampValue tsValue = addressTable.get(temp);
				if (tsValue == null){
					System.out.println("New UDP Connection established");
					tsValue = new TimeStampValue(packet.getLength(), System.currentTimeMillis());
					addressTable.put(temp, tsValue);
				}
				else{
					tsValue.value += packet.getLength();
					tsValue.timeStamp = System.currentTimeMillis();
				}
				if (((char)data[0]) == '0'){
					byte[] tempBuf = new byte[MAXIMUM_PACKET_SIZE];
					//Write back a packet containing how many total bytes were written. 
					tempBuf = ("You sent " + addressTable.get(temp).value + " bytes of data via UDP\r\n").getBytes();
					DatagramPacket tempPacket = new DatagramPacket(tempBuf, tempBuf.length, packet.getAddress(), packet.getPort());
					//send the packet to the remote destination. 
					client.send(tempPacket);
				}

				//check the time of all current values in the addressTablem, delete all over 2 minutes old.
				Iterator<TimeStampValue> it = addressTable.values().iterator();
				while (it.hasNext()) {
	          		if ((System.currentTimeMillis() - it.next().timeStamp) > ONE_MINUTE_IN_MILLISECONDS * 2) {
	            		it.remove();  // must use it.remove to prevent ConcurrentModificationException
	          		}
				}
			}
			

		}catch(IOException e){
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
				byte[] data = packet.getData();
				if (((char)data[0]) == '1'){
					byte[] tempBuf = ("Get ready for 100 MB of fun!").getBytes();
					System.out.println("I'm about to try and send 100 MB of data via UDP To: " + packet.getAddress());
					DatagramPacket sendPacket = new DatagramPacket(tempBuf, tempBuf.length, packet.getAddress(), packet.getPort());
					client.send(sendPacket);
					(new DumpData(packet.getAddress(), packet.getPort(), client)).start();
				}
				Thread.sleep(100);
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
/**
* This class is simply a compound object for holding both a value and a timestamp for cleaning out the 
* UDP map above. 
*
*@author William Daniels
*/
class TimeStampValue {
	public int value;
	public long timeStamp;

	public TimeStampValue(int value, long timeStamp){
		this.value = value;
		this.timeStamp = timeStamp;
	}
}

class DumpData extends Thread{
	private final static int MAXIMUM_PACKET_SIZE = 30 * 1024;
	private final int BYTES_IN_MEGABYTES = 1048576;
	private final static int MILLI_IN_SECONDS = 1000;


	private InetAddress sendAddress;
	private int port;
	private DatagramSocket client;


	public DumpData(InetAddress sendAddress, int port, DatagramSocket client){
		this.sendAddress = sendAddress;
		this.port = port;
		this.client = client;
	}

	@Override
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
	        System.out.println("Just finished sending 100 MB to the client via' UDP");
			byte[] buf = new byte[MAXIMUM_PACKET_SIZE];
			//make a new packet object
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        boolean continueOrNot = true;
	        long baselineTime = System.currentTimeMillis();
	        while (continueOrNot){
		        b = ("0").getBytes();
		        DatagramPacket sendPacketTwo = new DatagramPacket(b, b.length, sendAddress, port);
		        client.send(sendPacketTwo);
		        if ((System.currentTimeMillis() - baselineTime) > (MILLI_IN_SECONDS * 2)){
		        	continueOrNot = false;
		        }
		    }
        }catch(Exception e){
    		e.printStackTrace();
    	}

	}

}
