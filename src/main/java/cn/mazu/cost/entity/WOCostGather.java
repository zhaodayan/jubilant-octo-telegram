package cn.mazu.cost.entity;

import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;


//工单成本汇总
@NamedNativeQueries(
{
@NamedNativeQuery(name="WoCostQuery",
query ="select a.id,a.generatedate,a.dataCode,a.order_no as workorderno,"+
    "coalesce(b.xcwh,0) as xcwh,coalesce(b.xcwh*c.xccost,0) as xccb,"+
    "coalesce(b.mcwh,0) as mcwh,coalesce(b.mcwh*c.mccost,0) as mccb,"+
    "coalesce(b.fdwh,0) as fdwh,coalesce(b.fdwh*c.fdcost,0) as fdcb,"+
    "coalesce(b.cncwh,0) as cncwh,coalesce(b.cncwh*c.cnccost,0) as cnccb,"+
    "sum(b.xcwh*c.xccost+b.mcwh*c.mccost+b.fdwh*c.fdcost+b.cncwh*c.cnccost) as whcbTotal,"+
    "sum(acost) as tgcost,sum(bcost) as dgcost,sum(pcost) as cgcost,"+
    "sum(b.xcwh*c.xccost+b.mcwh*c.mccost+b.fdwh*c.fdcost+b.cncwh*c.cnccost)+coal" +
    "esce(sum(acost),0)+coalesce(sum(bcost),0)+coalesce(sum(pcost),0) as cbTotal "+
    "from workorder a "+
   "left join (select sum(xcsjwh) as xcwh,sum(mcsjwh) as mcwh,sum(cncsjwh) as cncwh,sum(fdsjwh) as fdwh,workorder_id from workorder_tracedetail "+
   "group by workorder_id) b on a.id = b.workorder_id "+
   "left join costconf c on (c.startDate<=a.generateDate and c.endDate >=a.generateDate) "+
   "left join (select "+
              "sum(case  when e.doc_type='AUTHORIZE' or e.doc_type='GAUTHORIZE' or e.doc_type='KGMAUTHORIZE' then coalesce(d.totalprice,0) else 0 end) as acost,"+
              "sum(case  when e.doc_type='PURCHASE' or e.doc_type='GPURCHASE' then coalesce(d.totalprice,0) else 0 end) as pcost,"+
              "sum(case  when e.doc_type='BOOKING' then coalesce(d.totalprice,0) else 0 end) as bcost,"+
              "e.doc_type,e.workorder_id from goods d "+
              "right join sign_doc e on d.signdoc_id = e.id group by e.workorder_id) f on f.workorder_id= a.id "+
    "where a.wostatus = 'BEDONE' group by a.id order by a.generatedate desc ",
resultSetMapping="WoCostQuery")			
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
			name="WoCostQuery",
			entities={@EntityResult(entityClass=cn.mazu.cost.entity.WOCostGather.class,
			fields={
				@FieldResult(name="id",column="id"),
				@FieldResult(name="xcwh",column="xcwh"),
				@FieldResult(name="workorderno",column="workorderno"),
				@FieldResult(name="mcwh",column="mcwh"),
				@FieldResult(name="generateDate",column="generateDate"),
				@FieldResult(name="fdwh",column="fdwh"),
				@FieldResult(name="cncwh",column="cncwh"),
				@FieldResult(name="dataCode",column="dataCode"),
				@FieldResult(name="cnccb",column="cnccb"),
				@FieldResult(name="mccb",column="mccb"),
				@FieldResult(name="xccb",column="xccb"),
				@FieldResult(name="fdcb",column="fdcb"),
				@FieldResult(name="cgcost",column="cgcost"),
				@FieldResult(name="dgcost",column="dgcost"),
				@FieldResult(name="tgcost",column="tgcost"),
				@FieldResult(name="whcbTotal",column="whcbTotal"),
				@FieldResult(name="cbTotal",column="cbTotal")})},//sql查询列名和实体定义列名对应
 			columns={
			@ColumnResult(name="id",type=java.lang.String.class),
			@ColumnResult(name="generateDate",type=java.util.Date.class),
            @ColumnResult(name="workorderno",type=java.lang.String.class),
            @ColumnResult(name="xcwh",type=java.lang.Double.class),
            @ColumnResult(name="xccb",type=java.lang.Double.class),
            @ColumnResult(name="mcwh",type=java.lang.Double.class),
            @ColumnResult(name="mccb",type=java.lang.Double.class),
            @ColumnResult(name="fdwh",type=java.lang.Double.class),
            @ColumnResult(name="fdcb",type=java.lang.Double.class),
            @ColumnResult(name="cncwh",type=java.lang.Double.class),
            @ColumnResult(name="cnccb",type=java.lang.Double.class),
            @ColumnResult(name="cgcost",type=java.lang.Double.class),
            @ColumnResult(name="dgcost",type=java.lang.Double.class),
            @ColumnResult(name="tgcost",type=java.lang.Double.class),
            @ColumnResult(name="dataCode",type=java.lang.String.class),
            @ColumnResult(name="whcbTotal",type=java.lang.Double.class),
            @ColumnResult(name="cbTotal",type=java.lang.Double.class)///此处的顺序为查询出来列的排列顺序
			})
}
)

