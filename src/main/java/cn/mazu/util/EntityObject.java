package cn.mazu.util;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class EntityObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2041415260596696499L;

	/**
	 * Generate a UUID for record.
	 */
	public String id=UUID.randomUUID().toString();
	
	/**
	 * Time of generate record.
	 */
	public Date generateDate = new Date();
	/*
	 * code for different login
	 * */
    public String dataCode = "";
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getGenerateDate() {
		return generateDate;
	}

	public void setGenerateDate(Date generateDate) {
		this.generateDate = generateDate;
	}
    
	public String getDataCode() {
		return dataCode;
	}

	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EntityObject ){
			EntityObject o = (EntityObject)obj;
			boolean b = o.getId().equals(this.getId());
			return b;
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
