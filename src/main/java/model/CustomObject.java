/**
 * 
 */
package model;

import java.io.Serializable;
import java.util.List;

/**
 * The Class CustomObject.
 *
 * @author Shantanoo
 */
public class CustomObject implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6506311153868340814L;

	/** The method name. */
	private String methodName;
	
	/** The parameters. */
	private List<Object> parameters;
	
	/** The p 2 P file. */
	private P2PFile p2PFile;
	
	/**
	 * Instantiates a new custom object.
	 *
	 * @param methodName the method name
	 * @param parameters the parameters
	 * @param p2pFile the p 2 p file
	 */
	public CustomObject(String methodName, List<Object> parameters, P2PFile p2pFile) {
		super();
		this.methodName = methodName;
		this.parameters = parameters;
		this.p2PFile = p2pFile;
	}
	
	/**
	 * Gets the method name.
	 *
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * Sets the method name.
	 *
	 * @param methodName the new method name
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public List<Object> getParameters() {
		return parameters;
	}
	
	/**
	 * Sets the parameters.
	 *
	 * @param parameters the new parameters
	 */
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * Gets the p 2 P file.
	 *
	 * @return the p 2 P file
	 */
	public P2PFile getP2PFile() {
		return p2PFile;
	}
	
	/**
	 * Sets the p 2 P file.
	 *
	 * @param p2pFile the new p 2 P file
	 */
	public void setP2PFile(P2PFile p2pFile) {
		p2PFile = p2pFile;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((p2PFile == null) ? 0 : p2PFile.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
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
		CustomObject other = (CustomObject) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (p2PFile == null) {
			if (other.p2PFile != null)
				return false;
		} else if (!p2PFile.equals(other.p2PFile))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}
}