//因为要使用NamedNativeQuery，无法，必须要加此标注
@Entity
public class WOCostGather extends EntityObject{
	@DisplayField(cname="工单号",etag=0)
	public String workorderno = "";
	@DisplayField(cname="铣床工时(h)",etag=1)
	public Double xcwh = 0.0;
	@DisplayField(cname="铣床成本(￥)",etag=2)
	public Double xccb = 0.0;
	@DisplayField(cname="磨床工时(h)",etag=3)
	public Double mcwh = 0.0;
	@DisplayField(cname="磨床成本(￥)",etag=4)
	public Double mccb = 0.0;
	@DisplayField(cname="放电工时(h)",etag=5)
	public Double fdwh = 0.0;
	@DisplayField(cname="放电成本(￥)",etag=6)
	public Double fdcb = 0.0;
	@DisplayField(cname="cnc工时(h)",etag=7)
	public Double cncwh = 0.0;
	@DisplayField(cname="cnc成本(￥)",etag=8)
	public Double cnccb = 0.0;
	@DisplayField(cname="工时成本小计(￥)",etag=14)
	public Double whcbTotal = 0.0;
   //成品零件
	/*@DisplayField(cname="零件成本",etag=9)
	public Double elcost = 0.0;*/
	@DisplayField(cname="采购成本(￥)",etag=10)
	public Double cgcost = 0.0;
	@DisplayField(cname="订购成本(￥)",etag=11)
	public Double dgcost = 0.0;
	@DisplayField(cname="托工成本(￥)",etag=12)
	public Double tgcost = 0.0;
	@DisplayField(cname="总成本",etag=15)
	public Double cbTotal = 0.0;
	@DisplayField(cname="查看明细",etag=13,dename="cn.mazu.cost.entity.WOCostDetailModel")//dename="cn.mazu.cost.entity.WOCostDetail"
	public String oper = "";
	
	public String getWorkorderno() {
		return workorderno;
	}
	public void setWorkorderno(String workorderno) {
		this.workorderno = workorderno;
	}
	
	public Double getXcwh() {
		return xcwh;
	}
	public void setXcwh(Double xcwh) {
		this.xcwh = xcwh;
	}
	
	public Double getMcwh() {
		return mcwh;
	}
	public void setMcwh(Double mcwh) {
		this.mcwh = mcwh;
	}
	
	public Double getFdwh() {
		return fdwh;
	}
	public void setFdwh(Double fdwh) {
		this.fdwh = fdwh;
	}
	
	public Double getXccb() {
		return xccb;
	}
	public void setXccb(Double xccb) {
		this.xccb = xccb;
	}
	public Double getMccb() {
		return mccb;
	}
	public void setMccb(Double mccb) {
		this.mccb = mccb;
	}
	public Double getFdcb() {
		return fdcb;
	}
	public void setFdcb(Double fdcb) {
		this.fdcb = fdcb;
	}
	public Double getCnccb() {
		return cnccb;
	}
	public void setCnccb(Double cnccb) {
		this.cnccb = cnccb;
	}
	public Double getCncwh() {
		return cncwh;
	}
	public void setCncwh(Double cncwh) {
		this.cncwh = cncwh;
	}
	/*public Double getElcost() {
		return elcost;
	}
	public void setElcost(Double elcost) {
		this.elcost = elcost;
	}*/
	public Double getCgcost() {
		return cgcost;
	}
	public void setCgcost(Double cgcost) {
		this.cgcost = cgcost;
	}
	public Double getDgcost() {
		return dgcost;
	}
	public void setDgcost(Double dgcost) {
		this.dgcost = dgcost;
	}
	public Double getTgcost() {
		return tgcost;
	}
	public void setTgcost(Double tgcost) {
		this.tgcost = tgcost;
	}
	public Double getWhcbTotal() {
		return whcbTotal;
	}
	public void setWhcbTotal(Double whcbTotal) {
		this.whcbTotal = whcbTotal;
	}
	public Double getCbTotal() {
		return cbTotal;
	}
	public void setCbTotal(Double cbTotal) {
		this.cbTotal = cbTotal;
	}
	@Transient
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
}
