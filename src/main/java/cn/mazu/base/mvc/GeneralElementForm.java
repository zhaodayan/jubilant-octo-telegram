package cn.mazu.base.mvc;

import cn.mazu.base.entity.GeneralElement;
import cn.mazu.mysql.Entity;
import cn.mazu.ui.GeneralElementMgr;
import cn.mazu.util.Util;
import cn.mazu.util.Util.ElementTechnology;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;

//通用零件字典表单
public class GeneralElementForm extends FormView{
    private GeneralElementModel geModel;
    private GeneralElement ge;
    private PopBox popBox;
    private GeneralElementMgr geMgr;
    private Entity entity;
    private String title;
    private ComboBox techBox = new ComboBox();
    //private StringListModel techModel = new StringListModel(techBox);
    //private List<ElementTechnology> techList = new ArrayList<ElementTechnology>();
    private String techStr="";
    
    public GeneralElementForm(String title,GeneralElement ge,PopBox p,GeneralElementMgr mgr){
    	this.ge = ge;
    	this.popBox = p;
    	this.geMgr = mgr;
    	this.entity = mgr.getDBEntity();
    	this.title = title;
    	geModel = new GeneralElementModel();
    	//this.ge = (gee==null?new GeneralElement():gee);
    	if(ge!=null&&!title.equals("新建")){
    		updateClientView();	
    		this.techStr = ge.getTechnologyStr();
    	}
 	   else
 		   this.ge = new GeneralElement();
    }
    
    @Override
    public Widget draw(final Template parent, String... args) {
    	    
			/*techModel.addString("请选择");
			techModel.setData(0,0,null,ItemDataRole.UserRole);   
			ElementTechnology data_arr[] ={ElementTechnology.CNC,ElementTechnology.COATING,ElementTechnology.DISCHARGE,
					ElementTechnology.FEEDING,ElementTechnology.GRIND,ElementTechnology.HORT,
					ElementTechnology.MILLING,ElementTechnology.OUTERASSIST,ElementTechnology.WORC};
			for(int j=0;j<data_arr.length;j++){
				techModel.addString(data_arr[j].name);
				techModel.setData(j+1, 0, data_arr[j], ItemDataRole.UserRole);
			}
			techBox.setModel(techModel);*/
    	    techBox = WidgetUtil.initSelFromEnum(ElementTechnology.class);
			
			this.setTemplateText(tr("generalelement-form"),TextFormat.XHTMLUnsafeText);
			Anchor submitbtn = new Anchor(new Link(""), "保存");
			submitbtn.setStyleClass("close");
			submitbtn.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					GeneralElementForm.this.updateModel(geModel);
					if(validationPassed(geModel)){
						/*if(geModel.saveOrUpdate(entity,ge)){
							geMgr.draw(parent, null);
							geMgr.doJavaScript("$('#bctable').datagrid();");
						}else{
							Util.infoMessage("Information Please", "失败");
						}*/
						if(geModel.saveOrUpdate(geMgr.getDBEntity(),ge)){
							if(geMgr.getSelectTr()!=null&&!geMgr.getSelectTr().getId().equals(ge.getId())||geMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
								geMgr.drawGrid();
								geMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+geMgr.getPaginationStart()*geMgr.getPaginationMax()+"});");
							}else if(geMgr.getSelectTr()!=null&&geMgr.getSelectTr().getId().equals(ge.getId())){//选中某一行，确实执行的是【编辑】操作
								geMgr.refreshSelTr(ge);
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
			if(title.equals("查看")){
				this.bindString("submit", "");
			}else
				this.bindWidget("submit", submitbtn);
			this.bindWidget("close", closebtn);
			this.updateView(geModel);
		
    	return this;
    }
    
    @Override
    protected Widget createFormWidget(String field) {
    	Widget le = null;
		  if(field.equals(geModel.ecode)||field.equals(geModel.ename))
				le = new LineEdit();
		  if(field.equals(geModel.eperiod)){
			  LineEdit input = new LineEdit();
			  input.setEmptyText("请填写数字");
			  le = input;
		  }
		  if(field.equals(geModel.eid)){
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(geModel.etech)){
			  ContainerWidget cw = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_DIV; 
				}
			  };
			  cw.setStyleClass("labeldiv");
			  for (final ElementTechnology et:Util.ElementTechnology.values()){
				   final CheckBox ck = new CheckBox(et.getName());
				   if(ge.getTechnologyStr()!=null&&ge.getTechnologyStr().contains(et.getName()))
					   ck.setChecked(true);
				   ck.changed().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
                    if(ck.isChecked())
                        techStr += et.getName()+",";                        
                    else
                    	techStr = techStr.replace(et.getName()+",", "");
					}
				});
				   cw.addWidget(ck);
				   
			  } 
			  le = cw;
		  }
			return le;
    }
    private void updateClientView(){
    	geModel.setValue(geModel.ecode, ge.getCode());
    	geModel.setValue(geModel.ename, ge.getName());
    	geModel.setValue(geModel.eperiod, ge.getPeriod());
    	geModel.setValue(geModel.etech, ge.getTechnologyStr());
    	geModel.setValue(geModel.eid,ge.getId());
    }
    
	@Override
	public void updateModelField(FormModel model, String field) {
		if(field.equals(GeneralElementModel.etech)){
			geModel.setValue(geModel.etech,techStr);
		}else 
	    	   super.updateModelField(model, field);
	}
}
