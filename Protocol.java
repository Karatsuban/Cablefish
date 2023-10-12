
class Protocol{

	protected String protocolName = null; // name of the protocol
	protected Protocol encapsulated = null; // protocol object encapsulated in this one
	protected ByteUtil data = null;

	protected Protocol(String protocolName, byte[] data){
		this.protocolName = protocolName;
		this.data = new ByteUtil(data);
	}

	protected Protocol(String protocolName, ByteUtil data){
		this.protocolName = protocolName;
		this.data = data;
	}

	protected void setEncapsulated(Protocol encapsulated){
		this.encapsulated = encapsulated;
	}

    public String toString(){
        String out = "It's "+this.protocolName;
        return out;
    }


}
