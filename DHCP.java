
class DHCP extends Protocol{

	private short op;
	private short htype;
	private short hlen;
	private short hops;
	private long xid;
	private int secs;
	private ByteUtil flags;
	private ByteUtil ciaddr;
	private ByteUtil yiaddr;
	private ByteUtil siaddr;
	private ByteUtil giaddr;
	private ByteUtil chaddr;

	private ByteUtil cookie;
	private ByteUtil options;

	private short opCode;
	private short opLen;
	private ByteUtil opVal;
	
	private short messageType;
	private String hostName;
	private String domainName;


	public DHCP(ByteUtil data){
		super("DHCP", data);

		this.parseData();
	}


	private void parseData(){
		this.op = this.data.readBytes(1).toShort();
		this.htype = this.data.readBytes(1).toShort();
		this.hlen = this.data.readBytes(1).toShort();
		this.hops = this.data.readBytes(1).toShort();
		this.xid = this.data.readBytes(4).toLong();
		this.secs = this.data.readBytes(2).toInt();
		this.flags = this.data.readBytes(2);
		this.ciaddr = this.data.readBytes(4);
		this.yiaddr = this.data.readBytes(4);
		this.siaddr = this.data.readBytes(4);
		this.giaddr = this.data.readBytes(4);
		this.chaddr = this.data.readBytes(hlen); // skip chaddr (often 6 bytes)
		
		this.data.skipBytes(10); // skip client hardware padding
		this.data.skipBytes(192); // skip bootp legacy padding
		this.cookie = this.data.readBytes(4); // skip magic cookie

		this.options = this.data.getRemainingBytes();

		if (this.options != null)
			this.parseOptions();
	}


	private void parseOptions(){
		boolean endReached = false;
		while (this.options.hasRemainingBytes() && !endReached){
			this.opCode = this.options.readBytes(1).toShort(); // read op code
			
			if (this.opCode != 255){
				this.opLen = this.options.readBytes(1).toShort(); // read lenght
				this.opVal = this.options.readBytes(this.opLen); // read 'opLen' bytes
			}

			switch (this.opCode){
				case 53: // DHCP message type
					this.messageType = this.opVal.toShort();
					break;
				case 12:
					this.hostName = this.opVal.toAlphaNum();
					break;
				case 15:
					this.domainName = this.opVal.toAlphaNum();
					break;
				case 255: // end option
					endReached = true;
					break;
				default:
			}
		}
	}


	private String msgType(){
		String type = "";
		switch (this.messageType){
		case 1:
			type = "Discover"; break;
		case 2:
			type = "Offer"; break;
		case 3:
			type = "Request"; break;
		case 4:
			type = "Decline"; break;
		case 5:
			type = "Ack"; break;
		case 6:
			type = "Nak"; break;
		case 7:
			type = "Release"; break;
		case 8:
			type = "Inform"; break;
		case 9:
			type = "Forcerenew"; break;
		case 10:
			type = "Leasequery"; break;
		case 11:
			type = "Leaseunassigned"; break;
		case 12:
			type = "Leaseunknown"; break;
		case 13:
			type = "Leaseactive"; break;
		case 14:
			type = "Bulkleasequery"; break;
		case 15:
			type = "Leasequerydone"; break;
		case 16:
			type = "Activeleasequery"; break;
		case 17:
			type = "Leasequerystatus"; break;
		case 18:
			type = "TLS"; break;
		default:
		}
		return type;
	}

	private String msgDirection(){
		String dir = "";
		if (this.op == 1)
			dir = "Request";
		else
			dir = "Reply";
		return dir;
	}


	private String printFlag(){
		String flag = "";
		if (this.flags.equals("8000")){
			flag = "Broadcast";
		}else if (this.flags.equals("0000")){
			flag = "Unicast";
		}
		return flag;
	}
	

	public String toString(){
		String out = "";
		out += this.gs()+this.protocolName+"\n";
		out += this.gs()+"Type: "+this.messageType+" "+this.msgType()+" ("+this.msgDirection()+")\n";
		out += this.gs()+"Hops: "+this.hops+"\n";
		out += this.gs()+"Transaction id: "+this.xid+"\n";
		out += this.gs()+"Client IP: "+this.ciaddr.asIPv4Addr()+"\n";
		out += this.gs()+"Your IP  : "+this.yiaddr.asIPv4Addr()+"\n";
		out += this.gs()+"Server IP: "+this.siaddr.asIPv4Addr()+"\n";
		out += this.gs()+"Relay IP : "+this.giaddr.asIPv4Addr()+"\n";
		out += this.gs()+"Client MAC: "+this.chaddr.asMacAddr()+"\n";
		out += this.gs()+"Flags: "+this.printFlag()+"\n";

		if (this.hostName != null)
			out += this.gs()+"Hostname: "+this.hostName+"\n";
		
		if (this.domainName != null)
			out += this.gs()+"Domaine name: "+this.domainName+"\n";

		return out;
	}


}
