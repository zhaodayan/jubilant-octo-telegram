package cn.mazu.surface.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.util.EntityObject;

//图面详情
@Entity
@Table(name="surface_detail")
public class SurfaceDetail extends EntityObject implements Cloneable {
	@DisplayField(cname="序号",etag=0)
	public String seqno = "";//序号 
	@DisplayField(cname="件号",etag=1,colspan="2")//为了匀一列给验证信息
    public String itemNo = "";//件号
	/*@DisplayField(cname="件名",etag=2)
    public String itemName = "";//件名*/
	@FilterField(fname="名称",o2fname="generalElement.name")
	@DisplayField(cname="件名",etag=2,colspan="2")
	public GeneralElement generalElement;
	@DisplayField(cname="数量",etag=3,colspan="2")
    public Double amount;//数量
	/*@DisplayField(cname="规格",etag=4,colspan="2")
    public String description = "";//规格*/
    @FilterField(fname="图号",o2fname="detailcode")
	@DisplayField(cname="图号或规格",etag=5,colspan="1")
	public String detailcode = "";
	/*@DisplayField(cname="图号",etag=5)
	public Drawing drawing;//图纸
	*/@DisplayField(cname="材质",etag=6,colspan="2")
    public String material = "";//材质
	@DisplayField(cname="操作",etag=7)
	public String oper = "";
    public Surface surface;//图面
    /*@DisplayField(cname="添加新图纸",etag=8)
    public String uploadDrawing = "";*/
    
    @Column(name="item_no",nullable=false)
	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}
	
	public String getDetailcode() {
		return detailcode;
	}

	public void setDetailcode(String detailcode) {
		this.detailcode = detailcode;
	}

	/*@Column(name="item_name",nullable=false)
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}*/
    @OneToOne
    @JoinColumn(name="generalElement_id")
	public GeneralElement getGeneralElement() {
		return generalElement;
	}
	
	public void setGeneralElement(GeneralElement generalElement) {
		this.generalElement = generalElement;
	}
	@Column(nullable=false)
	public Double getAmount() {
		return amount;
	}
    

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/*@Column(nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}*/
//	@Column(nullable=false)
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}
    @ManyToOne
    @JoinColumn(name="surface_id")
	public Surface getSurface() {
		return surface;
	}

	public void setSurface(Surface surface) {
		this.surface = surface;
	}
    /*@ManyToOne
    @JoinColumn(name="draw_id")
	public Drawing getDrawing() {
		return drawing;
	}
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}*/
    @Transient
	public String getSeqno() {
		return seqno;
	}

	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
    @Transient
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}

	@Override
	public SurfaceDetail clone() throws CloneNotSupportedException {
		SurfaceDetail cloned = (SurfaceDetail)super.clone();
		//cloned.generateDate = (Date)this.generateDate.clone();
		//cloned.drawing = (Drawing)this.drawing.clone();
		cloned.generalElement = (GeneralElement)this.generalElement.clone();
		return cloned;
	}
	
   /* @Transient
	public String getUploadDrawing() {
		return uploadDrawing;
	}
	public void setUploadDrawing(String uploadDrawing) {
		this.uploadDrawing = uploadDrawing;
	}*/
}
