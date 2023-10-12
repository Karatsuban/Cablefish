import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;



class LinkLayer extends Protocol{

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
		ByteUtil sourceAddr_arr = this.data.readBytes(6);
		ByteUtil destinationAddr_arr = this.data.readBytes(6);
		ByteUtil etherType = this.data.readBytes(2);
		ByteUtil data = this.data.getRemainingBytes();

		System.out.println("destination address = "+sourceAddr_arr);
		System.out.println("source address = "+destinationAddr_arr);
		System.out.println("etherType = "+etherType.toShort());

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

		out += "Packet sent at date: "+formattedDate.toString()+"\n";
		out += "Current Packet length: "+this.cpl+"\n";
		out += "Original Packet length: "+this.opl+"\n";

		out += "Encapsulated protocol:\n";
		out += this.encapsulated.toString();
		out += "\n";

		return out;
	}

}
