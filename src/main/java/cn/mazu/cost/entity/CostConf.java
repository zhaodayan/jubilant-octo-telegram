package cn.mazu.cost.entity;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="CostConf")
public class CostConf extends EntityObject
{

  @DisplayField(cname="序号", etag=0)
  public String seqno = "";

  @DisplayField(cname="铣床成本(￥/h)", etag=1)
  public Double xccost = 0.0;

  @DisplayField(cname="磨床成本(￥/h)", etag=2)
  public Double mccost = 0.0;

  @DisplayField(cname="放电成本(￥/h)", etag=3)
  public Double fdcost = 0.0;

  @DisplayField(cname="cnc成本(￥/h)", etag=4)
  public Double cnccost = 0.0;

  @DisplayField(cname="有效期起", etag=5)
  public Date startDate = new Date();

  @DisplayField(cname="有效期止", etag=6)
  public Date endDate = new Date();

  @Transient
  public String getSeqno() { return this.seqno; }

  public void setSeqno(String seqno) {
    this.seqno = seqno;
  }
  public Double getXccost() {
    return this.xccost;
  }
  public void setXccost(Double xccost) {
    this.xccost = xccost;
  }
  public Double getMccost() {
    return this.mccost;
  }
  public void setMccost(Double mccost) {
    this.mccost = mccost;
  }
  public Double getFdcost() {
    return this.fdcost;
  }
  public void setFdcost(Double fdcost) {
    this.fdcost = fdcost;
  }
  public Double getCnccost() {
    return this.cnccost;
  }
  public void setCnccost(Double cnccost) {
    this.cnccost = cnccost;
  }
  @Column(nullable=false)
  @Temporal(TemporalType.DATE)
  public Date getStartDate() { return this.startDate; }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
  @Column(nullable=false)
  @Temporal(TemporalType.DATE)
  public Date getEndDate() { return this.endDate; }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}