/**
 * 
 */
package model;

import java.io.File;
import java.io.Serializable;

/**
 * The Class P2PFile.
 *
 * @author Shantanoo
 */
public class P2PFile implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1323503574816464897L;

	/** The version. */
	private int version = 1;
	
	/** The ttr. */
	private int TTR;
	
	/** The origin server ID. */
	private String originServerID;
	
	/** The origin server super peer ID. */
	private String originServerSuperPeerID;
	
	/** The current address. */
	private String currentAddress;
	
	/** The file content. */
	byte[] fileContent;
	
	/** The file. */
	private File file;
	
	/** The file name. */
	private String fileName;
	
	/** The state. */
	private FileConsistencyState state;
	
	/**
	 * Instantiates a new p 2 P file.
	 *
	 * @param version the version
	 * @param tTR the t TR
	 * @param originServerID the origin server ID
	 * @param originServerSuperPeerID the origin server super peer ID
	 * @param currentAddress the current address
	 * @param fileContent the file content
	 * @param file the file
	 * @param fileName the file name
	 * @param state the state
	 */
	public P2PFile(int version, int tTR, String originServerID, String originServerSuperPeerID, String currentAddress, byte[] fileContent, File file, String fileName,
			FileConsistencyState state) {
		super();
		this.version = version;
		this.TTR = tTR;
		this.originServerID = originServerID;
		this.originServerSuperPeerID = originServerSuperPeerID;
		this.currentAddress = currentAddress;
		this.fileContent = fileContent;
		this.file = file;
		this.fileName = fileName;
		this.state = state;
	}
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Increment version.
	 */
	public void incrementVersion() {
		this.version++;
	}
	
	/**
	 * Gets the ttr.
	 *
	 * @return the ttr
	 */
	public int getTTR() {
		return TTR;
	}
	
	/**
	 * Sets the ttr.
	 *
	 * @param tTR the new ttr
	 */
	public void setTTR(int tTR) {
		TTR = tTR;
	}
	
	/**
	 * Gets the origin server ID.
	 *
	 * @return the origin server ID
	 */
	public String getOriginServerID() {
		return originServerID;
	}
	
	/**
	 * Sets the origin server ID.
	 *
	 * @param originServerID the new origin server ID
	 */
	public void setOriginServerID(String originServerID) {
		this.originServerID = originServerID;
	}
	
	/**
	 * Gets the origin server super peer ID.
	 *
	 * @return the origin server super peer ID
	 */
	public String getOriginServerSuperPeerID() {
		return originServerSuperPeerID;
	}
	
	/**
	 * Sets the origin server super peer ID.
	 *
	 * @param originServerSuperPeerID the new origin server super peer ID
	 */
	public void setOriginServerSuperPeerID(String originServerSuperPeerID) {
		this.originServerSuperPeerID = originServerSuperPeerID;
	}
	
	/**
	 * Gets the current address.
	 *
	 * @return the current address
	 */
	public String getCurrentAddress() {
		return currentAddress;
	}
	
	/**
	 * Sets the current address.
	 *
	 * @param currentAddress the new current address
	 */
	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
	
	/**
	 * Gets the file content.
	 *
	 * @return the file content
	 */
	public byte[] getFileContent() {
		return fileContent;
	}
	
	/**
	 * Sets the file content.
	 *
	 * @param fileContent the new file content
	 */
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public FileConsistencyState getState() {
		return state;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(FileConsistencyState state) {
		this.state = state;
	}
}