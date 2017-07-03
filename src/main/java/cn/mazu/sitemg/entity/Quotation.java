package cn.mazu.sitemg.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.QuotaType;

@Entity
@Table(name="quotation")
public class Quotation extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@DisplayField(cname="报价类型",etag=1)
	public QuotaType type=QuotaType.TRADI;//报价类型：传统报价，MGP报价
	@DisplayField(cname="模块数量",etag=2)
	public Integer modNum=0;//模块数量
	@DisplayField(cname="模座数量",etag=3)
	public Integer mobNum=0;//模座数量
	@DisplayField(cname="镶条数量",etag=4)
	public Integer mxtNum=0;//模窝镶条数量
	@DisplayField(cname="带球数量",etag=5)
	public Integer mdqNum=0;//模窝带球数量
	@DisplayField(cname="预计完成时间",etag=10)
	public Double totalDay = 0.0;//总耗时
	@DisplayField(cname="预计费用",etag=11)
	public Double totalCost = 0.0;//总费用
	
	@DisplayField(cname="客户名称",etag=5)
	public String cname="";//客户名称
	@DisplayField(cname="联系方式",etag=6)
	public String ctele="";//联系方式
	@DisplayField(cname="联系人",etag=7)
	public String clink="";//联系人
	@DisplayField(cname="邮箱",etag=8)
	public String cmail="";//邮箱
	@DisplayField(cname="联系地址",etag=9)
	public String caddr="";//联系地址
	

	public Integer getMobNum() {
		return mobNum;
	}
	public void setMobNum(Integer mobNum) {
		this.mobNum = mobNum;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public Double getTotalDay() {
		return totalDay;
	}
	public void setTotalDay(Double totalDay) {
		this.totalDay = totalDay;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	
	@Column(name="type")
	@Enumerated(EnumType.STRING)
	public QuotaType getType() {
		return type;
	}
	public void setType(QuotaType type) {
		this.type = type;
	}
	@Column(name="modNum")
	public Integer getModNum() {
		return modNum;
	}
	public void setModNum(Integer modNum) {
		this.modNum = modNum;
	}
	@Column(name="mxtNum")
	public Integer getMxtNum() {
		return mxtNum;
	}
	public void setMxtNum(Integer mxtNum) {
		this.mxtNum = mxtNum;
	}
	@Column(name="mdqNum")
	public Integer getMdqNum() {
		return mdqNum;
	}
	public void setMdqNum(Integer mdqNum) {
		this.mdqNum = mdqNum;
	}
	@Column(name="cname")
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	@Column(name="ctele")
	public String getCtele() {
		return ctele;
	}
	public void setCtele(String ctele) {
		this.ctele = ctele;
	}
	@Column(name="clink")
	public String getClink() {
		return clink;
	}
	public void setClink(String clink) {
		this.clink = clink;
	}
	@Column(name="cmail")
	public String getCmail() {
		return cmail;
	}
	public void setCmail(String cmail) {
		this.cmail = cmail;
	}
	@Column(name="caddr")
	public String getCaddr() {
		return caddr;
	}
	public void setCaddr(String caddr) {
		this.caddr = caddr;
	}
	
	
}
