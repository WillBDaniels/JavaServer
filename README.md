JavaServer
==========

I have been working on for the past month or so a Java server that allows you to repeatedly test connection speeds over various protocols. I found this to be a lacking area of open source projects, and I hope someone finds it useful! 
This Java server is used primarily to accomplish a few functions. 
1) To allow a client to connect and upload as much data as possible, as fast as possible when they connect to a certain port.
2) To download 100 MB (or a pre-determined size with HTTP) to the client as fast as possible to simulate a heavy load situation. 

This server allows for TCP, UDP and HTTP, is multi-Threaded, and should be able to run ad-infinum until closed by the hosting system. It does allow for simple NAT traversal, but only if the NAT is configured to hold open an outgoing port for a small amount of time

###Configuration

The only really necessary configuration would be to make sure you have Java installed and that you're available to listen to the selected ports on the server. 

If you need more advanced functionality, aka: port forwarding, etc. There is an ip_table bash script that will setup an iptables set of rules that will take all connections from port 443 and forward them to the respective ports. This makes it so that rather than worrying about extra ports, all you need to do is listen for 5 different IP addresses. 

ex: if my available ip addresses are: 192.168.0.0 and 192.168.0.2 my iptables routing would look like this in the file: 

ip_addresses=" 192.168.0.0"

for ip_address in ${ip_addresses}; do

  iptables -t nat -A OUTPUT -d ${ip_address} -p tcp --dport 443 -j DNAT --to-destination ${ip_address}:8000

  iptables -t nat -A PREROUTING -d ${ip_address} -p tcp --dport 443 -j DNAT --to-destination ${ip_address}:8000

  echo "setting up the routing for: " $ip_address

done

ip_addresses=" 192.168.0.2"

for ip_address in ${ip_addresses}; do

  iptables -t nat -A OUTPUT -d ${ip_address} -p tcp --dport 443 -j DNAT --to-destination ${ip_address}:8080

  iptables -t nat -A PREROUTING -d ${ip_address} -p tcp --dport 443 -j DNAT --to-destination ${ip_address}:8080
  
done

This gives me (and you!) the ability to connect to either port 8000 (TCP Download for the server) or port 8080 (tcp upload) by simply connecting to the proper IP address respectively. 
