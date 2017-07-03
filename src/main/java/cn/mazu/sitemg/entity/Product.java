package cn.mazu.sitemg.entity;



import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.RapidOrNot;

@Entity
@Table(name="product")
public class Product extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@DisplayField(cname="名称",etag=1)
	public String pname = "";//产品名称
	@DisplayField(cname="简介",etag=2)
	public String pintro = "";//产品简介
	@DisplayField(cname="是否快速报价",etag=4)
	public RapidOrNot rapidOrNot = RapidOrNot.RAPIDNO;//是否是快速报价，默认：不是

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public RapidOrNot getRapidOrNot() {
		return rapidOrNot;
	}
	public void setRapidOrNot(RapidOrNot rapidOrNot) {
		this.rapidOrNot = rapidOrNot;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	@Column(nullable=false)
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getPintro() {
		return pintro;
	}
	public void setPintro(String pintro) {
		this.pintro = pintro;
	}

}
