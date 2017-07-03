package cn.mazu.storage.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.StoreCategory;

//初始库存
@Entity
@Table(name="inventory")
public class Inventory extends EntityObject{
   @DisplayField(cname="",etag=0)
   public String seqno = "";//序号
   @FilterField(fname="货品名称",o2fname="name")
   @DisplayField(cname="货品名称",etag=1)
   public String name ="";//产品名称
   @DisplayField(cname="盘点日期",etag=9)
   public Date checkDate = new Date();//盘点日期
   @DisplayField(cname="初始数量",etag=2)
   public Double amount =0.0;//产品数量
   @DisplayField(cname="货品规格",etag=3)
   public String spec = "";//规格
   @DisplayField(cname="货品编号",etag=10)
   public String  code= "";
   @DisplayField(cname="预警值",etag=11)
   public Double threshold = 0.0;
   @DisplayField(cname="货品计量单位",etag=5)
   public String unit = "";//产品计量单位
   @DisplayField(cname="供货商",etag=4)
   public String supplier = "";//厂商
   //public StoreCategory allocation = StoreCategory.BANCHENGPINAREA;//货位，实际情况是没有那么多货位，现在就是分成几个仓别，所以改成仓别
   private String allocation = "";//货位
   /*@DisplayField(cname="仓别",etag=6)
   public StoreCategory storeCategory = StoreCategory.BANCHENGPINAREA;//货位，实际情况是没有那么多货位，现在就是分成几个仓别，所以改成仓别
*/   @DisplayField(cname="材质",etag=7)
   public String texture = "";
   //@DisplayField(cname="图号",etag=8)
   //public Drawing drawing;
   
   //public List<InventoryBook> bookList = new ArrayList<InventoryBook>();//来往明细
    @Column(nullable=false)   
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	 @Column(nullable=false)   
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	@Column(nullable=false)
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	//@Column(nullable=false)
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	//@Column(nullable=false)
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	//@Column(nullable=false)
	//@Enumerated(EnumType.STRING)
	public String getAllocation() {
		return allocation;
	}
	public void setAllocation(String allocation) {
		this.allocation = allocation;
	}
	/*@Enumerated(EnumType.STRING)
	public StoreCategory getStoreCategory() {
		return storeCategory;
	}
	public void setStoreCategory(StoreCategory storeCategory) {
		this.storeCategory = storeCategory;
	}*/
	@JoinColumn(name="check_date")
	@Column(nullable=false)
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	//@Column(nullable=false)
	public String getTexture() {
		return texture;
	}
	public void setTexture(String texture) {
		this.texture = texture;
	}
	/*@OneToOne
	@JoinColumn(name="drawing_id")
	public Drawing getDrawing() {
		return drawing;
	}
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}*/
	@Column(nullable=false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public Double getThreshold() {
		return threshold;
	}
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	
	/*@OneToMany(mappedBy="inventory")
	public List<InventoryBook> getBookList() {
		return bookList;
	}
	public void setBookList(List<InventoryBook> bookList) {
		this.bookList = bookList;
	}*/
	
}
