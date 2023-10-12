

class ARP extends Protocol{

	public ARP(byte[] data){
		super("ARP", data);
	}

	public ARP(ByteUtil data){
		super("ARP", data);
	}

    public String toString(){
        String out = "";
        out += this.protocolName+"\n";
        out += data;
        return out;
    }


}
