

class UDP extends Protocol{

	private int sourcePort;
	private int destPort;
	private int length;
	private ByteUtil payload;


	public UDP(ByteUtil data){
		super("UDP", data);

		this.parseData();
	}


	private void parseData(){
		this.sourcePort = this.data.readBytes(2).toInt();
		this.destPort = this.data.readBytes(2).toInt();
		this.length = this.data.readBytes(2).toInt(); // in bytes !
		this.data.skipBytes(2); // skip the checksum value

		if (this.length-2 > 0){
			this.payload = this.data.getRemainingBytes();

			if (this.payload.length >= 240){
				this.payload.skipBytes(236);
				boolean isDHCP = this.payload.getBytes(4).equals("63825363");
				this.payload.reset();
				if (isDHCP){
					this.setEncapsulated(new DHCP(this.payload));
				}
			}
		}
	}


	public String toString(){
		String out = "";
		out += this.gs()+this.protocolName+"\n";
		out += this.gs()+"Source port: "+this.sourcePort+"\n";
		out += this.gs()+"Destination port: "+this.destPort+"\n";
		if (this.payload != null){
			out += this.gs()+"len payload: "+this.payload.length+"\n";
		}

		if (this.encapsulated != null){
			out += this.gs()+"Encapsulated protocol:\n";
			out += this.encapsulated.toString(this.indent+1);
		}
		out += "\n";
		return out;
	}


}
