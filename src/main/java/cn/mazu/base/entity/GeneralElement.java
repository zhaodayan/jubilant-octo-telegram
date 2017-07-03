package cn.mazu.base.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.ElementTechnology;

//通用零件字典
@Entity
@Table(name="general_element")
public class GeneralElement extends EntityObject implements Cloneable{
	@DisplayField(cname="",etag=0)
	public String seqno = "";
	@DisplayField(cname="零件名称",etag=1)
    public String name = "";//零件名称
	@DisplayField(cname="零件编码",etag=2)
    public String code = "";//零件编码
	@DisplayField(cname="加工周期",etag=3)
    public short period;//加工周期
	@DisplayField(cname="加工工艺",etag=4)
    //public List<ElementTechnology> technologyList = new ArrayList<ElementTechnology>();//工艺
	public String technologyStr = "";
    @Column(nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	@Column(nullable=false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(nullable=false)
	public short getPeriod() {
		return period;
	}
	public void setPeriod(short period) {
		this.period = period;
	}
	/*@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	public ElementTechnology getTechnology() {
		return technology;
	}
	public void setTechnology(ElementTechnology technology) {
		this.technology = technology;
	}*/
	
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	/*@OneToMany
	@JoinColumn(name="techstr")
	public List<ElementTechnology> getTechnologyList() {
		return technologyList;
	}
	public void setTechnologyList(List<ElementTechnology> technologyList) {
		this.technologyList = technologyList;
	}
*/
	public String getTechnologyStr() {
		return technologyStr;
	}
	public void setTechnologyStr(String technologyStr) {
		this.technologyStr = technologyStr;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
}
