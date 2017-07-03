package cn.mazu.ui;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import cn.mazu.WApplication;
import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.mysql.Entity;
import cn.mazu.surface.entity.Surface;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.ElementTechnology;
import cn.mazu.util.Util.MainMenu;
import cn.mazu.util.Util.WOTraceDetailStatus;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.utils.WDate;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.webkit.servlet.WebResponse;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PaginationWidget;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.workorder.entity.WorkOrder;
import cn.mazu.workorder.entity.WorkOrderTraceDetail;
import cn.mazu.workorder.entity.WorkOrderWHModel;

public class WorkOrderMgr extends MiddleTpl {
	private Template parent;
	private String[] args;
	private Entity dbEntity;
	private WorkOrder traceWOrder ;
	private WorkOrderTraceMgr wotm;
	//防止不按照流程走，只是建了销售单，无图面数据后即来跟踪工单，所以先初始化下
	private List<EntityObject> surfaceDetailList = new ArrayList<EntityObject>();
	private List<EntityObject> dbDetailList = new ArrayList<EntityObject>();
	private List<EntityObject> overlapList = new ArrayList<EntityObject>();

	private static List<String> columnConfigList;//--->Array
	private HeadButton  detailhb;
	private boolean drawAllFlag;
	private int display_field_count,detaildb_display_count;//display_field_count显示字段用,detaildb_display_count显示明细的条数（隐藏的不显示）
	//保存入库的数据记录筛选traceFitlerFieldList，图面明细带过来的记录筛选surfaceFilterFieldList
	private List<Field> /*fieldList = new ArrayList<Field>(),*/traceFitlerFieldList = new ArrayList<Field>(),surfaceFilterFieldList = new ArrayList<Field>();
	/*private Set<Field> traceFitlerFieldList = new TreeSet<Field>();*/
	private List<String> detailFilterParamList = new ArrayList<String>();
	private AccountPermission ap ;
	private List<Widget> disabledW = new ArrayList<Widget>();
	private Template winOuter/* = new Template()*/,win1Outer,filterTpl,detailFilterTpl/* = new Template()*/;
	private Widget detailTraceHead,detailTraceBody;
	private DecimalFormat df = new DecimalFormat(".##");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static String dataCode = Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode"));
	//外协—下料—M（铣床）—H/T—G（磨床）—CNC—E（放电）—W/C（线割）—表面处理—组装
	static{
		columnConfigList = new ArrayList<String>();
		columnConfigList.add("wxyjInfo");//0 外协
    	columnConfigList.add("wxsjInfo");//1

    	columnConfigList.add("xlyjInfo");//2 下料
    	columnConfigList.add("xlsjInfo");//3
    	
    	columnConfigList.add("xcyjInfo");//4铣床
    	columnConfigList.add("xcyjwh");//5
    	columnConfigList.add("xcsjInfo");//6
    	columnConfigList.add("xcsjwh");//7
    	
    	columnConfigList.add("htyjInfo");//8 HT
    	columnConfigList.add("htsjInfo");//9
    	
    	
    	columnConfigList.add("mcyjInfo");//10 磨床
    	columnConfigList.add("mcyjwh");//11
    	columnConfigList.add("mcsjInfo");//12
    	columnConfigList.add("mcsjwh");//13
    	
    	columnConfigList.add("cncyjInfo");//14 cnc
    	columnConfigList.add("cncyjwh");//15
    	columnConfigList.add("cncsjInfo");//16
    	columnConfigList.add("cncsjwh");//17
    	
    	columnConfigList.add("fdyjInfo");//18 放电
    	columnConfigList.add("fdyjwh");//19
    	columnConfigList.add("fdsjInfo");//20
    	columnConfigList.add("fdsjwh");//21
    	
    	columnConfigList.add("wcyjInfo");//22 WC
    	columnConfigList.add("wcsjInfo");//23
    	
    	columnConfigList.add("bmyjInfo");//24 表面处理
    	columnConfigList.add("bmsjInfo");//25
    	
    	columnConfigList.add("qcyjInfo");//26 QC
    	columnConfigList.add("qcsjInfo");//27
    	
    	//columnConfigList.add("assembleInfo");//28 组装日期
    	columnConfigList.add("remark");//29备注
    	columnConfigList.add("detailStatus");//跟踪状态
	}
	public WorkOrderMgr() {
	   super("cn.mazu.workorder.entity.WorkOrder",
			   new Object[]{"like","dataCode",
			   dataCode,"and"});
	   this.dbEntity = getDBEntity();
	   setPropagationFlag(true);
	   try {
		   getHiddenList().add(getCurCls().getField("client"));
	} catch (SecurityException e) {
		e.printStackTrace();
	} catch (NoSuchFieldException e) {
		e.printStackTrace();
	}
	}
    @Override
    public Widget draw(Template parent, String... args) {
    	this.parent = parent;
		
		wotm = new WorkOrderTraceMgr();
		setDeCls(wotm.getCurCls());
		
		ap  = dbEntity.getCurrentAP();
		   if(ap!=null){
			   String[] mpArr = ap.getMenuPermission().split(";");
			   for (String mp:mpArr){
				   if(mp.contains(MainMenu.WORKODERMGR.desc)){
					  try {
						   if(mp.split(":")[1].contains(tr("edetail")))//只有有编辑，查看肯定不显示
							   getHiddenList().add(getCurCls().getField("viewDetail"));
						   else if(mp.split(":")[1].contains(tr("vdetail")))//若无编辑，但有查看，要显示查看
							   getHiddenList().add(getCurCls().getField("oper"));
						   else if(!mp.split(":")[1].contains(tr("vdetail"))&&!mp.split(":")[1].contains(tr("edetail"))){
							   getHiddenList().add(getCurCls().getField("oper"));
							   getHiddenList().add(getCurCls().getField("viewDetail"));
						   }
					   }catch (SecurityException e) {
						  e.printStackTrace();
					   }catch (NoSuchFieldException e) {
							e.printStackTrace();
					   }
					   break;
				   }
			   }
		   }
		
		Widget workOrderTbl = super.draw(parent, args);
		//全部工单的工时统计
		refreshWh("WOOverallSum",null);
		return workOrderTbl;

    }
    //组装日期
    @Override
    protected Widget getDisplayWidget(final EntityObject eo, Field field) {
        if(field.getName().equals("assembleDate")){
        	Anchor aDateLink = new Anchor(new Link(""),((WorkOrder)eo).getAssembleDate()==null?
        			"设置时间":sdf.format(((WorkOrder)eo).getAssembleDate()));
        	aDateLink.setStyleClass("aclick");
        	aDateLink.clicked().preventPropagation(getPropagationFlag());
        	aDateLink.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					PopBox win = new PopBox("组装时间"){
						@Override
						public Widget createFormTbl(String title,
								PopBox wbx) {
								try {
									return popDateSetBox(eo,wbx);
								} catch (Exception e) {
									e.printStackTrace();
									return null;
								}
						}
					};
					WorkOrderMgr.this.bindWidget("win", win);
					drawGrid();
					WorkOrderMgr.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+getPaginationStart()*getPaginationMax()+"});");
				}
			});
        	return aDateLink;
        }else 
    	     return super.getDisplayWidget(eo, field);
    }
    private Widget popDateSetBox(final EntityObject eo,final PopBox win){
    	 Template tpl = new Template();
			tpl.setTemplateText("<div class=\"tcontainer\"><table class=\"form_tb\"><tr>" +
			   		"<td>${dateEdit}</td>" +
			   		"</tr></table></div><div class=\"box_button\">${saveBtn}</div>", TextFormat.XHTMLUnsafeText);
			final DateEdit de = new DateEdit();
			de.setFormat("yyyy-MM-dd");
			de.setValueText(((WorkOrder)eo).getAssembleDate()==null?"":sdf.format(((WorkOrder)eo).getAssembleDate()));
			tpl.bindWidget("dateEdit", de);
			
			Anchor saveBtn = new Anchor(new Link(),tr("save"));
			saveBtn.setStyleClass("close");
			saveBtn.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					if(!de.getText().trim().equals("")){
						try {
							((WorkOrder)eo).setAssembleDate(sdf.parse(de.getText()));
							 if(getDBEntity().saveOrUpdate(eo)){
//								 WorkOrderMgr.this.draw(parent, null);
								 drawGrid();
								 WorkOrderMgr.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+getPaginationStart()*getPaginationMax()+"});");
							 }else {
								 Util.infoMessage(tr("Information Please").toString(), "设置失败");
							 }
							 win.setAttributeValue("style", "display:none;");
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			});
			tpl.bindWidget("saveBtn", saveBtn);
		 
    	return tpl;
    }
	@Override
    public Widget getSpecialTextWidget(EntityObject val, Field field) {
    	Text retText = new Text();
		    if(field.getType().getName().equals("cn.mazu.surface.entity.Surface")){//显示图面名称
				if(val!=null){
					retText.setText(WidgetUtil.getValFromProperty(val, "name").toString());
				}
		    }
	    return retText;
    }   
    
    @Override
    public Widget generateDetailPage(EntityObject eo,String dename){
				disabledW.clear();
				traceWOrder = (WorkOrder)eo;
				setDetailStart(0);
				this.setTemplateText("" +
						"${menubtn}"+
					"<div class='traceWrapa'>" +
					"<table class='detailtb' id='WODTb'>"+
					"            <thead>"+
					"            ${trhead}"+
					"            </thead>" +
				    			"${tbody}" +
				    "</table>" +
				    "${detailPagi}"+
				    "</div>" +
					"${win}${win1}",TextFormat.XHTMLUnsafeText);
				
				drawDetailGrid();//先画组件后处理
				try {
					if(getHiddenList().contains(getCurCls().getField("oper"))){//查看明细
						disabledAllSignal();
						wotm.btnstr = "返回";
					}else if(!getHiddenList().contains(getCurCls().getField("oper"))&&
							((WorkOrder)eo).getWostatus().equals(Util.WorkOrderStatus.BEDONE)){//编辑明细
						disabledAllSignal();
						wotm.btnstr = "返回";
					}else if(!getHiddenList().contains(getCurCls().getField("oper"))&&
							!((WorkOrder)eo).getWostatus().equals(Util.WorkOrderStatus.BEDONE)){
						wotm.btnstr = "隐藏,显示全部,删除,返回,筛选";
					}
				
				detailhb = new HeadButton("",wotm.btnstr){
					@Override//返回
					public void drawPreviousTbl() {
						WorkOrderMgr.this.clear();
						WorkOrderMgr.this.draw(parent, args);
						refreshWh("WOOverallSum",null);
						WorkOrderMgr.this.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+getPaginationStart()*getPaginationMax()+"});");
					}
					@Override//隐藏单条记录
					public void hideSelectedData() {
						WorkOrderTraceDetail detail = (WorkOrderTraceDetail)detailhb.getDeliveryObj();
						detail.setShowflag(false);
						if(dbEntity.saveOrUpdate(detail))
							drawDetailGrid();
							
					}
					@Override//显示全部记录
					public void drawAllData() {
						if(resetShowFlag())
							drawDetailGrid();
					}
					@Override//删除已跟踪的记录
					public void removeFromTbl() {
						if(dbEntity.isPersistObject((EntityObject)detailhb.getDeliveryObj())){
							SurfaceDetail sd = ((WorkOrderTraceDetail)detailhb.getDeliveryObj()).getSurfaceDetail();
							if(getDBEntity().removeEO((EntityObject)detailhb.getDeliveryObj())){
								drawDetailGrid();
							}else{
								Util.infoMessage(tr("Information Please").toString(), "删除失败");
							}
						}else{
							Util.infoMessage(tr("Information Please").toString(), "只能删除已跟踪的记录！");
						}
					}
					@Override
					public Widget generateFilterForm(String title, PopBox pb) {
						String filterText = "<div class='tcontainer'><ul class='form_ul' style='margin-bottom: 0px;'>";
						detailFilterTpl = new Template();
						for (Field filterField:traceFitlerFieldList){
							FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
							filterText += "<li><label>${"+ff.fname()+"}</label>"+"${"+ff.fname()+"inputVal}"+"</li>";
						}
						filterText+="<div style='clear:both;'></div></ul>";
						filterText+="<div class='butbox'>${queryBtn}${resetBtn}</div></div>";
						detailFilterTpl.setTemplateText(filterText, TextFormat.XHTMLUnsafeText);
						for (Field filterField:traceFitlerFieldList){
							LineEdit le = new LineEdit("");
							FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
							detailFilterTpl.bindString(ff.fname(),ff.fname());
							detailFilterTpl.bindWidget(ff.fname()+"inputVal", le);
						}
						PushButton resetBtn = new PushButton(tr("reset"));
						PushButton queryBtn = new PushButton(tr("query"));
						resetBtn.setStyleClass("btn-warning");
						queryBtn.setStyleClass("editcls");
						//final List<String> qryParamList = new ArrayList<String>();
						queryBtn.clicked().addListener(this, new Signal.Listener() {
							@Override
							public void trigger() {
								detailFilterParamList.clear();
								for (Field filterField:traceFitlerFieldList){
									FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
									LineEdit inputLe = (LineEdit)detailFilterTpl.resolveWidget(ff.fname()+"inputVal");
									detailFilterParamList.add("like");
									detailFilterParamList.add(ff.o2fname());
									detailFilterParamList.add("%"+inputLe.getText()+"%");
									detailFilterParamList.add("and");
								}
								setDetailStart(0);
								drawDetailGrid();
							}
						});
						resetBtn.clicked().addListener(this,new Signal.Listener() {
							@Override
							public void trigger() {
								for (Field filterField:traceFitlerFieldList){
									FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
									LineEdit inputLe = (LineEdit)detailFilterTpl.resolveWidget(ff.fname()+"inputVal");
									inputLe.setText("");
								}
	
							}
						});
						detailFilterTpl.bindWidget("resetBtn", resetBtn);
						detailFilterTpl.bindWidget("queryBtn", queryBtn);
						return detailFilterTpl;
						
						//return filterTpl;
					}
					
				};
				
				this.bindWidget("menubtn",detailhb.draw(parent, args));
				wotm.setHeadButton(detailhb);
				winOuter = new Template();
				winOuter.setTemplateText("${win}",TextFormat.XHTMLUnsafeText);
				winOuter.bindString("win", "");
				this.bindWidget("win", winOuter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return this;
    }
    private void drawDetailGrid(){
    	try {
			detailTraceHead = drawTraceHead(getDeCls());
			this.bindWidget("trhead",detailTraceHead);
			
			detailTraceBody = drawTraceGridContent(getDetailStart());
			this.bindWidget("tbody",detailTraceBody);
			
			//this.findById("WODTb_tableFix").setAttributeValue("style", "");
			refreshWh("WOSingleSum",((EntityObject)this.getHeadButton().getDeliveryObj()).getId().toCharArray());
			initJS();
			this.doJavaScript("FixTable('WODTb', 3, 1240, 400);");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private Widget drawTraceGridContent(int detailStart){
    	
    	Surface surface = traceWOrder.getSurface();
    	//setFilterFieldList(traceFitlerFieldList);
    //	List sdFieldFilterList = new ArrayList<Field>();
		if(surface!=null){
			//System.out.println("surface.getId():"+surface.getId());
			//该图面下的图面明细记录，每次试图从图面明细中获取过滤条件字段
			Object[] surfaceDetailArr = new Object[]{"equal","surface",surface,"and","","","","","","","",""};
			Object[] woTraceDetailArr = new Object[]{"equal","workOrder",traceWOrder,"and","","","","","","","",""};
			if(detailFilterParamList.size()>0){
				for (int j=0;j<detailFilterParamList.size();j++){
					//System.out.println("detailFilterParamList.size():"+detailFilterParamList.size()+"\tdetailFilterParamList.get(j):"+detailFilterParamList.get(j));
					surfaceDetailArr[4+j] = detailFilterParamList.get(j);
					woTraceDetailArr[4+j] = detailFilterParamList.get(j);
				}
				surfaceDetailArr[5] = "detailcode";
				surfaceDetailArr[9] = "generalElement.name";
			}
			/*for (Object o:surfaceDetailArr){
				System.out.println("surfaceDetailArr[i]:"+o.toString());
			}*/
			surfaceDetailList = dbEntity.getSubListQBC(SurfaceDetail.class, surfaceDetailArr, -1, -1);
			//要跟踪的工单跟踪明细
			dbDetailList = dbEntity.getSubListQBC(WorkOrderTraceDetail.class,woTraceDetailArr, -1,-1);
			for (EntityObject seo:surfaceDetailList){
				SurfaceDetail sd = (SurfaceDetail)seo;
				for(EntityObject dbeo:dbDetailList){
					WorkOrderTraceDetail wotd = (WorkOrderTraceDetail)dbeo;
					if(wotd.getSurfaceDetail().getId().equals(sd.getId())){
						overlapList.add(seo);
						break;
					}
				}
			}
			surfaceDetailList.removeAll(overlapList);
		}
    	
    	final ContainerWidget tbody = new ContainerWidget(){
    		@Override
    		public DomElementType getDomElementType() {
    			return DomElementType.DomElement_TBODY;
    		}
    	};
    	
    	tbody.setStyleClass("traceHeadtbody");
    	    Object[] wotd_display_count_arr = new Object[]{"equal","workOrder",traceWOrder,"and","equal","showflag",true,"and","","","","","","","",""};
    	    Object[] wotd_display_list_arr = new Object[]{"equal","workOrder",traceWOrder,"and","equal","showflag",true,"and","","","","","","","","","orderBy","asc","detailStatus","0.5",};
    	    if(detailFilterParamList.size()>0){
				for (int j=0;j<detailFilterParamList.size();j++){
					wotd_display_count_arr[8+j] = detailFilterParamList.get(j);
					wotd_display_list_arr[8+j] = detailFilterParamList.get(j);
				}
			}
    	    detaildb_display_count = dbEntity.getSubListQBC(WorkOrderTraceDetail.class,wotd_display_count_arr,-1,-1).size();
            //显示的
        	wotm.setPlist(dbEntity.getSubListQBC(WorkOrderTraceDetail.class,wotd_display_list_arr, getDetailStart()*getDetailMax(), getDetailMax()));
    		//先画数据库查询出来的，实际填写的跟踪记录
    		for (int j=0;j<wotm.getPlist().size();j++){
    			final WorkOrderTraceDetail tDetailDB = (WorkOrderTraceDetail)wotm.getPlist().get(j);
    			ContainerWidget trdb = new ContainerWidget(){
    				@Override
    				public DomElementType getDomElementType() {
    					return DomElementType.DomElement_TR;
    				}
    			};
    			tbody.addWidget(drawTdContentFromDb(trdb,tDetailDB));
    		}
    		if((getDetailStart()+1)*getDetailMax()>detaildb_display_count){ 
    			List<EntityObject> pagiSurfaceDetailList = null; 
    			if(Math.floor(detaildb_display_count/getDetailMax())==getDetailStart()){//交叉
    				if(surfaceDetailList.size()>getDetailMax()-detaildb_display_count%getDetailMax())
    				pagiSurfaceDetailList = surfaceDetailList.subList(0, getDetailMax()-detaildb_display_count%getDetailMax());
    				else
    					pagiSurfaceDetailList = surfaceDetailList.subList(0, surfaceDetailList.size());
    			}else if(getDetailStart()<Math.ceil((detaildb_display_count+surfaceDetailList.size())/getDetailMax())){//中间页面getDetailStart()<Math.ceil(detail_count/getDetailMax())
    				int start = getDetailMax()-detaildb_display_count%getDetailMax()
    						+(getDetailStart()-1-(int)Math.floor(detaildb_display_count/getDetailMax()))*getDetailMax();
    				pagiSurfaceDetailList = surfaceDetailList.subList(start, start+getDetailMax());
    				
    			}else{//末页
    				int start = getDetailMax()-detaildb_display_count%getDetailMax()
    						+(getDetailStart()-1-(int)Math.floor(detaildb_display_count/getDetailMax()))*getDetailMax();
    				pagiSurfaceDetailList = surfaceDetailList.subList(start,surfaceDetailList.size());
    			}
    			
    			//pagiSurfaceDetailList = surfaceDetailList.subList(getDetailStart()*getDetailMax(), toIndex);
        		//再画总的图面明细减去实际跟踪的，剩下的是待跟踪的
            	for (int i=0;i<pagiSurfaceDetailList.size();i++){
            		final SurfaceDetail detail = (SurfaceDetail)pagiSurfaceDetailList.get(i);
            		ContainerWidget trsf = new ContainerWidget(){
            			@Override
            			public DomElementType getDomElementType() {
            				return DomElementType.DomElement_TR;
            			}
            		};
            		tbody.addWidget(drawTdContentFromSurface(trsf,detail));
            	}
    		}
    		
        	
        	PaginationWidget pagi = new PaginationWidget(detaildb_display_count+surfaceDetailList.size(), getDetailStart(),getDetailMax()){
    			@Override
    			protected void selectPage(int page_id) {
    				super.selectPage(page_id);
    				setDetailStart(page_id);
    				WorkOrderMgr.this.bindWidget("trhead",drawTraceHead(getDeCls()));
    				WorkOrderMgr.this.bindWidget("tbody",drawTraceGridContent(page_id));
    				initJS();
    				WorkOrderMgr.this.doJavaScript("FixTable('WODTb', 3, 1240, 400);");
    				refreshWh("WOSingleSum",((EntityObject)WorkOrderMgr.this.getHeadButton().getDeliveryObj()).getId().toCharArray());
    			}
    		};
    		this.bindWidget("detailPagi", pagi);
    	return tbody;
    }
    //计算预计完成时间
    private String addHours(Date srcDate,int addHour){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcDate);
		calendar.add(Calendar.HOUR_OF_DAY, addHour);
		return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
    }
    /*traceDetail--被操作的实例
     * j-方便取到列名
     * wbx--弹出层
     * displayAll--false只显示文本编辑区域，true则连同日期编辑框一起显示,为兼容编辑备注，备注只有文本没有日期*/
    private Widget popBox(final WorkOrderTraceDetail traceDetail,final int j,final PopBox wbx,
    		final boolean displayAll,final Anchor a){
    	   Template tpl = new Template();
    	   String ttpl = "<div class=\"tcontainer\"><table class=\"form_tb\"><tr>" +
    	   		"<td>${dateTitle}</td><td>${dateEdit}</td>" +
    	   		"<td>备注：</td><td>${operDesc}</td>" +
    	   		"</tr></table></div><div class=\"box_button\">${saveBtn}</div>";
	       tpl.setTemplateText(ttpl, TextFormat.XHTMLUnsafeText);
	       tpl.bindString("dateEdit","");
	       ContainerWidget de = new ContainerWidget();
	       final Text out = new Text(de);
	       out.setAttributeValue("style", "color:black;");
	       final cn.mazu.widget.kit.composite.Calendar c1 = new cn.mazu.widget.kit.composite.Calendar(de);
	       c1.selectionChanged().addListener(this, new Signal.Listener() {
	           public void trigger() {
	               WDate d = null;
	               Set<WDate> selection = c1.getSelection();
	               if (selection.size() != 0) {
	                   d = selection.iterator().next();
	               }
	               out.setText(d.getYear()+"-"+d.getMonth()+"-"+d.getDay());
	           }
	       });
	       tpl.bindString("dateTitle", "");
	      
	       //该行的备注字段
	       String remark = traceDetail.getRemark().replace("{", "");
	       remark = remark.replace("}", "");
	       //预计、实际操作里面的备注
	       String fieldName = columnConfigList.get(j-4);
	       String[] setinfo = null;
	       final TextArea ta = new TextArea();
	    	   Object getInfo = WidgetUtil.getValFromProperty(traceDetail, fieldName);
	    	   setinfo = resolveTdInfo(getInfo==null?"":getInfo.toString());
	       
	       if(displayAll){
	    	   tpl.bindWidget("dateEdit", de);
	    	   tpl.bindString("dateTitle", "设定日期：");
	    	   ta.setText(setinfo[1]);
	    	   out.setText(setinfo[0]);
	    	   tpl.bindWidget("operDesc", ta);
	       }else{
	    	   tpl.bindString("dateEdidt", "");
	    	   tpl.bindString("dateTitle", "");
	    	   ta.setText(remark);
	    	   tpl.bindWidget("operDesc", ta);
	       }
	       
	       Anchor saveBtn = new Anchor(new Link(""), "保存");
	       saveBtn.setStyleClass("close");
	       saveBtn.clicked().addListener(this, new Signal.Listener() {
				@Override
				public void trigger() {
					String yjInfo = null;
					if(displayAll){
						yjInfo = joinStringInfo(out.getText().toString(),ta.getText());
					}else{
						yjInfo = joinStringInfo(ta.getText());
					}
					String fieldName = columnConfigList.get(j-4);
					    WidgetUtil.setValForProperty(traceDetail, fieldName, yjInfo);
					if(dbEntity.saveOrUpdate(traceDetail)){
						wbx.setAttributeValue("style", "display:none");
						drawDetailGrid();
					}else{
						Util.infoMessage("", "保存失败");
						wbx.setAttributeValue("style", "display:none");
					}
				}
		    });
	       tpl.bindWidget("saveBtn", saveBtn);
	       return tpl;
	       //return ta;
    }
    //已经保存入库的跟踪记录
    private Widget drawTdContentFromDb(ContainerWidget tr,final WorkOrderTraceDetail detail){
    	String techStr = detail.getSurfaceDetail().getGeneralElement().getTechnologyStr();
    	for(int j=0;j<33;j++){
    		final int colnum = j;
			final ContainerWidget td = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TD;
				}
			};
		
			td.setContentAlignment(AlignmentFlag.AlignLeft, AlignmentFlag.AlignMiddle);
			if(j>2)td.setStyleClass("cellData");
			if(j==0){
				CheckBox cb = new CheckBox();
				td.addWidget(cb);
				cb.clicked().addListener(this,new Signal.Listener() {
					@Override
					public void trigger() {
						detailhb.setDeliveryObj(detail);
					}
				});
			}else if(j==1){
				Text  t = new Text(detail.getSurfaceDetail().getGeneralElement().getName());
				td.addWidget(t);
			}else if(j==2){
				Text  t = new Text(detail.getSurfaceDetail().getDetailcode());
				td.addWidget(t);
			}else if(j==3){
				Text t = new Text(detail.getSurfaceDetail().getAmount()+"");
				td.addWidget(t);
			}else if(
					//时间
					 (j==4||j==5)&&techStr.contains(ElementTechnology.OUTERASSIST.name)//预计 外协+实际外协
					||(j==6||j==7)&&techStr.contains(ElementTechnology.FEEDING.name)//预计下料+实际下料
					||(j==12||j==13)&&techStr.contains(ElementTechnology.HORT.name)//预计HT+实际HT 
					||(j==26||j==27)&&techStr.contains(ElementTechnology.WORC.name)//预计WC+实际WC
					||(j==28||j==29)&&techStr.contains(ElementTechnology.COATING.name)//预计表面处理+实际表面处理
					||(j==8||j==10)&&techStr.contains(ElementTechnology.MILLING.name)//预计铣床 +实际铣床
					||(j==14||j==16)&&techStr.contains(ElementTechnology.GRIND.name)//预计磨床+实际磨床
					||(j==22||j==24)&&techStr.contains(ElementTechnology.DISCHARGE.name)//预计放电+实际放电
					||(j==18||j==20)&&techStr.contains(ElementTechnology.CNC.name)//预计CNC+实际CNC
					||(j==30||j==31)&&techStr.contains(ElementTechnology.QORC.name)//预计QC+实际QC
					/*||j==32*/){//组装日期
				drawCellDateInfo(j,j-4,detail,td);
			}else if(//工时
					  (j==9||j==11)&&techStr.contains(ElementTechnology.MILLING.name)//预计铣床工时+实际铣床工时
					||(j==15||j==17)&&techStr.contains(ElementTechnology.GRIND.name)//预计磨床工时+实际磨床工时
					||(j==23||j==25)&&techStr.contains(ElementTechnology.DISCHARGE.name)//预计放电工时+实际放电工时
					||(j==19||j==21)&&techStr.contains(ElementTechnology.CNC.name)){//预计cnc工时+实际cnc工时)
				drawCellWhInfo(j-4,detail,td)/*.setValueText(getWh(j-4,detail)+"")*/;
			}else if(j==32){//备注
				final Anchor remarklink = new Anchor(new Link(""),"查看详情",td);
				remarklink.setStyleClass("aclick");
				remarklink.clicked().addListener(this,new Signal.Listener() {
					@Override
						public void trigger() {
							PopBox win = new PopBox("备注详情"){
								@Override
								public Widget createFormTbl(String title,
										PopBox wbx) {
										try {
											return popBox(detail,colnum,wbx,false,remarklink);
										} catch (Exception e) {
											e.printStackTrace();
											return null;
										}
								}
							};
							winOuter.bindWidget("win", win);
							//WorkOrderMgr.this.bindWidget("win", win);
							//drawDetailGrid();
					}
				});
			}else if(j==34){//
				drawStatusSel(td,detail);
			}/*else if(j==33){//排序
				drawIndexGroup(td,detail);
			}*/else{
				new Anchor(new Link(""),"--",td);
			}
			//disabledW.add(td.getWidget(0));
			for (Widget w:td.getChildren())
			    disabledW.add(w);
			tr.addWidget(td);
			disabledW.add(tr);
		}
    	return tr;
    }
    
    //根据图面明细生成的记录
    private Widget drawTdContentFromSurface(ContainerWidget tr,SurfaceDetail detail){
			GeneralElement ge = detail.getGeneralElement();
			final String techStr = ge.getTechnologyStr();
			
			final WorkOrderTraceDetail traceDetail = new WorkOrderTraceDetail();
			traceDetail.setSurfaceDetail(detail);
		 
			traceDetail.setWorkOrder(traceWOrder);
			for(int j=0;j<33;j++){
				final int colnum = j; 
				final ContainerWidget td = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TD;
					}
				};
				td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
				if(j>2)td.setStyleClass("cellData");
				if(j==0){
					CheckBox cb = new CheckBox();
					cb.clicked().addListener(this,new Signal.Listener() {
						@Override
						public void trigger() {
							detailhb.setDeliveryObj(traceDetail);
						}
					});
					td.addWidget(cb);
				}else if(j==1){
					Text  t = new Text(detail.getGeneralElement().getName());
					td.addWidget(t);
				}else if(j==2){
					Text  t = new Text(detail.getDetailcode());
					td.addWidget(t);
				}else if(j==3){
					Text t = new Text(detail.getAmount()+"");
					td.addWidget(t);
				}else if(j==32){//备注
					setDateRemark(td,traceDetail,j,false,null);
				}else if((j==4&&techStr.contains(ElementTechnology.OUTERASSIST.name))//预计各列 外协
						||(j==6&&techStr.contains(ElementTechnology.FEEDING.name))//下料
						||(j==12&&techStr.contains(ElementTechnology.HORT.name))//HT 
						||(j==26&&techStr.contains(ElementTechnology.WORC.name))//WC
						||(j==28&&techStr.contains(ElementTechnology.COATING.name))//表面处理
						||(j==8&&techStr.contains(ElementTechnology.MILLING.name))//铣床
						||(j==14&&techStr.contains(ElementTechnology.GRIND.name))//磨床
						||(j==22&&techStr.contains(ElementTechnology.DISCHARGE.name))//放电
						||(j==18&&techStr.contains(ElementTechnology.CNC.name))
						||(j==30&&techStr.contains(ElementTechnology.QORC.name))){//预计信息,其中预计日期=工单里日期（目前取的是工单的生成日期）+零件的生产周期
					setDateRemark(td,traceDetail,j,true,ge);
					
				}else if((j==5&&techStr.contains(ElementTechnology.OUTERASSIST.name))//实际信息+组装日期
						||(j==7&&techStr.contains(ElementTechnology.FEEDING.name))
						||(j==13&&techStr.contains(ElementTechnology.HORT.name))
						||(j==27&&techStr.contains(ElementTechnology.WORC.name))
						||(j==29&&techStr.contains(ElementTechnology.COATING.name))
						||(j==10&&techStr.contains(ElementTechnology.MILLING.name))
						||(j==16&&techStr.contains(ElementTechnology.GRIND.name))
						||(j==24&&techStr.contains(ElementTechnology.DISCHARGE.name))
						||(j==31&&techStr.contains(ElementTechnology.QORC.name))
						||(j==20&&techStr.contains(ElementTechnology.CNC.name))/*||j==32*/){
					setDateRemark(td,traceDetail,j,true,null);
				}else if(
						((j==9||j==11)&&techStr.contains(ElementTechnology.MILLING.name))//预计铣床工时+实际铣床工时
						||((j==15||j==17)&&techStr.contains(ElementTechnology.GRIND.name))//预计磨床工时+实际磨床工时
						||((j==23||j==25)&&techStr.contains(ElementTechnology.DISCHARGE.name))//预计放电工时+实际放电工时
						||((j==19||j==21)&&techStr.contains(ElementTechnology.CNC.name))){//预计cnc工时+实际cnc工时)
					drawCellWhInfo(colnum-4,traceDetail,td);
				}else if(j==34){
					drawStatusSel(td,traceDetail);
				}else{
					new Anchor(new Link(""),"--",td);
				}
				for (Widget w:td.getChildren())
				    disabledW.add(w);
				tr.addWidget(td);
				disabledW.add(tr);
			}
       return tr;	
    }
    //将日期和备注信息拆开到数组中
    private String[] resolveTdInfo(String srcStr){
    	String[] retArr = new String[]{"设定日期","编辑信息"};
    	if(!srcStr.equals("")&&srcStr!=null){
    		srcStr = srcStr.replace("{", "");
    		srcStr = srcStr.replace("}","");
    		retArr = srcStr.split(",");
    	}
    	return retArr;
    }
    //将日期信息和备注信息合成"{日期},{备注}"样式
    private String joinStringInfo(String... str){
    	String retStr = "{";
    	for(String s:str){
    		retStr+=s+"},{";
    	}
    	int endIndex = retStr.lastIndexOf(",");
    	retStr = retStr.substring(0, endIndex);
    	return retStr;
    }
    
    //用于从图面生成的记录,没值，来设定
    private void setDateRemark(final ContainerWidget td,final WorkOrderTraceDetail traceDetail,final int column,final boolean displayFlag,GeneralElement ge){
    	final Anchor a = new Anchor(new Link(""),"设定备注",td);
    	if(displayFlag){
    		Date generateDate = traceDetail.getGenerateDate();
    		if(ge!=null){
    			String yjdatestr = addHours(generateDate, ge.getPeriod());
    			a.setText(yjdatestr);//预计
	    			WidgetUtil.setValForProperty(traceDetail, columnConfigList.get(column-4),joinStringInfo(yjdatestr,"编辑信息"));
    		}else{
    			a.setText("设定日期");//实际、组装
    		}
    	}
    		//a.setText("设定日期");
    	a.setStyleClass("aclick");
		a.clicked().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				PopBox win = new PopBox("设置信息"){
					@Override
					public Widget createFormTbl(String title,
							PopBox wbx) {
							try {
								return popBox(traceDetail,column,wbx,displayFlag,a);
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
					}
				};
				winOuter.bindWidget("win", win);
			/*WorkOrderMgr.this.bindWidget("win", win);
  		    drawDetailGrid();//不重新画表格了，哪里变替换哪里
*/			}
		});
    }
   /* //设定排序字段
    private boolean updateIndex(WorkOrderTraceDetail detail,int index_new){
    	detail.setIndex_(index_new);
    	return dbEntity.saveOrUpdate(detail);
    }*/
	
    private boolean resetShowFlag(){
    	List<EntityObject> resetList = new LinkedList<EntityObject>();
    	List<EntityObject> hiddenList = dbEntity.getSubListQBC(WorkOrderTraceDetail.class,new Object[]{"equal","workOrder",traceWOrder,"and","equal","showflag",false,"and",},-1,-1);
    	for (EntityObject eo:hiddenList){
    		WorkOrderTraceDetail detail = (WorkOrderTraceDetail)eo;
    		detail.setShowflag(true);
    		resetList.add(detail);
    	}
    	return dbEntity.saveOrUpdateBatch(resetList);
    }
    //画出单元表格中内容，预计时间、实际时间
    private void drawCellDateInfo(final int colno,int _index,final WorkOrderTraceDetail detail,ContainerWidget td){
    	String fieldName = columnConfigList.get(_index);
			Object getVal = WidgetUtil.getValFromProperty(detail, fieldName);
			String[] info_arr = resolveTdInfo(getVal==null?"":getVal.toString());
			final Anchor a = new Anchor(new Link(""),info_arr[0],td);
			a.setAttributeValue("title", info_arr[1]);//文字说明，如“取库存”等
			a.setStyleClass("aclick");
		    
			a.clicked().addListener(this,new Signal.Listener() {//点击后重新设定
				
				@Override
				public void trigger() {
					PopBox win = new PopBox("设置信息"){
						@Override
						public Widget createFormTbl(String title,
								PopBox wbx) {
								try {
									return popBox(detail,colno,wbx,true,a);
								} catch (Exception e) {
									e.printStackTrace();
									return null;
								}
						}
					};
					winOuter.bindWidget("win", win);
					/*WorkOrderMgr.this.bindWidget("win", win);
					drawDetailGrid();*/
				}
			});
    }
    //画出单元表格中内容，预计工时、实际工时,设定工时要刷新两个地方，排序+工时统计
    private Widget drawCellWhInfo(final int index_,final WorkOrderTraceDetail detail,final ContainerWidget td){
    	String fieldName = columnConfigList.get(index_);
		Object getVal = WidgetUtil.getValFromProperty(detail, fieldName);
    	final LineEdit le = new LineEdit(""+getVal);
		le.setAttributeValue("style", "width:20px;margin:0px;");
		PushButton setBtn = new PushButton("设置");
		setBtn.setStyleClass("editcls");
		setBtn.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				if(setDetailwh(index_,le.getText(),detail)){
			        //refreshIndex(detail);
			        //
					refreshWh("WOSingleSum",((EntityObject)WorkOrderMgr.this.getHeadButton().getDeliveryObj()).getId().toCharArray());
					drawDetailGrid();
				}else
					Util.infoMessage(tr("Information Please").toString(), "设置失败");
			}
		});
		td.addWidget(le);
		td.addWidget(setBtn);
		return td;
    }
    //已保存的记录，取到工时
    private Short getWh(int index_,WorkOrderTraceDetail detail){
    	String fieldName = columnConfigList.get(index_);
        Short retVal = 0;
    		Object retObj = WidgetUtil.getValFromProperty(detail, fieldName);
    		if(retObj!=null)
    			retVal = Short.valueOf(retObj.toString());
    	return retVal;
    }
    //值改变时,设置工时
    private boolean setDetailwh(int index_,String val,WorkOrderTraceDetail detail){
    	String fieldName = columnConfigList.get(index_);
    		WidgetUtil.setValForProperty(detail, fieldName, Double.valueOf(val));
    		return dbEntity.saveOrUpdate(detail);
    }
    
    //工单完结   
	@Override
	public boolean doEnd() {
		WorkOrder wo = (WorkOrder)getHeadButton().getDeliveryObj();
		System.out.println("wo.getWostatus().getName():"+wo.getWostatus().getName()+"\tWorkOrderStatus.BEDONE.name()："+WorkOrderStatus.BEDONE.name());
		if(wo.getWostatus().getName().equals(WorkOrderStatus.BEDONE.getName())){
			Util.infoMessage("", "该工单已经完结!");
			return false;
		}else{
			wo.setWostatus(WorkOrderStatus.BEDONE);
		    if(!dbEntity.endWorkOrder(wo)){
		    	Util.infoMessage("", "完结失败！");
				return false;
		    };
		    return true;
		}
	}
    //更改工时时要刷新工时统计,addWidget(),setParent()两码事？
    private void refreshWh(String qryName,Object param){	
    	win1Outer = new Template();
    	win1Outer.setTemplateText("${win1}",TextFormat.XHTMLUnsafeText);
		win1Outer.bindString("win1", "");
		WorkOrderWHModel wowhm = null;
    	
    	if(param==null)
    		wowhm = getWotdWh("workorder");
    	else 
    		wowhm = getWotdWh("workordertracedetail");
		Text whAddupTxt= new Text("");
			whAddupTxt.setText("预计总工时:"+df.format(wowhm.getTyj())+"&nbsp;&nbsp;铣床:"+df.format(wowhm.getXcyj())+"&nbsp;&nbsp;磨床:"+df.format(wowhm.getMcyj())
					+"&nbsp;&nbsp;放电:"+df.format(wowhm.getFdyj())+"&nbsp;&nbsp;cnc:"+df.format(wowhm.getCncyj())+"&nbsp;&nbsp;"
					+"实际总工时:"+df.format(wowhm.getTsj())+"&nbsp;&nbsp;铣床:"+df.format(wowhm.getXcsj())+"&nbsp;&nbsp;磨床:"+df.format(wowhm.getMcsj())+"&nbsp;&nbsp;"+
					"放电:"+df.format(wowhm.getFdsj())+"&nbsp;&nbsp;cnc:"+df.format(wowhm.getCncsj()));
			whAddupTxt.setAttributeValue("style", "position: absolute;right: 15px;top: 50px;" +
					"display: block;background-color: #efefef;height: 24px;line-height: 24px;text-align: center;" +
					"color: #247ab6;border-radius: 5px;padding: 0px 10px;border: 1px solid #ddd;");
			win1Outer.bindWidget("win1", whAddupTxt);
			this.bindWidget("win1", win1Outer);
			this.setAttributeValue("style", "position:relative;overflow:hidden;");
    }
    //easyui的信号没发控制？
    private void disabledAllSignal(){
    	 for (Widget w :disabledW){
    		  w.setDisabled(true);
    	 }
    }
    //跟踪明细状态
    private void drawStatusSel(ContainerWidget td,final WorkOrderTraceDetail detail){
    	td.clear();
    	final ComboBox statusSel = WidgetUtil.initSelFromEnum(WOTraceDetailStatus.class);
    	statusSel.setAttributeValue("style", "width:100px;margin:0px;");
    	if(detail.getDetailStatus()!=null){
    		WOTraceDetailStatus wds = WOTraceDetailStatus.getStatus(detail.getDetailStatus().getName());
    		if(wds!=null)
    			statusSel.setCurrentIndex(wds.getIndex());
    	}else
			statusSel.setCurrentIndex(0);
    	
    	statusSel.changed().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				if(WidgetUtil.getSelEO(statusSel)!=null){
					detail.setDetailStatus((WOTraceDetailStatus)WidgetUtil.getSelEO(statusSel));
					if(dbEntity.saveOrUpdate(detail)){
						drawDetailGrid();
						//refreshIndex(detail);
					}else
						Util.infoMessage(tr("Information Please").toString(), "设置失败");
				}
				
			}
		});
    	
    	td.addWidget(statusSel);
    };
    private Widget drawTraceHead(Class cls){
    	getDetailFieldList().clear();
    	Field[] tmfields = cls.getDeclaredFields();
		List<List<Field>> trList = new ArrayList<List<Field>>();
		for (int i=0;i<tmfields.length;i++){
			 Field f = tmfields[i];
			 DisplayField df = f.getAnnotation(DisplayField.class);
			 FilterField ff = f.getAnnotation(FilterField.class);
			 //循环中有要表格要显示的字段
			 if(df!=null){
				 int fieldIndex = df.trindex();
				 if(trList.size()<fieldIndex){
					 List<Field > tr = new ArrayList<Field>();
					 tr.add(f);
					 trList.add(tr);
				 }else{//为兼容工单跟踪的多行
					 trList.get(fieldIndex-1).add(f);
				 }
			 }
			 //循环中有筛选表单要的字段
			 if(ff!=null&&!traceFitlerFieldList.contains(f)){
				 traceFitlerFieldList.add(f);
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
    		tr.setStyleClass("traceHeadtr");
			for(Field field:outer){
				DisplayField df = field.getAnnotation(DisplayField.class);
				FilterField ff = field.getAnnotation(FilterField.class);
				ContainerWidget th = new ContainerWidget(){
	    			@Override
	    			public DomElementType getDomElementType() {
	    				return DomElementType.DomElement_TH;
	    			}
	    		};
	    		
				if(field.getName().equals("seqno")){
	    			th.setAttributeValue("data-options", "field:'F"+df.etag()+"',checkbox:true,align:'center'");
	    			//fieldList.add(field);
	    			getDetailFieldList().add(field);
	    			display_field_count++;
	    			th.setAttributeValue("colspan", df.colspan());
					th.setAttributeValue("rowspan", df.rowspan());
					tr.addWidget(th);
					cw.addWidget(tr);
	    		}else if(!getHiddenList().contains(field)){
	    			th.addWidget(new Text(df.cname()));
	    			th.setAttributeValue("data-options","field:'F"+df.etag()+"',align:'center'");
	    			//fieldList.add(field);
	    			getDetailFieldList().add(field);
	    			display_field_count++;
	    			th.setAttributeValue("colspan", df.colspan());
					th.setAttributeValue("rowspan", df.rowspan());
					tr.addWidget(th);
					cw.addWidget(tr);
	    		}
			}
		}
		return cw;
    }
    public void initJS() {
		WApplication app = WApplication.getInstance();
		initSlide();
		this.setJavaScriptMember(
				" Fixedcol",
				"new Wt3_3_1.Fixedcol(" + app.getJavaScriptClass() + ","
						+ this.getJsRef() + ");");
	}

	public void initSlide() {
		String THIS_JS = "js/fixedcol.js";
		WApplication app = WApplication.getInstance();

		if (!app.isJavaScriptLoaded(THIS_JS)) {
			if (app.getEnvironment().hasAjax()) {
				app.doJavaScript("window.Fixedcol_GZ = { loaded: true };", true);
			}
			String slideURL = WApplication.getRelativeResourcesUrl()
					+ "jquery.fixedcol.js";
			app.require(slideURL, "window['Fixedcol']");
			app.loadJavaScript(THIS_JS, wtjs());
		}
	}

	static WJavaScriptPreamble wtjs() {
		return new WJavaScriptPreamble(JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor, "Fixedcol",
				"function(p,d){jQuery.data(d,\"obj\",this);}");

	}
	//工单总工时统计，单条工单的工单跟踪明细统计
	private WorkOrderWHModel getWotdWh(String flag){
		WorkOrderWHModel wowhModel = new WorkOrderWHModel();
		WorkOrderWHModel singleWowoModel = new WorkOrderWHModel();
	   List<EntityObject> woList = new ArrayList<EntityObject>();
	   List<EntityObject> woTraceDetailList = new ArrayList<EntityObject>();
	   Object[] woArr = new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and","","","","","","","",""};
	   
	   Object[] wotdArr = null;
	   if(traceWOrder!=null)
	   wotdArr = new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and",
			   "equal","workOrder",traceWOrder,"and","","","","","","","",""};
	   else
		   wotdArr = new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and","","","","","","","",""};
	   
	   if(flag.equals("workorder")){
		   if(getQryParamList().size()>0){
			   for (int i=0;i<getQryParamList().size();i++){
				   woArr[4+i] = getQryParamList().get(i);
			   }
		   }
		   woList = dbEntity.getSubListQBC(WorkOrder.class,woArr,-1,-1);
		   //循环list取出，当前所选工单的工时
		   for (EntityObject eo:woList){
			   WorkOrder wo = (WorkOrder)eo;
			   if(!wo.getWostatus().equals(WorkOrderStatus.ACTIVATED)) continue;
			   List<WorkOrderTraceDetail> wotdList = wo.getWotracelist();
			   for (WorkOrderTraceDetail wotd:wotdList){
				   //if(wotd.get)
				   wowhModel.setCncyj(wowhModel.getCncyj()+wotd.getCncyjwh());
				   wowhModel.setCncsj(wowhModel.getCncsj()+wotd.getCncsjwh());
				   
				   wowhModel.setFdsj(wowhModel.getFdsj()+wotd.getFdsjwh());
				   wowhModel.setFdyj(wowhModel.getFdyj()+wotd.getFdyjwh());
				   
				   wowhModel.setMcsj(wowhModel.getMcsj()+wotd.getMcsjwh());
				   wowhModel.setMcyj(wowhModel.getMcyj()+wotd.getMcyjwh());
				   
				   wowhModel.setXcsj(wowhModel.getXcsj()+wotd.getXcsjwh());
				   wowhModel.setXcyj(wowhModel.getXcyj()+wotd.getXcyjwh());
			   }
		   }
		   wowhModel.setTsj(wowhModel.getCncsj()+wowhModel.getFdsj()+wowhModel.getXcsj()+wowhModel.getMcsj());
		   wowhModel.setTyj(wowhModel.getCncyj()+wowhModel.getFdyj()+wowhModel.getXcyj()+wowhModel.getMcyj());
		   return wowhModel;
	   }else{
		   if(detailFilterParamList.size()>0&&traceWOrder!=null){
			   for (int j=0;j<detailFilterParamList.size();j++){
				   wotdArr[8+j] = detailFilterParamList.get(j);
			   }
		   }else if(detailFilterParamList.size()>0&&traceWOrder==null){
			   for (int j=0;j<detailFilterParamList.size();j++){
				   wotdArr[4+j] = detailFilterParamList.get(j);
			   }
		   }
		   woTraceDetailList = dbEntity.getSubListQBC(WorkOrderTraceDetail.class,wotdArr,-1,-1);
		   for (EntityObject eo:woTraceDetailList){
			   WorkOrderTraceDetail wotd = (WorkOrderTraceDetail)eo;
			   singleWowoModel.setCncsj(singleWowoModel.getCncsj()+wotd.getCncsjwh());
			   singleWowoModel.setCncyj(singleWowoModel.getCncyj()+wotd.getCncyjwh());
			   
			   singleWowoModel.setFdsj(singleWowoModel.getFdsj()+wotd.getFdsjwh());
			   singleWowoModel.setFdyj(singleWowoModel.getFdyj()+wotd.getFdyjwh());
			   
			   singleWowoModel.setMcsj(singleWowoModel.getMcsj()+wotd.getMcsjwh());
			   singleWowoModel.setMcyj(singleWowoModel.getMcyj()+wotd.getMcyjwh());
			   
			   singleWowoModel.setXcsj(singleWowoModel.getXcsj()+wotd.getXcsjwh());
			   singleWowoModel.setXcyj(singleWowoModel.getXcyj()+wotd.getXcyjwh());
		   }
		   singleWowoModel.setTsj(singleWowoModel.getCncsj()+singleWowoModel.getFdsj()+singleWowoModel.getMcsj()+singleWowoModel.getXcsj());
		   singleWowoModel.setTyj(singleWowoModel.getCncyj()+singleWowoModel.getFdyj()+singleWowoModel.getMcyj()+singleWowoModel.getXcyj());
		   return singleWowoModel;
	   }
	}
    //要刷新工时	
	@Override
	public void doFilterClick() {
		setPaginationStart(0);
		drawGrid();
		refreshWh("WOOverallSum",null);
	}
	//导出工单跟踪明细
	@Override
	public Object exportCustomDoc(WebResponse resp) {
		String fileUrl = null;
		try {
		traceWOrder = (WorkOrder)getHeadButton().getDeliveryObj();
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("kitty");
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		fileUrl = traceWOrder.getOrderno()+"-"+Util.formatDateyyyyMMdd(new Date())+".xls";
		//画出标题行
		Field[] tmfields = getDeCls().getDeclaredFields();
		List<List<Field>> trList = new ArrayList<List<Field>>();
		for (int i=0;i<tmfields.length;i++){
			 Field f = tmfields[i];
			 DisplayField df = f.getAnnotation(DisplayField.class);
			 //循环中有要表格要显示的字段
			 if(df!=null){
				 int fieldIndex = df.trindex();
				 if(trList.size()<fieldIndex){
					 List<Field > tr = new ArrayList<Field>();
					 tr.add(f);
					 trList.add(tr);
				 }else{//为兼容工单跟踪的多行
					 trList.get(fieldIndex-1).add(f);
				 }
			 }
		}
		int startRow=0,endRow=0,startCol=0,endCol=0;
		HSSFCell cell = null;
		
		for (int j=0;j<trList.size();j++){
			HSSFRow row = sheet.createRow(j);
			List<Field> trFields = trList.get(j);
			for (int k=0;k<trFields.size();k++){
				Field headField = trFields.get(k);
				DisplayField  dfAnno = headField.getAnnotation(DisplayField.class);
				startRow = j;
				if(Integer.valueOf(dfAnno.rowspan())>1) 
					endRow = startRow + Integer.valueOf(dfAnno.rowspan())-1;
				else
					endRow = startRow;
				if(dfAnno.etag()<17)//外协的预计时间的etag下标为17，以后各列依次累加，故17以前都是第一行的头字段
					startCol = getStartCol(headField,trFields);
				else
					startCol = getStartCol(headField,trFields)+4;//，第二行要跳过【序号】、【名称】、【图号】和【数量】
				if(Integer.valueOf(dfAnno.colspan())>1)
					 endCol = startCol+Integer.valueOf(dfAnno.colspan())-1;
				 else
					 endCol = startCol;
				
				sheet.addMergedRegion(new Region((short)startRow,(short)startCol,(short)endRow,(short)endCol));
				cell = row.createCell(startCol);
				cell.setCellValue(dfAnno.cname().equals("")?"序号":dfAnno.cname());
				cell.setCellStyle(cellStyle);
			}
		}
		//开始记录
		//List<WorkOrderTraceDetail> wotdList = traceWOrder.getWotracelist();//同采购合同，刚操作【保存】getList是取不出记录的
        //数据库查询而来		
		List<EntityObject> wotdList = dbEntity.getSubListQBC(WorkOrderTraceDetail.class, new Object[]{"equal","workOrder",traceWOrder,"and",
			"equal","showflag",true,"and","equal","dataCode",dataCode,"and"}, -1, -1);
		List<SurfaceDetail> surfaceDetailList = traceWOrder.getSurface().getSdList();
		
		
		for (int l=0;l<wotdList.size();l++){
			WorkOrderTraceDetail traceDetail = (WorkOrderTraceDetail)wotdList.get(l);
			HSSFRow row = sheet.createRow(l+2);
			//序号、名称、图号和数量
			HSSFCell seqCell = row.createCell(0);
			seqCell.setCellValue(l+1);
			seqCell.setCellStyle(cellStyle);
			
			HSSFCell nameCell = row.createCell(1);
			nameCell.setCellValue(traceDetail.getSurfaceDetail().getGeneralElement().getName());
			
			HSSFCell drawingCell = row.createCell(2);
			drawingCell.setCellValue(traceDetail.getSurfaceDetail().getDetailcode());
			
			HSSFCell amountCell = row.createCell(3);
			amountCell.setCellValue(traceDetail.getSurfaceDetail().getAmount());
			//其他列
			for (int m=0;m<columnConfigList.size();m++){
				HSSFCell detailCell = row.createCell(m+4);
				String fieldName = columnConfigList.get(m);
				Object fieldVal = WidgetUtil.getValFromProperty(traceDetail, fieldName);
				detailCell.setCellValue(fieldVal+"");
				//cell.setCellStyle(cellStyle);
			}
		}
		List<SurfaceDetail> remainSDLL = new LinkedList<SurfaceDetail>();//添加的时候用LinkedList
		if(wotdList.size()<surfaceDetailList.size()){//
			for (SurfaceDetail surfaceDetail:surfaceDetailList){
				 for(EntityObject eo:wotdList){
					 WorkOrderTraceDetail traceDetail = (WorkOrderTraceDetail)eo;
					 if(!traceDetail.getSurfaceDetail().getId().equals(surfaceDetail.getId())){
						 //surfaceDetailList.remove(surfaceDetail);
						 remainSDLL.add(surfaceDetail);
					 }
				 }
			}
		}
		//System.out.println("remainSDList.size():"+remainSDLL.size());
		List<SurfaceDetail> remainSDAL = new ArrayList(remainSDLL);//访问的时候用ArrayList
		for (int n=0;n<remainSDAL.size();n++){
			SurfaceDetail sd = remainSDAL.get(n);
			HSSFRow row = sheet.createRow(wotdList.size()+2+n);
			
			HSSFCell seqCell = row.createCell(0);
			seqCell.setCellValue(wotdList.size()+n+1);
			seqCell.setCellStyle(cellStyle);
			
			HSSFCell nameCell = row.createCell(1);
			nameCell.setCellValue(sd.getGeneralElement().getName());
			
			HSSFCell drawingcodeCell = row.createCell(2);
			drawingcodeCell.setCellValue(sd.getDetailcode());
			
			HSSFCell amountCell = row.createCell(3);
			amountCell.setCellValue(sd.getAmount());
			
			//预计时间算一下，其余皆可空下
			for (int o=0;o<columnConfigList.size();o++){
				HSSFCell columnCell = row.createCell(o+4);
				String columnName = columnConfigList.get(o);
				if(columnName.contains("yjInfo")){
					columnCell.setCellValue(addHours(new Date(),sd.getGeneralElement().getPeriod()));
				}else
					columnCell.setCellValue("");
			}
			
		}
		resp.setContentType("application/x-excel");
		resp.addHeader("Content-Disposition", "attachment;filename="+fileUrl);
		
	  workBook.write(resp.getOutputStream());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tr("www.file")+"/"+fileUrl;
	}
	//递归获取当前表头字段的起始列
	private int getStartCol(Field field,List<Field> fieldList){
		int fieldIndex = fieldList.indexOf(field);
		if(fieldIndex>=1){
			Field previousField = fieldList.get(fieldIndex-1);//获取前一字段
			DisplayField dfAnno = previousField.getAnnotation(DisplayField.class);//前字段的标注
			int previousStartCol = getStartCol(previousField,fieldList);
			return  previousStartCol + Integer.valueOf(dfAnno.colspan());
		}else
			return 0;
	}
}
