import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;



class Frame extends Protocol{

	private int frameNb;
	private ByteUtil sourceAddr = null;
	private ByteUtil destinationAddr = null;
	private ByteUtil etherType = null;
	private ByteUtil encap_data = null;

	private boolean isLittleEndian;
	private int tsf;
	private int tss;
	private int cpl;
	private int opl;

	private int offset; // 'cursor' to look in the data


	public Frame(boolean isLittleEndian, int tsf, int tss, int cpl, int opl, byte[] data, int nb){

		super("Ethernet", data);

		this.isLittleEndian = isLittleEndian;
		this.tsf = tsf;
		this.tss = tss;
		this.cpl = cpl;
		this.opl = opl;
		this.offset = 0;
		this.frameNb = nb;

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

		Date date = new Date((long)this.tsf * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a");
		String formattedDate = sdf.format(date);

		out += this.gs()+"Frame "+this.frameNb+":\n";
		out += this.gs()+"Dest address: "+this.sourceAddr.asMacAddr()+"\n";
		out += this.gs()+"Src address: "+this.destinationAddr.asMacAddr()+"\n";
		out += this.gs()+"Packet sent at date: "+formattedDate.toString()+"\n";
		out += this.gs()+"Packet length: "+this.cpl+"\n";

		if (this.encapsulated != null){
			out += this.gs()+"Encapsulated protocol:\n";
			out += this.encapsulated.toString(this.indent+1);
		}
		out += "\n";

		return out;
	}

}
