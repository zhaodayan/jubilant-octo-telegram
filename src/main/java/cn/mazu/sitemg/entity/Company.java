package cn.mazu.sitemg.entity;



import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import cn.mazu.util.EntityObject;

@Entity
@Table(name="company")
public class Company extends EntityObject{
	public String content = "";//信息内容

	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
