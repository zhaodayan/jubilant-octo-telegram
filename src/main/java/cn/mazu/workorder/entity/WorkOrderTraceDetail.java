package cn.mazu.workorder.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.WOTraceDetailStatus;

//工单跟踪明细表//外协—下料—M（铣床）—H/T—G（磨床）—CNC—E（放电）—W/C（线割—表面处理—组装c
@Entity
@Table(name="workorder_tracedetail")
public class WorkOrderTraceDetail extends EntityObject{
    public WorkOrder workOrder;//工单表
    //public WOTraceFlow flow;//工单跟踪流程
    @FilterField(fname="图号",o2fname="surfaceDetail.detailcode")
    public SurfaceDetail surfaceDetail;//图面明细
    //第一行字段
    @DisplayField(cname="",etag=0,rowspan="2")
    public String seqno;
    //@FilterField(fname="名称",o2fname="name")
    @DisplayField(cname="名称",etag=1,rowspan="2")
    public String name = "";
    @DisplayField(cname="图号",etag=2,rowspan="2")
    public String drawingno = "";
    @DisplayField(cname="数量",etag=3,rowspan="2")
    public short quantity;
    @DisplayField(cname="外协",etag=4,colspan="2")
    public String WX = "";
    @DisplayField(cname="下料",etag=5,colspan="2")
    public String XL = "";
    @DisplayField(cname="铣床",etag=9,colspan="4")
    public String XC = "";
    @DisplayField(cname="H/T",etag=6,colspan="2")
    public String HT = "";
    @DisplayField(cname="磨床",etag=10,colspan="4")
    public String MC = "";
    @DisplayField(cname="CNC",etag=12,colspan="4")
    public String CNC = "";
    @DisplayField(cname="放电",etag=11,colspan="4")
    public String FD = "";
    @DisplayField(cname="W/C",etag=7,colspan="2")
    public String WC = "";
    @DisplayField(cname="表面处理",etag=8,colspan="2")
    public String BMCL = "";
    @DisplayField(cname="Q/C",etag=16,colspan="2")
    public String QC = "";
   
    
    @FilterField(fname="名称",o2fname="surfaceDetail.generalElement.name")
    //@DisplayField(cname="组装",etag=13,rowspan="2")
    public String assembleInfo = "";
    @DisplayField(cname="备注",etag=14,rowspan="2")
    public String remark = "";//备注
    //@DisplayField(cname="状态",etag=15,rowspan="2")
    public WOTraceDetailStatus detailStatus /*= WOTraceDetailStatus.TRACKING*/;//明细的状态
    //@DisplayField(cname="排序",etag=16,rowspan="2")    
    public Integer index_ = 1;//
    public boolean showflag = true;//显示标志
    //开启第二行字段
    //外协
	@DisplayField(cname="预计日期",etag=17,trindex=2)
	public String wxyjInfo = "";
	@DisplayField(cname="实际日期",etag=18,trindex=2)
	public String wxsjInfo = "";
	//下料
	@DisplayField(cname="预计日期",etag=19,trindex=2)
	public String xlyjInfo = "";
	@DisplayField(cname="实际日期",etag=20,trindex=2)
	public String xlsjInfo = "";
	//铣床
	@DisplayField(cname="预计日期",etag=27,trindex=2)
	public String xcyjInfo = "";
	@DisplayField(cname="预计工时",etag=28,trindex=2)//wh==work hour
	public Double xcyjwh = 0.0;
	@DisplayField(cname="实际日期",etag=29,trindex=2)
	public String xcsjInfo;
	@DisplayField(cname="实际工时",etag=30,trindex=2)
	public Double xcsjwh = 0.0;
	//ht
	@DisplayField(cname="预计日期",etag=21,trindex=2)
	public String htyjInfo = "";
	@DisplayField(cname="实际日期",etag=22,trindex=2)
	public String htsjInfo = "";
	//磨床
	@DisplayField(cname="预计日期",etag=31,trindex=2)
	public String mcyjInfo = "";
	@DisplayField(cname="预计工时",etag=32,trindex=2)
	public Double mcyjwh = 0.0;
	@DisplayField(cname="实际日期",etag=33,trindex=2)
	public String mcsjInfo = "";
	@DisplayField(cname="实际工时",etag=34,trindex=2)
	public Double mcsjwh = 0.0;
	//cnc
	@DisplayField(cname="预计日期",etag=39,trindex=2)
	public String cncyjInfo = "";
	@DisplayField(cname="预计工时",etag=40,trindex=2)
	public Double cncyjwh = 0.0;
	@DisplayField(cname="实际日期",etag=41,trindex=2)
	public String cncsjInfo = "";
	@DisplayField(cname="实际工时",etag=42,trindex=2)
	public Double cncsjwh = 0.0;
	//放电
	@DisplayField(cname="预计日期",etag=35,trindex=2)
	public String fdyjInfo = "";
	@DisplayField(cname="预计工时",etag=36,trindex=2)
	public Double fdyjwh = 0.0;
	@DisplayField(cname="实际日期",etag=37,trindex=2)
	public String fdsjInfo = "";
	@DisplayField(cname="实际工时",etag=38,trindex=2)
	public Double fdsjwh = 0.0;
	//wc
	@DisplayField(cname="预计日期",etag=23,trindex=2)
	public String wcyjInfo = "";
	@DisplayField(cname="实际日期",etag=24,trindex=2)
	public String wcsjInfo = "";
	//表面处理
	@DisplayField(cname="预计日期",etag=25,trindex=2)
	public String bmyjInfo = "";
	@DisplayField(cname="实际日期",etag=26,trindex=2)
	public String bmsjInfo = "";
	//QC
	@DisplayField(cname="预计日期",etag=43,trindex=2)
	public String qcyjInfo = "";
	@DisplayField(cname="实际日期",etag=44,trindex=2)
	public String qcsjInfo = "";
	
