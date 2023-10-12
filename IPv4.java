

class IPv4 extends Protocol{


	public IPv4(byte[] data){
		super("IPv4", data);
	}

	public IPv4(ByteUtil data){
		super("IPv4", data);
	}

	public String toString(){
		String out = "";
		out += this.protocolName+"\n";
		out += data;
		return out;
	}

}
