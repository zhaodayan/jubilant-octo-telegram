package cn.mazu.sitemg.mvc;


import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Device;
import cn.mazu.ui.Deviceinfo;
import cn.mazu.util.Util;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.ImageMgr;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class DeviceView extends FormView{
	private Device p;
	private SitemgEntity smgEntity;
	private DeviceModel pModel;
	private PopBox pb;
	private Template img = new Template();
	private Deviceinfo sdMgr;
	private String title = "";
	public DeviceView(Device p,PopBox pb,Deviceinfo sdMgr,String title){
		pModel = new DeviceModel();
		smgEntity = new SitemgEntity();
		this.p = p;
		this.pb = pb;
		this.sdMgr = sdMgr;
		this.title = title;
		img.setTemplateText("<div class='' style='float:left;'>${imgmgr}</div>",TextFormat.XHTMLUnsafeText);
		if(p!=null&&!title.equals("新建")){
			updateDeviceView();
		}else
			this.p = new Device();
	}

	@Override
	public Widget draw(final Template parent, final String... args) {
		this.setTemplateText(tr("form-device"), TextFormat.XHTMLUnsafeText);
		this.updateView(pModel);
		Anchor savebtn = new Anchor(new Link(""), "保存");
		savebtn.setStyleClass("close");
		savebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				DeviceView.this.updateModel(pModel);
				if(validationPassed(pModel)){
					
					/*if(pModel.saveOrUpdate(sdMgr.getDBEntity(),p)){
						sdMgr.draw(parent, null);
						sdMgr.doJavaScript("$('#bctable').datagrid();");
					}*/
					if(pModel.saveOrUpdate(sdMgr.getDBEntity(),p)){
						if(sdMgr.getSelectTr()!=null&&!sdMgr.getSelectTr().getId().equals(p.getId())||sdMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							sdMgr.drawGrid();
							sdMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+sdMgr.getPaginationStart()*sdMgr.getPaginationMax()+"});");
						}else if(sdMgr.getSelectTr()!=null&&sdMgr.getSelectTr().getId().equals(p.getId())){//选中某一行，确实执行的是【编辑】操作
							sdMgr.refreshSelTr(p);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}

					pb.setAttributeValue("style", "display:none");
				}
			}
		});
		if(title.equals("查看")){
			this.bindString("submit", "");
		}else
			this.bindWidget("submit", savebtn);
		Anchor closebtn = new Anchor(new Link(""), "关闭");
		closebtn.setStyleClass("close");
		closebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				pb.setAttributeValue("style", "display:none");
			}
		});
		this.bindWidget("close", closebtn);
		return this;
	}

	@Override
	protected Widget createFormWidget(String field) {
		Widget result = null;
		if(field.equals(DeviceModel.namefield))
			result = new LineEdit();
		else if(field.equals(DeviceModel.introfield))
			result = new TextArea();
		else if(field.equals(DeviceModel.imguploadfield)){
			img.bindWidget("imgmgr",new ImageMgr(p.getId(),"device"));
			result = img;
		}
		return result;
	}
	
	private void updateDeviceView(){
		pModel.setValue(pModel.introfield, p.getDintro());
		pModel.setValue(pModel.namefield, p.getDname());
	}
}
