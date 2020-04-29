package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.CustomObject;
import model.P2PFile;
import security.RSAEncryption;
import security.RSAKeysHelper;
import security.RSAPublicKey;
import server.IRemote;
import server.Server;

/**
 * The Class FileDownloader.
 */
/**
 * @author Shantanoo
 *
 */
public class FileDownloader extends Thread {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(FileDownloader.class);
	
	/** The remote ID. */
	private String remoteID;
	
	/** The remote. */
	private IRemote remote;
	
	/** The server. */
	private Server server;
	
	/** The file name. */
	private String fileName;
	
	/**
	 * Instantiates a new file downloader.
	 *
	 * @param remoteID the remote ID
	 * @param remote the remote
	 * @param server the server
	 * @param fileName the file name
	 */
	public FileDownloader(String remoteID, IRemote remote, Server server, String fileName) {
		super();
		this.remoteID = remoteID;
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
//			P2PFile p2PFile = remote.obtain(fileName, server.getIpAddress());
			List<Object> parameters = new ArrayList<>();
			parameters.add(fileName);
			parameters.add(server.getIpAddress());
			CustomObject obj = new CustomObject("obtain", parameters, null);
			
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
	        ObjectOutputStream objOutputStream = new ObjectOutputStream(byteOutputStream);
	        objOutputStream.writeObject(obj);
	        
	        RSAPublicKey pubKey = RSAKeysHelper.readPublicKey(remoteID, server.getSharedKeysDirectory());
			RSAEncryption rsa = new RSAEncryption(pubKey.getModulus(), pubKey.getPublicExponent(), null);
	        
			byte[] encryptedData = rsa.encryptData(byteOutputStream.toByteArray());
	        byte[] p2PFileBytes = remote.obtain(encryptedData);
	        byte[] decryptedData = rsa.decryptDataWithPublicKey(p2PFileBytes);
			
	        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decryptedData);
	        ObjectInputStream objInputStream = new ObjectInputStream(byteInputStream);
	        P2PFile p2PFile = (P2PFile)objInputStream.readObject();
	        
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
