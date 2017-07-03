package cn.mazu.widget;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.WApplication;
import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.mysql.Entity;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.MainMenu;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.utils.WDate;
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.WebResponse;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.composite.popup.Icon;
import cn.mazu.widget.kit.composite.popup.StandardButton;
import cn.mazu.widget.kit.composite.popup.WMessageBox;
import cn.mazu.widget.kit.model.ItemDataRole;
import cn.mazu.widget.kit.model.StringListModel;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
//抽取公共的部分，画表--表头---表体+分页
public class MiddleTpl extends HtmlTemplate {
	static public Logger logger = LoggerFactory.getLogger(MiddleTpl.class);
	
	private HeadButton bcButton;
	public String btnstr = "";
	//fieldList字段集合，detailList明细字段集合，filterList用作过滤的集合，hiddenList主表隐藏的字段集合，detailHiddenList明细表字段集合
	private List<Field> fieldList,detailFieldList = new ArrayList<Field>(),filterFieldList,hiddenList = new ArrayList<Field>(),detailHiddenList = new ArrayList<Field>();
	//qryParamList为主页面的查询过滤条件
	private List<String> qryParamList = new ArrayList<String>();
	//private Field hiddenField;//销售合同和工单管理用的是一个实体，但是显示的字段不完全一样，所以在调用的Mgr处另外设置下
	private int display_count = 0;
	private List<EntityObject> plist = new ArrayList<EntityObject>();//plist查询出的记录
	private Entity dbEntity;
	private Class curCls,deCls;
	
	private Object deliveryObj;//选中某一行后，那一行的对象
	private Object[] obj_arr;//为区分四类合同，做了前置查询条件，数组中有两个值：属性名-属性值；没有条件限定的其他类为1=1假条件,或看情况
	
	private int start_,detailStart,max_,detailMax;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decimalF = new DecimalFormat("#0.##");
	
	//筛选
	private Template filterTpl;
	//private ComboBox/*ComboBox fieldSel = new ComboBox(),*/operatorSel = new ComboBox();
	//private StringListModel /*fieldModel = new StringListModel(),*/operatorModel = new StringListModel();
	//private FormWidget inputval1,inputval2;
	//事件传播标志
	private boolean propagationFlag;
	
	//hql or sql
	private boolean navSqlFlag = false;
    public MiddleTpl() {}
    private Template parent,winOuter,pagiOuter;
    
	
	public MiddleTpl(String clsName,Object[] obj_arr){//queryCod = queryCondition
		this.start_ = 0;
		this.max_ = 10;
		this.detailStart = 0;
		this.detailMax = 10;
		dbEntity = new Entity();
		this.obj_arr = obj_arr;	
		try {
			curCls= Class.forName(clsName);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		fieldList = new ArrayList<Field>();
		//this.btnstr = "filtrate,view,create,edit,del";

	}
	
	@Override
	public Widget draw(final Template parent, String... args) {
		
		this.parent = parent;
		//this.setParent(parent);
		this.btnstr = getMenuPermission();
		trListeners.clear();
		//setChildWidget("superc",this);
		this.setTemplateText("${menubtn}"+
				"<table id='bctable' class='easyui-datagrid' "+//this.start_*this.max_
				"			data-options=\"rownumbers:true,pagination:false,pagiStart:"+this.start_*this.max_+",singleSelect:true,method:'get'," +
				"           fit:true,fitColumns:true,"+
				"           striped:true,left:'150px',top:'20px'\">"+
				"            <thead>"+
				"            ${trhead}"+
				"            </thead>" +
			    			"${tbody}"+
			    "</table>"
				+"${pagi}${win}${win1}",TextFormat.XHTMLUnsafeText);
		
		winOuter = new Template();
		winOuter.setTemplateText("${win}", TextFormat.XHTMLUnsafeText);
		winOuter.bindString("win","");
		pagiOuter = new Template();
		pagiOuter.setTemplateText("${pagi}", TextFormat.XHTMLUnsafeText);
		pagiOuter.bindString("pagi", "");
		this.bindWidget("win", winOuter);
    	this.bindWidget("pagi", pagiOuter);
		bcButton = new HeadButton("",this.btnstr){
			@Override
			public Widget generateSimpleForm(String title,PopBox pb) {
				return generateRelevantForm(title,pb);
			}
    		@Override
    		public void removeFromTbl() {
    			if(removeFromMT()){
    				MiddleTpl.this.draw(parent, null);
    				MiddleTpl.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+start_*max_+"});");
    			}else{
    				Util.infoMessage(tr("Information Please").toString(), "删除失败");
    			}
    		}
			@Override
			public Widget generateFilterForm(String title, PopBox pb) {//筛选
				return drawFilterForm(title, pb);
			}
			@Override
			public void drawAllData() {
				//super.drawAllData();
				showAllData();
			}
			@Override
			public void hideSelectedData() {
			    fadeSelDataOut();
			}
    		@Override
    		public void drawPreviousTbl() {
    		    previousTblDraw();
    		}
			@Override
			public Widget configWorkOrder(String title, PopBox pb) {
				return setWorkOrder(title,pb);
			}
			@Override
			public Object exportDoc(WebResponse resp) {
				return exportCustomDoc(resp);
			}
			@Override
			public void endSel() {
				if(doEnd()){
    				MiddleTpl.this.draw(parent, null);
    				MiddleTpl.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+start_*max_+"});");
    			}else{
    				Util.infoMessage(tr("Information Please").toString(), "设置失败");
    			}
			}
    	};
    	drawHB(parent,args);
    	
