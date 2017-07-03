package cn.mazu.sitemg.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.QuotaType;

@Entity
@Table(name="quotLibrary")
public class QuotLibrary extends EntityObject{
	public Double modpcycle = 0.0;//生产周期
	public Double modunitPrice = 0.0;//单位价格
	public Double mxtpcycle = 0.0;//生产周期
	public Double mxtunitPrice = 0.0;//单位价格
	public Double mdqpcycle = 0.0;//生产周期
	public Double mdqunitPrice = 0.0;//单位价格
	//模块
	public Double mobpcycle = 0.0;
	public Double mobunitPrice = 0.0;
	
	
	public QuotaType type=QuotaType.TRADI;//报价类型：传统报价，MGP报价

	public Double getMobpcycle() {
		return mobpcycle;
	}
	public void setMobpcycle(Double mobpcycle) {
		this.mobpcycle = mobpcycle;
	}
	public Double getMobunitPrice() {
		return mobunitPrice;
	}
	public void setMobunitPrice(Double mobunitPrice) {
		this.mobunitPrice = mobunitPrice;
	}	
	
	@Enumerated(EnumType.STRING)
	public QuotaType getType() {
		return type;
	}
	public void setType(QuotaType type) {
		this.type = type;
	}
	public Double getModpcycle() {
		return modpcycle;
	}
	public void setModpcycle(Double modpcycle) {
		this.modpcycle = modpcycle;
	}
	public Double getModunitPrice() {
		return modunitPrice;
	}
	public void setModunitPrice(Double modunitPrice) {
		this.modunitPrice = modunitPrice;
	}
	public Double getMxtpcycle() {
		return mxtpcycle;
	}
	public void setMxtpcycle(Double mxtpcycle) {
		this.mxtpcycle = mxtpcycle;
	}
	public Double getMxtunitPrice() {
		return mxtunitPrice;
	}
	public void setMxtunitPrice(Double mxtunitPrice) {
		this.mxtunitPrice = mxtunitPrice;
	}
	public Double getMdqpcycle() {
		return mdqpcycle;
	}
	public void setMdqpcycle(Double mdqpcycle) {
		this.mdqpcycle = mdqpcycle;
	}
	public Double getMdqunitPrice() {
		return mdqunitPrice;
	}
	public void setMdqunitPrice(Double mdqunitPrice) {
		this.mdqunitPrice = mdqunitPrice;
	}

	

}
