package cn.mazu.sys.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.mazu.auth.Session;
import cn.mazu.base.entity.Staff;
import cn.mazu.mysql.Entity;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.ui.AccountPMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.HibernateUtil;
import cn.mazu.util.Util;
import cn.mazu.util.Util.MainMenu;
import cn.mazu.util.Util.PermissionType;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.model.ItemDataRole;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;


public class AccountPForm extends FormView{
   private static List<String> menuPermissionList = new ArrayList<String>();
   private String title;
   private AccountPMgr apMgr;
   private PopBox pb;
   private AccountPModel apModel;
   private AccountPermission ap;
   
   private ComboBox staffSel = new ComboBox(),menuSel = new ComboBox();
   private StringListModel staffModel = new StringListModel();/*,menuModel = new StringListModel();*/
   private List<PermissionType> ptList = new ArrayList<PermissionType>();
   private String permissionStr = "",menuStr = "";

   
   private Map<String,String> menuPerMap = new HashMap<String,String>();
   
   private Session session_ = new Session(HibernateUtil.getEntityManager());
   private boolean editFlag = false;
   private String availUserName = "",availDataCode = "";
   public AccountPForm(String title,AccountPermission ap,PopBox pb,AccountPMgr apMgr) {
		this.ap = ap;
		  this.title = title;
		  this.pb = pb;
		  this.apModel = new AccountPModel();
		  this.apMgr = apMgr;
		  if(ap!=null&&!title.equals("新建")){
			updateClientView();
			editFlag = true;
		  }else
			this.ap = new AccountPermission();
		  resolveMenuPerStr();
		  List<EntityObject> apList = apMgr.getDBEntity().getSubListQBC(AccountPermission.class,new Object[]{"notEqual","id",this.ap.getId(),"and"}, -1, -1);
		  for (EntityObject eo:apList){
			  AccountPermission apermiss = ((AccountPermission)eo);
			  availUserName += apermiss.getUsername()+",";
			  availDataCode += apermiss.getDataCode()+",";
		  }
   }
   static{
	   menuPermissionList.add("permission");
	   menuPermissionList.add("setting");
	   menuPermissionList.add("create");
	   menuPermissionList.add("edit");
	   menuPermissionList.add("delete");
	   menuPermissionList.add("view");
//	   menuPermissionList.add("filtrate");
	   menuPermissionList.add("display");
	   menuPermissionList.add("hide");
	   menuPermissionList.add("finish");
	   menuPermissionList.add("return");
	   menuPermissionList.add("displayAll");
	   menuPermissionList.add("in_store");
	   menuPermissionList.add("out_store");
	   menuPermissionList.add("export");
	   menuPermissionList.add("filtrate");
	   menuPermissionList.add("vdetail");
	   menuPermissionList.add("edetail");
	   menuPermissionList.add("usedc");
   }
   @Override
	public Widget draw(final Template parent, String... args) {
       try {
		Entity dbEntity = apMgr.getDBEntity();
		   List<EntityObject> staffList = dbEntity.getSubListQBC(Staff.class, new Object[]{"like","dataCode","___","and"}, -1, -1);
		   for (EntityObject eo:staffList){
			   Staff staff = (Staff)eo;
			   staffModel.addString(staff.getName());
			   staffModel.setData(staffList.indexOf(eo), 0,staff,ItemDataRole.UserRole);
		   }
		   staffSel.setModel(staffModel);
		   
		   this.setTemplateText(tr("ap-form"),TextFormat.XHTMLUnsafeText);
		   Anchor submitbtn = new Anchor(new Link(""), "保存");
			submitbtn.setStyleClass("close");
			submitbtn.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					AccountPForm.this.updateModel(apModel);
					if(apValidate()){
						if(apModel.saveOrUpdate(apMgr.getDBEntity(),ap,session_)){
							if(apMgr.getSelectTr()!=null&&!apMgr.getSelectTr().getId().equals(ap.getId())||apMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
								apMgr.drawGrid();
								apMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+apMgr.getPaginationStart()*apMgr.getPaginationMax()+"});");
							}else if(apMgr.getSelectTr()!=null&&apMgr.getSelectTr().getId().equals(ap.getId())){//选中某一行，确实执行的是【编辑】操作
								apMgr.refreshSelTr(ap);
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
			if(title.equals("查看"))
				this.bindString("submit", "");
			else
				this.bindWidget("submit", submitbtn);
			this.bindWidget("close", closebtn);
			this.updateView(apModel);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   return this;
	}
   @Override
	protected Widget createFormWidget(String field) {
	   Widget fw = null;
		if(field.equals(apModel.username)||field.equals(apModel.datacode)){
			LineEdit le = new LineEdit();
			if(editFlag) le.setDisabled(true);
			fw = le;
		}else if(field.equals(apModel.staff)){
			fw = staffSel;
		}else if(field.equals(apModel.menuPermission)){
			ContainerWidget tbody = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TABLE;
				}
			};
			tbody.setStyleClass("pms_table");
			ContainerWidget thead = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TR;
				}
			};
			thead.setStyleClass("navth");
			//
			for (int k=0;k<menuPermissionList.size();k++){
				ContainerWidget ttitle = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TD;
					}
				};
				ttitle.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
				Text text = new Text(tr(menuPermissionList.get(k)),ttitle);
				thead.addWidget(ttitle);
			}
			tbody.addWidget(thead);
			
			for(int i=0;i<MainMenu.getAllDescendant().size();i++){
				MainMenu mm = MainMenu.getAllDescendant().get(i);
				ContainerWidget tr = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TR;
					}
				};
				ContainerWidget td1 = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TD;
					}
				};
				td1.setStyleClass("navtd");
				td1.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
				final String menuname = MainMenu.getMainMenuDesc(mm.getName());
				Text t = new Text(menuname,td1);
				tr.addWidget(td1);
				for(int j=0;j<menuPermissionList.size()-1;j++){
					String pp = menuPermissionList.get(j+1);
					ContainerWidget td = new ContainerWidget(){
						@Override
						public DomElementType getDomElementType() {
							return DomElementType.DomElement_TD;
						}
					};
					td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
					if(!pp.equals("")&&mm.permission.contains(pp)){
						final CheckBox ck = new CheckBox(td);
						final String permissionName = tr(pp).toString();
						if(menuPerMap.keySet().contains(menuname)&&
								menuPerMap.get(menuname).contains(permissionName)){
							ck.setChecked(true);
						}
						ck.clicked().addListener(this, new Signal.Listener() {
							@Override
							public void trigger() {
								String previousP = menuPerMap.get(menuname)==null?"":menuPerMap.get(menuname);
								//System.out.println("previousP:"+previousP);
	                           	if(ck.isChecked()&&!previousP.contains(permissionName+","))
	                           		previousP+=permissionName+",";
	                           	else
	                           		previousP = previousP.replace(permissionName+",","");
	                           	menuPerMap.put(menuname, previousP);
							}
						});
					}
					
					tr.addWidget(td);
				}
				tbody.addWidget(tr);
			}
			fw = tbody;
		}else if(field.equals(apModel.eid)||field.equals(apModel.menu)){
			LineEdit le = new LineEdit();
			le.hide();
			fw = le;
		}
		return fw;
	}
   private void updateClientView(){
	   apModel.setValue(apModel.eid, ap.getId());
	   //apModel.setValue(apModel.password, ap.getPassword());
	   if(ap.getStaff()!=null)apModel.setValue(apModel.staff, ap.getStaff().getName());
	   menuStr = ap.getMenuStr();
	   apModel.setValue(apModel.menu, menuStr);
	   apModel.setValue(apModel.username, ap.getUsername());
	   apModel.setValue(apModel.datacode, ap.getDataCode());
	   apModel.setValue(apModel.menuPermission, ap.getMenuPermission());
   }
   @Override
	public void updateModelField(FormModel model, String field) {
	    
		if(field.equals(apModel.staff)){
			int row = staffSel.getCurrentIndex();
			Staff staff = (Staff)staffModel.getData(row,0,ItemDataRole.UserRole);
			model.setValue(field, staff);
		}else if(field.equals(apModel.menu)){//保证只调用一次
			joinMapToString();
		}else if(!field.equals(apModel.menuPermission)&&!field.equals(apModel.menu)){
			super.updateModelField(model, field);
		}
	}
    //解析已有权限字符串
    private void resolveMenuPerStr(){
    	String menuPerStr = ap.getMenuPermission();
    	if(menuPerStr!=null&&!menuPerStr.trim().equals("")){//menu1:per1,per2,per3,per4;menu2:per1,per2;menu3:per1,per3;...
    		String[] menuStr = menuPerStr.split(";");
    		for (int i=0;i<menuStr.length;i++){
    			   String[] sArray2 = menuStr[i].split(":");
    			   menuPerMap.put(sArray2[0],sArray2[1]);
    		}
    	}
    }
    //将map中的字符串重新组合
    private void joinMapToString(){
    	String menus = "",menups = "";
    	for (Iterator iterator = menuPerMap.entrySet().iterator();iterator.hasNext();){
    		 Map.Entry<String, String> menuPerEntry = (Map.Entry<String, String>)iterator.next();
    		 if(!menuPerEntry.getValue().trim().equals("")){
    			 menups+=menuPerEntry.getKey()+":"+menuPerEntry.getValue()+";";
    			 menus += MainMenu.getMainMenuByDesc(menuPerEntry.getKey())+",";
    		 }
    	}
    	apModel.setValue(apModel.menuPermission, menups);
    	apModel.setValue(apModel.menu, menus);
    }
    //判断数据码和用户名是否重复
    private boolean apValidate(){
    	this.bindString("username-field-info", "");
    	this.bindString("dc-field-info", "");
    	boolean validateFlag = true;
    	String username = apModel.getValue(apModel.username).toString().trim();
    	String datacode = apModel.getValue(apModel.datacode).toString().trim();
    	if(username.equals("")){
    		this.bindString("username-field-info", "<span class='span'>"+tr("is null")+"</span>");
    		validateFlag = false;
    	}
    	else if(availUserName.contains(username)){
    		this.bindString("username-field-info", "<span class='span'>"+tr("usernameused")+"</span>");
    		validateFlag = false;	
    	}
    	if(datacode.equals("")){
    		this.bindString("dc-field-info", "<span class='span'>"+tr("is null")+"</span>");
		    validateFlag = false;
		}
    	else if(availDataCode.contains(datacode)){
    		this.bindString("dc-field-info", "<span class='span'>"+tr("datacodeused")+"</span>");
		    validateFlag = false;
    	}
    	return validateFlag;
    }
}
