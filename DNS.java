
class DNS extends Protocol{

	private long id;
	private int qr;
	private int opcode;
	private int aa;
	private int tc;
	private int rd;
	private int ra;
	private int ad;
	private int cd;
	private int rcode;

	private int answerRR;
	private int additionalRR;

	private ByteUtil name = null;
	private ByteUtil address = null;


	public DNS(ByteUtil data){
		super("DNS", data);

		this.parseData();
	}


	private void parseData(){
		this.id = this.data.readBytes(2).toLong();
		ByteUtil temp = null;
		temp = this.data.readBytes(1);
		this.qr = temp.getBit(0);
		this.opcode = temp.getBitsFromTo(1, 4);
		this.aa = temp.getBit(5);
		this.tc = temp.getBit(6);
		this.rd = temp.getBit(7);
		temp = this.data.readBytes(1);
		this.ra = temp.getBit(0);
		this.ad = temp.getBit(2);
		this.cd = temp.getBit(3);
		this.rcode = temp.getBitsFromTo(4, 7);

		this.data.skipBytes(2); // skip qcount
		this.answerRR = this.data.readBytes(2).toInt();
		this.data.skipBytes(2); // skip nscount
		this.additionalRR = this.data.readBytes(2).toInt();

		int nameLength = this.data.getIndexNext("00");
		this.name = this.data.readBytes(nameLength);

		this.data.skipBytes(5); // skip end of query

		if (this.qr == 1){
			// if this IS a response
			this.data.skipBytes(10); // skip begining of answer
			System.out.println("leng (bytes) = "+this.data.getBytes(2));
			int length = this.data.readBytes(2).toInt();
			System.out.println("LENGTH = "+length);
			this.address = this.data.readBytes(length);
		}

	}

	public String toString(){
		String out = "";
		out += this.gs()+"DNS\n";
		out += this.gs()+"ID: "+this.id+"\n";

		if (this.qr == 0)
			out += this.gs()+"Query";
		else if (this.qr == 1)
			out += this.gs()+"Response";

		switch (this.opcode){
			case 0: out += " (Query)"; break;
			case 1: out += " (Iquery)"; break;
			case 2: out += " (Status)"; break;
			case 4: out += " (Notify)"; break;
			case 6: out += " (Update)"; break;
			default:
		}
		out += "\n";

		if (this.rd == 1)
			out += this.gs()+"Recursion desired\n";

		if (this.ra == 1)
			out += this.gs()+"Recursion available\n";


		if (this.rcode != 0)
			out += this.gs()+"Reply code with error: "+this.rcode+"\n";

		if (this.qr == 0){
			out += this.gs()+"Query: "+this.name.toURL()+"\n";
		}else{
			out += this.gs()+"Response: "+this.name.toURL();
			out += " => "+this.address.asIPv4Addr()+"\n";
		}

		return out;
	}
}
