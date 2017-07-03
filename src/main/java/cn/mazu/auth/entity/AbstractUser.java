/*package cn.mazu.auth.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.UserGender;
@Entity
@Table(name="AbstractUser")
public class AbstractUser extends EntityObject {
	//�û�����ʵ����
    public String name = "";
    //�ֻ�
    public String tel = "";
    //QQ
    public String kjukju = "";
    //�Ա�
    private UserGender gender = UserGender.FEMALE;
    //�û�����
   // public UserIdentity uidentity = UserIdentity.SINGLE;
    //�û����
    public String intro = "";
    //������ǩ
    public String captag = "";
    //���ڵ�
    public String locality = "";
    //����,ÿ�����һ����Ŀ��ʵʱ����
    public double goodp = .0;
    //�нӵ���Ŀ,�б��+1
    public Integer carrycount = 0;
    
    //��������Ŀ
    public Set<Task> releaseSet = new HashSet<Task>();
    //�нӵ���Ŀ
    public Set<Task> carrySet = new HashSet<Task>();
    
	@Enumerated(EnumType.STRING)
	public UserGender getGender() {
		return gender;
	}
	public void setGender(UserGender gender) {
		this.gender = gender;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getCaptag() {
		return captag;
	}
	public void setCaptag(String captag) {
		this.captag = captag;
	}
	public double getGoodp() {
		return goodp;
	}
	public void setGoodp(double goodp) {
		this.goodp = goodp;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getKjukju() {
		return kjukju;
	}
	public void setKjukju(String kjukju) {
		this.kjukju = kjukju;
	}
	@Column(name="carry_count")
	public Integer getCarrycount() {
		return carrycount;
	}
	public void setCarrycount(Integer carrycount) {
		this.carrycount = carrycount;
	}
	
	@OneToMany(cascade={CascadeType.PERSIST},mappedBy="releaser",fetch=FetchType.LAZY)
	@OrderBy("id")
	public Set<Task> getReleaseSet() {
		return releaseSet;
	}
	public void setReleaseSet(Set<Task> releaseSet) {
		this.releaseSet = releaseSet;
	}
	@OneToMany(cascade={CascadeType.PERSIST},mappedBy="carrier",fetch=FetchType.LAZY)
	@OrderBy("id")
	public Set<Task> getCarrySet() {
		return carrySet;
	}
	public void setCarrySet(Set<Task> carrySet) {
		this.carrySet = carrySet;
	}
	
}*/