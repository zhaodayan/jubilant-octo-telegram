package cn.mazu.doc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.util.EntityObject;
@Entity
@Table(name="goods")
public class Goods extends EntityObject {
	@DisplayField(cname="序号",etag=9)
	public String seqno = "";//序号
	@FilterField(fname="品名",o2fname="goodsName")
	@DisplayField(cname="品名",etag=1,colspan="2")
	public String goodsName;//货品名称,理论上是不应该为null的，但为了判断在非销售合同中，是否为来自库存货品的新建记录，来拿null排除，所以不设定默认的值"";
	@FilterField(fname="图号",o2fname="drawingcode")
	@DisplayField(cname="图号",etag=2)
	public String drawingcode = "";//若是图面明细带过来的，则直接取图面明细的图号；若是在后来追加记录则为用户手动填写的图号。
	public SurfaceDetail surfaceDetail;//非销售合同中有工艺重合的图面，用在非销售合同
	public Inventory inventory;//若是原材料
	@DisplayField(cname="数量",etag=3,colspan="2")
	public Double amount = 0.0;//数量
	@DisplayField(cname="材质",etag=14,colspan="2")
	public String material = "";//材料
	@DisplayField(cname="尺寸(长*宽*高)",etag=13,colspan="2")
	public String size = "";
	@DisplayField(cname="单重(KG)",etag=10,colspan="2")
	public Double unitWeight = 0.0;
	@DisplayField(cname="总重(KG)",etag=11)
	public Double totalWeight = 0.0;
	@DisplayField(cname="重量KG",etag=15,colspan="2")
	public Double weight = 0.0;
	@DisplayField(cname="单价",etag=4,colspan="2")
	public Double unitprice = 0.0;
	@DisplayField(cname="加工费",etag=12,colspan="2")
	public Double processCost = 0.0;
	@DisplayField(cname="总价",etag=5)
	public Double totalPrice = 0.0;
	@DisplayField(cname="交货日期",etag=6)
	public Date deliverDate = new Date();
	@DisplayField(cname="备注",etag=7)
	public String remark="";//备注
	@DisplayField(cname="操作",etag=8,exportflag=false)
	public String oper = "";
	
	public Double subtotal;   //小计
	//非销售合同和销售合同都有货品明细
	public SignDoc signDoc;   //对应的单证,销售单除外
	//public WorkOrder workOrder;//对应的销售单===============销售单的明细已经被取消了
	
	
	@Column(name="size")
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	@Column(name="material")
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	@Column(name="unit_weight")
	public Double getUnitWeight() {
		return unitWeight;
	}
	public void setUnitWeight(Double unitWeight) {
		this.unitWeight = unitWeight;
	}
	@Column(name="process_cost")
	public Double getProcessCost() {
		return processCost;
	}
	public void setProcessCost(Double processCost) {
		this.processCost = processCost;
	}
	@Column(name="deliver_date")
	@Temporal(TemporalType.DATE)//从数据库中取回来的值不是java.util.Date,而是java.sql.Date,若是默认的时间戳，则取到的是java.util.Date
	public Date getDeliverDate() {
		return deliverDate;
	}
	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}

	@OneToOne
	@JoinColumn(name="surfacedetail_id",nullable=true)
	public SurfaceDetail getSurfaceDetail() {
		return surfaceDetail;
	}
	public void setSurfaceDetail(SurfaceDetail surfaceDetail) {
		this.surfaceDetail = surfaceDetail;
	}
	@ManyToOne
	@JoinColumn(name="inventory_id")
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	@ManyToOne
	@JoinColumn(name = "signdoc_id")
	public SignDoc getSignDoc() {
		return signDoc;
	}
	public void setSignDoc(SignDoc signDoc) {
		this.signDoc = signDoc;
	}
	
	@Column(name="goods_name")
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	@Column(name="amount")
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	@Column(name="unit_price")
	public Double getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(Double unitprice) {
		this.unitprice = unitprice;
	}
	@Column(name="remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Column(name="sub_total")
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
		//this.subtotal = this.getAmount()*this.getUnitprice();
	}
	
	public Double getTotalPrice() {
		//return this.unitprice*this.amount;
		return this.totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		/*this.unitprice = getUnitprice();
		this.amount = getAmount();*/
		this.totalPrice = totalPrice;
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
	@Column(name="drawing_code")
	public String getDrawingcode() {
		return drawingcode;
	}
	public void setDrawingcode(String drawingcode) {
		this.drawingcode = drawingcode;
	}
	@Column(name="total_weight")
	public Double getTotalWeight() {
		return totalWeight;
	}
	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
}
