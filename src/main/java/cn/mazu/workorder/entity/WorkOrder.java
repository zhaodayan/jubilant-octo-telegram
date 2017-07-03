package cn.mazu.workorder.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.base.entity.Client;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.surface.entity.Surface;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.WorkOrderStatus;
//工单
@Entity
@Table(name="workorder")
public class WorkOrder extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@FilterField(fname="工单号",o2fname="orderno")
	@DisplayField(cname="工单号",etag=1)
    public String orderno = "";//工单号
	@FilterField(fname="品名",o2fname="name")
	@DisplayField(cname="品名",etag=2)
    public String name = "";//品名
	//@FilterField(fname="客户名称")
	@DisplayField(cname="客户名称",etag=3)
    //public String companyName = "";//客户名称
	public Client client;
	@DisplayField(cname="数量",etag=4)
    public Double amount = 0.0;//数量
	//@FilterField(fname="交付日期")
	@DisplayField(cname="交付日期",etag=5)
    public Date deliveryDate;//交付日期
	//@FilterField(fname="下单日期")
	@DisplayField(cname="下单日期",etag=6)
    public Date orderDate ;//下单日期
	@DisplayField(cname="预计交期",etag=7)
    public Date predictDate ;//预计交期
	@DisplayField(cname="实际交期",etag=8)
    public Date realDate ;//实际交期
	@DisplayField(cname="出货日期",etag=9)
    public Date shipDate ;//出货日期
	@DisplayField(cname="图面",etag=11)
	public Surface surface;//图面
	@DisplayField(cname="状态",etag=13)
	public WorkOrderStatus wostatus = WorkOrderStatus.ACTIVATED;
	@DisplayField(cname="组装日期",etag=15)
	public Date assembleDate = new Date();
	@DisplayField(cname="备注",etag=10)
	public String remark = "";//备注
	@DisplayField(cname="查看明细",etag=14,dename="cn.mazu.workorder.entity.WorkOrderTraceDetail")
	public String viewDetail = "";
	@DisplayField(cname="编辑明细",etag=12,dename="cn.mazu.workorder.entity.WorkOrderTraceDetail")
    public String oper = "";
	//工单跟踪明细
	public List<WorkOrderTraceDetail> wotracelist = new ArrayList<WorkOrderTraceDetail>();
	public List<SignDoc> sdList = new LinkedList<SignDoc>();//成本核算处要访问该list，故选双向链表
   // public List<InventoryBook> bookList = new ArrayList<InventoryBook>();
    @Column(name="order_no",nullable=false)
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	@Column(name="name")//,nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/*@Column(name="company_name",nullable=false)
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}*/
    @ManyToOne
    @JoinColumn(name="client_id")
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	@Column(name="delivery_date")
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	@Column(name="order_date")//,nullable=false)
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	@Column(name="predict_date")
	public Date getPredictDate() {
		return predictDate;
	}
	public void setPredictDate(Date predictDate) {
		this.predictDate = predictDate;
	}
	@Column(name="real_date")
	public Date getRealDate() {
		return realDate;
	}
	public void setRealDate(Date realDate) {
		this.realDate = realDate;
	}
	@Column(name="ship_date")
	public Date getShipDate() {
		return shipDate;
	}
	public void setShipDate(Date shipDate) {
		this.shipDate = shipDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Transient
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	@OneToOne
	@JoinColumn(name="surface_id")
	public Surface getSurface() {
		return surface;
	}
	public void setSurface(Surface surface) {
		this.surface = surface;
	}
	@Enumerated(EnumType.STRING)
	public WorkOrderStatus getWostatus() {
		return wostatus;
	}
	public void setWostatus(WorkOrderStatus wostatus) {
		this.wostatus = wostatus;
	}
	@OneToMany(mappedBy="workOrder")
	public List<WorkOrderTraceDetail> getWotracelist() {
		return wotracelist;
	}
	public void setWotracelist(List<WorkOrderTraceDetail> wotracelist) {
		this.wotracelist = wotracelist;
	}
	@Transient
	@Column(columnDefinition="varchar(255) default ''")
	public String getViewDetail() {
		return viewDetail;
	}
	public void setViewDetail(String viewDetail) {
		this.viewDetail = viewDetail;
	}
	@OneToMany(mappedBy="workOrder")
	public List<SignDoc> getSdList() {
		return sdList;
	}
	public void setSdList(List<SignDoc> sdList) {
		this.sdList = sdList;
	}
	@Column(name="assemble_date")
	public Date getAssembleDate() {
		return assembleDate;
	}
	public void setAssembleDate(Date assembleDate) {
		this.assembleDate = assembleDate;
	}
	
	/*@Column(name="resemble")
	public Date getResembleDate() {
		return resembleDate;
	}
	public void setResembleDate(Date resembleDate) {
		this.resembleDate = resembleDate;
	}*/
	
}
