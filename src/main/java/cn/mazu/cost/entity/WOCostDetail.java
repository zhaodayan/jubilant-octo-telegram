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
//查询明细

@NamedNativeQueries(
{
@NamedNativeQuery(name="WoCostDetailQuery",
/*query ="select a.id,a.generateDate,a.goods_name as goodName, a.totalPrice as goodtotal," +
		"coalesce(f.xcsjwh,0) as xcwh,coalesce(f.xcsjwh,0)*g.xccost as xccb," +
		"coalesce(f.mcsjwh,0) as mcwh,coalesce(f.mcsjwh,0)*g.mccost as mccb," +
		"coalesce(f.fdsjwh,0) as fdwh,coalesce(f.fdsjwh,0)*g.fdcost as fdcb," +
		"coalesce(f.cncsjwh,0) as cncwh,coalesce(f.cncsjwh,0)*g.cnccost as cnccb from goods a"+
" left join sign_doc b on a.signdoc_id = b.id"+
" left join workorder c on b.workorder_id = c.id"+
" left join surface d on d.id = c.surface_id"+
" left join surface_detail e on e.surface_id = d.id and e.id = a.surfacedetail_id"+
" left join workorder_tracedetail f on f.surfacedetail_id = e.id " +
" inner join costconf g " +
" where c.id=?3 limit ?1,?2",//where c.id=?3
*/
query = "select a.name as goodName,a.id,a.generateDate,a.dataCode,"+//库存的原材料
		"0 as xcwh,0 as xccb,0 as mcwh,0 as mccb,"+
		"0 as fdwh,0 as fdcb,0 as cncwh,0 as cnccb,"+
		"d.pcost,d.acost,d.bcost,0 as totalwh,0 as totalwhcb,"+
		"d.pcost+d.acost+d.bcost as outcost  from inventory a "+
		"right join (select sum(case b.doc_type when 'PURCHASE' then c.totalprice else 0 end) as pcost,"+
		 "                 sum(case b.doc_type when 'AUTHORIZE' then c.totalprice else 0 end) as acost,"+
		  "                sum(case b.doc_type when 'BOOKING' then c.totalprice else 0 end) as bcost,c.inventory_id "+
		   "               from goods c "+
		    "              left join sign_doc b on b.id = c.signdoc_id "+ 
		     "             where b.workorder_id = ?3 and c.inventory_id is not null group by c.inventory_id) as d on a.id = d.inventory_id "+
		"union "+
		"select c.name as goodName,a.id,a.generateDate,a.dataCode," +//图面的明细
		"              sum(a.xcsjwh) as xcwh,sum(a.xcsjwh)*h.xccost as xccb,"+
		 "             sum(a.mcsjwh) as mcwh,sum(a.mcsjwh)*h.mccost as mccb,"+
		  "            sum(a.fdsjwh) as fdwh,sum(a.fdsjwh)*h.fdcost as fdcb,"+
		   "           sum(a.cncsjwh) as cncwh,sum(a.cncsjwh)*h.cnccost as cnccb,"+
		    "          coalesce(f.pcost,0) as pcost,coalesce(f.acost,0) as acost,coalesce(f.bcost,0) as bcost,"+
		     "         sum(a.xcsjwh)+sum(a.mcsjwh)+sum(a.fdsjwh)+sum(a.cncsjwh) as totalwh,"+
		      "        sum(a.xcsjwh)*h.xccost+sum(a.mcsjwh)*h.mccost+sum(a.fdsjwh)*h.fdcost+sum(a.cncsjwh)*h.cnccost as totalwhcb,"+
		       "       coalesce(f.pcost+f.acost+f.bcost,0) as outcost "+
		        "       from workorder_tracedetail a "+
		         "   left join surface_detail b on a.surfacedetail_Id = b.id "+
		          "  left join general_element c on c.id = b.generalelement_id "+
		           " left join (select sum(case d.doc_type when 'PURCHASE' then e.totalprice else 0 end) as pcost,"+
		            "                  sum(case d.doc_type when 'AUTHORIZE' then e.totalprice else 0 end) as acost,"+
		             "                 sum(case d.doc_type when 'BOOKING' then e.totalprice else 0 end) as bcost,e.surfacedetail_id "+
		              "         from goods e "+
		               "        left join sign_doc d on d.id = e.signdoc_id "+
		                "       where d.workorder_id = ?3 "+
		                 "      and e.surfacedetail_id is not null group by e.surfacedetail_id) f on f.surfacedetail_id = b.id "+
		            "left join workorder g on g.id = a.workorder_id "+
		            "left join costconf h on (g.generateDate>=h.startDate and g.generateDate<=h.endDate) "+
		            "where a.workorder_id =?3 group by c.id  "+
		            "union "+//纵向合计
		            "select '合计' as goodName,x.id,x.generateDate,x.dataCode,sum(x.xcwh) as xcwh,sum(x.xccb) as xccb," +
		            "sum(x.mcwh) as mcwh,sum(x.mccb) as mccb," +
		            "sum(x.fdwh) as fdwh,sum(x.fdcb) as fdcb,sum(x.cncwh) as cncwh,sum(x.cnccb) as cnccb," +
		            "sum(x.pcost) as pcost,sum(x.acost) as acost,sum(x.bcost) as bcost,sum(x.totalwh) as totalwh," +
		            "sum(x.totalwhcb) as totalwhcb," +
		            "sum(x.outcost) as outcost from (select a.name as goodName,a.id,a.generateDate,a.dataCode," +
		            		"0 as xcwh,0 as xccb,0 as mcwh,0 as mccb," +
		            		"0 as fdwh,0 as fdcb,0 as cncwh,0 as cnccb," +
		            		"d.pcost,d.acost,d.bcost,0 as totalwh,0 as totalwhcb," +
		            		"d.pcost+d.acost+d.bcost as outcost  from inventory a " +
		            		"right join (select sum(case b.doc_type when 'PURCHASE' then c.totalprice else 0 end) as pcost," +
		            		"sum(case b.doc_type when 'AUTHORIZE' then c.totalprice else 0 end) as acost," +
		            		"sum(case b.doc_type when 'BOOKING' then c.totalprice else 0 end) as bcost,c.inventory_id " +
		            		"from goods c " +
		            		"left join sign_doc b on b.id = c.signdoc_id " +
		            		"where b.workorder_id = ?3 and c.inventory_id is not null group by c.inventory_id) as d on a.id = d.inventory_id " +
		            		"union " +
		            		"select c.name as goodName,a.id,a.generateDate,a.dataCode," +
		            		        "sum(a.xcsjwh) as xcwh,sum(a.xcsjwh)*h.xccost as xccb," +
		            		        "sum(a.mcsjwh) as mcwh,sum(a.mcsjwh)*h.mccost as mccb," +
		            		        "sum(a.fdsjwh) as fdwh,sum(a.fdsjwh)*h.fdcost as fdcb," +
		            		        "sum(a.cncsjwh) as cncwh,sum(a.cncsjwh)*h.cnccost as cnccb," +
		            		        "coalesce(f.pcost,0) as pcost,coalesce(f.acost,0) as acost,coalesce(f.bcost,0) as bcost," +
		            		        "sum(a.xcsjwh)+sum(a.mcsjwh)+sum(a.fdsjwh)+sum(a.cncsjwh) as totalwh," +
		            		        "sum(a.xcsjwh)*h.xccost+sum(a.mcsjwh)*h.mccost+sum(a.fdsjwh)*h.fdcost+sum(a.cncsjwh)*h.cnccost as totalwhcb," +
		            		        "coalesce(f.pcost+f.acost+f.bcost,0) as outcost " +
		            		        "from workorder_tracedetail a " + 
		            		        "left join surface_detail b on a.surfacedetail_Id = b.id " +
		            		        "left join general_element c on c.id = b.generalelement_id " +
		            		        "left join (select sum(case d.doc_type when 'PURCHASE' then e.totalprice else 0 end) as pcost," +
		            		        "sum(case d.doc_type when 'AUTHORIZE' then e.totalprice else 0 end) as acost," +
		            		        "sum(case d.doc_type when 'BOOKING' then e.totalprice else 0 end) as bcost,e.surfacedetail_id " +
		            		        "from goods e " +
		            		        "left join sign_doc d on d.id = e.signdoc_id " +
		            		        "where d.workorder_id = ?3 " +
		            		        "and e.surfacedetail_id is not null group by e.surfacedetail_id) f on f.surfacedetail_id = b.id " +
		            		        "left join workorder g on g.id = a.workorder_id " +
		            		        "left join costconf h on (g.generateDate>=h.startDate and g.generateDate<=h.endDate) " +
		            		        "where a.workorder_id =?3 group by c.id ) x ",

resultSetMapping="WoCostDetailQuery")			
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
			name="WoCostDetailQuery",
			entities={@EntityResult(entityClass=cn.mazu.cost.entity.WOCostDetail.class,
			fields={
				@FieldResult(name="id",column="id"),
				@FieldResult(name="goodName",column="goodName"),
				@FieldResult(name="generateDate",column="generateDate"),
				@FieldResult(name="mcwh",column="mcwh"),
				@FieldResult(name="xcwh",column="xcwh"),
				@FieldResult(name="fdwh",column="fdwh"),
				@FieldResult(name="cncwh",column="cncwh"),
				@FieldResult(name="mccb",column="mccb"),
				@FieldResult(name="xccb",column="xccb"),
				@FieldResult(name="fdcb",column="fdcb"),
				@FieldResult(name="cnccb",column="cnccb"),
				@FieldResult(name="acost",column="acost"),
				@FieldResult(name="bcost",column="bcost"),
				@FieldResult(name="pcost",column="pcost"),
				@FieldResult(name="totalwh",column="totalwh"),
				@FieldResult(name="totalwhcb",column="totalwhcb"),
				@FieldResult(name="outcost",column="outcost"),
				@FieldResult(name="dataCode",column="dataCode")})},//无顺序要求，只是对应一下
 			columns={
			@ColumnResult(name="id",type=java.lang.String.class),
			@ColumnResult(name="generateDate",type=java.util.Date.class),
            @ColumnResult(name="goodName",type=java.lang.String.class),
            @ColumnResult(name="xcwh",type=java.lang.Double.class),
            @ColumnResult(name="mcwh",type=java.lang.Double.class),
            @ColumnResult(name="fdwh",type=java.lang.Double.class),
            @ColumnResult(name="cncwh",type=java.lang.Double.class),
            @ColumnResult(name="xccb",type=java.lang.Double.class),
            @ColumnResult(name="mccb",type=java.lang.Double.class),
            @ColumnResult(name="fdcb",type=java.lang.Double.class),
			@ColumnResult(name="cnccb",type=java.lang.Double.class),
			@ColumnResult(name="acost",type=java.lang.Double.class),
			@ColumnResult(name="bcost",type=java.lang.Double.class),
			@ColumnResult(name="pcost",type=java.lang.Double.class),
			@ColumnResult(name="totalwhcb",type=java.lang.Double.class),
			@ColumnResult(name="totalwh",type=java.lang.Double.class),
			@ColumnResult(name="outcost",type=java.lang.Double.class),
			@ColumnResult(name="dataCode",type=java.lang.String.class)
			})
}
)
@Entity
public class WOCostDetail extends EntityObject{
   @DisplayField(cname="序号",etag=6)
   public String seqno;
   @DisplayField(cname="零件或原材料名称",etag=0)
   public String goodName = "";
   /*@DisplayField(cname="零件或原材料总价",etag=1)
   public Double goodtotal = 0.0;*/
   @DisplayField(cname="铣床工时(h)",etag=2)
   public Double xcwh = 0.0;
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
	public Double getCncwh() {
		return cncwh;
	}
	public void setCncwh(Double cncwh) {
		this.cncwh = cncwh;
	}
	/*public Double getGoodtotal() {
		return goodtotal;
	}
	public void setGoodtotal(Double goodtotal) {
		this.goodtotal = goodtotal;
	}*/
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
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
	
}
