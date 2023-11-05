import java.io.*;


public class CableFish
{

	ParsePcapFile pcf = null;
	String[] args = null;

	private int frame;
	private ProtocolName filter;
	private ProtocolName followName;

	public CableFish(String[] args){
		//pcf = new ParseCaptureFile(fileName);

		this.args = args;

		pcf = new ParsePcapFile(this.args[0]);

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
		this.followName = ProtocolName.NONE;
		boolean showOutput = true;

		while (argIndex < this.args.length)
		{

			switch (this.args[argIndex])
			{
				case "frame":
					this.frame = Integer.valueOf(this.args[argIndex+1]);
					argIndex += 2;
					break;

				case "filter":
					String temp = this.args[argIndex+1];
					if (ProtocolName.isName(temp)){
						this.filter = ProtocolName.getFromString(temp);
					}else{
						System.out.println("Protocol name not recognized: '"+temp+"'");
					}
					argIndex += 2;
					break;

				case "follow-stream":
					switch (this.args[argIndex+1]){
						case "tcp":
							this.followName = ProtocolName.TCP;
							argIndex += 2;
							break;
						default:
							argIndex += 1;
					}
					break;

				case "no-output":
					showOutput = false;
					argIndex += 1;
					break;

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
		else
			System.out.println("No filter!");

		
		String out = this.pcf.toString(this.frame, this.filter);

		if (showOutput)
			System.out.println(out);


		if (this.followName != ProtocolName.NONE){
			this.pcf.followStream(this.followName);
		}

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
