set path = "C:\Program Files\Java\jdk1.8.0_151\bin"
timeout /t 5
java -classpath "F:\Workspace\GnutellaP2PFileSharing\target\GnutellaP2PFileSharing\lib\classes;F:\Workspace\GnutellaP2PFileSharing\target\GnutellaP2PFileSharing\lib\*" -Dlog4j.configurationFile=file:///f:/Workspace/GnutellaP2PFileSharing/target/GnutellaP2PFileSharing/lib/classes/log4j2.properties -Djava.rmi.server.codebase=file:f:/Workspace/GnutellaP2PFileSharing/target/GnutellaP2PFileSharing/lib/classes -Djava.security.policy=file:///f:/Workspace/GnutellaP2PFileSharing/target/GnutellaP2PFileSharing/lib/classes/security.policy client.Client all-to-all Client2
timeout /t 50