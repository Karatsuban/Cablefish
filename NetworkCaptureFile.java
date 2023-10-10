import java.io.*;
import java.util.HexFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.math.BigInteger;
import java.nio.ByteBuffer;


class NetworkCaptureFile{

	String fileName = null;
	File file = null;
	FileInputStream fis = null;
	boolean isLittleEndian;
	int f;
	int BCS;
	Packet[] packets = null;

	public NetworkCaptureFile(String fileName){
		this.fileName = fileName;
		

		if (this.openFile() != 0){
			System.exit(1);
		}

		this.encodingType();
		this.parseHeader();
		this.parsePackets();
	}


	private void encodingType(){
		byte[] magicNumber = new byte[4];

		try{
			this.fis.read(magicNumber, 0, 4);
		}catch (IOException e){
			System.out.printf(e.getMessage());
		}


		byte[] ref = HexFormat.of().parseHex("D4C3B2A1");

		/*
		for (int i=0; i<4; i++){
			System.out.println(magicNumber[i]+" "+ref[i]);
		}
		*/

		if (Arrays.equals(magicNumber, ref)){
			this.isLittleEndian = false;
		}else{
			this.isLittleEndian = true;
		}

	}


	private int openFile(){
		int ret_val = 0;
		this.file = new File(this.fileName);

		try{
			this.fis = new FileInputStream(this.file);
		}catch (FileNotFoundException e){
			System.out.println("No file found:"+e.getMessage());
			ret_val = -1;
		}
		return ret_val;
	}


	private byte[] reverseByteArray(byte[] b){
		byte ret[] = new byte[b.length];
		for(int i=0; i<b.length; i++){
			ret[b.length-i-1] = b[i];
		}
		return ret;
	}


	private byte[] readBytesFromFile(int len){
		return this.readBytesFromFile(len, true);
	}

	private byte[] readBytesFromFile(int len, boolean useEndian){
		// read len bytes from the file
		// if useEndian is True, reverse (or not) the array according to 'isLittleEndian' value
		byte[] ret = new byte[len];
		int read = 0;

		try{
			read = this.fis.read(ret, 0, len);
		}catch (IOException e){
			System.out.println(e.getMessage());
			ret = null; // End of file
		}

		if (read <= 0){
			ret = null;
		}

		if (ret != null){
		
			if (!this.isLittleEndian && useEndian){
				ret = this.reverseByteArray(ret);
			}
		}

		return ret;
	}


	private void skipBytes(int len){
		try{
			this.fis.skip(len);
		}catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	}


	private void printByteArray(byte[] b){
		for (int i=0; i<b.length; i++){
			System.out.print(b[i]+" ");
		}
		System.out.println();
	}



	private int bytesToInt(byte[] b){
		return ByteBuffer.wrap(b).getInt();
	}


	private int parseHeader(){
		int ret_val = 0;
		int ret;

		byte[] majorVersion = this.readBytesFromFile(2); // read 16 bits
		byte[] minorVersion = this.readBytesFromFile(2); // read 16 bits

		this.skipBytes(8); // skip 64 bits

		int snapLen = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
		byte[] linkType_arr = this.readBytesFromFile(4); // read 32 bits
		int linkType = this.bytesToInt(linkType_arr);

		this.BCS = linkType_arr[0] & 0xe;
		this.f = linkType_arr[0] & 0x1;

		return ret_val;
	}

	private int parsePackets(){
		int ret_val = 0;

		boolean isOver = false;

		byte[] timeStampFirst_arr;
		int timeStampFirst, timeStampSecond;
		int capturedPacketLength, originalPacketLength;
		byte[] data = null;


		while (!isOver){

			timeStampFirst_arr = this.readBytesFromFile(4); // read 32 bits

			if (timeStampFirst_arr != null){
				timeStampFirst = this.bytesToInt(timeStampFirst_arr); // read 32 bits
				timeStampSecond = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
				capturedPacketLength = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
				originalPacketLength = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
	
				data = this.readBytesFromFile(capturedPacketLength, false); // read capturedPacketLength bytes

				Packet packet = new Packet(this.isLittleEndian,
											timeStampFirst, 
											timeStampSecond,
											capturedPacketLength,
											originalPacketLength, 
											data);

				// TODO : ne pas parser les data avec isLittleEndian !
				// -> add an 'ignore' parameter to ignore this conversion in readBytes

				System.out.println(packet);
			}else{
				isOver = true;
			}
		}

		return ret_val;
	}


}
