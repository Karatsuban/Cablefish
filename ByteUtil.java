import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.nio.charset.StandardCharsets;

final class ByteUtil
{

	public byte[] data = null;
	public int offset;
	public int length;

	public ByteUtil(byte[] data){
		this.data = data;
		this.offset = 0;
		this.length = this.data.length;
	}

	public ByteUtil(byte data){
		this(new byte[]{data});
	}

	public ByteUtil(int data){
		this((byte)data);
	}

	// Conversion methods

	public long toLong(){
		// takes 8 bytes, convert them to long
		long out = 0;
		if (this.length < 8)
			out = ByteBuffer.wrap(this.padToLength(8)).getLong();
		else
			out = ByteBuffer.wrap(this.data).getLong();
		return out;
	}


    public int toInt(){
        // takes 4 bytes, convert them to int
		
		int out = 0;
		if (this.length < 4)
			out = ByteBuffer.wrap(this.padToLength(4)).getInt();
		else
	        out = ByteBuffer.wrap(this.data).getInt();
		return out;
    }

    public short toShort(){
        // takes 2 bytes, converts them to short
		short out = 0;
		if (this.length < 2)
			out = ByteBuffer.wrap(this.padToLength(2)).getShort();
        else
			out = ByteBuffer.wrap(this.data).getShort();
		return out;
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

	public boolean isZeroes(){
		// returns whether the bytes are equal to zero
		boolean out = true;
		int index = 0;
		while (index < this.length && out)
		{
			if (this.data[index] != 0)
				out = false;
			index += 1;
		}
		return out;
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

	public ByteUtil getBytes(int len){
		// returns len bytes from this.data WITHOUT moving the offset
        byte[] b = Arrays.copyOfRange(this.data, this.offset, this.offset+len);
		return new ByteUtil(b);
	}

    public ByteUtil getRemainingBytes(){
        // read the remaining bytes from this.data
        byte[] b = Arrays.copyOfRange(this.data, this.offset, this.data.length);
        return new ByteUtil(b);
    }

	public void reset(){
		// move offset back to 0
		this.offset = 0;
	}

	// Getters

	public byte[] getData(){
		return this.data;
	}

	public int getBit(int n){
		// return the nth bit from the first byte currently being read
		// treated as if the bits are indexed like this : 0 1 2 3 4 5 6 7
		int out = 0;
		if (n>=8 || n < 0){
			out = -1;
		}else{
			out = (this.data[this.offset] >> (7-n)) & 1;
		}
		return out;
	}

	public int getBitsFromTo(int start, int stop){
		// return the bits between start and stop INCLUDED from the bytes being currently read
		// treated as if the bits are indexed like this : 0 1 2 3 4 5 6 7
		int out = 0;
		if (start < 0 || start > 7 || start >= stop){
			out = -1; // error
		}else{
			int val = this.data[this.offset] << start; // remove the bits before start
			val = val >> (start + (7-stop)); // remove the bits after stop
		}
		return out;
	}

	public int getIndexNext(byte b){
		// returns the index (relative to offset) of the next occurence of b in data
		int id = 0;
		boolean isFound = false;
		int foundId = -1;
		while (this.offset+id < this.length-1 && !isFound){
			if (this.data[this.offset+id] == b){
				isFound = true;
				foundId = id;
			}
			id += 1;
		}
		return foundId;
	}

	public int getIndexNext(CharSequence b){
		// returns the index (relative to offset) of the next occurence of the FIRST CHAR of b in data
		return this.getIndexNext(HexFormat.of().parseHex(b)[0]);
	}


	public int length(){
		return this.length;
	}

	public int getRemainingLength(){
		// return the number of remaining bytes
		return this.length-this.offset;
	}

	public Boolean hasRemainingBytes(){
		// returns true if there are remaining bytes to read
		return this.getRemainingLength() > 0;
	}

	// ToString method

	public String toString(){
        String out = "";
        for (int i=0; i<this.data.length; i++){
            out += String.format("%02x ", this.data[i]);
        }
        return out;
	}

	public String toAlphaNum(){
		return new String(this.data);
	}

	public String toURL(){
		String url = "";
		for (int i=0; i<this.data.length; i++){
			if (this.data[i] > 16)
				url += new String(new byte[]{this.data[i]});
			else if (this.data[i] == 2)
				url += ".";
		}
		return url;
	}

	public String asIPv4Addr(){
		String out = "";
		if (this.data.length < 4){
			out += " ";
		}else{
			for (int i=0; i<4; i++){
				out += Integer.toString(Byte.toUnsignedInt(this.data[i]));
				if (i != 3) out += ".";
			}
		}
		return out;
	}
	
	public String asMacAddr(){
		String out = "";
		if (this.data.length < 6){
			out += " ";
		}else{
			for (int i=0; i<6; i++){
				out += String.format("%02x", this.data[i]);
				if (i!=5) out += ":";
			}
		}
		return out;
	}

	// Helper functions

	private byte[] padToLength(int length){
		// pad this.data up to lenght by putting 0 bytes BEFORE this.data
		if (this.length >= length)
			return this.data; // already long enough

		int pad_length = length-this.length;
		byte[] paddArr = new byte[pad_length];
		Arrays.fill(paddArr, (byte) 0);

		byte[] both = Arrays.copyOf(paddArr, paddArr.length + this.length);
		System.arraycopy(this.data, 0, both, paddArr.length, this.length);

		return both;
	}

}
