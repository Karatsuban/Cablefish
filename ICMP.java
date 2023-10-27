

class ICMP extends Protocol{

	ByteUtil type = null;
	ByteUtil code = null;
	ByteUtil checksum = null;
	ByteUtil restHeader = null;


	public ICMP(ByteUtil data){
		super("ICMP", data);

		this.parseData();
	}


	private void parseData(){

		type = this.data.readBytes(1);
		code = this.data.readBytes(1);
		checksum = this.data.readBytes(2);
		restHeader = this.data.readBytes(4);

	}

	private String getControlMessage(){
		int type = this.type.getData()[0] & 0xF;
		String out = "";
		switch (type){
			case 0:
				out = "Echo reply";
				break;
			case 3:
				out = "Destination unreachable";
				break;
			case 5:
				out = "Redirect message";
				break;
			case 8:
				out = "Echo request (ping)";
				break;
			case 11:
				out = "Time exceeded";
				break;
			default:
				out = "#Control message not implemented#";
		};
		return out;
	}


	public String toString(){
		String out = "";
		out += this.gs()+this.protocolName+"\n";
		//out += "data: "+this.data;
		out += this.gs()+"Type: "+this.type+"("+this.getControlMessage()+")\n";

		out += "\n";
		return out;
	}


}
