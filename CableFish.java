import java.io.*;


public class CableFish
{

	//ParseCaptureFile pcf = null;
	ParsePcapFile_test pcf = null;
	String[] args = null;

	private int frame;
	private ProtocolName filter;
	private boolean followTCPStream;

	public CableFish(String[] args){
		//pcf = new ParseCaptureFile(fileName);

		this.args = args;

		pcf = new ParsePcapFile_test(this.args[0]);

		this.parseArgs();
	}	


	public void parseArgs(){

		/*
		Options: 
			filter <protocol 1 name> ... <protocol x name> => display only those x protocoles
			follow-tcp-stream
			trame <nb> : display only this trame
		*/
		int argIndex = 1;

		this.frame = -1;
		this.filter = ProtocolName.ALL;
		this.followTCPStream = false;

		while (argIndex < this.args.length)
		{
			switch (this.args[argIndex])
			{
				case "frame":
					this.frame = Integer.valueOf(this.args[argIndex+1]);
					argIndex += 2;
					break;
				case "filter":
					String temp =this.args[argIndex+1];
					if (ProtocolName.isName(temp)){
						this.filter = ProtocolName.getFromString(temp);
					}else{
						System.out.println("Protocol name not recognized: "+temp);
					}
					argIndex += 2;
					break;
				case "follow":
					switch (this.args[argIndex+1]){
						case "tcp-stream":
							this.followTCPStream = true;
							argIndex += 2;
							break;
						default:
							argIndex += 1;
					}
					
				default:
					argIndex += 1;

			}
		}

		if (this.frame < 0)
			System.out.println("Get all frames");
		else
			System.out.println("Get frame "+this.frame);

		if (this.filter != null)
			System.out.println("Getting protocol "+this.filter.getName());

		if (this.followTCPStream)
			System.out.println("Following TCP stream");

		String out = this.pcf.toString(this.frame, this.filter);
		System.out.println(out);
	}


	public static void main(String[] args)
	{
		if (args.length == 0){
			System.out.println("Please input at least one argument !");
			System.exit(1);
		}

		CableFish cf = new CableFish(args);


	}
}
