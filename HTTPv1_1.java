
class HTTPv1_1 extends Protocol{

	public HTTPv1_1(ByteUtil data){
		super("HTTPv1_1", data);

		this.parseData();
	}

	public void parseData(){
	}

	public String toString(){
		String out = this.gs()+"Data: #"+this.data.toAlphaNum()+"#\n";
		return out;
	}

}
