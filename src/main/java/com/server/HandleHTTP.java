package com.server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

class HandleHttp extends Thread{


private final static int BYTES_IN_MEGABYTES = 1048576;
Socket client;
BufferedReader is;
DataOutputStream os;
InputStream ins;

    //Constructor
    public HandleHttp(Socket client) { 
        this.client = client;
        try {
            ins = client.getInputStream();
            is = new BufferedReader(new InputStreamReader
            (ins));
            os = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.out.println("Exception: "+e.getMessage());
        }
        this.start();
   }
   //The main method that breaks down all of the client input string (GET request, etc.)
    public void run() {
        try {

            String request = is.readLine();
            
            if (request.contains("GET"))
                shipDocument(os);
            else if (request.contains("POST"))
                retrieveDocuments(ins);
            // BufferedReader rd = new BufferedReader(is));
            //System.out.println("here...?");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //Sends the Selected document to the Client
    public static void shipDocument(DataOutputStream out) throws Exception {
        double currentBytes = 0.0;
        int i = 0;

        try {
            //System.out.println((int)f.length());

            ByteBuffer buf = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
            byte[] b = new byte[BYTES_IN_MEGABYTES];
            new Random().nextBytes(b);
            buf.put(b);
            buf.flip();
            out.writeBytes("HTTP/1.0 200 OK\r\n");
            out.writeBytes("Content Length: " + BYTES_IN_MEGABYTES * 100 + "bytes\r\n");
            out.writeBytes("File Contents: \r\n");
            while (currentBytes <= (BYTES_IN_MEGABYTES * 100)){
                out.write(buf.array(), 0, BYTES_IN_MEGABYTES);
                buf.clear();
                buf.put(b);
                buf.flip();
                i++;
                currentBytes = (i * BYTES_IN_MEGABYTES);
            }
            out.writeBytes("Content-Type: random/bytes\r\n\r\n");
            out.flush();
        } catch (Exception e) {
            out.writeBytes("<html><head><title>error</title></head><body>\r\n\r\n");
            out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
            out.writeBytes("Content-Type: text/html\r\n\r\n");
            out.writeBytes("</body></html>");
            out.flush();
        } finally {
            out.close();
        }
    }

    public static void retrieveDocuments(InputStream ins){
        int i = 0;
        byte[] b = new byte[BYTES_IN_MEGABYTES];
        ByteBuffer buf = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
        try {
            while (ins.available() != 0){
                ins.read(b);
                buf.put(b);
                Arrays.fill(b, (byte)0);
                buf.clear();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}