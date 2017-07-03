package cn.mazu.sitemg.entity;



import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

@Entity
@Table(name="device")
public class Device extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@DisplayField(cname="名称",etag=1)
	public String dname = "";//设备名称
	@DisplayField(cname="简介",etag=2)
	public String dintro = "";//设备简介
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	@Column(nullable=false)
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getDintro() {
		return dintro;
	}
	public void setDintro(String dintro) {
		this.dintro = dintro;
	}

}
