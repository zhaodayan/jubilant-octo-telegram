package cn.mazu.sitemg.entity;


import java.util.Date;

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
@Table(name="repair")
public class Repair extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@DisplayField(cname="产品名称",etag=1)
	public String proname = "";//产品名称
	@DisplayField(cname="产品编号",etag=2)
	public String proid = "";//产品编号
	@DisplayField(cname="生产原料名称",etag=3)
	public String matername = "";//生产原料名称：
	@DisplayField(cname="模具名称",etag=4)
	public String modname = "";//模具名称
	@DisplayField(cname="模具编号",etag=5)
	public String modid = "";//模具编号
	@DisplayField(cname="需求完成日",etag=6)
	public Date completeDate = new Date();//需求完成日
	@DisplayField(cname="修理原因",etag=7)
	public String repairCause = "";//修理原因
	@DisplayField(cname="其他要求",etag=8)
	public String otherRequest = "";//其他要求
	@DisplayField(cname="申请人",etag=9)
	public String applicant = "";//申请人
	@DisplayField(cname="申请日期",etag=10)
	public Date applyDate = new Date();//申请日期
	
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public String getProname() {
		return proname;
	}
	public void setProname(String proname) {
		this.proname = proname;
	}

	public String getProid() {
		return proid;
	}
	public void setProid(String proid) {
		this.proid = proid;
	}

	public String getMatername() {
		return matername;
	}
	public void setMatername(String matername) {
		this.matername = matername;
	}

	public String getModname() {
		return modname;
	}
	public void setModname(String modname) {
		this.modname = modname;
	}

	public String getModid() {
		return modid;
	}
	public void setModid(String modid) {
		this.modid = modid;
	}

	public Date getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="repair_cause")
	public String getRepairCause() {
		return repairCause;
	}
	public void setRepairCause(String repairCause) {
		this.repairCause = repairCause;
	}
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="other_request")
	public String getOtherRequest() {
		return otherRequest;
	}
	public void setOtherRequest(String otherRequest) {
		this.otherRequest = otherRequest;
	}
	
	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	@Column(name="apply_date")
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	
	

}
