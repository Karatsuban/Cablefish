

class UDP extends Protocol{

	public UDP(ByteUtil data){
		super("UDP", data);

		this.parseData();
	}


	private void parseData(){
		//System.out.println("Parsing UDP...");
	}


	public String toString(){
		String out = "";
		out += this.gs()+this.protocolName+"\n";
		out += this.gs()+"data: "+this.data;
		out += "\n";
		return out;
	}


}
