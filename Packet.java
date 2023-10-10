import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;
import java.util.Arrays;


class Packet{

	private boolean isLittleEndian;
	private int tsf;
	private int tss;
	private int cpl;
	private int opl;
	private byte[] data;

	private int offset; // 'cursor' to look in the data

	public Packet(boolean isLittleEndian, int tsf, int tss, int cpl, int opl, byte[] data){

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
        return ByteBuffer.wrap(b).getInt();
    }

	private short bytesToShort(byte[] b){
		return ByteBuffer.wrap(b).getShort();
	}


	private void skipBytes(int len){
		this.offset += len;
	}

	private byte[] readBytesFromData(int len){
		// read len bytes from this.data

		System.out.println("here: "+this.data.length+" "+this.offset+" "+len);

		byte[] b = Arrays.copyOfRange(this.data, this.offset, this.offset+len);
		this.offset += len;
		return b;
	}


	private String getByteArrayRepr(byte[] b){
        String out = "";
		for (int i=0; i<b.length; i++){
            out += String.format("%02x ", b[i]);
        }
		return out;
	}
	

    private void printByteArray(byte[] b){
        System.out.println(this.getByteArrayRepr(b));
    }



	// FONCTIONS

	private void readData(){
		this.skipBytes(
		this.readBytesFromData(2);
		byte[] totalLen = this.readBytesFromData(2); // 16 bits
		this.printByteArray(totalLen);
		System.out.println("Total length = "+this.bytesToShort(totalLen));

	}

	public String toString(){
		String out = "";

		Date date = new Date((long)this.tsf * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a");
		String formattedDate = sdf.format(date);

		out += "Packet sent at date: "+formattedDate.toString()+"\n";
		out += "Current Packet length: "+this.cpl+"\n";
		out += "Original Packet length: "+this.opl+"\n";

		out += this.getByteArrayRepr(this.data);
		out += "\n";

		return out;
	}

}
