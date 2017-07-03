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
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class ImageMgrList extends HtmlTemplate {
	private FileUpload upload = new FileUpload();
	Map<String, File> fileList = new TreeMap<String, File>().descendingMap();
	private String count = "4";
	@Bus(value="cid")
	public String fn="48fc40ec-dc92-4fab-8dd1-0668d30e42e4";
	ContainerWidget div= new ContainerWidget();
	final WButtonGroup group = new WButtonGroup();
	private int cur_page = 0;
	private int rec_num = 4;
	public ImageMgrList(){
		this.setTemplateText("${UL}${upload}",TextFormat.XHTMLUnsafeText);
	}
	public ImageMgrList(String cid){
		this.setTemplateText("<div class='ImageMgrList'>" +
				"${UL}"+
				"<div class='img_btn'>" +
				"${upload}"+
				"</div></div>", TextFormat.XHTMLUnsafeText);
		this.fn = cid;
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
					String fpath = tr("base.picpath")+"/"+fn+"/";
				
					Map<String, File> existList = new HashMap<String, File>();
					Util.findFiles(fpath, fn+"small"+"*", existList);
					//已上传图片数量
					if(existList.size()<4){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						String ymd = sdf.format(new Date());
						String fname = fn  ;
						if(group.getSelectedButtonIndex()==0)
							fname = fname+"small"+ymd;
						/*else if(group.getSelectedButtonIndex()==1)
							fname = fname+"large"+ymd;
						else 
							fname = fname+"list"+ymd;*/
						
						File dirFile = new File(fpath);
						if (!dirFile.exists()) {
							dirFile.mkdirs();
						}
						List<UploadedFile> ls = upload.getUploadedFiles();//点击上传加载的图片
						
						for(UploadedFile file : ls){
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
						ImageMgrList.this.drawUL();
					}else{
						Util.infoMessage(tr("Information please").toString(), "最多上传4张图片");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		//big small pic
		ContainerWidget c = new ContainerWidget();
		c.setStyleClass("pool_tip");
		RadioButton button = new RadioButton(TYPE.SMALL.name,c);
		button.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				drawUL();
			}
		});
		group.addButton(button);
		button = new RadioButton(TYPE.LARGE.name,c);
		button.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				drawUL();
			}
		});
		group.addButton(button);
		button = new RadioButton(TYPE.LIST.name,c);
		button.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				drawUL();
			}
		});
		group.addButton(button);
		this.bindWidget("group", c);
		
		group.setSelectedButtonIndex(0);//设置图片格式为small
		
		drawUL();
		this.bindWidget("upload", upload);
		return this;
	}

	private void drawUL() {
		ContainerWidget div = new ContainerWidget();
		div.setStyleClass("img_item_box");
		ContainerWidget ul = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_UL;
			}
		};
		ul.setAttributeValue("style", "margin: 0px auto;padding: 0px;");
		final String fpath = tr("base.picpath")+"/"+fn+"/";
		if(group.getSelectedButtonIndex()==0)
			pageList(TYPE.SMALL,fpath);
		/*else if(group.getSelectedButtonIndex()==1)
			pageList(TYPE.LARGE,fpath);
		else
			pageList(TYPE.LIST,fpath);*/
		
		for(String str : fileList.keySet()){
			ContainerWidget li = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_LI;
				}
			};
			//delete
			final Anchor delete = new Anchor(new Link(""),"删除");
			delete.setAttributeValue("style", "cursor:pointer;");
			delete.setStyleClass("Btndelete");
			delete.setAttributeValue("name", str);
			delete.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					File file = new File(fpath+delete.getAttributeValue("name"));
					if(file.delete()){
						ImageMgrList.this.drawUL();
					}
					
				}
			});
			
			
			Image _img = new Image(new Link(tr("www.pic")+"/"+fn+"/"+str));
			_img.setStyleClass("upimg");
			li.addWidget(_img);
			li.addWidget(delete);
			ul.addWidget(li);
		}
		div.addWidget(ul);
		this.bindWidget("UL", div);
		
	}
	public void pageList(TYPE t,String fpath){
		fileList.clear();
		Util.findFiles(fpath, fn+t.en+"*", fileList);
		int page = (int) Math.ceil(fileList.size()/(double)rec_num);
		cur_page = (cur_page >= page&&page>0) ? page-1:cur_page;
		int begin = cur_page*rec_num;
		int end = begin+rec_num;
		Iterator it = fileList.entrySet().iterator();
		int i = 0;
		while(it.hasNext()){
			it.next();
			if(i<begin||(i>=end&&i<fileList.size()))
				it.remove();
			i +=1;
		}
		
	}
	enum TYPE{
		LARGE("大","large",0),SMALL("小","small",1),LIST("表","list",2);
		private String name;
		private String en;
		private int index;
		
		private TYPE(String name, String en,int index){
			this.name = name;
			this.index = index;
			this.en = en;
		}
		
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
				DomElement div = DomElement.createNew(DomElementType.DomElement_DIV);
				div.setAttribute("class", "upload");
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
