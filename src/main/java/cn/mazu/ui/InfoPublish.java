package cn.mazu.ui;

import java.util.List;

import cn.mazu.mysql.Entity;
import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Company;
import cn.mazu.util.Util;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.ImageMgrList;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.SimpleHtmlEdit;
import cn.mazu.widget.kit.web.interact.Template;

public class InfoPublish extends HtmlTemplate {
	private SimpleHtmlEdit editor;
	
	private SitemgEntity smgEntity;
	private Entity entity;
	private Company c;
	public InfoPublish(){
		smgEntity = new SitemgEntity();
		entity = new Entity();
		String text="<div class='wrap'>" +
				"<span class='comleft'><h3>公司简介</h3>" +
				"${editor}${savebtn}</span>" +
				"<span class='comright'><h3>公司概貌</h3>" +
				"${imgList}" +
				"<div style='position:absolute;bottom:5px;left:120px;color:#247AB6;font-size:12px;'>【请上传不小于 980 x 240 像素的图片】</div>" +
				"</span><span style='clear:both;'></span>" +
				"</div>";
		this.setTemplateText(text, TextFormat.XHTMLUnsafeText);
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
				
		editor = new SimpleHtmlEdit("",null);
		editor.setWidth(new WLength(600));
		editor.setHeight(new WLength(420));
		List<Company> cls = smgEntity.getCompanyList();
		if(cls.size()!=0){
			c = cls.get(0);
			editor.setText(c.getContent());			
		}else{
			c = new Company();
		}
		this.bindWidget("editor", editor);
		PushButton savebtn = new PushButton("保  存");
		savebtn.setStyleClass("btn-primary editcls");
		this.bindWidget("savebtn", savebtn);
		savebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				c.setContent(editor.getText());
				if(entity.saveOrUpdate(c)){
					Util.infoMessage(tr("information please").toString(), "保存成功");
					InfoPublish.this.draw(parent, args);
				}
			}
		});
		
		ImageMgrList mgrList = new ImageMgrList("flexs");
		this.bindWidget("imgList", mgrList);
		return this;
	}
	
	
}
