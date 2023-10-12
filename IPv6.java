

class IPv6 extends Protocol{

	public IPv6(byte[] data){
		super("IPv6", data);
	}

	public IPv6(ByteUtil data){
		super("IPv6", data);
	}

    public String toString(){
        String out = "";
        out += this.protocolName+"\n";
        out += data;
        return out;
    }
	

}
