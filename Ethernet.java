
class Ethernet extends Protocol{

	private ByteUtil sourceAddr = null;
	private ByteUtil destinationAddr = null;
	private ByteUtil etherType = null;
	private ByteUtil encap_data = null;


	public Ethernet(byte[] data){

		super("Ethernet", data);

		this.parseData();


	}

	// FUNCTIONS

	private void parseData(){
		this.sourceAddr = this.data.readBytes(6);
		this.destinationAddr = this.data.readBytes(6);
		this.etherType = this.data.readBytes(2);
		this.encap_data = this.data.getRemainingBytes();


		if (this.etherType.equals("0800")){
				this.setEncapsulated(new IPv4(this.encap_data));

		}else if (this.etherType.equals("0806")){
				this.setEncapsulated(new ARP(this.encap_data));

		}else if (this.etherType.equals("86dd")){
				this.setEncapsulated(new IPv6(this.encap_data));

		}else{
				System.out.println("Unimplemented protocol "+this.etherType);
		}	

	}


	public String toString(){
		String out = "";

		out += this.gs()+"Dest address: "+this.sourceAddr.asMacAddr()+"\n";
		out += this.gs()+"Src address: "+this.destinationAddr.asMacAddr()+"\n";

		if (this.encapsulated != null){
			out += this.gs()+"Encapsulated protocol:\n";
			out += this.encapsulated.toString(this.indent+1);
		}
		out += "\n";

		return out;
	}

}
