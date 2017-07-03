package cn.mazu.base.mvc;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.mazu.base.entity.Client;
import cn.mazu.ui.ClientMgr;
import cn.mazu.util.Util;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.UploadedFile;
import cn.mazu.widget.FileMgrList;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.composite.Panel;
import cn.mazu.widget.kit.web.WFileUpload;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.SimpleHtmlEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.AnchorTarget;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

public class ClientForm extends FormView{
	private ClientModel cModel;
    private Client client;
    private PopBox popBox;
    private ClientMgr cMgr;
    private String title;
    private WFileUpload wf;
    private Map<String,File> fileMap = new HashMap<String,File>();
    private Widget le = null;
    private Template t  = new  Template("");
    
    public ClientForm(String title,Client ge,PopBox p,ClientMgr mgr){
    	this.client = ge;
    	this.popBox = p;
    	this.cMgr = mgr;
    	this.title = title;
    	cModel = new ClientModel();
    	if(ge!=null&&!title.equals("新建"))
    		updateClientView();
    	else
    		this.client = new Client();
    	
    	t.setTemplateText("${panel}${wf}",TextFormat.XHTMLUnsafeText);
 		t.bindString("panel", "");
 		t.setAttributeValue("style", "display: inline-block;min-height:36px;");
    }
    @Override
    public Widget draw(final Template parent, String... args) {
    	this.setTemplateText(tr("client-form"),TextFormat.XHTMLUnsafeText);
    	Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				ClientForm.this.updateModel(cModel);
				if(validationPassed(cModel)){
					/*if(cModel.saveOrUpdate(cMgr.getDBEntity(),client)){
						cMgr.draw(parent, null);
						cMgr.doJavaScript("$('#bctable').datagrid();");
					}else{
						Util.infoMessage(tr("Information Please"), "失败");
					}
					popBox.setAttributeValue("style", "display:none");*/
					if(cModel.saveOrUpdate(cMgr.getDBEntity(),client)){
						if(cMgr.getSelectTr()!=null&&!cMgr.getSelectTr().getId().equals(client.getId())||cMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							cMgr.drawGrid();
							cMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+cMgr.getPaginationStart()*cMgr.getPaginationMax()+"});");
						}else if(cMgr.getSelectTr()!=null&&cMgr.getSelectTr().getId().equals(client.getId())){//选中某一行，确实执行的是【编辑】操作
							cMgr.refreshSelTr(client);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}
					popBox.setAttributeValue("style", "display:none");
				}
				
			}
		});
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				popBox.setAttributeValue("style", "display:none");
			}
		});
		if(title.equals("查看"))
			this.bindString("submit", "");
		else
			this.bindWidget("submit", submitbtn);
		this.bindWidget("close", closebtn);
		this.updateView(cModel);
    	return this;
    }
    @Override
    protected Widget createFormWidget(String field) {
//    	Widget le = null;
		  if(field.equals(cModel.linktel)||field.equals(cModel.ename)
				  ||field.equals(cModel.linkman)||field.equals(cModel.address)
				  ||field.equals(cModel.linkPhone)||field.equals(cModel.linkFax)
				  ||field.equals(cModel.linkBank)||field.equals(cModel.lbankNO)
				  ||field.equals(cModel.siteAddr)||field.equals(cModel.addrCode)
				  ||field.equals(cModel.email))
				le = new LineEdit();
		  else if(field.equals(cModel.eid)){
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(cModel.description)){
				le = new TextArea();
		  }else if(field.equals(cModel.addition)){
			/*  wf = new WFileUpload();
			    wf.uploaded().addListener(wf, new Signal.Listener() {
			    	@Override
					public void trigger() {
						try {
							String fpath = tr("base.filepath")+"/client/"+client.getId()+"/";
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							
							File dirFile = new File(fpath);
							if (!dirFile.exists()) {
								dirFile.mkdirs();
							}
							List<UploadedFile> ls = wf.getUploadedFiles();
							final ContainerWidget dwrap = new ContainerWidget(){
								@Override
								public DomElementType getDomElementType() {
									return DomElementType.DomElement_DIV;
								}
							};
							dwrap.setStyleClass("dwrap");
							for(UploadedFile file : ls){
								File source = new File(new String(file.getSpoolFileName().getBytes(),"utf-8"));
								int suffix = file.getClientFileName().lastIndexOf(".");
								String fname = new String(file.getClientFileName().getBytes(),"UTF-8");
								fname = fname.substring(0,fname.lastIndexOf("."));
								
								File source = new File(file.getSpoolFileName());
								int suffix = file.getClientFileName().lastIndexOf(".");
								String fname = file.getClientFileName().substring(0,file.getClientFileName().lastIndexOf("."));
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
								
								dwrap.addWidget(new Text(fname));
							}
							t.bindWidget("panel", dwrap);
							le = t;
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				});
			    wf.changed().addListener(wf, new Signal.Listener() {
			    	@Override
					public void trigger() {
			    		wf.upload();
			    	}
				});
			t.bindWidget("wf", wf);   
			le = t;*/
			  
			  le = new FileMgrList("client",client.getId(),!title.equals("查看"));
		  }
			return le;
    }
    private void updateClientView(){
    	cModel.setValue(cModel.eid, client.getId());
    	cModel.setValue(cModel.ename, client.getName());
    	cModel.setValue(cModel.linkman, client.getLinkman());
    	cModel.setValue(cModel.linktel, client.getLinktel());
    	cModel.setValue(cModel.linkPhone, client.getLinkphone());
    	cModel.setValue(cModel.linkFax, client.getLinkfax());
    	cModel.setValue(cModel.email,client.getEmail());
    	cModel.setValue(cModel.siteAddr, client.getSiteAddr());
    	cModel.setValue(cModel.address, client.getAddress());
    	cModel.setValue(cModel.addrCode, client.getAddrCode());
    	cModel.setValue(cModel.linkBank, client.getLinkbank());
    	cModel.setValue(cModel.lbankNO, client.getLbankNO());
    	cModel.setValue(cModel.description, client.getDescription()==null?"":client.getDescription());
    	//上传的附件
//		wf = new WFileUpload();
	/*	String pathName = tr("base.filepath")+"/client/"+client.getId()+"/";
		Util.findFiles(pathName,"*",fileMap);
		final ContainerWidget cw = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_DIV;
			}
		};
		
		
		for(Iterator iterator = fileMap.entrySet().iterator();iterator.hasNext();){
			final Map.Entry<String, File> entry = (Map.Entry<String, File>)iterator.next();
			
			final ContainerWidget wrap = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_DIV;
				}
			};
			wrap.setStyleClass("filewrap");
			String downUrl = tr("www.file").toString()+"/client/"+client.getId()+"/" + entry.getKey();
			try {
				Anchor downSource;
				downSource = new Anchor(new Link(downUrl),entry.getValue().getCanonicalFile().getName());
				downSource.setStyleClass("downUrl");
				downSource.setTarget(AnchorTarget.TargetNewWindow);
				downSource.setToolTip("下载附件");
				wrap.addWidget(downSource);
			} catch (IOException e) {
				e.printStackTrace();
			}
			final PushButton deletebtn = new PushButton("删除");
			deletebtn.setStyleClass("editcls");
			deletebtn.clicked().addListener(deletebtn, new Signal.Listener() {
				@Override
				public void trigger() {
                    	entry.getValue().getAbsoluteFile().delete();
                    	fileMap.remove(entry.getKey());
                    	cw.removeWidget(wrap);
                    	cw.removeWidget(deletebtn);
				}
			});
			wrap.addWidget(deletebtn);
			cw.addWidget(wrap);
		}		
		cw.addWidget(this.createFormWidget(cModel.addition));
		cw.setStyleClass("cwWFile");*/
//		this.bindWidget("addition-field", cw);
    }
}
