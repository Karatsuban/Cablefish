import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;



class LinkLayer{

	private boolean isLittleEndian;
	private int tsf;
	private int tss;
	private int cpl;
	private int opl;
	private byte[] data;

	private int offset; // 'cursor' to look in the data

	public LinkLayer(boolean isLittleEndian, int tsf, int tss, int cpl, int opl, byte[] data){

		this.isLittleEndian = isLittleEndian;
		this.tsf = tsf;
		this.tss = tss;
		this.cpl = cpl;
		this.opl = opl;
		this.data = data;
		this.offset = 0;

		this.readData();


	}


	// HELPER FUNCTIONS

    private int bytesToInt(byte[] b){
		// convert by to int
        return ByteBuffer.wrap(b).getInt();
    }

	private short bytesToShort(byte[] b){
		// converts b to short
		return ByteBuffer.wrap(b).getShort();
	}

	private boolean compareBytes(byte[] a, CharSequence b){
		// compare a byte array and a charsequence representing bytes
		return this.compareBytes(a, HexFormat.of().parseHex(b));
	}

	private boolean compareBytes(byte[] a, byte[] b){
		// compare two byte arrays
		return Arrays.equals(a, b);
	}

	private void skipBytes(int len){
		// move offset to skip bytes
		this.offset += len;
	}

	private byte[] readBytes(int len){
		// read len bytes from this.data
		byte[] b = Arrays.copyOfRange(this.data, this.offset, this.offset+len);
		this.offset += len;
		return b;
	}


	private String getBytesRepr(byte[] b){
        String out = "";
		for (int i=0; i<b.length; i++){
            out += String.format("%02x ", b[i]);
        }
		return out;
	}
	

    private void printBytes(byte[] b){
        System.out.println(this.getBytesRepr(b));
    }



	// FONCTIONS

	private void readData(){
		byte[] sourceAddr_arr = this.readBytes(6);
		byte[] destinationAddr_arr = this.readBytes(6);
		byte[] etherType = this.readBytes(2);
		System.out.println("destination address = "+this.getBytesRepr(sourceAddr_arr));
		System.out.println("source address = "+this.getBytesRepr(destinationAddr_arr));
		System.out.println("etherType = "+this.bytesToShort(etherType));

		if (this.compareBytes(etherType, "0800")){
				System.out.println("It's IPv4!");
		}else if (this.compareBytes(etherType, "0806")){
				System.out.println("It's ARP");
		}else if (this.compareBytes(etherType, "86dd")){
				System.out.println("It's IPv6!");
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

		out += this.getBytesRepr(this.data);
		out += "\n";

		return out;
	}

}
