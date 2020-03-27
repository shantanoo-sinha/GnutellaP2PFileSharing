/**
 * 
 */
package model;

import java.io.Serializable;

/**
 * The Class MessageID.
 *
 * @author Shantanoo
 */
public class MessageID implements Serializable {
	
	/** serialVersionUID. */
	private static final long serialVersionUID = 7301828132227360955L;
	
	/** The leaf node id. */
	private String leafNodeId;
	
	/** The sequence number. */
	private Long sequenceNumber = 0L;
	
	/** The leaf node id address. */
//	private String leafNodeIdAddress;
	
	/** The file not found on any node. */
//	private boolean fileNotFoundOnAnyNode = true;
	
	/**
	 * Instantiates a new message ID.
	 */
	private MessageID() {
		super();
	}
	
	/**
	 * Gets the leaf node id address.
	 *
	 * @return the leafNodeIdAddress
	 */
	/*public String getLeafNodeIdAddress() {
		return leafNodeIdAddress;
	}*/

	/**
	 * Sets the leaf node id address.
	 *
	 * @param leafNodeIdAddress the leafNodeIdAddress to set
	 */
	/*public void setLeafNodeIdAddress(String leafNodeIdAddress) {
		this.leafNodeIdAddress = leafNodeIdAddress;
	}*/

	/**
	 * Checks if is file not found on any node.
	 *
	 * @return the fileNotFoundOnAnyNode
	 */
	/*public boolean isFileNotFoundOnAnyNode() {
		return fileNotFoundOnAnyNode;
	}*/

	/**
	 * Sets the file not found on any node.
	 *
	 * @param fileNotFoundOnAnyNode the fileNotFoundOnAnyNode to set
	 */
	/*public void setFileNotFoundOnAnyNode(boolean fileNotFoundOnAnyNode) {
		this.fileNotFoundOnAnyNode = fileNotFoundOnAnyNode;
	}*/

	/**
	 * Instantiates a new message ID.
	 *
	 * @param leafNodeId the leaf node id
	 * @param sequenceNumber the sequence number
	 */
	public MessageID(String leafNodeId, Long sequenceNumber) {
		this();
		this.leafNodeId = leafNodeId;
		this.sequenceNumber = sequenceNumber;
	}
	
	/**
	 * Gets the leaf node id.
	 *
	 * @return the leafNodeId
	 */
	public String getLeafNodeId() {
		return leafNodeId;
	}
	
	/**
	 * Sets the leaf node id.
	 *
	 * @param leafNodeId the leafNodeId to set
	 */
	public void setLeafNodeId(String leafNodeId) {
		this.leafNodeId = leafNodeId;
	}
	
	/**
	 * Gets the sequence number.
	 *
	 * @return the sequenceNumber
	 */
	public Long getSequenceNumber() {
		return sequenceNumber;
	}
	
	/**
	 * Sets the sequence number.
	 *
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leafNodeId == null) ? 0 : leafNodeId.hashCode());
		result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageID other = (MessageID) obj;
		if (leafNodeId == null) {
			if (other.leafNodeId != null)
				return false;
		} else if (!leafNodeId.equals(other.leafNodeId))
			return false;
		if (sequenceNumber == null) {
			if (other.sequenceNumber != null)
				return false;
		} else if (!sequenceNumber.equals(other.sequenceNumber))
			return false;
		return true;
	}
}