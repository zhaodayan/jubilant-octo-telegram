package cn.mazu.drawing.mvc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mazu.WApplication;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.base.entity.Staff;
import cn.mazu.drawing.entity.Drawing;
import cn.mazu.surface.entity.Surface;
import cn.mazu.ui.DrawingMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.Side;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.UploadedFile;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.WFileUpload;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.SelectionBox;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.AnchorTarget;


public class DrawingForm extends FormView {
   private DrawingModel dModel;
   private PopBox pb;
   private DrawingMgr dMgr;
   private Drawing drawing;
   
   private String title;
   private ComboBox surfaceSel = new ComboBox(),elementSel = new ComboBox();
   private ComboBox authorSel = new ComboBox();
   //private StringListModel authorDropModel = new StringListModel(authorSel);
   
   private WFileUpload wf;
   private Map<String,File> fileMap = new HashMap<String,File>();
   private Template t  = new  Template("");
   private Widget le = null;
   private String elementcode = "",surfacecode = "";
   private List<EntityObject> elements;
   public DrawingForm(String title,Drawing drawing,PopBox pb,DrawingMgr dMgr) {
	  this.drawing = drawing;
	  this.pb = pb;
	  this.dMgr = dMgr;
	  this.title = title;
	  dModel = new DrawingModel();
	  if(drawing!=null&&!title.equals("新建"))
		  updateClientView();
	  else
		  this.drawing = new Drawing();
	  elements = dMgr.getDBEntity().getSubListQBC(GeneralElement.class,new Object[]{"like","dataCode",
			 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"},-1,-1);

	  t.setTemplateText("${panel}${wf}",TextFormat.XHTMLUnsafeText);
	  t.bindString("panel", "");
	  t.setAttributeValue("style", "display: inline-block;width:282px;height: 55px;background-color:#f1f1f2;");

   }
   @Override
	public Widget draw(final Template parent, String... args) {
	   List<EntityObject> surfaceList = dMgr.getDBEntity().getSubListQBC(Surface.class, new Object[]{"like","dataCode","___","and"}, -1, -1);
	   surfaceSel = WidgetUtil.initSelFromList(surfaceList, "name");
	   //作者    	
	   List<EntityObject> staffList = dMgr.getDBEntity().getSubListQBC(Staff.class, new Object[]{"like","dataCode","___","and"},-1,-1);
	   authorSel = WidgetUtil.initSelFromList(staffList, "name");
	   
	   List<EntityObject> elementList = dMgr.getDBEntity().getSubListQBC(GeneralElement.class, new Object[]{"like","dataCode","___","and"}, -1, -1);
	   elementSel = WidgetUtil.initSelFromList(elementList,"name");
	   
	   this.setTemplateText(tr("drawing-form"),TextFormat.XHTMLUnsafeText);
		Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				DrawingForm.this.updateModel(dModel);
				if(validationPassed(dModel)){
					if(dModel.saveOrUpdate(dMgr.getDBEntity(),drawing)){
						if(dMgr.getSelectTr()!=null&&!dMgr.getSelectTr().getId().equals(drawing.getId())||dMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							dMgr.drawGrid();
							dMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+dMgr.getPaginationStart()*dMgr.getPaginationMax()+"});");
						}else if(dMgr.getSelectTr()!=null&&dMgr.getSelectTr().getId().equals(drawing.getId())){//选中某一行，确实执行的是【编辑】操作
							dMgr.refreshSelTr(drawing);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}
					pb.setAttributeValue("style", "display:none");
				}
			}
		});
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				pb.setAttributeValue("style", "display:none");
			}
		});
		if(title.equals("查看")){
			this.bindString("submit", "");
		}else
			this.bindWidget("submit", submitbtn);
		this.bindWidget("close", closebtn);
		this.updateView(dModel);
		return this;
	}   
   private SelectionBox select_widget = new SelectionBox();
   @Override
	protected Widget createFormWidget(String field) {
		  if(field.equals(dModel.author)){
			le = authorSel;  
		  }else if(field.equals(dModel.drawing)){
			  wf = new WFileUpload();
			 
			    wf.uploaded().addListener(wf, new Signal.Listener() {
			    	@Override
					public void trigger() {
						try {
							Util.deleteFiles(tr("base.filepath")+"/drawing/"+drawing.getId()+"/","*");
							String fpath = tr("base.filepath")+"/drawing/"+drawing.getId()+"/";
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							File dirFile = new File(fpath);
							if (!dirFile.exists()) {
								dirFile.mkdirs();
							}
							List<UploadedFile> ls = wf.getUploadedFiles();
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
								else
									fname = fname+file.getClientFileName().substring(suffix);
								File target = new File(fpath+"/"+fname);
								Util.copyFile(source, target);
								source.delete();
								
								String uploadUrl = tr("www.file").toString()+"/drawing/"+drawing.getId()+"/"+fname;
								Anchor uploadAnchor = new Anchor(new Link(uploadUrl),fname);
								uploadAnchor.setStyleClass("downUrl");
								uploadAnchor.setTarget(AnchorTarget.TargetNewWindow);
								uploadAnchor.setToolTip("下载图纸");
								    t.bindWidget("panel", uploadAnchor);
								    le = t;
								
							}
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
			   String pathName = tr("base.filepath")+"/drawing/"+drawing.getId()+"/";
			   Util.findFiles(pathName,"*",fileMap);
				String downUrl = "";
				if(fileMap.size()>0){
					String entry = fileMap.keySet().iterator().next();
					downUrl = tr("www.file").toString()+"/drawing/"+drawing.getId()+"/" + entry;
					Anchor downSource = new Anchor(new Link(downUrl),fileMap.get(entry).getName());
					downSource.setStyleClass("downUrl");
					downSource.setTarget(AnchorTarget.TargetNewWindow);
					downSource.setToolTip("下载图纸");
					t.bindWidget("panel", downSource);
				}
			  le = t;
		  }else if(field.equals(dModel.surface)){
			  le = surfaceSel;
			  surfaceSel.changed().addListener(this,new Signal.Listener() {
				@Override
				public void trigger() {
					if(WidgetUtil.getSelEO(surfaceSel)!=null){
						elementcode = ((GeneralElement)elements.get(select_widget.getCurrentIndex())).getCode();
						surfacecode = ((Surface)WidgetUtil.getSelEO(surfaceSel)).getCode();
						DrawingForm.this.bindWidget("code-field",new LineEdit(surfacecode+"-"+elementcode));
					}
				}
			});
		  }else if(field.equals(dModel.element)){
			  final Template tpl = new Template();
			  String ttpl = "<table cellspacing='2'>"
						+ "<tr><td>${select_widget_input}${filterW}</td></tr>"
						+ "<tr><td>${select_widget}</td></tr>"
						+" </table>";
			  tpl.setTemplateText(ttpl, TextFormat.XHTMLUnsafeText);
			  tpl.setAttributeValue("style", "display:inline-block;background-color: #EDF6FF;");
			  final LineEdit select_widget_input = new LineEdit();
			  select_widget_input.setText(((GeneralElement)elements.get(0)).getName()+"-"+((GeneralElement)elements.get(0)).getCode());
			  for (EntityObject eo : elements) {
					GeneralElement el = (GeneralElement)eo;
					select_widget.addItem(el.getName() + "-" + el.getCode());
					if(dModel.valueText(dModel.name).equals(el.getName()+"-"+el.getCode())){
						select_widget.setCurrentIndex(elements.indexOf(eo));
						select_widget_input.setText(dModel.valueText(dModel.name));
						}
			   }
			  
			  PushButton filterW = new PushButton(tr("search"));
				filterW.setStyleClass("editcls");
				filterW.setAttributeValue("style", "vertical-align:top");
				filterW.clicked().addListener(this,new Signal.Listener() {
					@Override
					public void trigger() {
						select_widget.clear();
						String txtInput = select_widget_input.getText().trim();
						elements = dMgr.getDBEntity().getSubListQBC(GeneralElement.class,new Object[]{
							       "like","dataCode",
							       Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and",
							       "like","name","%"+txtInput+"%","and","like","code","%"+txtInput+"%","or"},-1,-1);
						for (EntityObject eo : elements) {
							GeneralElement el = (GeneralElement)eo;
							select_widget.addItem(el.getName() + "-" + el.getCode());
						}
					}
				});
			  
			  
			  select_widget.activated().addListener(null, new Signal.Listener() {
				@Override
				public void trigger() {
					int selindex = select_widget.getCurrentIndex();
						elementcode = ((GeneralElement)elements.get(selindex)).getCode();
						select_widget_input.setText(((GeneralElement)elements.get(selindex)).getName()+"-"+elementcode);
						DrawingForm.this.bindWidget("code-field",new LineEdit(surfacecode+"-"+elementcode));
				}
			  });
			  select_widget.setMargin(new WLength(10), EnumSet.of(Side.Right));
			  tpl.bindWidget("select_widget_input", select_widget_input);
			  tpl.bindWidget("filterW", filterW);
			  tpl.bindWidget("select_widget", select_widget);
			 le = tpl;
		  }else if(field.equals(dModel.eid)){
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(dModel.code)){
			  le =new LineEdit();
		  }
			return le;
	}
    private void updateClientView(){
    	dModel.setValue(dModel.eid, drawing.getId());
    	dModel.setValue(dModel.author, drawing.getAuthor());
    	dModel.setValue(dModel.code, drawing.getCode());
    	if(drawing.getSurface()!=null){dModel.setValue(dModel.surface, drawing.getSurface().getName());surfacecode = drawing.getSurface().getCode();}
    	if(drawing.getGeneralElement()!=null){
    		dModel.setValue(dModel.element, drawing.getGeneralElement().getName());
    		dModel.setValue(dModel.name, drawing.getGeneralElement().getName()+"-"+drawing.getGeneralElement().getCode());
    		elementcode = drawing.getGeneralElement().getCode();
        }
    }
    @Override
	public void updateModelField(FormModel model, String field) {
		if(field.equals(dModel.surface)){
			dModel.setValue(field, WidgetUtil.getSelEO(surfaceSel));
		}else if(field.equals(dModel.element)){
			dModel.setValue(field, elements.get(select_widget.getCurrentIndex()));
		}else if(field.equals(dModel.author)){
			dModel.setValue(field,WidgetUtil.getSelEO(authorSel)==null?"":WidgetUtil.getSelDisplayStr(authorSel));
		}else if(field.equals(dModel.code)){
			dModel.setValue(field, surfacecode+"-"+elementcode);
		}else{
			super.updateModelField(model, field);
		}
	}
}
