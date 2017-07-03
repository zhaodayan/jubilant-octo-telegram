/*package cn.mazu.auth.entity;

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
 * A JPA implementation for an authentication identity
 * 
 * This class is used by {@link AuthInfo}, and stores identities.
 *//*
@Entity
@Table(name="AuthIdentity")
public class AuthIdentity extends EntityObject{

	*//**
	 * Default constructor.
	 *//*
	public AuthIdentity() {
	}

	*//**
	 * Constructor.
	 *//*
	public AuthIdentity(String provider, String identity) {
		this.provider = provider;
		this.identity = identity;
	}

	*//**
	 * Returns the identity owner.
	 *//*
	@ManyToOne(cascade={CascadeType.REFRESH,CascadeType.PERSIST})
	@JoinColumn(name="authInfo_id")
	public AuthInfo getAuthInfo() {
		return authInfo;
	}
	
	*//**
	 * Set the identity owner.
	 *//*
	
	public void setAuthInfo(AuthInfo info) {
		this.authInfo = info;
	}

	public String getProvider() {
		return provider;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
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
	private String provider;
	private String identity;
}
*/