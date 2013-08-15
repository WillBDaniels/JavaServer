JavaServer
==========

I have been working on for the past month or so a Java server that allows you to repeatedly test connection speeds over various protocols. I found this to be a lacking area of open source projects, and I hope someone finds it useful! 
This Java server is used primarily to accomplish a few functions. 
1) To allow a client to connect and upload as much data as possible, as fast as possible when they connect to a certain port.
2) To download 100 MB (or a pre-determined size with HTTP) to the client as fast as possible to simulate a heavy load situation. 

This server allows for TCP, UDP and HTTP, is multi-Threaded, and should be able to run ad-infinum until closed by the hosting system. It does allow for simple NAT traversal, but only if the NAT is configured to hold open an outgoing port for a small amount of time

You can find a link to the javadocs at: http://willbdaniels.github.io/JavaServer/

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

###Building and Running

If you want to use the server on your local system, very little help is needed as far as building. The server is built with gradle, which is automatically downloaded upon running the script 

For windows: 

		gradlew

for mac/linux: 

		./gradlew

This will compile the server, and create a useable jar file in the build/libs directory. The command for running this jar is: 

		java -jar build/libs/{name of jar}.jar

At the moment, the 'name of jar' is JavaServer.jar. That is subject to change based upon your own configurations, preferences, etc etc.

##Notes

 This program DOES require java to be installed on the base machine. That might seem obvious, but it does a lot of things automatically, so it's easy to get complacent. 

 This is not really an all-around all-purpose server. It lacks a lot of commonly found things in servers, that aren't necessary for the scope of the problem this is solving. 

 The javadocs for this program are provided through a program called Doxygen, to use it, you need to download it from the source at http://www.stack.nl/~dimitri/doxygen/download.html There are instructions for all platforms there! You'll also have to set your environment variable so that typing the command "Doxygen" on the command line (or terminal) gives you the current Doxygen version. 

 ##Known bugs
 
 There is an issue with the way TCP is handled, a better checking of end of stream is desired. 

 There is currently no thread pool implemented, which would be a valuable update in later updates. 

 For the Http Upload method, there is some inconsistency where it could block indefinitely, and therefore keep the thread alive forever. 


 ##Contact

 If you'd like to contribute to this project, or you just have a question about it's operation, feel free to contact me at willbdaniels@gmail.com. I check that relatively frequently, so it shouldn't be a big issue.




