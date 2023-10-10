import java.io.*;


public class CableFish
{

	NetworkCaptureFile ncf = null;

	public CableFish(String fileName){
		ncf = new NetworkCaptureFile(fileName);
	}	


	public void launchUI(){
		boolean isOver = false;

		System.out.println("Welcome to CableFish UI !");

		while (!isOver){

			// scan the content of System.in to parse commands

			isOver = true;
		}

	}


	public static void main(String[] args)
	{
		if (args.length == 0){
			System.out.println("Please input at least one argument !");
			System.exit(1);
		}

		CableFish cf = new CableFish(args[0]);

		cf.launchUI();

	}
}