    	this.bindString("win1", "");
    	//this.bindString("winF","");
    	drawGrid();
		return this;
	}
	protected void drawHB(Template parent,String[] args){
		this.bindWidget("menubtn", bcButton.draw(parent, args));
	}
	public void drawGrid(){
		this.bindWidget("trhead",drawTblHead(curCls));
    	this.bindWidget("tbody", drawTblBody(this.start_*this.max_));
		this.bindWidget("pagi", drawPagination());
	}
	//画表头
	public Widget drawTblHead(Class cls){
		Field[] tmfields = cls.getDeclaredFields();
		filterFieldList = new ArrayList<Field>();
		List<List<Field>> trList = new ArrayList<List<Field>>();
		for (int i=0;i<tmfields.length;i++){
			 Field f = tmfields[i];
			 DisplayField df = f.getAnnotation(DisplayField.class);
			 FilterField ff = f.getAnnotation(FilterField.class);
			 //循环中有要表格要显示的字段
			 if(df!=null){
				 int fieldIndex = df.trindex();
				 if(trList.size()<fieldIndex){
					 List<Field> tr = new ArrayList<Field>();
					 tr.add(f);
					 trList.add(tr);
				 }else{//为兼容工单跟踪的多行
					 trList.get(fieldIndex-1).add(f);
				 }
                 
			 }
			 //循环中有筛选表单要的字段
			 if(ff!=null){
				 filterFieldList.add(f);
			 }
		}
		
		ContainerWidget cw = new ContainerWidget(){//包装多tr用
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_DIV;
			}
		};
		for (List<Field> outer:trList){
			
    		ContainerWidget tr = new ContainerWidget(){
    			@Override
    			public DomElementType getDomElementType() {
    				return DomElementType.DomElement_TR;
    			}
    		};
			for(Field field:outer){
				DisplayField df = field.getAnnotation(DisplayField.class);
				ContainerWidget th = new ContainerWidget(){
	    			@Override
	    			public DomElementType getDomElementType() {
	    				return DomElementType.DomElement_TH;
	    			}
	    		};
	    		
	    		//System.out.println("fieldname:"+field.getName()+"\t field:"+field);
				if(field.getName().equals("seqno")){
	    			th.setAttributeValue("data-options", "field:'F"+df.etag()+"',checkbox:true,align:'center'");
	    			fieldList.add(field);
	    			display_count++;
	    			th.setAttributeValue("colspan", df.colspan());
					th.setAttributeValue("rowspan", df.rowspan());
					tr.addWidget(th);
					cw.addWidget(tr);
	    		}else if(!getHiddenList().contains(field)){
	    			th.addWidget(new Text(df.cname()));
	    			th.setAttributeValue("data-options","field:'F"+df.etag()+"',align:'center'");
	    			fieldList.add(field);
	    			display_count++;
	    			th.setAttributeValue("colspan", df.colspan());
					th.setAttributeValue("rowspan", df.rowspan());
					tr.addWidget(th);
					cw.addWidget(tr);
	    		}
			}
		}
		return cw;
	}
	//画表体
	private HashMap<String, ContainerWidget> trListeners = new HashMap<String, ContainerWidget>();
	private ContainerWidget sel_tr ;
	public ContainerWidget getSelectTr(){
		return sel_tr;
	}
	public Widget getSelectTd(Integer i){
		return ((ContainerWidget)getSelectTr().getChildren().get(i)).getChildren().get(0);
	}
	public Widget drawTblBody(int startI){
		obj_arr = getObj_arr();
		if(getNavSqlFlag())
			plist = dbEntity.getNavListFromGeneral(curCls,obj_arr,startI,max_);
		else
			plist = dbEntity.getSubListQBC(curCls,obj_arr,startI,max_);
		
		//表格内容体
		ContainerWidget tbody = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TBODY;
			}
		};
		trListeners.clear();
		for (int j=0;j<plist.size();j++){
			final EntityObject eo = plist.get(j);
			if(trListeners.get(eo.getId())==null){			
				final ContainerWidget tr = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TR;
					}
				};
				tr.setId("seltr"+j);
				trListeners.put(eo.getId(),tr);
				tr.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						bcButton.setDeliveryObj(eo);
						sel_tr = trListeners.get(eo.getId());
						sel_tr.setId(eo.getId());
					}
				});
			}
			final ContainerWidget tr = trListeners.get(eo.getId());
			tr.clear();//编辑的时候要清空原有内容
			for (int i=0;i<display_count;i++){
				ContainerWidget td = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TD;
					}
				};
				td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
				Field f = fieldList.get(i);
				if(f.getName().equals("seqno")){//首列的复选框
					/*CheckBox ckbox = new CheckBox();
					td.addWidget(ckbox);*/
				}else{
					td.addWidget(getDisplayWidget(eo,f));//获取文本框Text组件及其里面显示的内容
				}
				tr.addWidget(td);
			}
			tbody.addWidget(tr);
		}
		return tbody;
	}
	
	public Widget drawPagination(){
		int totalCount = 0;
		if(getNavSqlFlag())
			totalCount = dbEntity.getTotalNumberNative(curCls, obj_arr);
		else
			totalCount = dbEntity.getTotalNumber(curCls, obj_arr);
		PaginationWidget pagi = new PaginationWidget(totalCount, start_,max_){
			@Override
			protected void selectPage(int page_id) {
				trListeners.clear();
				super.selectPage(page_id);
				start_ = page_id;//js的原因，表头也得重新画一次，优化？
//				System.out.println("page_id:"+page_id);
				bcButton.setDeliveryObj(null);
				MiddleTpl.this.bindWidget("trhead",drawTblHead(curCls));
				MiddleTpl.this.bindWidget("tbody", drawTblBody(start_*max_));
				MiddleTpl.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+start_*max_+"});");
			}
		};
		this.bindWidget("pagi", pagi);
		return pagi;
	}

	public void setPaginationStart(int startVal){
		this.start_ = startVal;
	}
	public void setPaginationMax(int maxVal){
		this.max_ = maxVal;
	}
	public int getPaginationStart(){
		return this.start_;
	}
	public int getPaginationMax(){
		return this.max_;
	}
	public HeadButton getHeadButton(){
		return this.bcButton;
	}
	public void setHeadButton(HeadButton hb){
		this.bcButton = hb;
	}
	public Widget generateRelevantForm(String title,PopBox p){
		return null;
	}
	public Widget drawFilterForm(String title,final PopBox p){
		String filterText = "<div class='tcontainer'><ul class='form_ul' style='margin-bottom: 0px;'>";
		filterTpl = new Template();
		//初始化查询字段
		for (Field filterField:filterFieldList){
			FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
			filterText += "<li><label>${"+ff.fname()+"}</label>"+"${"+ff.fname()+"inputVal}"+"</li>";
		}
		filterText+="<div style='clear:both;'></div></ul>";
		filterText+="<div class='butbox'>${queryBtn}${resetBtn}</div></div>";
		filterTpl.setTemplateText(filterText, TextFormat.XHTMLUnsafeText);
		for (Field filterField:filterFieldList){
			LineEdit le = new LineEdit("");
			FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
			filterTpl.bindString(ff.fname(),ff.fname());
			filterTpl.bindWidget(ff.fname()+"inputVal", le);
		}
		PushButton resetBtn = new PushButton(tr("reset"));
		PushButton queryBtn = new PushButton(tr("query"));
		resetBtn.setStyleClass("btn-warning");
		queryBtn.setStyleClass("editcls");
		//final List<String> qryParamList = new ArrayList<String>();
		queryBtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				qryParamList.clear();
				for (Field filterField:filterFieldList){
					FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
					LineEdit inputLe = (LineEdit)filterTpl.resolveWidget(ff.fname()+"inputVal");
					qryParamList.add("like");
					qryParamList.add(ff.o2fname());
					qryParamList.add("%"+inputLe.getText()+"%");
					qryParamList.add("and");
					System.out.println("getText():"+inputLe.getText());
					if(inputLe.getText().trim().equals("")){//某些字段在级联关联对象时不存在，like '%%' 就会查询不到，所以手动加了isNull条件
						System.out.println("kimsoohyun");
						qryParamList.add("isnull");
						qryParamList.add(ff.o2fname());
						qryParamList.add(ff.o2fname());
						qryParamList.add("or");
						System.out.println("inuyasha");
					}
				}
				
				obj_arr = qryParamList.toArray();
				/*for (int k=0;k<obj_arr.length;k++){
					System.out.println("obj_arr[k]:"+obj_arr[k]);
				}*/
				//drawGrid();
				doFilterClick();
                MiddleTpl.this.doJavaScript("$('#bctable').datagrid();");
			}
		});
		resetBtn.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				for (Field filterField:filterFieldList){
					FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
					LineEdit inputLe = (LineEdit)filterTpl.resolveWidget(ff.fname()+"inputVal");
					inputLe.setText("");
				}
			}
		});
		filterTpl.bindWidget("resetBtn", resetBtn);
		filterTpl.bindWidget("queryBtn", queryBtn);
		return filterTpl;
	}
	public void doFilterClick(){
		this.start_ = 0;
		drawGrid();
	}
	public boolean removeFromMT(){
    	return getDBEntity().removeEO((EntityObject)bcButton.getDeliveryObj());
	}
	protected Widget getDisplayWidget(final EntityObject eo,Field field){
		Object val = null;
		Widget w = new Text("");
		
		try {
			if(field.getName().equals("oper")||field.getName().equals("viewDetail")){
				 DisplayField df = (DisplayField)field.getAnnotation(DisplayField.class);
				    final String dename = df.dename();
					PushButton editBtn = new PushButton(df.cname());//编辑|查看明细
					editBtn.setStyleClass("editcls");
					editBtn.clicked().preventPropagation(getPropagationFlag());
					editBtn.clicked().addListener(this, new Signal.Listener() {
						@Override
						public void trigger() {
						   bcButton.setDeliveryObj(eo);
						   generateDetailPage(eo,dename);
						}
					});
					return editBtn;
			 }
			val = WidgetUtil.getValFromProperty(eo, field);
			 if(val!=null){//库存盘点的地方有“规格”字段取值中由“&”，为防止XSS注入，一种方法是改为全角如注释；或者在setText之前将其TextFormat的格式改掉！
				 ((Text)w).setTextFormat(TextFormat.XHTMLUnsafeText);
				/* if(val.toString().indexOf("&")>-1){
					 val = val.toString().replace("&", Util.ban2quan("&"));
	                 w = new Text(val.toString());
				 }else */if(field.getType().getName().equals("java.lang.String")||field.getType().getName().equals("short")
							||field.getType().getName().equals("java.lang.Integer")||field.getType().getName().equals("java.lang.Short")){
					  //w =  new Text(val.toString());
					 ((Text)w).setText(val.toString());
				 }else if(field.getType().getName().equals("java.lang.Double")||field.getType().getName().equals("double")){
					  //w = new Text(decimalF.format(Double.valueOf(val.toString())));
					 ((Text)w).setText(decimalF.format(Double.valueOf(val.toString())));
				 }else if(field.getType().getName().equals("java.util.Date")){
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					((Text)w).setText(sdf.format(val));
					// w = new Text(sdf.format(val));
				 }else if(field.getType().isEnum()){
					 Object enumVal = null;
				     try {
						enumVal = field.getType().getMethod("getName").invoke(val);
					} catch (IllegalArgumentException e) {//为兼容类型更改，原String类型改为枚举类型
						enumVal = val.toString();
						//e.printStackTrace();
					}
				     if(enumVal!=null)//w =  new Text(enumVal.toString());
				    	 ((Text)w).setText(enumVal.toString());
				 }else if(EntityObject.class.isAssignableFrom(field.getType())){
					 w = getSpecialTextWidget((EntityObject)val,field);
				 }
				 
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return w;
	}
	//获取【编辑明细】的表头
	public Widget getDetailHead(String dename){
		detailFieldList = new ArrayList<Field>();
		try{
		  deCls= Class.forName(dename);
		}catch(Exception e){
			e.printStackTrace();
		}
		Field[] fieldArr = deCls.getFields();
		ContainerWidget thead = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_THEAD;
			}
		};
		//序号归并到实体类的一列，不做例外处理
		for (Field field:fieldArr){
			DisplayField df = field.getAnnotation(DisplayField.class);
			if(df!=null&&!getDetailHiddenList().contains(field)){
				ContainerWidget th = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TH;
					}
				};
				th.setAttributeValue("colspan",df.colspan());
				th.addWidget(new Text(df.cname()));
				thead.addWidget(th);
				detailFieldList.add(field);
			}
				
		}
		return thead;
	}
	//另外一种表格
	public Widget generateDetailDiv(final String title,final PopBox wbx,final EntityObject eo,String dename){
		String htmlText  = "<div class=\"tcontainer\">${filterDiv}<table class='detailtb'>" +
		"${thead}"+
		"${tbody}</table>${summation}${detailPagi}</div>${box_button}";
		
		setDetailStart(0);
		
		Template formtbl = new Template();
		formtbl.setTemplateText(htmlText, TextFormat.XHTMLUnsafeText);
    	//绑定表头
		formtbl.bindWidget("thead", getDetailHead(dename));
		
		Template box_button = new Template();		
		box_button.setStyleClass("box_button");
		box_button.setTemplateText("${cancelbtn}", TextFormat.XHTMLUnsafeText);
		
		getDetailTblBody(formtbl,wbx,title,eo,dename);
		
		Anchor cancelbtn = new Anchor(new Link(""), "关闭");
		cancelbtn.setStyleClass("close");
		cancelbtn.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				wbx.setAttributeValue("style", "display:none");
			}
		});
		box_button.bindWidget("cancelbtn", cancelbtn);
		formtbl.bindWidget("box_button", box_button);
		formtbl.bindWidget("filterDiv",drawDetailFilterForm());
		formtbl.bindWidget("summation", getSummation());
		//formtbl.bindString("summation","");
    	    return formtbl;
	}
	public Entity getDBEntity(){
		return this.dbEntity;
	}
	
	//生成编辑明细--弹出层上面的组件
	public Widget generateDetailWidget(Field field,/*Object val,*/Map<Field,FormWidget> fieldWMap,EntityObject deo,EntityObject eo){
		return null;
	}
	//编辑明细--弹出层页面--输入明细--点【保存】，对页面上输入框的属性进行set赋值
	public boolean setPropertyFromWidget(EntityObject deo,Map<Field,FormWidget> fMap,EntityObject eo){
		return true;
	}
	//点击主表格的【编辑明细】
	public Widget generateDetailPage(final EntityObject eo,final String dename){
		    //弹出框类型的，适用范围-各类合同+图面明细
			PopBox win = new PopBox("明细"){
				@Override
				public Widget createFormTbl(String title,
						PopBox wbx) {
						try {
							return generateDetailDiv(title, wbx,eo,dename);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
				}
			};
		//this.bindWidget("win", win);
		//drawGrid();
		winOuter.bindWidget("win", win);
		//this.doJavaScript("$('#bctable').datagrid();");
        return win;
	}
	
	//主表格中具体显示的内容，排除通用类型后，主要是自定义的对象类型，cn.mazu.busitype.entity.busientity
	public Widget getSpecialTextWidget(EntityObject eo,Field field){
		return null;
	}
	//生成明细表格的主体部分,目前是两种规则，销售合同+图面一类；销售合同以外的合同一类
	//参数说明：1，tpl,捆绑模板
    //2，eo，主表对象，即明细关联的父对象
   //3,dename,明细对象的完整类名称	
	public void getDetailTblBody(final Template tpl,final PopBox wbx,final String title,final EntityObject eo,final String dename){
		//找到明细类中引用的实体主类的属性名称
		Field[] df_ar = deCls.getFields();
		List<Field> detailAllFieldList = new ArrayList<Field>(Arrays.asList(df_ar));
		detailAllFieldList.removeAll(detailFieldList);
		Object[] objDetailArr = new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and","","","",""};
		for (Field f:detailAllFieldList){
			 if(eo.getClass().isAssignableFrom(f.getType())){
				 objDetailArr[4] = "equal";
				 objDetailArr[5] = f.getName();
				 objDetailArr[6] = eo;
				 objDetailArr[7] = "and";
				 break;
			 }
		}
		List<EntityObject> totalDetailList = dbEntity.getSubListQBC(deCls, objDetailArr, -1, -1);//查询明细类的记录---计算总条数
		List<EntityObject> detaildataList = dbEntity.getSubListQBC(deCls, objDetailArr, detailStart*detailMax,detailMax);//查询明细类的记录--显示的记录数目
		//System.out.println("getTotalDetailList.size() in mt:"+totalDetailList.size());
		PaginationWidget pagi = new PaginationWidget(totalDetailList.size()+1, detailStart,detailMax){
			@Override
			protected void selectPage(int page_id) {
				super.selectPage(page_id);
				detailStart = page_id;//js的原因，表头也得重新画一次，优化？
				tpl.bindWidget("thead", getDetailHead(dename));
				getDetailTblBody(tpl,wbx,title,eo,dename);
			}
		};
		tpl.bindWidget("detailPagi", pagi);
		ContainerWidget tbody = new ContainerWidget(){//模板体
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TBODY;
			}
		};
		
		for (int i=0;i<detaildataList.size();i++){//逐行显示  数据库中查询出的记录
			EntityObject deo = detaildataList.get(i);
			tbody.addWidget(drawDetaiTrContent(eo,deo,wbx,title,i,tpl));
		}
        /*if(totalDetailList.size())*/		//如果是最后一页，则显示这条
		if(Math.ceil(totalDetailList.size()/detailMax)==detailStart)
		   drawDetailTrSpecial(tbody,eo,wbx,title,detaildataList,tpl);
		tpl.bindWidget("tbody", tbody);
	}
	//获取其他地方得来的明细记录，为兼容采购合同，若无，则显示一行空记录，作为新增记录的入口
	//默认父类的方法针对图面
    public void drawDetailTrSpecial(ContainerWidget tbody,EntityObject eo,PopBox wbx,String title,List<EntityObject> dlist,Template tpl){
    	EntityObject newdeo = null;
		try {
			newdeo = (EntityObject)deCls.newInstance();
			tbody.addWidget(drawDetaiTrContent(eo,newdeo,wbx,title,dlist.size(),tpl));
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    //具体某个td里面放置的东西，一行中的各列
    public Widget drawDetaiTrContent(/*Field detailField,*/final EntityObject eo,
    		final EntityObject deo,//final Map fieldWMap,
    		final PopBox wbx,final String title,int i,final Template tpl){
    	
    	final Map<Field,FormWidget> fieldWMap = new HashMap<Field,FormWidget>();//字段本身
    	final Map<Field,Text> fvMap = new HashMap<Field,Text>();//字段的验证信息
		final ContainerWidget tr = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TR;
			}
		};
    	
		for(int j=0;j<detailFieldList.size();j++){//逐列显示
			final Field detailField = detailFieldList.get(j);//取到字段field
			ContainerWidget td = new ContainerWidget(){//画一个td
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TD;
				}
			};
			td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);//设置td的对齐方式
			td.setAttributeValue("colspan",((DisplayField)detailField.getAnnotation(DisplayField.class)).colspan());
			tr.addWidget(td);
			
	    	if(((DisplayField)detailField.getAnnotation(DisplayField.class)).cname().equals("操作")){//操作字段的内容是【保存】和【删除】两个按钮
				Template tcont = new Template();
				tcont.setTemplateText("${savebtn}${deletebtn}", TextFormat.XHTMLUnsafeText);
				PushButton saveBtn = new PushButton("保存");
				saveBtn.setStyleClass("editcls");
				saveBtn.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						if(validatePassed(fieldWMap,deo)){//先验证
							if(setPropertyFromWidget(deo,fieldWMap,eo)){
								tpl.bindWidget("summation",getSummation());//订购合同处的"总价"替换
								wbx.draw(title);//明细多页记录的时候跳回首页
								//tr.clear();
							}else
								Util.infoMessage("", "保存失败");
						}
					}
				});
				
				PushButton deleteBtn = new PushButton("删除");
				deleteBtn.setStyleClass("editcls");
				deleteBtn.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						//为绑定事件到对应按钮上，按钮各自创建，绑定事件，原来的写法事件绑定不具体，不管点击【是】还是【否】都触发msbox.clicked()。。。事件，不能区分
						final WMessageBox msbox = new WMessageBox();
						msbox.setText("确认删除这条记录？");
						msbox.setIcon(Icon.Information);
						
						PushButton okbtn = msbox.addButton("是", StandardButton.Ok);
						okbtn.clicked().addListener(okbtn, new Signal.Listener() {
							@Override
							public void trigger() {
								if(dbEntity.removeEO(deo)){
									tpl.bindWidget("summation",getSummation());//订购合同处的"总价"替换
									wbx.draw(title);
								}
								else
									Util.infoMessage(tr("Information Please").toString(), "删除失败");
								
								if (msbox != null)
									msbox.remove();
								
							}
						});
						
						PushButton calbtn = msbox.addButton("否", StandardButton.Cancel);
						calbtn.setMargin(2);
						calbtn.clicked().addListener(calbtn, new Signal.Listener() {
							@Override
							public void trigger() {
								if (msbox != null)
									msbox.remove();
							}
						});
						
						msbox.setWidth(new WLength(200));
				    	msbox.show();
					}
				});
				tcont.bindWidget("savebtn", saveBtn);
				//是持久化对象，且是由关联关系的父子记录，排除由别的实体带来的未保存记录，如订购合同中图面带来的记录
				if(dbEntity.isPersistObject(deo)&&dbEntity.hasAssociation(eo,deo)){
					tcont.bindWidget("deletebtn", deleteBtn);
				}else{
					tcont.bindString("deletebtn", "");
				}
				td.addWidget(tcont);
			}else{
					if(detailField.getName().equals("seqno")){
	                    Text t = new Text(getDetailStart()*getDetailMax()+(i+1)+"");
                        td.addWidget(t);
					}else{
							Widget detailWidget = generateDetailWidget(detailField,/*retval,*/fieldWMap,deo,eo);//如有不安全字符，则在该方法执行的时候就会报错，等取到组件再设置TextFormat的时候为时晚矣
							td.addWidget(detailWidget);
					}
			}
	    	tr.addWidget(td);
		}
    	return tr;
    }
    
   
	public Object[] getObj_arr() {
		return obj_arr;
	}

	public void setObj_arr(Object[] obj_arr) {
		this.obj_arr = obj_arr;
	}
	
	public boolean getPropagationFlag() {
		return propagationFlag;
	}

	public void setPropagationFlag(boolean propagationFlag) {
		this.propagationFlag = propagationFlag;
	}
    //--显示全部
	public void showAllData(){}
    //--隐藏选中的数据行	
	public void fadeSelDataOut(){}
	//--返回
	public void previousTblDraw(){}
	//设置工单
	public Widget setWorkOrder(String title, PopBox pb){
		return null;
	}
	//弹出明细窗口的字段提交验证
	public boolean validatePassed(Map fmap,EntityObject deo){
		return true;
	}
	public Object exportCustomDoc(WebResponse resp){
		return null;
	}
	public boolean getNavSqlFlag() {
		return navSqlFlag;
	}

	public void setNavSqlFlag(boolean navSqlFlag) {
		this.navSqlFlag = navSqlFlag;
	}
	
	public List<EntityObject> getPlist() {
		return plist;
	}
	public void setPlist(List<EntityObject> plist) {
		this.plist = plist;
	}
	public List<Field> getDetailFieldList() {
		return detailFieldList;
	}
	public void setDetailFieldList(List<Field> detailFieldList) {
		this.detailFieldList = detailFieldList;
	}
    
	public List<Field> getDetailHiddenList() {
		return detailHiddenList;
	}

	public void setDetailHiddenList(List<Field> detailHiddenList) {
		this.detailHiddenList = detailHiddenList;
	}
    
	public int getDetailStart() {
		return detailStart;
	}

	public void setDetailStart(int detailStart) {
		this.detailStart = detailStart;
	}

	public int getDetailMax() {
		return detailMax;
	}

	public void setDetailMax(int detailMax) {
		this.detailMax = detailMax;
	}

	public Class getCurCls() {
		return curCls;
	}

	public void setCurCls(Class curCls) {
		this.curCls = curCls;
	}
    
	public Class getDeCls() {
		return deCls;
	}
	public void setDeCls(Class deCls) {
		this.deCls = deCls;
	}
	public List<Field> getHiddenList() {
	   return hiddenList;
	}


	public void setHiddenList(List<Field> hiddenList) {
		this.hiddenList = hiddenList;
	}
    
	public List<Field> getFilterFieldList() {
		return filterFieldList;
	}

	public void setFilterFieldList(List<Field> filterFieldList) {
		this.filterFieldList = filterFieldList;
	}
    
	public List<String> getQryParamList() {
		return qryParamList;
	}

	public void setQryParamList(List<String> qryParamList) {
		this.qryParamList = qryParamList;
	}

	public boolean doEnd(){
		return false;
	}
	//点【编辑明细】后弹出窗口，在窗口中做二次筛选，现在只有“采购合同”在用，若图面也要筛选，请重写此方法.
	public Widget drawDetailFilterForm(){
		return null;
	}
	//订购单处要进行总价合计
	public Widget getSummation(){
		return null;
	}
	public void detailFitlerQry(List<String> qryParamList){
		for (Field filterField:filterFieldList){
			FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
			LineEdit inputLe = (LineEdit)filterTpl.resolveWidget(ff.fname()+"inputVal");
			qryParamList.add("like");
			qryParamList.add(ff.o2fname());
			qryParamList.add("%"+inputLe.getText()+"%");
			qryParamList.add("and");
		}
		obj_arr = qryParamList.toArray();
		drawGrid();
        MiddleTpl.this.doJavaScript("$('#bctable').datagrid();");
	}
	//不要命名为getParent()，人为地重新设置了this的parent，违背初衷
	public Template getMyParent(){
		return this.parent;
	}
	//private AccountPermission ap  = dbEntity.getCurrentAP();
	//获取当前菜单的权限
	private String getMenuPermission(){
		AccountPermission ap  = dbEntity.getCurrentAP();
		String selmenuname = ((Text)((HtmlTemplate)this.parent).getChildWidget("selid")).getText().toString();
		if(ap!=null){
			String[] secMenuArr = ap.getMenuPermission().split(";");//Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("menuper")).split(";");
			for (String secMenu:secMenuArr){
				if(selmenuname.equals(MainMenu.getMainMenuByDesc(secMenu.split(":")[0]).name)){
					return secMenu.split(":")[1];
				}
			}
		}
		return "";
	}
	//编辑时刷新选中的那一行
	public void refreshSelTr(EntityObject selEo){
		for (Field f:fieldList){
			if(f.getName().equals("seqno")){
				continue;
			}else if(!f.getName().equals("oper")&&!f.getName().equals("viewDetail")){
				((Text)getSelectTd(fieldList.indexOf(f))).setText(((Text)getDisplayWidget(selEo,f)).getText());
			}
		}
	}
}
