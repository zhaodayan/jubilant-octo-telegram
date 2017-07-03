package cn.mazu.storage.entity;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.SqlResultSetMappings;

@NamedNativeQueries({@javax.persistence.NamedNativeQuery(name="InventoryWarnQuery", 
query="select a.id,a.generateDate,a.code,a.dataCode,a.name,b.amt+a.amount as amt,a.spec as spec,a.threshold from inventory a " +
		"join (select sum(book_amt) as amt ,inventory_id from inventory_book " +
		"group by inventory_id) as b on a.id = b.inventory_id " +
		"where a.threshold>b.amt+a.amount order by a.generateDate desc ", resultSetMapping="InventoryWarnQuery")})
@SqlResultSetMappings({@javax.persistence.SqlResultSetMapping(name="InventoryWarnQuery", 
entities={@javax.persistence.EntityResult(entityClass=InventoryWarn.class, 
fields={@javax.persistence.FieldResult(name="id", column="id"), 
	    @javax.persistence.FieldResult(name="name", column="name"), 
	    @javax.persistence.FieldResult(name="code", column="code"), 
	    @javax.persistence.FieldResult(name="spec", column="spec"), 
	    @javax.persistence.FieldResult(name="generateDate", column="generateDate"), 
	    @javax.persistence.FieldResult(name="amt", column="amt"), 
	    @javax.persistence.FieldResult(name="dataCode", column="dataCode"),
	    @javax.persistence.FieldResult(name="threshold", column="threshold")})}, 
	    columns={@javax.persistence.ColumnResult(name="id", type=String.class), 
		@javax.persistence.ColumnResult(name="generateDate", type=java.util.Date.class), 
		@javax.persistence.ColumnResult(name="amt", type=Double.class), 
		@javax.persistence.ColumnResult(name="name", type=String.class), 
		@javax.persistence.ColumnResult(name="code", type=String.class),
		@javax.persistence.ColumnResult(name="spec", type=String.class),
		@javax.persistence.ColumnResult(name="dataCode", type=String.class),
		@javax.persistence.ColumnResult(name="threshold", type=Double.class)})})
@Entity
public class InventoryWarn extends EntityObject
{

  @DisplayField(cname="货品名称", etag=1)
  public String name = "";
  @DisplayField(cname="货品规格", etag=5)
  public String spec = "";

  @DisplayField(cname="货品编号", etag=2)
  public String code = "";
  
  
  @DisplayField(cname="数量", etag=3)
  public Double amt = Double.valueOf(0.0D);

  @DisplayField(cname="预警值", etag=4)
  public Double threshold = Double.valueOf(0.0D);

  
  
  public String getSpec() {
	return spec;
	}
	
	public void setSpec(String spec) {
		this.spec = spec;
	}

public String getName() { return this.name; }

  public void setName(String name) {
    this.name = name;
  }
  public String getCode() {
    return this.code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public Double getAmt() {
    return this.amt;
  }
  public void setAmt(Double amt) {
    this.amt = amt;
  }
  public Double getThreshold() {
    return this.threshold;
  }
  public void setThreshold(Double threshold) {
    this.threshold = threshold;
  }
}