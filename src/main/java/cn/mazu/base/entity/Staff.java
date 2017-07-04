package cn.mazu.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.UserGender;
@Entity
@Table(name="staff")
public class Staff extends EntityObject {
	@DisplayField(cname="",etag=0)
	public String seqno = "";
	@FilterField(fname="姓名",o2fname="name")
	@DisplayField(cname="姓名",etag=1)
    public String name = "";
	@DisplayField(cname="身份证号码",etag=4)
	public String nationID = "";// 身份证号码
	@DisplayField(cname="性别",etag=2)
	public UserGender gender;// = UserGender.MALE;
	@DisplayField(cname="出生日期",etag=3)
	public Date birthDate;
	@DisplayField(cname="籍贯",etag=5)
	public String nativePlace = "";//籍贯
	@DisplayField(cname="学历",etag=6)
	public String education = "";//学历	
	@DisplayField(cname="部门",etag=7)
	public Dept dept;
	@DisplayField(cname="进厂时间",etag=8)
	public Date joinDate;//进场时间	
    @DisplayField(cname="联系方式",etag=9)
    public String linktel = "";

    @DisplayField(cname="邮箱",etag=10)
    public String email = "";
   
    @DisplayField(cname="员工描述",etag=11)
    public String remark = "";
   
    
    
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Column(nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public String getNationID() {
		return nationID;
	}
	public void setNationID(String nationID) {
		this.nationID = nationID;
	}
	public String getNativePlace() {
		return nativePlace;
	}
	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
//	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	public UserGender getGender() {
		return gender;
	}
	public void setGender(UserGender gender) {
		this.gender = gender;
	}
	//@Column(nullable=false)
	@ManyToOne
	@JoinColumn(name="dept_id")
	public Dept getDept() {
		return dept;
	}
	public void setDept(Dept dept) {
		this.dept = dept;
	}
//	@Column(nullable=false)
	public String getLinktel() {
		return linktel;
	}
	public void setLinktel(String linktel) {
		this.linktel = linktel;
	}
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	
}
