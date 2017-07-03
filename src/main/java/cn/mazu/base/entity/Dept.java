package cn.mazu.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

//基础数据----部门
@Entity
@Table(name="dept")
public class Dept extends EntityObject{
   @DisplayField(cname="",etag=0)
   public String seqno = "";
   @DisplayField(cname="名称",etag=1)
   public String name="";
   @DisplayField(cname="部门编码",etag=2)
   public String code = "";
   @DisplayField(cname="父级部门",etag=3)
   public Dept parent;
   @Column(nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	@Column(nullable=false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@ManyToOne
	@JoinColumn(name="parent_id")
	public Dept getParent() {
		return parent;
	}
	public void setParent(Dept parent) {
		this.parent = parent;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	
}

