
class Protocol{

	protected String protocolName = null; // name of the protocol
	protected Protocol encapsulated = null; // protocol object encapsulated in this one
	protected ByteUtil data = null;
	protected int indent = 0; // indent value when printing

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

	public String toString(int indent){
		this.indent = indent;
		return this.toString();
	}

	protected String gs(){
		return "  ".repeat(this.indent);
	}

    public String toString(){
        String out = "It's "+this.protocolName;
        return out;
    }


}
