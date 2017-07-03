package cn.mazu.workorder.entity;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import cn.mazu.util.EntityObject;

//工时统计
@NamedNativeQueries(
{
@NamedNativeQuery(name="WOOverallSum",//全部工单工时统计
query = "select a.id,a.generateDate,a.dataCode,sum(cncyjwh) as cncyj,sum(fdyjwh) as fdyj,sum(xcyjwh) as xcyj,"+
        "sum(mcyjwh) as mcyj,(sum(cncyjwh)+sum(fdyjwh)+sum(xcyjwh)+sum(mcyjwh)) as tyj,"+
        "sum(cncsjwh) as cncsj,sum(fdsjwh) as fdsj,sum(xcsjwh) as xcsj,sum(mcsjwh) as mcsj,"+
        "(sum(cncsjwh)+sum(fdsjwh)+sum(xcsjwh)+sum(mcsjwh)) as tsj from workorder_tracedetail a "+
        "left join workorder b on a.workorder_id = b.id "+
        "where b.wostatus = 'ACTIVATED'",
resultSetMapping="WOOverallSum"),

@NamedNativeQuery(name="WOSingleSum",//单个工单工时统计
query ="select a.id,a.generateDate,a.dataCode,sum(cncyjwh) as cncyj,sum(fdyjwh) as fdyj,sum(xcyjwh) as xcyj,"+
       "sum(mcyjwh) as mcyj,(sum(cncyjwh)+sum(fdyjwh)+sum(xcyjwh)+sum(mcyjwh)) as tyj,"+
       "sum(cncsjwh) as cncsj,sum(fdsjwh) as fdsj,sum(xcsjwh) as xcsj,sum(mcsjwh) as mcsj,"+
       "(sum(cncsjwh)+sum(fdsjwh)+sum(xcsjwh)+sum(mcsjwh)) as tsj from workorder_tracedetail a "+
       "left join workorder b on a.workorder_id = b.id "+
       "where b.id = ?3",
resultSetMapping="WOSingleSum")
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
			name="WOOverallSum",
			entities={@EntityResult(entityClass=cn.mazu.workorder.entity.WorkOrderWHSum.class,
			fields={
				@FieldResult(name="id",column="id"),
				@FieldResult(name="generateDate",column="generateDate"),
				@FieldResult(name="cncyj",column="cncyj"),
				@FieldResult(name="fdyj",column="fdyj"),
				@FieldResult(name="xcyj",column="xcyj"),
				@FieldResult(name="mcyj",column="mcyj"),
				@FieldResult(name="tyj",column="tyj"),
				@FieldResult(name="cncsj",column="cncsj"),
				@FieldResult(name="mcsj",column="mcsj"),
				@FieldResult(name="xcsj",column="xcsj"),
				@FieldResult(name="fdsj",column="fdsj"),
				@FieldResult(name="tsj",column="tsj"),
				@FieldResult(name="dataCode",column="dataCode"),
				})},
 			columns={
			@ColumnResult(name="id",type=java.lang.String.class),
			@ColumnResult(name="generateDate",type=java.util.Date.class),
			@ColumnResult(name="cncyj",type=java.lang.Double.class),
            @ColumnResult(name="fdyj",type=java.lang.Double.class),
            @ColumnResult(name="xcyj",type=java.lang.Double.class),
            @ColumnResult(name="mcyj",type=java.lang.Double.class),
            @ColumnResult(name="tyj",type=java.lang.Double.class),
            @ColumnResult(name="cncsj",type=java.lang.Double.class),
            @ColumnResult(name="fdsj",type=java.lang.Double.class),
            @ColumnResult(name="xcsj",type=java.lang.Double.class),
            @ColumnResult(name="mcsj",type=java.lang.Double.class),
            @ColumnResult(name="tsj",type=java.lang.Double.class),
            @ColumnResult(name="dataCode",type=java.lang.String.class),
			}),
			@SqlResultSetMapping(
					name="WOSingleSum",
					entities={@EntityResult(entityClass=cn.mazu.workorder.entity.WorkOrderWHSum.class,
					fields={
						@FieldResult(name="id",column="id"),
						@FieldResult(name="generateDate",column="generateDate"),
						@FieldResult(name="cncyj",column="cncyj"),
						@FieldResult(name="fdyj",column="fdyj"),
						@FieldResult(name="xcyj",column="xcyj"),
						@FieldResult(name="mcyj",column="mcyj"),
						@FieldResult(name="tyj",column="tyj"),
						@FieldResult(name="cncsj",column="cncsj"),
						@FieldResult(name="mcsj",column="mcsj"),
						@FieldResult(name="xcsj",column="xcsj"),
						@FieldResult(name="fdsj",column="fdsj"),
						@FieldResult(name="tsj",column="tsj"),
						@FieldResult(name="dataCode",column="dataCode")
						})},
		 			columns={
					@ColumnResult(name="id",type=java.lang.String.class),
					@ColumnResult(name="generateDate",type=java.util.Date.class),
					@ColumnResult(name="cncyj",type=java.lang.Double.class),
		            @ColumnResult(name="fdyj",type=java.lang.Double.class),
		            @ColumnResult(name="xcyj",type=java.lang.Double.class),
		            @ColumnResult(name="mcyj",type=java.lang.Double.class),
		            @ColumnResult(name="tyj",type=java.lang.Double.class),
		            @ColumnResult(name="cncsj",type=java.lang.Double.class),
		            @ColumnResult(name="fdsj",type=java.lang.Double.class),
		            @ColumnResult(name="xcsj",type=java.lang.Double.class),
		            @ColumnResult(name="mcsj",type=java.lang.Double.class),
		            @ColumnResult(name="tsj",type=java.lang.Double.class),
		            @ColumnResult(name="dataCode",type=java.lang.String.class)
					}
					)
}
)
@Entity
public class WorkOrderWHSum extends EntityObject{
   public Double xcyj = 0.0; //铣床预计工时
   public Double mcyj = 0.0; //磨床预计工时
   public Double cncyj = 0.0;//cnc预计工时
   public Double fdyj = 0.0;//放电预计工时
   public Double tyj = 0.0;//预计总工时
   
