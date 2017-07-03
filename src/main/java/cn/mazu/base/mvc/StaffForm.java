package cn.mazu.base.mvc;

import java.util.Date;
import java.util.List;

import cn.mazu.WApplication;
import cn.mazu.base.entity.Dept;
import cn.mazu.base.entity.Staff;
import cn.mazu.mysql.Entity;
import cn.mazu.ui.StaffMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.UserGender;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.container.Anchor;

public class StaffForm extends FormView{
	private StaffModel sModel;
    private Staff staff;
    private PopBox popBox;
    private StaffMgr sMgr;
    private String title;
    private ComboBox deptCB = new ComboBox();
    private StringListModel deptDropModel = new StringListModel(deptCB);
    
    private ComboBox gender = new ComboBox();
    public cn.mazu.widget.AddressSelect sb;
    
    public StaffForm(String title,Staff staff,PopBox popBox,StaffMgr sMgr) {
	    this.staff = staff;
	    this.popBox = popBox;
	    this.sMgr = sMgr;
	    this.title = title;
	    sModel = new StaffModel();
	    sb = new cn.mazu.widget.AddressSelect("","","");
	    if(staff!=null&&!title.equals("新建"))
	    	updateClientView();
	    else
	    	this.staff = new Staff();
	}
    @Override
    public Widget draw(final Template parent, String... args) {
    	Entity entity = sMgr.getDBEntity();
		//性别
    	gender = WidgetUtil.initSelFromEnum(UserGender.class);
        //部门    	
    	
		List<EntityObject> deptList = entity.getSubListQBC(Dept.class,new Object[]{"like","dataCode","___","and"},-1,-1);
			//Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"},-1,-1);
		deptCB = WidgetUtil.initSelFromList(deptList,"name");
    	
    	this.setTemplateText(tr("staff-form"),TextFormat.XHTMLUnsafeText);
    	Anchor submitbtn = new Anchor(new Link(""), "保存");
		submitbtn.setStyleClass("close");
		submitbtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				StaffForm.this.updateModel(sModel);
				if(validationPassed(sModel)){					
					if(sModel.saveOrUpdate(sMgr.getDBEntity(),staff)){
						if(sMgr.getSelectTr()!=null&&!sMgr.getSelectTr().getId().equals(staff.getId())||sMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
							sMgr.drawGrid();
							sMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+sMgr.getPaginationStart()*sMgr.getPaginationMax()+"});");
						}else if(sMgr.getSelectTr()!=null&&sMgr.getSelectTr().getId().equals(staff.getId())){//选中某一行，确实执行的是【编辑】操作
							sMgr.refreshSelTr(staff);
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
		this.updateView(sModel);
    	return this;
    }
    @Override
    protected Widget createFormWidget(String field) {
    	Widget le = null;
		  if(field.equals(sModel.linktel)||field.equals(sModel.name)||field.equals(sModel.email)
				  ||field.equals(sModel.nationID)||field.equals(sModel.education))
				le = new LineEdit();
		  else if(field.equals(sModel.gender)){
		    	le = gender;
		  }else if(field.equals(sModel.deptfield)){
			  le = deptCB;
		  }else if(field.equals(sModel.joinDate)||field.equals(sModel.birthDate)){
			  DateEdit d = new DateEdit();
		      d.setFormat("yyyy-MM-dd");
		      le = d;			  
		  }else if(field.equals(sModel.nativePlace)){
			  sb.setAttributeValue("style", "display:inline-block;width:282px;");
			  le = sb;
		  }else if(field.equals(sModel.remark)){
			  le = new TextArea();
		  }else if(field.equals(sModel.idfield)){
			  le =new LineEdit();
			  le.hide();
		}
			return le;
    }
    private void updateClientView(){
    	sModel.setValue(sModel.idfield,staff.getId());
    	sModel.setValue(sModel.name,staff.getName());
    	sModel.setValue(sModel.nationID,staff.getNationID());
    	if(staff.getGender()!=null)sModel.setValue(sModel.gender, staff.getGender().name);
    	if(staff.getBirthDate()!=null)sModel.setValue(sModel.birthDate,Util.formatDateyyyyMMdd(staff.getBirthDate()));
    	sModel.setValue(sModel.education,staff.getEducation());
    	if(staff.getJoinDate()!=null)sModel.setValue(sModel.joinDate,Util.formatDateyyyyMMdd(staff.getJoinDate()));
    	sModel.setValue(sModel.linktel, staff.getLinktel());
    	sModel.setValue(sModel.email, staff.getEmail());
    	sModel.setValue(sModel.remark,staff.getRemark());
    	if(staff.getDept()!=null)sModel.setValue(sModel.deptfield, staff.getDept().getName());
    	if(staff.getGender()!=null)sModel.setValue(sModel.gender, staff.getGender().getName());
    	String addra = staff.getNativePlace();
		String[] addrary = addra.split(",");
		if(addrary.length>2){
			sb = new cn.mazu.widget.AddressSelect(addrary[0],addrary[1],addrary[2]);
		}else if(addrary.length>1){
			sb = new cn.mazu.widget.AddressSelect(addrary[0],addrary[1],"");
		}else if(addrary.length>0){
			sb = new cn.mazu.widget.AddressSelect(addrary[0],"","");
		}else if(addrary.length==0){
			sb = new cn.mazu.widget.AddressSelect("","","");
		}
		sModel.setValue(sModel.nativePlace,sb);
    }
  
    @Override
    public void updateModelField(FormModel model, String field) {
    	if(field.equals(sModel.deptfield))
    		sModel.setValue(field,WidgetUtil.getSelEO(deptCB));
    	else if(field.equals(sModel.gender))
    		sModel.setValue(field,WidgetUtil.getSelEO(gender));
    	else if(field.equals(sModel.nativePlace)){
    		sModel.setValue(field, sb.getAddrStr());	
    	}else 
    	   super.updateModelField(model, field);
    }
}