	@ManyToOne
	@JoinColumn(name="workorder_id")
	public WorkOrder getWorkOrder() {
		return workOrder;
	}
	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}
	/*@ManyToOne
	@JoinColumn(name="flow_id")
	public WOTraceFlow getFlow() {
		return flow;
	}
	public void setFlow(WOTraceFlow flow) {
		this.flow = flow;
	}*/
	
	/*public Date getAssembleDate() {
		return assembleDate;
	}
	public void setAssembleDate(Date assembleDate) {
		this.assembleDate = assembleDate;
	}*/
	@OneToOne
	@JoinColumn(name="surfacedetail_id")
	public SurfaceDetail getSurfaceDetail() {
		return surfaceDetail;
	}
	public void setSurfaceDetail(SurfaceDetail surfaceDetail) {
		this.surfaceDetail = surfaceDetail;
	}
	public String getRemark() {
		return remark;
	}
	public String getAssembleInfo() {
		return assembleInfo;
	}
	public void setAssembleInfo(String assembleInfo) {
		this.assembleInfo = assembleInfo;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Transient
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Transient
	public String getDrawingno() {
		return drawingno;
	}
	public void setDrawingno(String drawingno) {
		this.drawingno = drawingno;
	}
	@Transient
	public short getQuantity() {
		return quantity;
	}
	public void setQuantity(short quantity) {
		this.quantity = quantity;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	@Transient
	public String getWX() {
		return WX;
	}
	public void setWX(String wX) {
		WX = wX;
	}
	@Transient
	public String getXL() {
		return XL;
	}
	public void setXL(String xL) {
		XL = xL;
	}
	@Transient
	public String getXC() {
		return XC;
	}
	public void setXC(String xC) {
		XC = xC;
	}
	@Transient
	public String getHT() {
		return HT;
	}
	public void setHT(String hT) {
		HT = hT;
	}
	@Transient
	public String getMC() {
		return MC;
	}
	public void setMC(String mC) {
		MC = mC;
	}
	@Transient
	public String getFD() {
		return FD;
	}
	public void setFD(String fD) {
		FD = fD;
	}
	@Transient
	public String getWC() {
		return WC;
	}
	public void setWC(String wC) {
		WC = wC;
	}
	@Transient
	public String getCNC() {
		return CNC;
	}
	public void setCNC(String cNC) {
		CNC = cNC;
	}
	@Transient
	public String getBMCL() {
		return BMCL;
	}
	public void setBMCL(String bMCL) {
		BMCL = bMCL;
	}
	public String getWxyjInfo() {
		return wxyjInfo;
	}
	public void setWxyjInfo(String wxyjInfo) {
		this.wxyjInfo = wxyjInfo;
	}
	public String getWxsjInfo() {
		return wxsjInfo;
	}
	public void setWxsjInfo(String wxsjInfo) {
		this.wxsjInfo = wxsjInfo;
	}
	public String getXlyjInfo() {
		return xlyjInfo;
	}
	public void setXlyjInfo(String xlyjInfo) {
		this.xlyjInfo = xlyjInfo;
	}
	public String getXlsjInfo() {
		return xlsjInfo;
	}
	public void setXlsjInfo(String xlsjInfo) {
		this.xlsjInfo = xlsjInfo;
	}
	public String getXcyjInfo() {
		return xcyjInfo;
	}
	public void setXcyjInfo(String xcyjInfo) {
		this.xcyjInfo = xcyjInfo;
	}
	public String getXcsjInfo() {
		return xcsjInfo;
	}
	public void setXcsjInfo(String xcsjInfo) {
		this.xcsjInfo = xcsjInfo;
	}
	public String getHtyjInfo() {
		return htyjInfo;
	}
	public void setHtyjInfo(String htyjInfo) {
		this.htyjInfo = htyjInfo;
	}
	public String getHtsjInfo() {
		return htsjInfo;
	}
	public void setHtsjInfo(String htsjInfo) {
		this.htsjInfo = htsjInfo;
	}
	public String getMcyjInfo() {
		return mcyjInfo;
	}
	public void setMcyjInfo(String mcyjInfo) {
		this.mcyjInfo = mcyjInfo;
	}
	public String getMcsjInfo() {
		return mcsjInfo;
	}
	public void setMcsjInfo(String mcsjInfo) {
		this.mcsjInfo = mcsjInfo;
	}
	public String getFdyjInfo() {
		return fdyjInfo;
	}
	public void setFdyjInfo(String fdyjInfo) {
		this.fdyjInfo = fdyjInfo;
	}
	public String getFdsjInfo() {
		return fdsjInfo;
	}
	public void setFdsjInfo(String fdsjInfo) {
		this.fdsjInfo = fdsjInfo;
	}
	public String getCncyjInfo() {
		return cncyjInfo;
	}
	public void setCncyjInfo(String cncyjInfo) {
		this.cncyjInfo = cncyjInfo;
	}
	public String getCncsjInfo() {
		return cncsjInfo;
	}
	public void setCncsjInfo(String cncsjInfo) {
		this.cncsjInfo = cncsjInfo;
	}
	public String getWcyjInfo() {
		return wcyjInfo;
	}
	public void setWcyjInfo(String wcyjInfo) {
		this.wcyjInfo = wcyjInfo;
	}
	public String getWcsjInfo() {
		return wcsjInfo;
	}
	public void setWcsjInfo(String wcsjInfo) {
		this.wcsjInfo = wcsjInfo;
	}
	public String getBmyjInfo() {
		return bmyjInfo;
	}
	public void setBmyjInfo(String bmyjInfo) {
		this.bmyjInfo = bmyjInfo;
	}
	public String getBmsjInfo() {
		return bmsjInfo;
	}
	public void setBmsjInfo(String bmsjInfo) {
		this.bmsjInfo = bmsjInfo;
	}
	public Integer getIndex_() {
		return index_;
	}
	public void setIndex_(Integer index_) {
		this.index_ = index_;
	}
	public boolean isShowflag() {
		return showflag;
	}
	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}
	@Column(nullable=false)
	public Double getXcyjwh() {
		return xcyjwh;
	}
	public void setXcyjwh(Double xcyjwh) {
		this.xcyjwh = xcyjwh;
	}
	@Column(nullable=false)
	public Double getXcsjwh() {
		return xcsjwh;
	}
	public void setXcsjwh(Double xcsjwh) {
		this.xcsjwh = xcsjwh;
	}
	@Column(nullable=false)
	public Double getMcyjwh() {
		return mcyjwh;
	}
	public void setMcyjwh(Double mcyjwh) {
		this.mcyjwh = mcyjwh;
	}
	@Column(nullable=false)
	public Double getMcsjwh() {
		return mcsjwh;
	}
	public void setMcsjwh(Double mcsjwh) {
		this.mcsjwh = mcsjwh;
	}
	@Column(nullable=false)
	public Double getFdyjwh() {
		return fdyjwh;
	}
	public void setFdyjwh(Double fdyjwh) {
		this.fdyjwh = fdyjwh;
	}
	@Column(nullable=false)
	public Double getFdsjwh() {
		return fdsjwh;
	}
	public void setFdsjwh(Double fdsjwh) {
		this.fdsjwh = fdsjwh;
	}
	@Column(nullable=false)
	public Double getCncyjwh() {
		return cncyjwh;
	}
	public void setCncyjwh(Double cncyjwh) {
		this.cncyjwh = cncyjwh;
	}
	@Column(nullable=false)
	public Double getCncsjwh() {
		return cncsjwh;
	}
	public void setCncsjwh(Double cncsjwh) {
		this.cncsjwh = cncsjwh;
	}
	@Transient
	public String getQC() {
		return QC;
	}
	public void setQC(String qC) {
		QC = qC;
	}
	public String getQcyjInfo() {
		return qcyjInfo;
	}
	public void setQcyjInfo(String qcyjInfo) {
		this.qcyjInfo = qcyjInfo;
	}
	public String getQcsjInfo() {
		return qcsjInfo;
	}
	public void setQcsjInfo(String qcsjInfo) {
		this.qcsjInfo = qcsjInfo;
	}
	//@Enumerated(EnumType.STRING)
	//@Column(columnDefinition="varchar(255) default 'TRACKING'")
	@Column(columnDefinition="varchar(255) default ''")
	public WOTraceDetailStatus getDetailStatus() {
		return detailStatus;
	}
	public void setDetailStatus(WOTraceDetailStatus detailStatus) {
		this.detailStatus = detailStatus;
	}
	
}
