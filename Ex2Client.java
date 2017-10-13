// ============================================================================
// file: Ex2Client.java
// ============================================================================
// Programmer: David Shin
// Date: 10/12/2017
// Class: CS 380 ("Computer Networks")
// Time: TR 3:00 - 4:50pm
// Instructor: Mr. Davarpanah
// Exercise : 2
//
// Description:
//      This program connects to a remote server, and recieves 200 half-byte
//      hexadecimal values, combines each half-byte to 100 bytes, generates a
//      CRC32 error code, and receives validation from server.
//
// ============================================================================
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Ex2Client {
    static Socket socket = null;
    static byte[] sByte = new byte[1];
    static byte[] halfByteArray = new byte[200];
    static byte[] ByteArray = new byte[100];
    
    public static void main(String[] args) {
	try {
	    //successful connection to the server will result in a valid
	    //socket used to communicate
	    socket = new Socket("18.221.102.182", 38102);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	//Read half-bytes from server
	try {
	    
	    System.out.print("Bytes from server: ");
	    for(int i = 0; i < 200; i++){
		socket.getInputStream().read(sByte);
		halfByteArray[i] = sByte[0];
		if(i % 20 == 0)
		    System.out.printf("\n  ");
		System.out.printf("%01X", halfByteArray[i]);
	    }
	    System.out.println();
	} catch (IOException e2) {
	    e2.printStackTrace();
	}

	//Put the half-bytes together
	for(int i = 0; i < 100; i++){
	    byte firstHalf = (byte) halfByteArray[i * 2];
	    byte secondHalf = (byte) halfByteArray[i * 2 + 1];
	    ByteArray[i] = (byte) ((byte) (firstHalf << 4) + (secondHalf));
	}

	//Generate the CRC
	CRC32 crc = new CRC32();
	crc.update(ByteArray);

	ByteBuffer b = ByteBuffer.allocate(4);
	b.putInt((int) crc.getValue());

	//Send CRC code
	try {
	    System.out.print("Generated CRC32: ");
	    for(int i = 0; i < 4; i++){
		socket.getOutputStream().write(b.array()[i]);
		System.out.printf("%02X",b.array()[i]);
	    }
	    System.out.println();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	//Get the server Response
	try {
	    socket.getInputStream().read(halfByteArray);
	    byte indicator = 1;
	   
	    if(halfByteArray[0] == indicator)
		System.out.println("Response good.");
	    else
	       System.out.println("Response bad.");
	    
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	//Closing the socket
	try {
	    socket.close();
	    System.out.println("Disconnected from server");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
