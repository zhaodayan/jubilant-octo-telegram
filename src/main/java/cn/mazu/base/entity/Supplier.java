package cn.mazu.base.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.util.EntityObject;
//基础数据--供应商管理
@Entity
@Table(name="supplier")
public class Supplier extends EntityObject {
   @DisplayField(cname="",etag=0)
   public String seqno = "";
   @FilterField(fname="名称",o2fname="name")
   @DisplayField(cname="供方名称",etag=1)
   public String name = "";
   @DisplayField(cname="联系人",etag=2)
   public String linkman = "";
   @DisplayField(cname="电话",etag=3)
   public String linktel = "";
   @DisplayField(cname="传真",etag=4)
   public String linkfax = "";
   @DisplayField(cname="邮箱",etag=5)
   public String linkemail = "";
//描述
   public String description = "";
   public String getLinkemail() {
	   return linkemail;
   }
   public void setLinkemail(String linkemail) {
	   this.linkemail = linkemail;
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
//	@Column(nullable=false)
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
	/*public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}*/
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLinkfax() {
		return linkfax;
	}
	public void setLinkfax(String linkfax) {
		this.linkfax = linkfax;
	}
	
}
