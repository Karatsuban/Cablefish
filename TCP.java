

class TCP extends Protocol{

	int sourcePort;
	int destinationPort;
	ByteUtil sequenceNumber = null;
	ByteUtil ackNumber = null;
	int dataOffset;
	boolean cwr;
	boolean ece;
	boolean urg;
	boolean ack;
	boolean psh;
	boolean rst;
	boolean syn;
	boolean fin;
	ByteUtil flags = null;
	ByteUtil windowSize = null;
	ByteUtil checksum = null;
	ByteUtil urgentPointer = null;
	ByteUtil options = null;
	ByteUtil payload = null;


	public TCP(ByteUtil data){
		super("TCP", data);

		this.parseData();
	}


	private void parseData(){
		this.sourcePort = this.data.readBytes(2).toInt();
		this.destinationPort = this.data.readBytes(2).toInt();
		this.sequenceNumber = this.data.readBytes(4);
		this.ackNumber = this.data.readBytes(4);
		byte temp = this.data.readBytes(1).getData()[0];
		this.dataOffset = (temp >> 4) & 0x0F; // shift the 4 highest bits 4x to the right and get an int
		this.dataOffset *= 4; // get the offset in BYTES
		this.flags = this.data.readBytes(1);
		cwr = this.flags.getBit(7) == 1;
		ece = this.flags.getBit(6) == 1;
		urg = this.flags.getBit(5) == 1;
		ack = this.flags.getBit(4) == 1;
		psh = this.flags.getBit(3) == 1;
		rst = this.flags.getBit(2) == 1;
		syn = this.flags.getBit(1) == 1;
		fin = this.flags.getBit(0) == 1;
		this.windowSize = this.data.readBytes(2);
		this.checksum = this.data.readBytes(2);
		this.urgentPointer = this.data.readBytes(2);

		if (this.dataOffset > 20){
			this.options = this.data.readBytes(this.dataOffset-20);
		}


		this.payload = this.data.getRemainingBytes();


		if (this.destinationPort == 80 || this.destinationPort == 473 ||
			this.sourcePort == 80 || this.sourcePort == 473)
		{
			if (this.payload.length != 0)
			{
				if (!this.payload.isZeroes()){
					this.setEncapsulated(new HTTPv1_1(this.payload));
				}
			}
		}

	}

	private String getFlagsRepr(){
		String[] flags = {"FIN", "SYN", "RST", "PSH", "ACK", "URG", "ECE", "CWR"};
		String out = "(";
		for (int i=7; i>=0; i--){
			if (this.flags.getBit(i) == 1)
				out += flags[i]+" ";
		}
		out += ")";
		return out;
	}


	public String toString(){
		String out = "";
		out += this.gs()+this.protocolName+"\n";
		
		out += this.gs()+"Source port: "+this.sourcePort+"\n";
		out += this.gs()+"Destination port: "+this.destinationPort+"\n";
		out += this.gs()+"Sequence number: "+this.sequenceNumber.toLong()+"\n";
		out += this.gs()+"Ack number: "+this.ackNumber.toLong()+"\n";
		out += this.gs()+"Data offset: "+this.dataOffset+"\n";
		out += this.gs()+"Flags: "+this.flags+" "+this.getFlagsRepr()+"\n";
		out += this.gs()+"Window: "+this.windowSize.toLong()+"\n";

		if (this.options != null)
		{
			if (this.options.length != 0)
				out += this.gs()+"There are options: "+this.options+"\n";
		}

		out += this.gs()+"PAYLOAD SIZE: "+this.payload.length+"\n";

		if (this.encapsulated != null)
		{
			out += this.gs()+"Encapsulated protocol:\n";
			out += "Payload size: "+this.payload.length+"\n";
			out += this.encapsulated.toString(this.indent+1)+"\n";
			out += this.encapsulated.protocolName+"\n";
		}
		


		out += "\n";
		return out;
	}


}