   public Double xcsj = 0.0; //铣床实际工时
   public Double mcsj = 0.0; //磨床实际工时
   public Double cncsj = 0.0; //cnc实际工时
   public Double fdsj = 0.0; //放电实际工时
   public Double tsj = 0.0;   //实际总工时
   @Column(precision=6,scale=2)
	public Double getXcyj() {
		return xcyj;
	}
	public void setXcyj(Double xcyj) {
		this.xcyj = xcyj;
	}
	@Column(precision=6,scale=2)
	public Double getMcyj() {
		return mcyj;
	}
	public void setMcyj(Double mcyj) {
		this.mcyj = mcyj;
	}
	@Column(precision=6,scale=2)
	public Double getCncyj() {
		return cncyj;
	}
	public void setCncyj(Double cncyj) {
		this.cncyj = cncyj;
	}
	@Column(precision=6,scale=2)
	public Double getFdyj() {
		return fdyj;
	}
	public void setFdyj(Double fdyj) {
		this.fdyj = fdyj;
	}
	@Column(precision=6,scale=2)
	public Double getTyj() {
		return tyj;
	}
	public void setTyj(Double tyj) {
		this.tyj = tyj;
	}
	@Column(precision=6,scale=2)
	public Double getXcsj() {
		return xcsj;
	}
	public void setXcsj(Double xcsj) {
		this.xcsj = xcsj;
	}
	@Column(precision=6,scale=2)
	public Double getMcsj() {
		return mcsj;
	}
	public void setMcsj(Double mcsj) {
		this.mcsj = mcsj;
	}
	@Column(precision=6,scale=2)
	public Double getCncsj() {
		return cncsj;
	}
	public void setCncsj(Double cncsj) {
		this.cncsj = cncsj;
	}
	@Column(precision=6,scale=2)
	public Double getFdsj() {
		return fdsj;
	}
	public void setFdsj(Double fdsj) {
		this.fdsj = fdsj;
	}
	@Column(precision=6,scale=2)
	public Double getTsj() {
		return tsj;
	}
	public void setTsj(Double tsj) {
		this.tsj = tsj;
	}
}
