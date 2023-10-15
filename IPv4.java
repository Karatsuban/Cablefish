

class IPv4 extends Protocol{


	int IHL;
	ByteUtil totalLength = null;
	ByteUtil identification =null;
	int flags;
	int fragmentOffset;
	ByteUtil ttl = null;
	ByteUtil protocol = null;
	ByteUtil headerChecksum = null;
	ByteUtil sourceIPAddr = null;
	ByteUtil destinationIPAddr = null;
	ByteUtil options = null;

	public IPv4(byte[] data){
		super("IPv4", data);
		
		this.parseData();

	}

	public IPv4(ByteUtil data){
		super("IPv4", data);

		this.parseData();
	}


	private void parseData(){
		byte[] versionIHL = this.data.readBytes(1).getData() ; // 8 bits
		this.IHL = (versionIHL[0] & 0xF) * 4 ; // internet header lenght in BYTES
		ByteUtil DSCP = this.data.readBytes(1);
		ByteUtil totalLength = this.data.readBytes(2); // total packet length (header+data)
		ByteUtil identification = this.data.readBytes(2);
		byte[] flagsFragmentOffset = this.data.readBytes(2).getData(); // Flags + fragment offset
		int flags = flagsFragmentOffset[0] & 0xe;
		// TODO  : do something about fragment offset !
		this.ttl = this.data.readBytes(1);
		this.protocol = this.data.readBytes(1);
		this.headerChecksum = this.data.readBytes(2);
		this.sourceIPAddr = this.data.readBytes(4);
		this.destinationIPAddr = this.data.readBytes(4);
		if (this.IHL > 20){
			this.options = this.data.readBytes(this.IHL-20); // read the rest of the header, ie the options
		}
		ByteUtil encapsulated_data = this.data.getRemainingBytes(); // get the packet's data

		if (this.protocol.equals("01")){
			this.setEncapsulated(new ICMP(encapsulated_data));
	
		}else if (this.protocol.equals("06")){
			this.setEncapsulated(new TCP(encapsulated_data));
	
		}else if (this.protocol.equals("11")){
			this.setEncapsulated(new UDP(encapsulated_data));

		}else{
			System.out.println("Unknown protocol : "+this.protocol);
		}
		


	}

	public String toString(){
		String out = "";
		out += this.protocolName+"\n";
		out += "Internet Header Lenghth : "+this.IHL+"\n";
		out += "Flags: "+this.flags+"\n";
		out += "TTL: "+this.ttl.toByte()+"\n";
		out += "Protocol: "+this.protocol+"\n";
		out += "source IP address: "+this.sourceIPAddr.asIPv4Addr()+"\n";
		out += "Destination IP address: "+this.destinationIPAddr.asIPv4Addr()+"\n";

		if (this.encapsulated != null){
			out += "Encapsulated protocol:\n";
			out += this.encapsulated.toString();
		}

		out += "\n";

		return out;
	}

}
