package cn.mazu.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.base.entity.Staff;
import cn.mazu.util.EntityObject;
@Entity 
@Table(name="account_permission")
public class AccountPermission extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@FilterField(fname="用户名")
	@DisplayField(cname="用户名",etag=1)	
    public String username = "";
	/*@DisplayField(cname="密码",etag=2)
	@FilterField(fname="密码")
    public String password = "";*/
	@DisplayField(cname="员工",etag=4)
	public Staff staff;//对应的员工
	
	@FilterField(fname="菜单")
	//@DisplayField(cname="菜单",etag=3)
	public String menuStr = "";
	
	@DisplayField(cname="数据码",etag=5)//数据层面的显示控制
	public String dataCodea =AccountPermission.this.dataCode;
	
	public String menuPermission = ""; //格式如下：menu1:per1,per2,per3,per4;menu2:per1,per2;menu3:per1,per3;...
	
	public String passwordHash = "";//hash后的加密密码
	public String passwordMethod = "";//BCrypt
	public String passwordSalt = "";
	
	
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/*public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}*/
	@ManyToOne
	@JoinColumn(name="staff_id")
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	//@Lob
	public String getMenuStr() {
		return menuStr;
	}
	public void setMenuStr(String menuStr) {
		this.menuStr = menuStr;
	}
	//@Lob
	public String getMenuPermission() {
		return menuPermission;
	}
	public void setMenuPermission(String menuPermission) {
		this.menuPermission = menuPermission;
	}
	@Column(columnDefinition="varchar(255) default ''")
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	@Column(columnDefinition="varchar(255) default ''")
	public String getPasswordMethod() {
		return passwordMethod;
	}
	public void setPasswordMethod(String passwordMethod) {
		this.passwordMethod = passwordMethod;
	}
	@Column(columnDefinition="varchar(255) default ''")
	public String getPasswordSalt() {
		return passwordSalt;
	}
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
	@Transient
	public String getDataCodea() {
		return dataCode;
	}
	public void setDataCodea(String dataCodea) {
		this.dataCodea = dataCodea;
	}
	
}
