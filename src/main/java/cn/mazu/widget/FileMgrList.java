package cn.mazu.widget;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.mazu.WApplication;
import cn.mazu.WObject;
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
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.RadioButton;
import cn.mazu.widget.kit.web.form.WButtonGroup;
import cn.mazu.widget.kit.web.interact.Image;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.AnchorTarget;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class FileMgrList extends HtmlTemplate {
	private FileUpload upload = new FileUpload();
	Map<String, File> fileList = new TreeMap<String, File>().descendingMap();
	private String count = "4";
	@Bus(value="cid")
	public String fn="48fc40ec-dc92-4fab-8dd1-0668d30e42e4";
	ContainerWidget div= new ContainerWidget();
	final WButtonGroup group = new WButtonGroup();
	private int cur_page = 0;
	private int rec_num = 4;
	private String fid;
	private boolean flag;//控制上传按钮是否显示
	public FileMgrList(){
		this.setTemplateText("${UL}${upload}",TextFormat.XHTMLUnsafeText);
	}
	public FileMgrList(String fileName,String fid,boolean flag){
		this.setTemplateText("" +
				"${UL}"+
				"<div class='img_btn'>" +
				"${upload}"+
				"</div>", TextFormat.XHTMLUnsafeText);
		this.fn = fileName;
		this.fid = fid;
		this.flag = flag;
		this.setStyleClass("fileWrapper");
		draw(this);
	}
	public Template draw(Template parent,String... args){
		upload.changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				upload.upload();
			}
		});
		upload.uploaded().addListener(this, new Signal.Listener() {
			public void trigger() {
				try {
					String fpath = tr("base.filepath")+"/"+fn+"/"+fid+"/";
				
					Map<String, File> existList = new HashMap<String, File>();
					Util.findFiles(fpath, "*", existList);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					
					File dirFile = new File(fpath);
					if (!dirFile.exists()) {
						dirFile.mkdirs();
					}
					List<UploadedFile> ls = upload.getUploadedFiles();//点击上传加载的图片
					
					for(UploadedFile file : ls){
						File source = new File(file.getSpoolFileName());
						int suffix = file.getClientFileName().lastIndexOf(".");
						String fname = file.getClientFileName();
						fname = fname.substring(0,fname.lastIndexOf("."));
						String ymd = sdf.format(new Date());
						fname+=ymd;
						
						if(file.getContentType().contains("image/gif"))
							fname = fname+".gif";
						else if(file.getContentType().contains("image/jpeg"))
							fname = fname+".jpg";
						else if(file.getContentType().contains("image/png"))
							fname = fname+".png";
						else if(file.getContentType().contains("text/plain"))
							fname = fname+".txt";
						else
							fname = fname+file.getClientFileName().substring(suffix);
						File target = new File(fpath+"/"+fname);
						Util.copyFile(source, target);
						source.delete();						
					}
					FileMgrList.this.drawUL();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		drawUL();
		this.bindWidget("upload", upload);
		return this;
	}

	private void drawUL() {
		ContainerWidget ul = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_UL;
			}
		};
		ul.setAttributeValue("style", "margin: 0px auto;padding: 0px;");
		final String fpath = tr("base.filepath")+"/"+fn+"/"+fid+"/";
		fileList.clear();
		Util.findFiles(fpath, "*", fileList);
		
		for(String str : fileList.keySet()){
			ContainerWidget li = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_LI;
				}
			};
			li.setAttributeValue("style", "float:none;");
			//delete
			final PushButton deletebtn = new PushButton("删除");
			deletebtn.setStyleClass("editcls");
			deletebtn.setAttributeValue("name", str);
			deletebtn.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					File file = new File(fpath+deletebtn.getAttributeValue("name"));
					if(file.delete()){
						FileMgrList.this.drawUL();
					}
					
				}
			});
			String downUrl = tr("www.file")+"/"+fn+"/"+fid+"/"+str;
			Anchor downSource;
			downSource = new Anchor(new Link(downUrl),fileList.get(str).getName());
			downSource.setStyleClass("downUrl");
			downSource.setTarget(AnchorTarget.TargetNewWindow);
			downSource.setToolTip("下载附件");
			li.addWidget(downSource);
			
			if(flag){
				deletebtn.setAttributeValue("style", "display:inline-block;");
			}else{
				deletebtn.setAttributeValue("style", "display:none;");
			}
			li.addWidget(deletebtn);
			ul.addWidget(li);
		}
		this.bindWidget("UL", ul);
		
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
						"margin:0;padding:0;display:inline");
				form.setProperty(Property.PropertyTarget, "if" + this.getId());
				DomElement d = DomElement
						.createNew(DomElementType.DomElement_SPAN);
				d.addChild(i);
				form.addChild(d);
				DomElement input = DomElement.createNew(DomElementType.DomElement_INPUT);
				input.setAttribute("class", "fileBtn");
				if(flag){
					input.setAttribute("style", "display:inline-block;");
				}else{
					input.setAttribute("style", "display:none;");
				}
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
				
				form.addChild(input);
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
