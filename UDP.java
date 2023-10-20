

class UDP extends Protocol{

	public UDP(ByteUtil data){
		super("UDP", data);

		this.parseData();
	}


	private void parseData(){
		System.out.println("Parsing UDP...");
	}


	public String toString(){
		String out = "";
		out += this.protocolName+"\n";
		out += "data: "+this.data;
		out += "\n";
		return out;
	}


}
