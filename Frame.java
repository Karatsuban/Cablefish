import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HexFormat;


class Frame extends Protocol{


	private int frameNb;
	private int tsf;
	private int dataLen;

	public Frame(int frameNb, int tsf, int dataLen, byte[] data){

		super("Frame", data);
		this.frameNb = frameNb;
		this.tsf = tsf;
		this.dataLen = dataLen;

		this.setEncapsulated(new Ethernet(data));
	}


	public String toString(){
		String out = "";
        Date date = new Date((long)this.tsf * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a");
        String formattedDate = sdf.format(date);


		out += this.gs()+"Frame "+this.frameNb+"\n";
		out += this.gs()+"Time: "+formattedDate+"\n";
		out += this.gs()+"Packet len: "+this.dataLen+"\n";
		out += this.encapsulated.toString(this.indent+1)+"\n";

		return out;
	}


}
