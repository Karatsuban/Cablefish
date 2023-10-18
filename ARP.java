

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
		if (payload.length != 0)
			System.out.println("Payload (size = "+payload.length+") : "+payload);
	}

    public String toString(){
        String out = "";
        out += this.protocolName+"\n";
		out += "Hardware address length: "+this.hardwareAddressLength+"\n";
		out += "Protocol address length: "+this.protocolAddressLength+"\n";
		out += "Operation: "+this.operation;
		if (this.operation == 1)
			out += " (request)\n";
		else
			out += " (reply)\n";
		out += "Sender hardware address: "+this.senderHardwareAddress.asMacAddr()+"\n";
		out += "Sender protocol address: "+this.senderProtocolAddress.asIPv4Addr()+"\n"; // HERE TODO : does this protocol address chages according to the protocol ? isn't IPv4 the only protocol used for this ?
		out += "Target hardware address: "+this.targetHardwareAddress.asMacAddr()+"\n";
		out += "Target protocol address: "+this.targetProtocolAddress.asIPv4Addr()+"\n";
		out += "\n";
        return out;
    }


}
