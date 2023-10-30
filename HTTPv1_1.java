
class HTTPv1_1 extends Protocol{

	private String header;
	private String content;

	private boolean isResponse;

	public HTTPv1_1(ByteUtil data){
		super("HTTPv1_1", data);

		this.parseData();

	}

	public void parseData(){
		ByteUtil temp = this.data.getBytes(8);
		if (temp.toAlphaNum().equals("HTTP/1.1")){
			this.isResponse = true;
		}else{
			this.isResponse = false;
		}

		this.header = "";
		this.content = "";

		boolean isSep = false;
		String[] values = this.data.toAlphaNum().split("\\r\\n");
		for (String a: values){
			if (a.isEmpty())
				isSep = true;
			if (!isSep){
				this.header += a;
			}else{
				this.content += a;
			}
		}
		
	}

	public String toString(){
		String out = this.gs()+this.protocolName+"\n";
		//out += this.gs()+"Data: #"+this.data.toAlphaNum()+"#\n";
		out += this.gs()+"Header: "+this.header+"\n";
		out += this.gs()+"Content: "+this.content+"\n";
		return out;
	}

}
