package cn.mazu.storage.entity;


import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

//库存汇总
@NamedNativeQueries(
{
@NamedNativeQuery(name="InventorySumQuery",
query ="select a.id,a.generateDate,a.dataCode,a.amount+sum(coalesce(b.book_amt,0)) as amt,a.name ,a.code,a.spec as spec,"+
		 "coalesce(max(b.book_date),'no record') as latestDate from inventory a "+
		 "left outer join inventory_book b on a.id = b.inventory_id "+
		 "group by a.id " +
		 "union "+
		 "select f.id,c.generateDate,c.dataCode,"+
		 "sum(c.book_amt) as amt,f.name,f.code,concat(g.order_no,'-',f.code) as spec,"+
		 "coalesce(max(c.book_date)) as latestDate from inventory_book c "+
		 "left join workorder_tracedetail d on d.id = c.tracedetail_id "+
		 "left join workorder g on g.id = d.workorder_id "+
		 "left join surface_detail e on e.id = d.surfacedetail_id "+
		 "left join general_element f on f.id = e.generalElement_id "+
		 "where c.inventory_id is null "+
		 "group by f.id " +
		 "order by latestDate ",
resultSetMapping="InventorySumQuery")			
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
			name="InventorySumQuery",
			entities={@EntityResult(entityClass=cn.mazu.storage.entity.InventorySum.class,
			fields={
				@FieldResult(name="id",column="id"),
				@FieldResult(name="name",column="name"),
				@FieldResult(name="code",column="code"),
				@FieldResult(name="spec",column="spec"),
				@FieldResult(name="generateDate",column="generateDate"),
				@FieldResult(name="amt",column="amt"),
				@FieldResult(name="dataCode",column="dataCode"),
				@FieldResult(name="latestDate",column="latestDate")})},//sql查询列名和实体定义列名对应
 			columns={
			@ColumnResult(name="id",type=java.lang.String.class),
			@ColumnResult(name="generateDate",type=java.util.Date.class),
			@ColumnResult(name="amt",type=java.lang.Double.class),
            @ColumnResult(name="name",type=java.lang.String.class),
            @ColumnResult(name="code",type=java.lang.String.class),
            @ColumnResult(name="spec",type=java.lang.String.class),
            @ColumnResult(name="latestDate",type=java.lang.String.class),
            @ColumnResult(name="dataCode",type=java.lang.String.class)
			})
}
)
@Entity
public class InventorySum extends EntityObject{
	@DisplayField(cname="货品名称",etag=1)
    public String name = "";
	@DisplayField(cname="货品规格",etag=5)
    public String spec = "";
	@DisplayField(cname="货品编号",etag=2)
    public String code = "";
	@DisplayField(cname="统计数量",etag=3)
    public Double amt ;
	@DisplayField(cname="最后来往日期",etag=4)
	public String latestDate = "";
	
	
	 
	  public String getSpec() {
		return spec;
		}
		
		public void setSpec(String spec) {
			this.spec = spec;
		}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Double getAmt() {
		return amt;
	}
	public void setAmt(Double amt) {
		this.amt = amt;
	}
	public String getLatestDate() {
		return latestDate;
	}
	public void setLatestDate(String latestDate) {
		this.latestDate = latestDate;
	}
	
}
