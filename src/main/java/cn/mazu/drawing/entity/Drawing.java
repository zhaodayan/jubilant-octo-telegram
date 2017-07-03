package cn.mazu.drawing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.surface.entity.Surface;
import cn.mazu.util.EntityObject;

//图纸
@Entity
@Table(name="drawing")
public class Drawing extends EntityObject /*implements Cloneable*/{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
//	@DisplayField(cname="名称",etag=1)
    //public String name = "";//图纸名称
	@DisplayField(cname="图号或规格",etag=2)	
    public String code = "";//图纸编号或规格
	@DisplayField(cname="作者",etag=3)	
    public String author = "";//作者
	@DisplayField(cname="图面",etag=4)
	public Surface surface;
	@DisplayField(cname="零件",etag=5)
	public GeneralElement generalElement;
	/*@DisplayField(cname="图纸类型",etag=4)
	public DrawingType drawingType = DrawingType.FACEDRAWING;*/
    /*@Column(nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}*/
	@Column(nullable=false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	@ManyToOne
	@JoinColumn(name="surface_id")
	public Surface getSurface() {
		return surface;
	}
	public void setSurface(Surface surface) {
		this.surface = surface;
	}
	@OneToOne
	@JoinColumn(name="element_id")
	public GeneralElement getGeneralElement() {
		return generalElement;
	}
	public void setGeneralElement(GeneralElement generalElement) {
		this.generalElement = generalElement;
	}
	/*@Enumerated(EnumType.STRING)
	public DrawingType getDrawingType() {
		return drawingType;
	}
	public void setDrawingType(DrawingType drawingType) {
		this.drawingType = drawingType;
	}*/
	/*@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}*/
	
}
