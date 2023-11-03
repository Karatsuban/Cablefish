import java.io.*;
import java.util.HexFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

class ParsePcapFile{

	String fileName = null;
	File file = null;
	FileInputStream fis = null;
	boolean isLittleEndian;
	int f;
	int BCS;
	ArrayList<Frame> packets = new ArrayList<Frame>();


	public ParsePcapFile(String fileName){
		this.fileName = fileName;
		

		if (this.openFile() != 0){
			System.exit(1);
		}

		this.encodingType();
		this.parseHeader();
		this.parsePackets();
	}


	private void encodingType(){
		ByteUtil magicNumber = null;
		byte[] temp = new byte[4];
		int read;

		try{
			read = this.fis.read(temp, 0, 4);
			magicNumber = new ByteUtil(temp);
		}catch (IOException e){
			System.out.printf(e.getMessage());
		}


		if (magicNumber.equals("D4C3B2A1")){
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

		int frameNb = 1;

		while (!isOver){

			timeStampFirst_arr = this.readBytesFromFile(4); // read 32 bits

			if (timeStampFirst_arr != null){


				timeStampFirst = this.bytesToInt(timeStampFirst_arr); // read 32 bits
				timeStampSecond = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
				capturedPacketLength = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
				originalPacketLength = this.bytesToInt(this.readBytesFromFile(4)); // read 32 bits
	
				data = this.readBytesFromFile(capturedPacketLength, false); // read capturedPacketLength bytes

				Frame link = new Frame(this.isLittleEndian,
											timeStampFirst, 
											timeStampSecond,
											capturedPacketLength,
											originalPacketLength, 
											data,
											frameNb);


				this.packets.add(link);

				frameNb += 1;

			}else{
				isOver = true;
			}
		}

		return ret_val;
	}



	public ArrayList<Frame> getProtocolPackets(ProtocolName name){
		// return all the packets with the name
		ArrayList<Frame> retPackets = new ArrayList<Frame>();
		for (Frame packet: this.packets){
			if (packet.hasProtocol(name)){
				retPackets.add(packet);
			}
		}
		return retPackets;
	}


	private void followTCPStream(){
		ProtocolName prot = ProtocolName.TCP;
		ArrayList<Frame> TCPPackets = this.getProtocolPackets(prot);
		if(TCPPackets.size() == 0){
			System.out.println("No '"+prot.getName()+"' packet found!");
			return;
		}
		String tcpStream = ""; // content of the exchange
		TCP temp = (TCP)TCPPackets.get(0).getProtocol(prot); // sender
		int dest_port = temp.getDestPort();
		int sender_port = temp.getSrcPort();
		long seq_nb = temp.getSequenceNumber();
		long ack_nb = temp.getAckNumber(); // should be 0

		System.out.println(seq_nb + " " + ack_nb);

		int tcp_count = 0;
		for (Frame packet: TCPPackets){
			System.out.println(packet.getProtocol(prot).toString(0));
			// verify that the packet if from the sender OR the receiver
			// verify the seq_nb and ack_nb match expected
			// check the flags
			// if PUSH is set : get the data
		}
	}


	public void followStream(ProtocolName prot){
		System.out.println("Following "+prot.getName()+"\n");
		switch (prot){
			case TCP:
				this.followTCPStream();
				break;
			default:
				System.out.println("Cannot follow '"+prot.getName()+"' stream!");
		}


	}



	public String toString(int frame, ProtocolName filter)
	{
		// Display only the specified frame if frame > 0 else all
		// Display only the specified protocol if filter != ProtocolName.ALL
		String out = "";
		Frame packet = null;
		if(frame >= this.packets.size()) {
			out = "Error: specified frame out of range";
		}else{
			if (frame < 0) {
				for (int i=0; i<this.packets.size(); i++){
					packet = this.packets.get(i);
					if (packet.hasProtocol(filter)){
						out += "Frame "+(i+1)+":\n";
						out += packet.toString(0);
					}
				}
			}else{
				packet = this.packets.get(frame-1);
				if (packet.hasProtocol(filter)){
					out += "Frame "+frame+":\n";
					out += packet.toString(0);
				}
			}
		}
		return out;
	}

}
