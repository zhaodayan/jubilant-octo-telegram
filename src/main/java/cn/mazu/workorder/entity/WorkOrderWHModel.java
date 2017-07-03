package cn.mazu.workorder.entity;
//工时统计模型
public class WorkOrderWHModel {
   private Double xcyj = 0.0; //铣床预计工时
   private Double mcyj = 0.0; //磨床预计工时
   private Double cncyj = 0.0;//cnc预计工时
   private Double fdyj = 0.0;//放电预计工时
   private Double tyj = 0.0;//预计总工时
   
   private Double xcsj = 0.0; //铣床实际工时
   private Double mcsj = 0.0; //磨床实际工时
   private Double cncsj = 0.0; //cnc实际工时
   private Double fdsj = 0.0; //放电实际工时
   private Double tsj = 0.0;   //实际总工时
   
	public Double getXcyj() {
		return xcyj;
	}
	public void setXcyj(Double xcyj) {
		this.xcyj = xcyj;
	}
	public Double getMcyj() {
		return mcyj;
	}
	public void setMcyj(Double mcyj) {
		this.mcyj = mcyj;
	}
	public Double getCncyj() {
		return cncyj;
	}
	public void setCncyj(Double cncyj) {
		this.cncyj = cncyj;
	}
	public Double getFdyj() {
		return fdyj;
	}
	public void setFdyj(Double fdyj) {
		this.fdyj = fdyj;
	}
	public Double getTyj() {
		return tyj;
	}
	public void setTyj(Double tyj) {
		this.tyj = tyj;
	}
	public Double getXcsj() {
		return xcsj;
	}
	public void setXcsj(Double xcsj) {
		this.xcsj = xcsj;
	}
	public Double getMcsj() {
		return mcsj;
	}
	public void setMcsj(Double mcsj) {
		this.mcsj = mcsj;
	}
	public Double getCncsj() {
		return cncsj;
	}
	public void setCncsj(Double cncsj) {
		this.cncsj = cncsj;
	}
	public Double getFdsj() {
		return fdsj;
	}
	public void setFdsj(Double fdsj) {
		this.fdsj = fdsj;
	}
	public Double getTsj() {
		return tsj;
	}
	public void setTsj(Double tsj) {
		this.tsj = tsj;
	}
}
