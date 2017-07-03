package cn.mazu.doc.entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.base.entity.Supplier;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.DOCType;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.workorder.entity.WorkOrder;
//签订的单证，即托工，采购，订购单子
@Entity
@Table(name="sign_doc")
public class SignDoc extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号	
	@FilterField(fname="订单号码",o2fname="orderno")
	@DisplayField(cname="订单号码",etag=2)
	public String orderno = "";//订单号码
	@DisplayField(cname="订单日期",etag=4)
	public Date orderDate;//订单日期
	@DisplayField(cname="工单号",etag=7)
	public WorkOrder workOrder ;
	@DisplayField(cname="合同类型",etag=15)
	public DOCType docType = DOCType.PURCHASE;//单证类型，采购单、订购单、托工单
	@DisplayField(cname="合同状态",etag=16)
	public WorkOrderStatus wostatus = WorkOrderStatus.ACTIVATED;
	@DisplayField(cname="工艺",etag=14)
	public String techStr = "";
	//@FilterField(fname="我方联系人")
	@DisplayField(cname="我方联系人",etag=3)
	public String proContactor = "";//联系人
	@DisplayField(cname="我方联系电话",etag=5)
	public String produceTele = "";//生产商联系电话
	@DisplayField(cname="我方传真",etag=6)
	public String produceFax = "";//传真
	@DisplayField(cname="我方地址",etag=1)
	public String proAddr = "";//地址
	@FilterField(fname="供应商名称",o2fname="supplier.name")
	@DisplayField(cname="供货商名称",etag=8)
	public Supplier supplier;
	@DisplayField(cname="供货商联系人",etag=9)
	public String supContactor = "";//联系人
	@DisplayField(cname="供货商联系电话",etag=10)
	public String supTele = "";//供货商联系电话
	@DisplayField(cname="供货商传真",etag=11)
	public String supFax = "";
	@DisplayField(cname="编辑明细",etag=13,dename="cn.mazu.doc.entity.Goods")
	public String oper = "";
	//要goodsList
	public List<Goods> goodsList = new LinkedList<Goods>();
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public WorkOrderStatus getWostatus() {
		return wostatus;
	}
	public void setWostatus(WorkOrderStatus wostatus) {
		this.wostatus = wostatus;
	}
	@Column(name="sup_fax")//,nullable=false)
	public String getSupFax() {
		return supFax;
	}

	public void setSupFax(String supFax) {
		this.supFax = supFax;
	}
	/*@Column(name="deliver_date")
	public Date getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}*/
	
	@Enumerated(EnumType.STRING)
	@Column(name="doc_type",nullable=false)
	public DOCType getDocType() {
		return docType;
	}

	public void setDocType(DOCType docType) {
		this.docType = docType;
	}

	@Column(name="pro_addr")//,nullable=false)
	public String getProAddr() {
		return proAddr;
	}

	public void setProAddr(String proAddr) {
		this.proAddr = proAddr;
	}
	@Column(name="order_no",nullable=false)
	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderNO) {
		this.orderno = orderNO;
	}
	@Column(name="pro_contactor")//,nullable=false)
	public String getProContactor() {
		return proContactor;
	}

	public void setProContactor(String proContactor) {
		this.proContactor = proContactor;
	}
	@Column(name="order_date")
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	@Column(name="pro_tele")//,nullable=false)
	public String getProduceTele() {
		return produceTele;
	}

	public void setProduceTele(String produceTele) {
		this.produceTele = produceTele;
	}
	@Column(name="pro_fax")//,nullable=false)
	public String getProduceFax() {
		return produceFax;
	}

	public void setProduceFax(String produceFax) {
		this.produceFax = produceFax;
	}
	/*@Column(name="sup_name",nullable=false)
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}*/
	@Column(name="sup_contactor")//,nullable=false)
	public String getSupContactor() {
		return supContactor;
	}

	public void setSupContactor(String supContactor) {
		this.supContactor = supContactor;
	}
	@Column(name="sup_tele")//,nullable=false)
	public String getSupTele() {
		return supTele;
	}

	public void setSupTele(String supTele) {
		this.supTele = supTele;
	}
	/*@Column(name="word_order",nullable=false)
	public String getWorkOrder() {
		return workOrder;
	}
	public void setWorkOrder(String workOrder) {
		this.workOrder = workOrder;
	}*/
    @ManyToOne
    @JoinColumn(name="workorder_id")
	public WorkOrder getWorkOrder() {
		return workOrder;
	}
	
	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}
	/*@Lob
	@Column(name="buy_terms",nullable=false)
	public String getBuyTerms() {
		return buyTerms;
	}

	public void setBuyTerms(String buyTerms) {
		this.buyTerms = buyTerms;
	}*/
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

	public String getTechStr() {
		return techStr;
	}

	public void setTechStr(String techStr) {
		this.techStr = techStr;
	}
    @ManyToOne
    @JoinColumn(name="supplier_id")
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	@OneToMany(mappedBy="signDoc")
	public List<Goods> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List<Goods> goodsList) {
		this.goodsList = goodsList;
	}
		
}
