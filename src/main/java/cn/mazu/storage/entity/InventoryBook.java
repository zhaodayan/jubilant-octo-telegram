package cn.mazu.storage.entity;

import java.util.Date;

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
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.IBookType;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.workorder.entity.WorkOrder;
import cn.mazu.workorder.entity.WorkOrderTraceDetail;

/*库存来往明细*/
@Entity
@Table(name="inventory_book")
public class InventoryBook extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	public Inventory inventory;
	@DisplayField(cname="货品名称",etag=5)
	public String invName = "";
	@DisplayField(cname="货品编号",etag = 6)
	public String invNo = "";
	@DisplayField(cname="规格",etag=7)
	public String spec = "";//规格
	@FilterField(fname="工单号",o2fname="traceDetail.workOrder.orderno")
	public WorkOrder workOrder;//对应的工单,出库单
	@FilterField(fname="图号",o2fname="traceDetail.surfaceDetail.detailcode")
    public SignDoc sd ;//非销售合同，入库单
    public WorkOrderTraceDetail traceDetail;//入库，【工单管理】-执行“完结”操作，则该工单下所有跟踪明细记录被当做入库记录
    
    @DisplayField(cname="来往数量",etag=2)
    public Double bookamt = 0.0;//来往数量
    @DisplayField(cname="来往日期",etag=3)
    public Date bookDate = new Date();//来往日期
    @DisplayField(cname="记录员",etag=4)
    public String recorder = "";//记录员
    @DisplayField(cname="类型",etag=1)
    public IBookType ibType = IBookType.INBOOK;
    @DisplayField(cname="仓别",etag=8)
    public StoreCategory sc = StoreCategory.BANCHENGPINAREA;
    
    
    
    @ManyToOne
    @JoinColumn(name="inventory_id")
    //@Column(nullable=false)//@ManyToOne,@Column只允许出现一个
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
    @Transient		
	public String getInvName() {
    	return getInventory()==null?this.getTraceDetail().getSurfaceDetail().getGeneralElement().getName()
    			:getInventory().getName();
	}
	public void setInvName(String invName) {
		this.invName = invName;
	}
	@Transient
	public String getInvNo() {
		return getInventory()==null?this.getTraceDetail().getSurfaceDetail().getGeneralElement().getCode()
				:getInventory().getCode();
	}
	public void setInvNo(String invNo) {
		this.invNo = invNo;
	}
	@Transient
	public String getSpec() {
		return getInventory()==null?this.getTraceDetail().getSurfaceDetail().getDetailcode():
   			                   getInventory().getSpec();
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	@ManyToOne
	@JoinColumn(name="workorder_id")
	//@Column(nullable=false)
	public WorkOrder getWorkOrder() {
		return workOrder;
	}
	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}
	@Column(name="book_amt",nullable=false)
	public Double getBookamt() {
		//return this.getTraceDetail()==null?bookamt:this.getTraceDetail().getSurfaceDetail().getAmount();
		return bookamt;
	}
	public void setBookamt(Double bookamt) {
		this.bookamt = bookamt;
	}
	@Column(name="book_date",nullable=false)
	public Date getBookDate() {
		return bookDate;
	}
	public void setBookDate(Date bookDate) {
		this.bookDate = bookDate;
	}
	//@Column(nullable=false)
	public String getRecorder() {
		return recorder;
	}
	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}
	@Enumerated(EnumType.STRING)
	public IBookType getIbType() {
		return ibType;
	}
	public void setIbType(IBookType ibType) {
		this.ibType = ibType;
	}
	@ManyToOne
	@JoinColumn(name="signdoc_id")
	public SignDoc getSd() {
		return sd;
	}
	public void setSd(SignDoc sd) {
		this.sd = sd;
	}
	@Enumerated(EnumType.STRING)
	public StoreCategory getSc() {
		return sc;
	}
	public void setSc(StoreCategory sc) {
		this.sc = sc;
	}
	@OneToOne
	@JoinColumn(name="tracedetail_id")
	public WorkOrderTraceDetail getTraceDetail() {
		return traceDetail;
	}
	public void setTraceDetail(WorkOrderTraceDetail traceDetail) {
		this.traceDetail = traceDetail;
	}
	
	/*@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}*/
	
}
