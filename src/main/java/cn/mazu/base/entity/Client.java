package cn.mazu.base.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

/*基础数据--客户管理*/
@Entity
@Table(name="client")
public class Client extends EntityObject{
   @DisplayField(cname="",etag=0)
   public String seqno = "";
   @DisplayField(cname="客户名称",etag=1)
   public String name = "";
   @DisplayField(cname="联系人",etag=2)
   public String linkman = "";
   @DisplayField(cname="固定电话",etag=3)
   public String linktel = "";
   @DisplayField(cname="手机",etag=4)
   public String linkphone = "";
   @DisplayField(cname="传真",etag=5)
   public String linkfax = "";   
   @DisplayField(cname="e-mail",etag=6)
   public String email = "";
   @DisplayField(cname="网址",etag=7)
   public String siteAddr = "";
   @DisplayField(cname="地址",etag=8)
   public String address = "";
   @DisplayField(cname="邮政编码",etag=9)
   public String addrCode = "";
   @DisplayField(cname="开户银行",etag=10)
   public String linkbank = ""; 
   @DisplayField(cname="银行账号",etag=11)
   public String lbankNO = ""; 
//描述
   public String description = "";
   
   public String getLinkphone() {
		return linkphone;
	}
	public void setLinkphone(String linkphone) {
		this.linkphone = linkphone;
	}
	public String getLinkfax() {
		return linkfax;
	}
	public void setLinkfax(String linkfax) {
		this.linkfax = linkfax;
	}
	public String getSiteAddr() {
		return siteAddr;
	}
	public void setSiteAddr(String siteAddr) {
		this.siteAddr = siteAddr;
	}
	public String getAddrCode() {
		return addrCode;
	}
	public void setAddrCode(String addrCode) {
		this.addrCode = addrCode;
	}
	public String getLinkbank() {
		return linkbank;
	}
	public void setLinkbank(String linkbank) {
		this.linkbank = linkbank;
	}
	public String getLbankNO() {
		return lbankNO;
	}
	public void setLbankNO(String lbankNO) {
		this.lbankNO = lbankNO;
	}
   public String getEmail() {
	   return email;
   }
   public void setEmail(String email) {
	   this.email = email;
   }
   @Column(nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	@Column(nullable=false)
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	//@Column(nullable=false)
	public String getLinktel() {
		return linktel;
	}
	public void setLinktel(String linktel) {
		this.linktel = linktel;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
