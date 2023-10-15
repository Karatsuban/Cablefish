import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;


final class ByteUtil
{

	public byte[] data = null;
	public int offset;

	public ByteUtil(byte[] data){
		this.data = data;
		this.offset = 0;
	}

	public ByteUtil(byte data){
		this(new byte[]{data});
	}

	// Conversion methods
	
    public int toInt(){
        // takes 4 bytes, convert them to int
		
		int out = 0;
		/*byte[] temp = null;
		switch (this.data.length){
			case 1:
				temp = new byte[]{0x0, 0x0, 0x0, this.data[0]};
				break;
			case 2:
				temp = new byte[]{0x0, 0x0, this.data[0], this.data[1]};
				break;
			case 3:
				temp = new byte[]{0x0, this.data[0], this.data[1], this.data[2]};
				break;
			case 4:
				temp = this.data;
				break;
			default:
		};
		System.out.println(new ByteUtil(temp));
		*/
        out = ByteBuffer.wrap(this.data).getInt();
		return out;
    }

    public short toShort(){
        // takes 2 bytes, converts them to short
        return ByteBuffer.wrap(this.data).getShort();
    }

	public byte toByte(){
		// converts b to byte
        return this.data[0];
	}
		

	// Comparison methods

    public boolean equals(CharSequence b){
        // compare a byte array and a charsequence representing bytes
        return this.equals(HexFormat.of().parseHex(b));
    }

    public boolean equals( byte[] b){
        // compare two byte arrays
        return Arrays.equals(this.data, b);
    }


	// Reading methods

    public void skipBytes(int len){
        // move offset to skip bytes
        this.offset += len;
    }

    public ByteUtil readBytes(int len){
        // read len bytes from this.data
        byte[] b = Arrays.copyOfRange(this.data, this.offset, this.offset+len);
        this.offset += len;
        return new ByteUtil(b);
    }

    public ByteUtil getRemainingBytes(){
        // read the remaining bytes from this.data
        byte[] b = Arrays.copyOfRange(this.data, this.offset, this.data.length);
        return new ByteUtil(b);
    }


	// Getters

	public byte[] getData(){
		return this.data;
	}

	public int getBit(int n){
		// return the nth bit from the first byte currently being read
		if (n>=8 || n < 0){
			return -1;
		}else{
			return (this.data[this.offset] >> n) & 1;
		}
	}

	// ToString method

	public String toString(){
        String out = "";
        for (int i=0; i<this.data.length; i++){
            out += String.format("%02x ", this.data[i]);
        }
        return out;
	}

	public String asIPv4Addr(){
		String out = "";
		if (this.data.length < 4){
			out += " ";
		}else{
			for (int i=0; i<4; i++){
				out += this.data[i] & 0xF; // from byte to int
				if (i != 3) out += ".";
			}
		}
		return out;
	}
	

}
