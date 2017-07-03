package cn.mazu.surface.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

//图面，零件的cad
@Entity
@Table(name="surface")
public class Surface extends EntityObject{
   @DisplayField(cname="",etag=0)
   public String seqno = "";//序号
   @DisplayField(cname="公司名称",etag=1)
   public String companyName = "";//公司名称
   @DisplayField(cname="图面名称",etag=2)
   public String name = "";//名称
   @DisplayField(cname="制表",etag=3)
   public String lister = "";//制表
   @DisplayField(cname="检查",etag=4)
   public String checker = "";//检查
   @DisplayField(cname="核准",etag=5)
   public String approver = "";//核准
/*   @DisplayField(cname="图号",etag=6)
   public Drawing drawing;//图号
*/ @DisplayField(cname="图号",etag=6)
   public String code;
   @DisplayField(cname="编辑明细",etag=7,dename="cn.mazu.surface.entity.SurfaceDetail")
   public String oper = "";
   public Surface copySurface;//复制的图面
   public List<SurfaceDetail> sdList = new ArrayList<SurfaceDetail>();
   //public WorkOrder workOrder;//关联工单
   
   @Column(name="company_name")//,nullable=false)   
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	@Transient
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	@Column(nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLister() {
		return lister;
	}
	public void setLister(String lister) {
		this.lister = lister;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	/*@Column(name="surface_no",nullable=false)
	public String getSurfaceNo() {
		return surfaceNo;
	}
	public void setSurfaceNo(String surfaceNo) {
		this.surfaceNo = surfaceNo;
	}*/
	
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	/*@ManyToOne
	@JoinColumn(name="drawing_id")
	public Drawing getDrawing() {
		return drawing;
	}
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}*/
	@ManyToOne
	@JoinColumn(name="copy_id")
	public Surface getCopySurface() {
		return copySurface;
	}
	public void setCopySurface(Surface copySurface) {
		this.copySurface = copySurface;
	}
	@Column(nullable=false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@OneToMany(mappedBy="surface",fetch=FetchType.LAZY)
	public List<SurfaceDetail> getSdList() {
		return sdList;
	}
	public void setSdList(List<SurfaceDetail> sdList) {
		this.sdList = sdList;
	}
	
}
