

class ARP extends Protocol{

	ByteUtil hardwareType = null;
	ByteUtil protocolType = null;
	short hardwareAddressLength;
	short protocolAddressLength;
	short operation;
	ByteUtil senderHardwareAddress = null;
	ByteUtil senderProtocolAddress = null;
	ByteUtil targetHardwareAddress = null;
	ByteUtil targetProtocolAddress = null;
	private ByteUtil payload = null;

	public ARP(byte[] data){
		super("ARP", data);

		this.parseData();
	}

	public ARP(ByteUtil data){
		super("ARP", data);

		this.parseData();
	}


	private void parseData(){
		this.hardwareType = this.data.readBytes(2);
		this.protocolType = this.data.readBytes(2);
		this.hardwareAddressLength = this.data.readBytes(1).toShort();
		this.protocolAddressLength = this.data.readBytes(1).toShort();
		this.operation = this.data.readBytes(2).toShort(); // no risk of overflowing here
		this.senderHardwareAddress = this.data.readBytes(6);
		this.senderProtocolAddress = this.data.readBytes(4);
		this.targetHardwareAddress = this.data.readBytes(6);
		this.targetProtocolAddress = this.data.readBytes(4);

		ByteUtil payload = this.data.getRemainingBytes();
	}

    public String toString(){
        String out = "";
        out += this.gs()+this.protocolName+"\n";
		out += this.gs()+"Hardware address length: "+this.hardwareAddressLength+"\n";
		out += this.gs()+"Protocol address length: "+this.protocolAddressLength+"\n";
		out += this.gs()+"Operation: "+this.operation;
		if (this.operation == 1)
			out += " (request)\n";
		else
			out += " (reply)\n";
		out += this.gs()+"Sender hardware address: "+this.senderHardwareAddress.asMacAddr()+"\n";
		out += this.gs()+"Sender protocol address: "+this.senderProtocolAddress.asIPv4Addr()+"\n"; // HERE TODO : does this protocol address chages according to the protocol ? isn't IPv4 the only protocol used for this ?
		out += this.gs()+"Target hardware address: "+this.targetHardwareAddress.asMacAddr()+"\n";
		out += this.gs()+"Target protocol address: "+this.targetProtocolAddress.asIPv4Addr()+"\n";

		if (this.payload != null)
		{
			if (this.payload.length != 0)
				out += this.payload+"\n";
		}
		out += "\n";
        return out;
    }


}
