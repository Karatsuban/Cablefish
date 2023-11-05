# Cablefish
A network packet analyzer 


## How to use

Simply type `$make` to compile the files.  
To execute, type `java CableFish <your_file.pcap>`

There are additional parameters:
- filter <PROTOCOL_NAME> : to filter to one particular protocol
- follow-stream TCP : to follow a TCP stream (not working currently)
- frame <number> : show only this frame
- no-output : do not print anything

Protocols implemented:
- ARP
- ICMP
- IPv6
- DNS
- HTTPv1
- DHCP
- UDP
- TCP

  Protocols and features NOT implemented (lack of time):
  - Quic
  - Follow TCP stream

