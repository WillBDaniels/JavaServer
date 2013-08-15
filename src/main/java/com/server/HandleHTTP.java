package com.server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

/**
* This class is for handling the specific Http protocol, both POST and GET. 
* It is initialized with only a client socket, as we need to process the input stream from it
* Currently it ONLY checks for the first header to contain the word either GET or POST. if neither are encountered, 
* it just closes the connecction. 
*
* @author William Daniels
* @version 1.1
*/
class HandleHttp extends Thread{

//The number of bytes in a megabyte
private final static int BYTES_IN_MEGABYTES = 1048576;
private Socket client;
private BufferedReader is;
private DataOutputStream os;
private InputStream ins;
private double contentLength = 0.0;
public Map<String, String> headerKeyPair = new HashMap<String,String>();


    /**
    * The only constructor, requires you have a client.
    *
    * @param Socket "client" the client that is connected to the http Port.
    */
    public HandleHttp(Socket client) { 
        this.client = client;
        try {
            //Build the necessary streams from the client. 
            ins = client.getInputStream();
            is = new BufferedReader(new InputStreamReader
            (ins));
            os = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.out.println("Exception: "+e.getMessage());
        }
        this.start();
   }
   /**
    * The main method that breaks down all of the client input string (GET request, etc.)
    */ 
    public void run() {
        System.out.println("Checking HTTP Request...");
        try {

            String firstLine = is.readLine();
            contentLength = parseHeaders(is);
            if (firstLine.contains("GET /ping")){
                System.out.println("Handling HTTP Ping request...");
                pingTest(os);
            }
            else if (firstLine.contains("GET")){
                System.out.println("Handling HTTP GET request...");
                dataDump(os);
            }
            else if (firstLine.contains("POST")){
                System.out.println("Handling HTTP POST request...");
                blackHole(ins, os);
            }
            else {
                System.out.println("HTTP request Verb Unsupported...");
                os.writeBytes("<html><head><title>Verb Unsupported</title></head><body>\r\n\r\n");
                os.writeBytes("HTTP/1.0 400 BadRequst" + "\r\n");
                os.writeBytes("Content-Type: text/html\r\n\r\n");
                os.writeBytes("</body></html>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Don't forget to cleanup!
        finally{
            try{
                is.close();
                client.close();
                os.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }


    /**
    * This method parses up the headers sent by the client and places them into a map. 
    *
    *@param inStream a BufferedReader used to read the headers line at a time until you reach the blank line
    */
    private double parseHeaders(BufferedReader inStream){
        try {
            String temp = inStream.readLine();
            while ((temp.length()) > 2){
                //Parse up all of the headers and put them in the map
                headerKeyPair.put(temp.substring(0, temp.indexOf(":")).trim(), temp.substring(temp.indexOf(":") + 1, temp.length()));
                System.out.println(temp);
                temp = inStream.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        if (headerKeyPair.containsKey("Content-Length"))
            return Double.parseDouble(headerKeyPair.get("Content-Length"));
        else
            return 0.0;
    }
    /**
     * Dumps 100 MB of data onto the client as fast as possible
     *
     * @param out a DataOutputStream that represents the outgoing connection
     *       to the client. 
     * @throws IOException to catch any read/write errors. 
     */
    public void dataDump(DataOutputStream out) throws IOException {
        double currentBytes = 0.0;
        int i = 0;
        try {
            //make a byte buffer of the proper size, fill it with a random byte array.
            ByteBuffer buf = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
            byte[] b = new byte[BYTES_IN_MEGABYTES];
            new Random().nextBytes(b);
            buf.put(b);
            //don't forget to flip it for writing!
            buf.flip();
            out.writeBytes("HTTP/1.0 200 OK\r\n");
            out.writeBytes("Content Length: " + BYTES_IN_MEGABYTES * 100 + "bytes\r\n");
            out.writeBytes("File Contents: \r\n");
            //Keep writing the same buffer over and over until we've written 100 MB
            double size = 0;
            if (headerKeyPair.get("Size") != null){
                size = Double.parseDouble(headerKeyPair.get("Size"));
            }
            if (size != 0)
                System.out.println("Writing " + (size/BYTES_IN_MEGABYTES) + " MB to client");
            else
                System.out.println("Writing the default: " + (BYTES_IN_MEGABYTES/BYTES_IN_MEGABYTES) + " MB to the client");
            //This defaults to writing at least 1 MB to the client, even if the 'size' header comes back 0.
            while (currentBytes <= (size)){
                out.write(buf.array(), 0, BYTES_IN_MEGABYTES);
                buf.clear();
                buf.put(b);
                buf.flip();
                i++;
                currentBytes = (i * BYTES_IN_MEGABYTES);
            }
            System.out.println("Finished writing data via HTTP to the client.");
            out.writeBytes("Content-Type: random/bytes\r\n\r\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Sending error response");
            //write a standard error message out if something goes wrong, IE: IOException
            //or the client closes the connection, etc. etc. 
            out.writeBytes("<html><head><title>error</title></head><body>\r\n\r\n");
            out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
            out.writeBytes("Content-Type: text/html\r\n\r\n");
            out.writeBytes("</body></html>");
        } finally {
            out.close();
        }
    }

    /**
    * This method acts as the 'black hole' when a client POSTS to the server
    * It's assumed that the client will send as much data as it can, as fast as 
    * possible. it simple overwrites the old data and does nothing with it. EX: 
    * 'black hole'.
    *
    *@param ins the basic client inputStream, to allow single byte reading. 
    */
    public void blackHole(InputStream ins, DataOutputStream out) throws IOException{
        int bytesRead = 0;
        DataInputStream myStream = new DataInputStream(ins);
        //make the byteBuffer and back it with a large enough byte array.
        byte[] b = new byte[BYTES_IN_MEGABYTES];
        ByteBuffer buf = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
        try {

            System.out.println("Attempting to read as much information from the client as possible.");
            long startTime = System.currentTimeMillis();
            //while the input stream has something available, keep filling, emptying and re-filling. 
            while (bytesRead < contentLength){
                if(myStream.available() > 0) {
                    int read = myStream.read(b);
                    if (read == -1)
                    {
                        System.out.println("Read -1, end of stream");
                        break;
                    }
                    bytesRead += read;
                    System.out.println("Read " + read + " this time (" + bytesRead + " total so far)");
                    buf.put(b);
                    Arrays.fill(b, (byte)0);
                    buf.clear();
                }
                if (System.currentTimeMillis() - startTime > 60000) {
                    System.out.println("timing out after reading " + bytesRead + " bytes");
                    break;
                }
            }
            out.writeBytes("HTTP/1.0 200 OK\r\n");
            //out.writeBytes("Bytes Read: " + bytesRead + "bytes\r\n");
            System.out.println("Done receiving data");
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Sending error response");
            //Write error if there's an IOException
            out.writeBytes("<html><head><title>error</title></head><body>\r\n\r\n");
            out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
            out.writeBytes("Content-Type: text/html\r\n\r\n");
            out.writeBytes("</body></html>");
        }
        finally{
            try{
                ins.close();
                myStream.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    /**
    * This method is specifically for testing the ping from a client.
    *
    *@param out a DataOutputStream that allows the server to write bytes to the client.  
    */
    public void pingTest(DataOutputStream out) throws IOException
    {
        try
        {
            out.writeBytes("HTTP/1.0 200 OK\r\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();

            //Write error if there's an IOException
            out.writeBytes("<html><head><title>error</title></head><body>\r\n\r\n");
            out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
            out.writeBytes("Content-Type: text/html\r\n\r\n");
            out.writeBytes("</body></html>");
        }
    }
}