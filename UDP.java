

class UDP extends Protocol{

	private int sourcePort;
	private int destPort;
	private int length;
	private ByteUtil payload;


	public UDP(ByteUtil data){
		super("UDP", data);

		this.parseData();
	}


	private void parseData(){
		this.sourcePort = this.data.readBytes(2).toInt();
		this.destPort = this.data.readBytes(2).toInt();
		this.length = this.data.readBytes(2).toInt(); // in bytes !
		this.data.skipBytes(2); // skip the checksum value

		if (this.length-2 > 0){
			this.payload = this.data.getRemainingBytes();

			// testing if it's encapsulating DHCP
			if (this.payload.length >= 240){
				this.payload.skipBytes(236);
				// check whether the payload contains DHCP's magic cookie
				boolean isDHCP = this.payload.getBytes(4).equals("63825363");
				this.payload.reset();
				if (isDHCP){
					// settint DHCP as the encapsulated protocol
					this.setEncapsulated(new DHCP(this.payload));
					return;
				}
			}


			// testing if it's encapsulating DNS
			int dns_count = 0;
			if (this.destPort == 53 || this.sourcePort == 53){
				dns_count += 1;
			}
			if (this.payload.length >= 12){ // at least 12 bytes long (DNS header length)
				this.payload.skipBytes(2); // would skip 'id'
				int qr = this.payload.readBytes(1).getBit(7);
				this.payload.skipBytes(1); // would skip the last head byte
				ByteUtil qdcount = this.payload.readBytes(2); // would read qdcount
				if (qdcount.equals("0001")){
					dns_count += 10;
				}
				ByteUtil ancount = this.payload.readBytes(2); // would read ancount
				ByteUtil nscount = this.payload.readBytes(2); // would read arcount

				if (qr == 0 || ancount.isZeroes() || nscount.isZeroes()){
					dns_count += 1;
				}
				
				this.payload.reset();

				if (dns_count >= 10){
					this.setEncapsulated(new DNS(this.payload));
					return;
				}
			}
			

		}
	}


	public String toString(){
		String out = "";
		out += this.gs()+this.protocolName+"\n";
		out += this.gs()+"Source port: "+this.sourcePort+"\n";
		out += this.gs()+"Destination port: "+this.destPort+"\n";
		if (this.payload != null){
			out += this.gs()+"len payload: "+this.payload.length+"\n";
		}

		if (this.encapsulated != null){
			out += this.gs()+"Encapsulated protocol:\n";
			out += this.encapsulated.toString(this.indent+1);
		}
		out += "\n";
		return out;
	}


}
