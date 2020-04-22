package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.P2PFile;
import server.IRemote;
import server.Server2;

/**
 * The Class FileDownloader.
 */
/**
 * @author Shantanoo
 *
 */
public class FileDownloader2 extends Thread {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(FileDownloader2.class);
	
	/** The remote. */
	private IRemote remote;
	
	/** The server. */
	private Server2 server;
	
	/** The file name. */
	private String fileName;
	
	/**
	 * Instantiates a new file downloader.
	 *
	 * @param remote the remote
	 * @param server the server
	 * @param fileName the file name
	 */
	public FileDownloader2(IRemote remote, Server2 server, String fileName) {
		super();
		this.remote = remote;
		this.server = server;
		this.fileName = fileName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			logger.info("[" + server.getId() + "] " + "Requested file download from leaf node " + server.getIpAddress());
			P2PFile p2PFile = remote.obtain(fileName, server.getIpAddress());
			if(server.writeFileContent(p2PFile, fileName)) {
				logger.info("[" + server.getId() + "] " + "Requested file " + fileName + " downloaded successfully.");
			} else {
				logger.info("[" + server.getId() + "] " + "Failed to download the requested file " + fileName + ".");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
