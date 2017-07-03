/*package cn.mazu.auth.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.mazu.util.EntityObject;


*//**
 * A JPA implementation for an authentication token.
 * 
 * This class is used by {@link AuthInfo}, and stores authentication tokens.
 * 
 * @see AuthInfo
 *//*
@Entity
@Table(name="AuthToken")
public class AuthToken extends EntityObject{
	*//**
	 * Default constructor.
	 *//*
	public AuthToken() {
	}

	*//**
	 * Constructor.
	 *//*
	public AuthToken(String value, Date expiryDate) {
		this.value = value;
		this.expiryDate = expiryDate;
	}

	*//**
	 * Returns the token owner.
	 *//*
	@ManyToOne(cascade=CascadeType.REFRESH)
	@JoinColumn(name="authInfo_id")
	public AuthInfo getAuthInfo() {
		return authInfo;
	}
	
	*//**
	 * Set the token owner.
	 *//*
	public void setAuthInfo(AuthInfo info) {
		this.authInfo = info;
	}

	*//**
	 * Returns the token value.
	 *//*
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	*//**
	 * Returns the token expiry date.
	 *//*
	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY) 
//	public long getId() {
//		return id;
//	}
//	
//	public void setId(long id) {
//		this.id = id;
//	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

//	private long id;
	private int version;
	private AuthInfo authInfo;
	private String value;
	private Date expiryDate;
}*/