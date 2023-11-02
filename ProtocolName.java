
public enum ProtocolName{

	ARP ("ARP"),
	IPv4 ("IPv4"),
	IPv6 ("IPv6"),
	UDP ("UDP"),
	TCP ("TCP"),
	HTTPv1_1 ("HTTPv1_1"),
	ICMP ("ICMP"),
	DHCP ("DHCP"),
	NONE ("NONE"),
	ALL ("ALL");

	private final String name;

	ProtocolName(String name){
		this.name = name;
	}

	public static boolean isName(String name)
	{
		boolean out = false;
		for (ProtocolName n: ProtocolName.values()){
			if (n.getName().equals(name))
				out = true;
		}
		return out;
	}

	public static ProtocolName getFromString(String name)
	{
		ProtocolName out = null;
		for (ProtocolName n: ProtocolName.values()){
			if (n.getName().equals(name))
				out = n;
		}
		return out;

	}

	public final String getName(){
		return this.name;
	}

}
