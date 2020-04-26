/**
 * 
 */
package model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Shantanoo
 *
 */
public class CustomObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6506311153868340814L;

	private String methodName;
	private List<Object> parameters;
	private P2PFile p2PFile;
	/**
	 * @param methodName
	 * @param parameters
	 * @param p2pFile
	 */
	public CustomObject(String methodName, List<Object> parameters, P2PFile p2pFile) {
		super();
		this.methodName = methodName;
		this.parameters = parameters;
		this.p2PFile = p2pFile;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<Object> getParameters() {
		return parameters;
	}
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	public P2PFile getP2PFile() {
		return p2PFile;
	}
	public void setP2PFile(P2PFile p2pFile) {
		p2PFile = p2pFile;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((p2PFile == null) ? 0 : p2PFile.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}
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
