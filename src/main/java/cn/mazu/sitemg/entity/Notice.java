package cn.mazu.sitemg.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.mazu.annotation.DisplayField;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util.NoticeType;

@Table(name="notice")
@Entity
public class Notice extends EntityObject{
	@DisplayField(cname="",etag=0)
	public String seqno = "";//序号
	@DisplayField(cname="标题",etag=1)
	public String title = "";
	@DisplayField(cname="公告类型",etag=2)
	public NoticeType type = NoticeType.INNERNT;//公告类型：内部，外部
	@DisplayField(cname="发布时间",etag=3)
	public Date publishDate = new Date();//发布时间
	public String content = "";
	@Transient
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	@Column(nullable=false)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(nullable=false)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public NoticeType getType() {
		return type;
	}
	public void setType(NoticeType type) {
		this.type = type;
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	
	
}
