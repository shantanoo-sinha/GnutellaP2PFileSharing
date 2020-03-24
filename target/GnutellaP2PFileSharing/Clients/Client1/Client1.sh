rmiregistry 2001 &
export CLASSPATH="$CLASSPATH:./../Server/:./../lib/*"
echo $CLASSPATH
rmic -classpath $CLASSPATH -d ./../Server client.Client
sleep 5
java -classpath $CLASSPATH -Dlog4j.configuration="file:./../log4j.properties" -Djava.rmi.server.codebase=file:./../Server/ client.ClientDriver 2000 2001 Client1
sleep 50