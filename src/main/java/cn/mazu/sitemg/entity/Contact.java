package cn.mazu.sitemg.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.mazu.util.EntityObject;

@Entity
@Table(name="contact")
public class Contact extends EntityObject{
	public String address = "";
	public String tele = "";
	public String fax = "";
	public String cname = "";
	public String cQQ = "";
	public String email = "";
	public String siteaddr = "";
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTele() {
		return tele;
	}
	public void setTele(String tele) {
		this.tele = tele;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getcQQ() {
		return cQQ;
	}
	public void setcQQ(String cQQ) {
		this.cQQ = cQQ;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(name="site_addr")
	public String getSiteaddr() {
		return siteaddr;
	}
	public void setSiteaddr(String siteaddr) {
		this.siteaddr = siteaddr;
	}

	

}
