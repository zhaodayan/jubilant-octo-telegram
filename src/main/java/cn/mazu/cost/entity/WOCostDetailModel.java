package cn.mazu.cost.entity;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

//工单陈本统计明细
public class WOCostDetailModel extends EntityObject{
	   @DisplayField(cname="序号",etag=6)
	   public String seqno;//序号
	   @DisplayField(cname="名称",etag=0)
	   public String goodName = "";//零件或原材料名称
	   @DisplayField(cname="图号或规格",etag=16)
	   public String drawingCode = "";
	   @DisplayField(cname="铣床工时(h)",etag=2)
	   public Double xcwh = 0.0;//铣床工时(h)
	   @DisplayField(cname="铣床成本(￥)",etag=3)
	   public Double xccb = 0.0;
	   @DisplayField(cname="磨床工时(h)",etag=4)
	   public Double mcwh = 0.0;
	   @DisplayField(cname="磨床成本(￥)",etag=5)
	   public Double mccb = 0.0;
	   @DisplayField(cname="放电工时(h)",etag=6)
	   public Double fdwh = 0.0;
	   @DisplayField(cname="放电成本(￥)",etag=7)
	   public Double fdcb = 0.0;
	   @DisplayField(cname="cnc工时(h)",etag=8)
	   public Double cncwh = 0.0;
	   @DisplayField(cname="cnc成本(￥)",etag=9)
	   public Double cnccb = 0.0;
	   @DisplayField(cname="总工时(h)",etag=13)
	   public Double totalwh = 0.0;
	   @DisplayField(cname="工时总成本(￥)",etag=14)
	   public Double totalwhcb = 0.0;
	   @DisplayField(cname="托工成本(￥)",etag=1)
	   public Double acost = 0.0;
	   @DisplayField(cname="采购成本(￥)",etag=10)
	   public Double pcost = 0.0;
	   @DisplayField(cname="订购成本(￥)",etag=11)
	   public Double bcost = 0.0;
	   @DisplayField(cname="外发总成本(￥)",etag=12)
	   public Double outcost = 0.0;
	   @DisplayField(cname="总成本(￥)",etag=15)
	   public Double totalCb = 0.0;
	
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public String getGoodName() {
		return goodName;
	}
	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}
	public Double getXcwh() {
		return xcwh;
	}
	public void setXcwh(Double xcwh) {
		this.xcwh = xcwh;
	}
	public Double getXccb() {
		return xccb;
	}
	public void setXccb(Double xccb) {
		this.xccb = xccb;
	}
	public Double getMcwh() {
		return mcwh;
	}
	public void setMcwh(Double mcwh) {
		this.mcwh = mcwh;
	}
	public Double getMccb() {
		return mccb;
	}
	public void setMccb(Double mccb) {
		this.mccb = mccb;
	}
	public Double getFdwh() {
		return fdwh;
	}
	public void setFdwh(Double fdwh) {
		this.fdwh = fdwh;
	}
	public Double getFdcb() {
		return fdcb;
	}
	public void setFdcb(Double fdcb) {
		this.fdcb = fdcb;
	}
	public Double getCncwh() {
		return cncwh;
	}
	public void setCncwh(Double cncwh) {
		this.cncwh = cncwh;
	}
	public Double getCnccb() {
		return cnccb;
	}
	public void setCnccb(Double cnccb) {
		this.cnccb = cnccb;
	}
	public Double getTotalwh() {
		return totalwh;
	}
	public void setTotalwh(Double totalwh) {
		this.totalwh = totalwh;
	}
	public Double getTotalwhcb() {
		return totalwhcb;
	}
	public void setTotalwhcb(Double totalwhcb) {
		this.totalwhcb = totalwhcb;
	}
	public Double getAcost() {
		return acost;
	}
	public void setAcost(Double acost) {
		this.acost = acost;
	}
	public Double getPcost() {
		return pcost;
	}
	public void setPcost(Double pcost) {
		this.pcost = pcost;
	}
	public Double getBcost() {
		return bcost;
	}
	public void setBcost(Double bcost) {
		this.bcost = bcost;
	}
	public Double getOutcost() {
		return outcost;
	}
	public void setOutcost(Double outcost) {
		this.outcost = outcost;
	}
	public Double getTotalCb() {
		return totalCb;
	}
	public void setTotalCb(Double totalCb) {
		this.totalCb = totalCb;
	}
	public String getDrawingCode() {
		return drawingCode;
	}
	public void setDrawingCode(String drawingCode) {
		this.drawingCode = drawingCode;
	}
}
