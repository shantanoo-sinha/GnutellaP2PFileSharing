package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.MessageID;
import server.Server;
import util.Constants;

/**
 * The Class Client.
 *
 * @author Shantanoo
 */
public class Client {
	
	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Client.class);

	/** The id. */
	private String id;
	
	/** The server. */
	private Server server;
	
	/** The files dir path. */
	private String filesDirPath = null;
	
	/** The sequence number. */
	private Long sequenceNumber = 0l;

	/**
	 * Instantiates a new client.
	 *
	 * @param id the id
	 * @param peerNetworkTopology the peer network topology
	 */
	public Client(String id, String peerNetworkTopology) {
		super();
		this.id = id;
		initServer(id, peerNetworkTopology);
	}

	/**
	 * Inits the server.
	 *
	 * @param id the id
	 * @param peerNetworkTopology the peer network topology
	 */
	private void initServer(String id, String peerNetworkTopology) {
		try {
			server = new Server(id, peerNetworkTopology);
		} catch (Exception e) {
			logger.error("Exception: Unable to initiate Server for client " + id + ":\n" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the server.
	 *
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Sets the server.
	 *
	 * @param server the new server
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Gets the files dir path.
	 *
	 * @return the files dir path
	 */
	public String getFilesDirPath() {
		return filesDirPath;
	}

	/**
	 * Sets the files dir path.
	 *
	 * @param filesDirPath the new files dir path
	 */
	public void setFilesDirPath(String filesDirPath) {
		this.filesDirPath = filesDirPath;
	}
	
	/**
	 * Retrieve file.
	 *
	 * @param fileName the file name
	 */
	public void retrieveFile(String fileName){
		String leafNodeId = Constants.RMI_LOCALHOST + server.getProperty(id + ".port").trim() + Constants.PEER_SERVER;
		MessageID messageID = new MessageID(leafNodeId, sequenceNumber++);
		try {
        	//logger.info("[" + this.id + "] Querying file " + fileName + " from " + id);
        	server.query(messageID, fileName);
//        	server.query(messageID, server.getTTL(), fileName, server.getIpAddress());
        } catch (Exception e) {
        	logger.error("[" + this.id + "] Client exception: Unable to retrieve file.");
            logger.error("[" + this.id + "] Unable to query file " + fileName + " from " + leafNodeId + ":\n" + e.toString());
            e.printStackTrace();
        }
    }
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String topology = args[0];
		String id = args[1];
		logger.info("*******************************************************************");
		logger.info("[" + id + "] " + "Initialized Topology:" + topology);
		logger.info("*******************************************************************");
		
		Properties prop = new Properties();
		File USER_DIR = new File(System.getProperty(Constants.USER_DIR));
		File TOPOLOGY_FILE_PATH = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator + Constants.TOPOLOGY_FOLDER);
		
		InputStream is = null;
		try {
			is = new FileInputStream(new File(TOPOLOGY_FILE_PATH + File.separator + topology + ".txt"));
			prop.load(is);
			/*prop.forEach((key, value) -> logger.info(key + ":" + value));
			prop.forEach((key, value) -> logger.info(key + ":" + prop.getProperty((String) key)));*/
		} catch (Exception e) {
			logger.error("[" + id + "] " + "Client exception: Unable to load topology.");
			e.printStackTrace();
		}
		
		Client client = new Client(id, topology);
		logger.info("[" + id + "] " + id + " initialized on port: " + prop.getProperty(id + ".port"));
		logger.info("*******************************************************************");

		if(client.server.isSuperPeer()) {
			while (true) {
				
			}
		} else {
			Scanner scanner = new Scanner(System.in);
			String input;
			logger.info("\nEnter 'exit' to exit the application"
					+ "\nEnter the name of file (with extension) you want to download:\n");
			
			while (true) {
				/*logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter the name of file (with extension) you want to download:\n");*/
				input = scanner.nextLine();
				if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {
					logger.info("\nClient exiting...\n");
					scanner.close();
					System.exit(0);
				} else if (input != null && (input.trim().contains(Constants.SPACE) || !input.trim().contains(Constants.DOT))) {
					logger.info("Incorrect command");
					logger.info("USAGE: <file name with extension>");
					logger.info("EXAMPLE: <123.txt>");
					logger.info("EXAMPLE: <e or exit>");
				} else {
					long startTime = System.currentTimeMillis();
					if(input.contains(";")) {
						logger.info("[" + id + "] " + "Received multi-file requests");
						logger.info("[" + id + "] " + "Sending file request one-by-one");
						Arrays.asList(input.split(";")).stream().filter(x -> !x.isEmpty()).forEach(fileName -> {
							logger.info("[" + id + "] " + "Requesting file: " + fileName);
							client.retrieveFile(fileName);
						});
					} else {
						client.retrieveFile(input);
					}
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;
					double elapsedTimeInMSecond = (double) elapsedTime / 1000.000;
					logger.info("Process completed in " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS) + "(" + elapsedTimeInMSecond + ") second");
				}
				logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter the name of file (with extension) you want to download:\n");
			}
		}
	}
}
