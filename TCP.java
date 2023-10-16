

class TCP extends Protocol{

	ByteUtil sourcePort = null;
	ByteUtil destinationPort = null;
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



	public TCP(ByteUtil data){
		super("TCP", data);

		this.parseData();
	}


	private void parseData(){
		this.sourcePort = this.data.readBytes(2);
		this.destinationPort = this.data.readBytes(2);
		this.sequenceNumber = this.data.readBytes(4);
		this.ackNumber = this.data.readBytes(4);
		System.out.println("Seq number ="+this.sequenceNumber.toInt());
		byte temp = this.data.readBytes(1).getData()[0];
		this.dataOffset = (temp >> 4) & 0x0F; // shift the 4 highest bits 4x to the right and get an int
		this.dataOffset *= 4; // get the offset in BYTES
		System.out.println("Offset = "+this.dataOffset);
		this.flags = this.data.readBytes(1);
		temp = flags.getData()[0];
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

		ByteUtil data = this.data.getRemainingBytes();


	}


	public String toString(){
		String out = "";
		out += this.protocolName+"\n";
		
		out += "Source port: "+this.sourcePort.toInt()+"\n";
		out += "Destination port: "+this.destinationPort.toInt()+"\n";
		out += "\n";
		return out;
	}


}
