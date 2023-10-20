import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;



class LinkLayer extends Protocol{

	ByteUtil sourceAddr = null;
	ByteUtil destinationAddr = null;
	ByteUtil etherType = null;

	private boolean isLittleEndian;
	private int tsf;
	private int tss;
	private int cpl;
	private int opl;

	private int offset; // 'cursor' to look in the data

	public LinkLayer(boolean isLittleEndian, int tsf, int tss, int cpl, int opl, byte[] data){

		super("Ethernet", data);

		this.isLittleEndian = isLittleEndian;
		this.tsf = tsf;
		this.tss = tss;
		this.cpl = cpl;
		this.opl = opl;
		this.offset = 0;

		this.readData();


	}

	// FUNCTIONS

	private void readData(){
		sourceAddr = this.data.readBytes(6);
		destinationAddr = this.data.readBytes(6);
		etherType = this.data.readBytes(2);
		data = this.data.getRemainingBytes();


		if (etherType.equals("0800")){
				this.setEncapsulated(new IPv4(data));

		}else if (etherType.equals("0806")){
				this.setEncapsulated(new ARP(data));

		}else if (etherType.equals("86dd")){
				this.setEncapsulated(new IPv6(data));

		}else{
				System.out.println("Don't know this protocol!");
		}	

	}

	public String toString(){
		String out = "";

		Date date = new Date((long)this.tsf * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a");
		String formattedDate = sdf.format(date);


		out += "destination address: "+this.sourceAddr.asMacAddr()+"\n";
		out += "source address: "+this.destinationAddr.asMacAddr()+"\n";
		out += "etherType: "+this.etherType.toInt()+"\n";
		out += "Packet sent at date: "+formattedDate.toString()+"\n";
		out += "Current Packet length: "+this.cpl+"\n";
		out += "Original Packet length: "+this.opl+"\n";

		out += "Encapsulated protocol:\n";
		out += this.encapsulated.toString();
		out += "\n";

		return out;
	}

}
