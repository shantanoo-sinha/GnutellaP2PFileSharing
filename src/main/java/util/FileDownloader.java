package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.P2PFile;
import server.IRemote;
import server.Server;

public class FileDownloader extends Thread {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(FileDownloader.class);
	
	private IRemote remote;
	private Server server;
	private String fileName;
	/*private P2PFile p2PFile;*/
	
	/**
	 * @param remote
	 * @param server
	 * @param fileName
	 */
	public FileDownloader(IRemote remote, Server server, String fileName) {
		super();
		this.remote = remote;
		this.server = server;
		this.fileName = fileName;
	}

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
