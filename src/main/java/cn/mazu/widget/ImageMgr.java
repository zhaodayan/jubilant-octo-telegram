package cn.mazu.widget;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mazu.WApplication;
import cn.mazu.annotation.Bus;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.DomElement;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.Property;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.UploadedFile;
import cn.mazu.widget.HtmlTemplate;
import cn.mazu.widget.event.EventSignal;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.WFileUpload;
import cn.mazu.widget.kit.web.interact.Image;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class ImageMgr extends HtmlTemplate {
	@Bus(value="absid")
	public String fn="48fc40ec-dc92-4fab-8dd1-0668d30e42e4";
	/*private String style = "";
	private String count = "5";*/
	private String filename = "images";
	private FileUpload upload = new FileUpload();
	ContainerWidget div= new ContainerWidget();
	public ImageMgr(){
		this.setTemplateText("${UL}${upload}",TextFormat.XHTMLUnsafeText);
	}
	
	public ImageMgr(String absid,String filename){
		this.setTemplateText("${UL}${upload}",TextFormat.XHTMLUnsafeText);
		this.fn = absid;
		this.filename = filename;
		draw(this);
	}
	
	public Template draw(Template parent,String... args){
		/*for(String str : args){
			if(str.contains("style")){
				style = tr(str).toString();
				this.setAttributeValue("style", style);
			}else if(str.contains("count"))
				count = str.replace("count-", "");
			else if(str.contains("cargoid")){
				fn = str.replace("cargoid=", "");
			}
		}*/
		drawDIV();
		upload.changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				upload.upload();
			}
		});
		upload.uploaded().addListener(this, new Signal.Listener() {
			public void trigger() {
				try {
					String fpath = tr("base.picpath")+"/"+filename+"/"+fn+"/";
					Map<String, File> fileList = new HashMap<String, File>();
					//删除该目录下所有文件
					Util.deleteFiles(fpath, fn+"*");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String ymd = sdf.format(new Date());
					String fname = fn +ymd ;
					File dirFile = new File(fpath);
					if (!dirFile.exists()) {
						dirFile.mkdirs();
					}
					List<UploadedFile> ls = upload.getUploadedFiles();
					
					for(UploadedFile file : ls){
//						System.out.println(file.getContentType());
						File source = new File(file.getSpoolFileName());
						if(file.getContentType().contains("image/gif"))
							fname = fname+".gif";
						else if(file.getContentType().contains("image/jpeg"))
							fname = fname+".jpg";
						else if(file.getContentType().contains("image/png"))
							fname = fname+".png";
						File target = new File(fpath+"/"+fname);
						Util.copyFile(source, target);
						source.delete();
						
					}
					ImageMgr.this.drawDIV();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		this.bindWidget("UL", div);
		this.bindWidget("upload", upload);
		return this;
	}
	
	private void drawDIV() {
		div.clear();
		div.setStyleClass("ImageMgrUL");
		ContainerWidget ul = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_UL;
			}
		};
		final String fpath = tr("base.picpath")+"/"+filename+"/"+fn+"/";
		Map<String, File> fileList = new HashMap<String, File>();
		Util.findFiles(fpath, fn+"*", fileList);
		for(String str : fileList.keySet()){
			ContainerWidget li = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_LI;
				}
			};
			
			Anchor img = new Anchor(new Link(Link.Type.InternalPath, "/"),"");
			img.setStyleClass("pimg");
			Image _img = new Image(new Link(tr("www.pic")+"/"+filename+"/"+fn+"/"+str),img);
			li.addWidget(img);
			ul.addWidget(li);
		}
		if(fileList.size()==0){
			ContainerWidget li = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_LI;
				}
			};
			Anchor img = new Anchor(new Link(Link.Type.InternalPath,""));
			img.setStyleClass("pimg");
			Image _img = new Image("images/logo.png",img);
			_img.setStyleClass("upimg");
			li.addWidget(img);
			ul.addWidget(li);
		}
		div.addWidget(ul);
		this.bindWidget("UL", div);
	}
	
	class FileUpload extends WFileUpload{

		@Override
		protected DomElement createDomElement(WApplication app) {
			DomElement result = DomElement.createNew(this.getDomElementType());
			if (result.getType() == DomElementType.DomElement_FORM) {
				result.setId(this.getId());
			} else {
				result.setName(this.getId());
			}
			EventSignal change = this.voidEventSignal(
					WFileUpload.CHANGE_SIGNAL, false);
			if (this.fileUploadTarget_ != null) {
				DomElement i = DomElement
						.createNew(DomElementType.DomElement_IFRAME);
				i.setProperty(Property.PropertyClass, "Wt-resource");
				i.setProperty(Property.PropertySrc,
						this.fileUploadTarget_.getUrl());
				i.setName("if" + this.getId());
				if (app.getEnvironment().agentIsIE()) {
					i.setAttribute("APPLICATION", "yes");
				}
				DomElement form = result;
				form.setAttribute("method", "post");
				form.setAttribute("action", this.fileUploadTarget_.getUrl());
				form.setAttribute("enctype", "multipart/form-data");
				form.setProperty(Property.PropertyStyle,
						"margin:0;padding:0;display:block");
				form.setProperty(Property.PropertyTarget, "if" + this.getId());
				DomElement d = DomElement
						.createNew(DomElementType.DomElement_SPAN);
				d.addChild(i);
				form.addChild(d);
				DomElement div = DomElement.createNew(DomElementType.DomElement_DIV);
				div.setAttribute("class", "ImageMgr");
				DomElement div_img = DomElement.createNew(DomElementType.DomElement_DIV);
				div_img.setAttribute("class", "btnImg");
				DomElement input = DomElement.createNew(DomElementType.DomElement_INPUT);
				input.setAttribute("class", "hide");
				input.setAttribute("type", "file");
				if (this.flags_.get(BIT_MULTIPLE)) {
					input.setAttribute("multiple", "multiple");
				}
				input.setAttribute("name", "data");
				input.setAttribute("size", String.valueOf(this.textSize_));
				input.setId("in" + this.getId());
				if (!this.isEnabled()) {
					input.setProperty(Property.PropertyDisabled, "true");
				}
				if (change != null) {
					this.updateSignalConnection(input, change, "change", true);
				}
				
				div_img.addChild(input);
				div.addChild(div_img);
				form.addChild(div);
			} else {
				result.setAttribute("type", "file");
				if (this.flags_.get(BIT_MULTIPLE)) {
					result.setAttribute("multiple", "multiple");
				}
				result.setAttribute("size", String.valueOf(this.textSize_));
				if (!this.isEnabled()) {
					result.setProperty(Property.PropertyDisabled, "true");
				}
				if (change != null) {
					this.updateSignalConnection(result, change, "change", true);
				}
			}
			this.updateDom(result, true);
			this.flags_.clear(BIT_ENABLE_AJAX);
			return result;
		}
		
	}
}
