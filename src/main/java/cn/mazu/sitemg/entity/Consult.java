package cn.mazu.sitemg.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;

@Entity
@Table(name="consult")
public class Consult extends EntityObject{
	
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号

	@DisplayField(cname="标题",etag=1)
	public String title = "";
	@DisplayField(cname="姓名",etag=2)
	public String csname = "";
	@DisplayField(cname="地址",etag=3)
	public String csaddr = "";
	@DisplayField(cname="单位",etag=4)
	public String cscompany = "";
	@DisplayField(cname="电子邮箱",etag=5)
	public String csemail = "";
	@DisplayField(cname="电话",etag=6)
	public String cstele = "";
	@DisplayField(cname="传真",etag=7)
	public String csfax = "";
	@DisplayField(cname="咨询内容",etag=8)
	public String content = "";
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	
	public String getCsname() {
		return csname;
	}
	public void setCsname(String csname) {
		this.csname = csname;
	}
	public String getCscompany() {
		return cscompany;
	}
	public void setCscompany(String cscompany) {
		this.cscompany = cscompany;
	}
	public String getCsemail() {
		return csemail;
	}
	public void setCsemail(String csemail) {
		this.csemail = csemail;
	}
	public String getCstele() {
		return cstele;
	}
	public void setCstele(String cstele) {
		this.cstele = cstele;
	}
	public String getCsfax() {
		return csfax;
	}
	public void setCsfax(String csfax) {
		this.csfax = csfax;
	}
	public String getCsaddr() {
		return csaddr;
	}
	public void setCsaddr(String csaddr) {
		this.csaddr = csaddr;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
