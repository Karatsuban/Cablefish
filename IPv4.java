

class IPv4 extends Protocol{


	private int IHL;
	private ByteUtil totalLength = null;
	private ByteUtil identification =null;
	private ByteUtil fragmentOffset;
	private boolean dontFragment;
	private boolean moreFragments;
	private short ttl;
	private ByteUtil protocol = null;
	private ByteUtil headerChecksum = null;
	private ByteUtil sourceIPAddr = null;
	private ByteUtil destinationIPAddr = null;
	private ByteUtil options = null;


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
		ByteUtil flagsFragmentOffset = this.data.readBytes(2); // Flags + fragment offset
		this.dontFragment = flagsFragmentOffset.getBit(6) == 1;
		this.moreFragments = flagsFragmentOffset.getBit(5) == 1;
		this.fragmentOffset = null; // TODO : calculate it properly !
		this.ttl = this.data.readBytes(1).toShort();
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
		out += this.gs()+this.protocolName+"\n";
		out += this.gs()+"Flags: ";
		if (this.dontFragment)
			out += "Don't fragment\n";
		if (this.moreFragments)
			out += "More fragments\n";
		if (!this.dontFragment && !this.moreFragments)
			out += "(No flags)\n";
		out += this.gs()+"TTL: "+this.ttl+"\n";
		out += this.gs()+"Source IP address: "+this.sourceIPAddr.asIPv4Addr()+"\n";
		out += this.gs()+"Destination IP address: "+this.destinationIPAddr.asIPv4Addr()+"\n";


		if (this.encapsulated != null){
			out += this.gs()+"Encapsulated protocol:\n";
			out += this.encapsulated.toString(this.indent+1);
		}

		out += "\n";

		return out;
	}

}
