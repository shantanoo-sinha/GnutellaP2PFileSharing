/**
 * 
 */
package model;

import java.io.File;
import java.io.Serializable;

/**
 * @author Shantanoo
 *
 */
public class P2PFile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1323503574816464897L;

	private int version = 1;
	private int TTR;
	private String originServerID;
	byte[] fileContent;
	private File file;
	private String fileName;
	private FileConsistencyState state;
	
	/**
	 * @param version
	 * @param tTR
	 * @param originServerID
	 * @param fileContent
	 * @param file
	 * @param fileName
	 * @param state
	 */
	public P2PFile(int version, int tTR, String originServerID, byte[] fileContent, File file, String fileName,
			FileConsistencyState state) {
		super();
		this.version = version;
		TTR = tTR;
		this.originServerID = originServerID;
		this.fileContent = fileContent;
		this.file = file;
		this.fileName = fileName;
		this.state = state;
	}
	
	public int getVersion() {
		return version;
	}
	public void incrementVersion() {
		this.version++;
	}
	public int getTTR() {
		return TTR;
	}
	public void setTTR(int tTR) {
		TTR = tTR;
	}
	public String getOriginServerID() {
		return originServerID;
	}
	public void setOriginServerID(String originServerID) {
		this.originServerID = originServerID;
	}
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public FileConsistencyState getState() {
		return state;
	}
	public void setState(FileConsistencyState state) {
		this.state = state;
	}
}