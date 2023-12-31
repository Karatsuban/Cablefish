

class TCP extends Protocol{

	int sourcePort;
	int destinationPort;
	private long sequenceNumber;
	private long ackNumber;
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

	// GETTERS

	public long getAckNumber(){
		return this.ackNumber;
	}

	public long getSequenceNumber(){
		return this.sequenceNumber;
	}

	public int getDestPort(){
		return this.destinationPort;
	}

	public int getSrcPort(){
		return this.sourcePort;
	}


	// PARSER

	private void parseData(){
		this.sourcePort = this.data.readBytes(2).toInt();
		this.destinationPort = this.data.readBytes(2).toInt();
		this.sequenceNumber = this.data.readBytes(4).toLong();
		this.ackNumber = this.data.readBytes(4).toLong();
		byte temp = this.data.readBytes(1).getData()[0];
		this.dataOffset = (temp >> 4) & 0x0F; // shift the 4 highest bits 4x to the right and get an int
		this.dataOffset *= 4; // get the offset in BYTES
		this.flags = this.data.readBytes(1);
		cwr = this.flags.getBit(0) == 1;
		ece = this.flags.getBit(1) == 1;
		urg = this.flags.getBit(2) == 1;
		ack = this.flags.getBit(3) == 1;
		psh = this.flags.getBit(4) == 1;
		rst = this.flags.getBit(5) == 1;
		syn = this.flags.getBit(6) == 1;
		fin = this.flags.getBit(7) == 1;
		this.windowSize = this.data.readBytes(2);
		this.checksum = this.data.readBytes(2);
		this.urgentPointer = this.data.readBytes(2);

		if (this.dataOffset > 20){
			this.options = this.data.readBytes(this.dataOffset-20);
			this.parseOption();
		}


		this.payload = this.data.getRemainingBytes();
		if (this.payload != null){
			if (!this.payload.isZeroes()){
				this.parsePayload();
			}
		}


	}

	private void parsePayload(){

		ByteUtil firstBytes = this.payload.getBytes(5);

		//System.out.println("#"+firstBytes.toAlphaNum()+"#");

		switch (firstBytes.toAlphaNum()){
			case "GET /":
			case "HTTP/":
				//System.out.println("THIS IS HTTP");
				this.setEncapsulated(new HTTPv1_1(this.payload));
			default:
		}
	}


	private void parseOption(){
		short optionKind;
		short optionLength;
		ByteUtil optionData = null;

		while (this.options.getRemainingLength() != 0){

			optionKind = this.options.readBytes(1).toShort();
			switch (optionKind){
				case 0:
					// End of options list
					break;
				case 1:
					// No operations
					break;
				case 2:
					// Maximum segment size
					optionLength = this.options.readBytes(1).toShort();
					break;
				case 3:
					// Window scale
					optionLength = this.options.readBytes(1).toShort();
					break;
				case 4:
					// Selective ack permitted
					optionLength = this.options.readBytes(1).toShort();
					break;
				case 5:
					// SACK
					break;
				case 8:
					// Timestamp and echo or prev timestamp
				default:
			}

		}

	}


	private String getFlagsRepr(){
		String[] flags = {"CWR", "ECE", "URG", "ACK", "PSH", "RST", "FIN"};
		String out = "(";
		for (int i=0; i<7; i++){
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
		out += this.gs()+"Sequence number: "+this.sequenceNumber+"\n";
		out += this.gs()+"Ack number: "+this.ackNumber+"\n";
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
			out += this.encapsulated.toString(this.indent+1)+"\n";
		}
		


		out += "\n";
		return out;
	}


}
