
class DHCP extends Protocol{

	private short op;
	private short htype;
	private short hlen;
	private short hops;
	private long XID; // TODO delete, useless, fixed !
	private int secs;
	private int flags;
	private long ciaddr;
	private long yiaddr;
	private long siaddr;
	private long giaddr;
	private ByteUtil chaddr;

	private ByteUtil cookie;
	private ByteUtil remaining;


	public DHCP(ByteUtil data){
		super("DHCP", data);

		this.parseData();
	}

	private void parseData(){
		this.op = this.data.readBytes(1).toShort();
		this.htype = this.data.readBytes(1).toShort();
		this.hlen = this.data.readBytes(1).toShort();
		this.hops = this.data.readBytes(1).toShort();

		this.data.skipBytes(4); // skip XID
		
		this.secs = this.data.readBytes(2).toInt();
		this.flags = this.data.readBytes(2).toInt();
		this.ciaddr = this.data.readBytes(4).toLong();
		this.yiaddr = this.data.readBytes(4).toLong();
		this.siaddr = this.data.readBytes(4).toLong();
		this.giaddr = this.data.readBytes(4).toLong();

		this.data.skipBytes(hlen); // skip chaddr (often 6 bytes)
		this.data.skipBytes(10); // client hardware padding
		this.data.skipBytes(192); // this much 
		this.cookie = this.data.readBytes(4); // skip magic cookie

		this.remaining = this.data.getRemainingBytes();
	}

	public String toString(){
		String out = "";
		out += this.gs()+"OP: "+this.op+"\n";
		out += this.gs()+"Htype: "+this.htype+"\n";
		out += this.gs()+"Hlen: "+this.hlen+"\n";
		out += this.gs()+"Hops: "+this.hops+"\n";
		out += this.gs()+"Secs: "+this.secs+"\n";
		out += this.gs()+"Flags: "+this.flags+"\n";
		out += this.gs()+"COOKIE: "+this.cookie+"\n";
		out += this.gs()+"Remaining: ("+this.remaining.length+")"+this.remaining+"\n";
		out += this.gs()+this.protocolName+"\n";

		return out;
	}


}
