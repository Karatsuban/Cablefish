
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

	protected Protocol getProtocol(ProtocolName name){
		// return itsels if it matches name
		// else recursively calls this method on its encapsulated
		// return null if it has no encapsulated protocol
		Protocol prot;
		if (this.protocolName.equals(name.getName())){
			prot = this;
		}else{
			if (this.encapsulated == null)
				prot = null;
			else
				prot = this.encapsulated.getProtocol(name);
		}

		return prot;
	}


	public String toString(int indent){
		this.indent = indent;
		return this.toString();
	}

	protected String gs(){
		return "  ".repeat(this.indent);
	}

    public String toString(){
        String out = this.gs()+"It's "+this.protocolName;
        return out;
    }

	public boolean hasProtocol(ProtocolName name){
		// return true if the protocol itself has this name
		// or if it encapsulates a protocol name like this
		boolean out = false;
		if (this.protocolName.equals(name.getName()) || name.equals(ProtocolName.ALL)){
			out = true;
		}
		else{
			if (this.encapsulated != null){
				out = this.encapsulated.hasProtocol(name);
			}else{
				out = false;
			}
		}
		return out;
	}
}
