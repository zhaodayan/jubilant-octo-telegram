package cn.mazu.sitemg.mvc;

import java.util.Date;

import cn.mazu.sitemg.SitemgEntity;
import cn.mazu.sitemg.entity.Notice;
import cn.mazu.ui.CommentInfo;
import cn.mazu.util.Util;
import cn.mazu.util.Util.NoticeType;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.model.ItemDataRole;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.SimpleHtmlEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class NoticeView extends FormView{
	private Notice nt;
	private SitemgEntity smgEntity;
	private NoticeModel ntModel;
	private PopBox pb;
	private CommentInfo comInfo;
	private String type = "";

	private ComboBox nTypeSelect = new ComboBox();
	//private StringListModel ntypeModel = new StringListModel(nTypeSelect);
	SimpleHtmlEdit editor = new SimpleHtmlEdit();
	
	public NoticeView(Notice nt,PopBox pb,CommentInfo comInfo,String type){
		//editor.setWidth(new WLength(700));
		ntModel = new NoticeModel();
		smgEntity = new SitemgEntity();
		this.nt = nt;
		this.pb = pb;
		this.type = type;
		this.comInfo = comInfo;
		if(nt!=null&&!type.equals("新建"))
			updateNoticeView();
		else
			this.nt = new Notice();
	}
	
	@Override
	public Widget draw(final Template parent, String... args) {
	/*	ntypeModel.addString("请选择类型");
		ntypeModel.setData(0, 0, null, ItemDataRole.UserRole);
		NoticeType data_arr[] = {NoticeType.INNERNT,NoticeType.OUTERNT};
		for(int j=0;j<data_arr.length;j++){
			ntypeModel.addString(data_arr[j].name);
			ntypeModel.setData(j+1, 0, data_arr[j], ItemDataRole.UserRole);
		}
		nTypeSelect.setModel(ntypeModel);*/
		nTypeSelect = WidgetUtil.initSelFromEnum(NoticeType.class);
		
		this.setTemplateText(tr("form-notice"),TextFormat.XHTMLUnsafeText);
		this.updateView(ntModel);
		
		Anchor savebtn = new Anchor(new Link(""), "保存");
		savebtn.setStyleClass("close");
		savebtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				NoticeView.this.updateModel(ntModel);
				if(validationPassed(ntModel)){
					/*if(ntModel.saveOrUpdate(comInfo.getDBEntity(),nt)){
						comInfo.draw(parent, null);
						comInfo.doJavaScript("$('#bctable').datagrid();");
					}*/
					if(ntModel.saveOrUpdate(comInfo.getDBEntity(),nt)){
						if(comInfo.getSelectTr()!=null&&!comInfo.getSelectTr().getId().equals(nt.getId())||comInfo.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							comInfo.drawGrid();
							comInfo.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+comInfo.getPaginationStart()*comInfo.getPaginationMax()+"});");
						}else if(comInfo.getSelectTr()!=null&&comInfo.getSelectTr().getId().equals(nt.getId())){//选中某一行，确实执行的是【编辑】操作
							comInfo.refreshSelTr(nt);
						}
					}else{
						Util.infoMessage(tr("Information Please").toString(), "失败");
					}

					pb.setAttributeValue("style", "display:none");
				}
			}
		});
		if(type.equals("查看")){
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
		if(field.equals(NoticeModel.nocontentField)){
			editor = new SimpleHtmlEdit();
			editor.setWidth(new WLength(700));
			editor.setText((nt.getContent()!=null&&!nt.getContent().equals(""))?nt.getContent():"");
			result = editor;
		}else if(field.equals(NoticeModel.nodateField)){
			DateEdit d = new DateEdit();
			d.setFormat("yyyy-MM-dd");
			d.setText(Util.formatDateyyyyMMdd(new Date()));
			result = d;
		}else if(field.equals(NoticeModel.notitleField)){
			result = new LineEdit();
		}else if(field.equals(NoticeModel.notypeField)){
			result = nTypeSelect;
		}
		return result;
	}
	

	@Override
	public void updateModelField(FormModel model, String field) {

		if(field.equals(NoticeModel.notypeField)){
			ntModel.setValue(ntModel.notypeField, WidgetUtil.getSelEO(nTypeSelect));
		}else if(field.equals(NoticeModel.nocontentField)){
			ntModel.setValue(ntModel.nocontentField, editor.getText());
		}else
			super.updateModelField(model, field);
	}

	private void updateNoticeView(){
//		ntModel.setValue(ntModel.nocontentField,nt.getContent());
		ntModel.setValue(ntModel.nodateField,Util.formatDateyyyyMMdd(nt.getPublishDate()));
		ntModel.setValue(ntModel.notitleField,nt.getTitle());
		ntModel.setValue(ntModel.notypeField, nt.getType().name);
	}
	
}
