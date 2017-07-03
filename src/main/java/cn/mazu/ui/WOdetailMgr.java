/*package cn.mazu.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import cn.mazu.utils.WLength;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.script.JavaScriptObjectType;
import cn.mazu.webkit.script.JavaScriptScope;
import cn.mazu.webkit.script.WJavaScriptPreamble;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.WebWidget;
import cn.mazu.widget.kit.web.form.CheckBox;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.form.TextArea;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.workorder.entity.WorkOrder;
import cn.mazu.workorder.entity.WorkOrderTraceDetail;
import cn.mazu.workorder.entity.WorkOrderWHSum;

public class WOdetailMgr extends MiddleTpl{
	private Template parent;
	private String[] args;
	private Entity dbEntity;
	private WorkOrder traceWOrder ;
	private WorkOrderTraceMgr wotm;
	//防止不按照流程走，只是建了销售单，无图面数据后即来跟踪工单，所以先初始化下
	private List<EntityObject> surfaceDetailList = new ArrayList<EntityObject>();

	private static List<String> columnConfigList;//--->Array
	private HeadButton  detailhb;
	private boolean drawAllFlag;
	private int display_count;
	private List<Field> fieldList = new ArrayList<Field>(),hiddenFieldList = new ArrayList<Field>();
	private AccountPermission ap ;
	private List<Widget> disabledW = new ArrayList<Widget>();

	static{
		columnConfigList = new ArrayList<String>();
		columnConfigList.add("wxyjInfo");//0 外协
    	columnConfigList.add("wxsjInfo");//1

    	columnConfigList.add("xlyjInfo");//2 下料
    	columnConfigList.add("xlsjInfo");//3
    	
    	columnConfigList.add("htyjInfo");//4 HT
    	columnConfigList.add("htsjInfo");//5
    	
    	columnConfigList.add("wcyjInfo");//6 WC
    	columnConfigList.add("wcsjInfo");//7
    	
    	columnConfigList.add("bmyjInfo");//8 表面处理
    	columnConfigList.add("bmsjInfo");//9
    	
    	columnConfigList.add("xcyjInfo");//10 铣床
    	columnConfigList.add("xcyjwh");//11
    	columnConfigList.add("xcsjInfo");//12
    	columnConfigList.add("xcsjwh");//13
    	
    	
    	columnConfigList.add("mcyjInfo");//14 磨床
    	columnConfigList.add("mcyjwh");//15
    	columnConfigList.add("mcsjInfo");//16
    	columnConfigList.add("mcsjwh");//17
    	
    	columnConfigList.add("fdyjInfo");//18 放电
    	columnConfigList.add("fdyjwh");//19
    	columnConfigList.add("fdsjInfo");//20
    	columnConfigList.add("fdsjwh");//21
    	
    	columnConfigList.add("cncyjInfo");//22 cnc
    	columnConfigList.add("cncyjwh");//23
    	columnConfigList.add("cncsjInfo");//24
    	columnConfigList.add("cncsjwh");//25
    	
    	columnConfigList.add("assembleInfo");//26 组装日期
    	columnConfigList.add("remark");//27备注
    	
	}
	public WOdetailMgr() {
	   super("cn.mazu.workorder.entity.WorkOrder",
			   new Object[]{"like","dataCode",
			   Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and"});
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
    	    
			dename = "cn.mazu.workorder.entity.WorkOrderTraceDetail";//改参数...
			traceWOrder = (WorkOrder)eo;
			wotm.setObj_arr(new Object[]{"equal","workOrder",traceWOrder,"and",
                                        "orderBy","asc","index_",""});
			drawDetailGrid();//先画组件后处理
			try{
				if(getHiddenList().contains(getCurCls().getField("oper"))){//查看明细
					disabledAllSignal();
					wotm.btnstr = "返回";
				}else if(!getHiddenList().contains(getCurCls().getField("oper"))&&
						((WorkOrder)eo).getWostatus().equals(Util.WorkOrderStatus.BEDONE)){//编辑明细
					disabledAllSignal();
					wotm.btnstr = "返回";
				}else if(!getHiddenList().contains(getCurCls().getField("oper"))&&
						!((WorkOrder)eo).getWostatus().equals(Util.WorkOrderStatus.BEDONE)){
					wotm.btnstr = "隐藏,显示全部,删除,返回";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			detailhb = new HeadButton("",wotm.btnstr){
				@Override//返回
				public void drawPreviousTbl() {
					WOdetailMgr.this.clear();
					WOdetailMgr.this.draw(parent, args);
					refreshWh("WOOverallSum",null);
					WOdetailMgr.this.doJavaScript("$('#bctable').datagrid();");
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
							Util.infoMessage("Information Please", "删除失败");
						}
					}else{
						Util.infoMessage("Information Please", "只能删除已跟踪的记录！");
					}
				}
				
			};
			
			this.bindWidget("menubtn",detailhb.draw(parent, args));
			wotm.setHeadButton(detailhb);
			this.setTemplateText("" +
					"${menubtn}"+
				"<div class='traceWrapa'>" +
				"<table class='detailtb' id='WODTb'>"+
				"            <thead>"+
				"            ${trhead}"+
				"            </thead>" +
			    			"${tbody}" +
			    "</table>" +
			    "</div>" +
				"${win}${win1}",TextFormat.XHTMLUnsafeText);
			
			
			this.bindString("win","");
			return this;
    }
    private void drawDetailGrid(){
		this.bindWidget("trhead",drawTraceHead(getDeCls()));
		this.bindWidget("tbody",drawTraceGridContent());
		initJS();
		this.doJavaScript("FixTable('WODTb', 3, 1240, 400);");
		refreshWh("WOSingleSum",((EntityObject)this.getHeadButton().getDeliveryObj()).getId().toCharArray());
    }
    private Widget drawTraceGridContent(){
    	final ContainerWidget tbody = new ContainerWidget(){
    		@Override
    		public DomElementType getDomElementType() {
    			return DomElementType.DomElement_TBODY;
    		}
    	};
//    	tbody.setStyleClass("traceHeadtbody");
    		Surface surface = traceWOrder.getSurface();
        	if(surface!=null)surfaceDetailList = dbEntity.getSubListQBC(SurfaceDetail.class, new Object[]{"equal","surface",surface,"and"}, -1, -1);
        	wotm.setPlist(dbEntity.getSubListQBC(WorkOrderTraceDetail.class,wotm.getObj_arr(), -1, -1));
    		//先画数据库查询出来的，实际填写的跟踪记录
    		for (int j=0;j<wotm.getPlist().size();j++){
    			final WorkOrderTraceDetail tDetailDB = (WorkOrderTraceDetail)wotm.getPlist().get(j);
    			for (EntityObject eo :surfaceDetailList){//该记录已经在数据库存在时，要把生成list中的记录剔除
    				SurfaceDetail surfaceDetail = (SurfaceDetail)eo;
    				if(surfaceDetail.getId().equals(tDetailDB.getSurfaceDetail().getId())){
    					surfaceDetailList.remove(eo);
    					break;
    				}
    			}
    			if(!tDetailDB.isShowflag())continue;//这条记录不显示了，隐藏的
    			ContainerWidget trdb = new ContainerWidget(){
    				@Override
    				public DomElementType getDomElementType() {
    					return DomElementType.DomElement_TR;
    				}
    			};
    			tbody.addWidget(drawTdContentFromDb(trdb,tDetailDB));
    		}
    		//再画总的图面明细减去实际跟踪的，剩下的是待跟踪的
        	for (int i=0;i<surfaceDetailList.size();i++){
        		final SurfaceDetail detail = (SurfaceDetail)surfaceDetailList.get(i);
        		ContainerWidget trsf = new ContainerWidget(){
        			@Override
        			public DomElementType getDomElementType() {
        				return DomElementType.DomElement_TR;
        			}
        		};
        		tbody.addWidget(drawTdContentFromSurface(trsf,detail));
        	}
    	return tbody;
    }
    //计算预计完成时间
    private String addHours(Date srcDate,int addHour){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcDate);
		calendar.add(Calendar.HOUR_OF_DAY, addHour);
		return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
    }
    traceDetail--被操作的实例
     * j-方便取到列名
     * wbx--弹出层
     * displayAll--false只显示文本编辑区域，true则连同日期编辑框一起显示,为兼容编辑备注，备注只有文本没有日期
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
    }
    //已经保存入库的跟踪记录
    private Widget drawTdContentFromDb(ContainerWidget tr,final WorkOrderTraceDetail detail){
    	String techStr = detail.getSurfaceDetail().getGeneralElement().getTechnologyStr();
    	for(int j=0;j<34;j++){
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
					||(j==8||j==9)&&techStr.contains(ElementTechnology.HORT.name)//预计HT+实际HT 
					||(j==10||j==11)&&techStr.contains(ElementTechnology.WORC.name)//预计WC+实际WC
					||(j==12||j==13)&&techStr.contains(ElementTechnology.COATING.name)//预计表面处理+实际表面处理
					||(j==14||j==16)&&techStr.contains(ElementTechnology.MILLING.name)//预计铣床 +实际铣床
					||(j==18||j==20)&&techStr.contains(ElementTechnology.GRIND.name)//预计磨床+实际磨床
					||(j==22||j==24)&&techStr.contains(ElementTechnology.DISCHARGE.name)//预计放电+实际放电
					||(j==26||j==28)&&techStr.contains(ElementTechnology.CNC.name)//预计CNC+实际CNC
					||j==30){//组装日期
				drawCellDateInfo(j,j-4,detail,td);
			}else if(//工时
					  (j==15||j==17)&&techStr.contains(ElementTechnology.MILLING.name)//预计铣床工时+实际铣床工时
					||(j==19||j==21)&&techStr.contains(ElementTechnology.GRIND.name)//预计磨床工时+实际磨床工时
					||(j==23||j==25)&&techStr.contains(ElementTechnology.DISCHARGE.name)//预计放电工时+实际放电工时
					||(j==27||j==29)&&techStr.contains(ElementTechnology.CNC.name)){//预计cnc工时+实际cnc工时)
				drawCellWhInfo(j-4,detail,td).setValueText(getWh(j-4,detail)+"");
			}else if(j==31){//备注
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
							WOdetailMgr.this.bindWidget("win", win);
							drawDetailGrid();
					}
				});
			}else if(j==32){//
				drawStatusSel(td,detail);
			}else if(j==33){//排序
				drawIndexGroup(td,detail);
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
    
    //根据图面明细生成的记录
    private Widget drawTdContentFromSurface(ContainerWidget tr,SurfaceDetail detail){
			GeneralElement ge = detail.getGeneralElement();
			final String techStr = ge.getTechnologyStr();
			
			final WorkOrderTraceDetail traceDetail = new WorkOrderTraceDetail();
			traceDetail.setSurfaceDetail(detail);
		 
			traceDetail.setWorkOrder(traceWOrder);
			for(int j=0;j<34;j++){
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
				}else if(j==31){//备注
					setDateRemark(td,traceDetail,j,false,null);
				}else if((j==4&&techStr.contains(ElementTechnology.OUTERASSIST.name))//预计各列 外协
						||(j==6&&techStr.contains(ElementTechnology.FEEDING.name))//下料
						||(j==8&&techStr.contains(ElementTechnology.HORT.name))//HT 
						||(j==10&&techStr.contains(ElementTechnology.WORC.name))//WC
						||(j==12&&techStr.contains(ElementTechnology.COATING.name))//表面处理
						||(j==14&&techStr.contains(ElementTechnology.MILLING.name))//铣床
						||(j==18&&techStr.contains(ElementTechnology.GRIND.name))//磨床
						||(j==22&&techStr.contains(ElementTechnology.DISCHARGE.name))//放电
						||(j==26&&techStr.contains(ElementTechnology.CNC.name))){//预计信息,其中预计日期=工单里日期（目前取的是工单的生成日期）+零件的生产周期
					setDateRemark(td,traceDetail,j,true,ge);
					
				}else if((j==5&&techStr.contains(ElementTechnology.OUTERASSIST.name))//实际信息+组装日期
						||(j==7&&techStr.contains(ElementTechnology.FEEDING.name))
						||(j==9&&techStr.contains(ElementTechnology.HORT.name))
						||(j==11&&techStr.contains(ElementTechnology.WORC.name))
						||(j==13&&techStr.contains(ElementTechnology.COATING.name))
						||(j==16&&techStr.contains(ElementTechnology.MILLING.name))
						||(j==20&&techStr.contains(ElementTechnology.GRIND.name))
						||(j==24&&techStr.contains(ElementTechnology.DISCHARGE.name))
						||(j==28&&techStr.contains(ElementTechnology.CNC.name))||j==30){
					setDateRemark(td,traceDetail,j,true,null);
				}else if(
						((j==15||j==17)&&techStr.contains(ElementTechnology.MILLING.name))//预计铣床工时+实际铣床工时
						||((j==19||j==21)&&techStr.contains(ElementTechnology.GRIND.name))//预计磨床工时+实际磨床工时
						||((j==23||j==25)&&techStr.contains(ElementTechnology.DISCHARGE.name))//预计放电工时+实际放电工时
						||((j==27||j==29)&&techStr.contains(ElementTechnology.CNC.name))){//预计cnc工时+实际cnc工时)
					drawCellWhInfo(colnum-4,traceDetail,td);
				}else if(j==32){
					drawStatusSel(td,traceDetail);
				}else if(j==33){
                      ContainerWidget nulDiv = new ContainerWidget(td){
                    	  @Override
                    	public DomElementType getDomElementType() {
                    		return DomElementType.DomElement_DIV;
                    	}
                      };
                      nulDiv.setWidth(new WLength(100));
                      nulDiv.setHeight(new WLength(27));
                      nulDiv.setId(traceDetail.getId());
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
			WOdetailMgr.this.bindWidget("win", win);
  		    drawDetailGrid();//不重新画表格了，哪里变替换哪里
			
			}
		});
    }
    //设定排序字段
    private boolean updateIndex(WorkOrderTraceDetail detail,int index_new){
    	detail.setIndex_(index_new);
    	return dbEntity.saveOrUpdate(detail);
    }
	
    private boolean resetShowFlag(){
    	List<EntityObject> resetList = new ArrayList<EntityObject>();
    	for (EntityObject eo:wotm.getPlist()){
    		WorkOrderTraceDetail detail = (WorkOrderTraceDetail)eo;
    		if(!detail.isShowflag()){
    			detail.setShowflag(true);
    			resetList.add(detail);
    		}
    	}
    	return dbEntity.saveOrUpdateBatch(resetList);
    }
    //画出单元表格中内容，预计时间、实际时间
    private void drawCellDateInfo(final int colno,int _index,final WorkOrderTraceDetail detail,ContainerWidget td){
    	String fieldName = columnConfigList.get(_index);
			Object getVal = WidgetUtil.getValFromProperty(detail, fieldName);
			String[] info_arr = resolveTdInfo(getVal==null?"":getVal.toString());
			final Anchor a = new Anchor(new Link(""),info_arr[0],td);
			//td.setWidth(new WLength(96));//设置宽度
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
					WOdetailMgr.this.bindWidget("win", win);
					drawDetailGrid();
				}
			});
    }
    //画出单元表格中内容，预计工时、实际工时,设定工时要刷新两个地方，排序+工时统计
    private Widget drawCellWhInfo(final int index_,final WorkOrderTraceDetail detail,final ContainerWidget td){
    	String fieldName = columnConfigList.get(index_);
		Object getVal = WidgetUtil.getValFromProperty(detail, fieldName);
    	final LineEdit le = new LineEdit(""+getVal);
    	final String leid = le.getId();
		le.setAttributeValue("style", "width:20px;margin:0px;");
		PushButton setBtn = new PushButton("设置");
		setBtn.setStyleClass("editcls");
		setBtn.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				System.out.println("td.getParent():"+td.getParent());
		    	System.out.println("td.getParent().getParent().getParent():"+getChildrenById((WebWidget)td.getParent().getParent().getParent(),leid));
				if(setDetailwh(index_,le.getText(),detail)){
			        refreshIndex(detail);
			        drawDetailGrid();
				}else
					Util.infoMessage("Information Please", "设置失败");
			}
		});
		td.addWidget(le);
		td.addWidget(setBtn);
		//td.setWidth(new WLength(168));//设置宽度
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
		//判断是否所有明细都跟踪完毕，若无，则不允许完结
		if(!dbEntity.hasTracingDetail(wo)){
			wo.setWostatus(WorkOrderStatus.BEDONE);
			return dbEntity.saveOrUpdate(wo);
		}
		else{
			Util.infoMessage("", "尚有未完成的跟踪记录，不能完结!");
			return false;
		}
				
	}
	//不刷新整张表了，但刷新排序是必要的
    @SuppressWarnings("el-syntax")
    private void refreshIndex(WorkOrderTraceDetail detail){
    	    Widget orderPosi = findById(detail.getId());
    	    if(orderPosi==null)
    	    	return;
    	    else
    	    	drawIndexGroup((ContainerWidget)orderPosi,detail);
    	    
    }
    //刷新和画是两个逻辑
    private void drawIndexGroup(ContainerWidget indexContainer,final WorkOrderTraceDetail detail){
    	indexContainer.clear();
    	final LineEdit le = new LineEdit(detail.getIndex_()+"");
		le.setAttributeValue("style", "width:20px;margin:0px;");
		PushButton orderBtn = new PushButton("排序");
		orderBtn.setStyleClass("editcls");
		orderBtn.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				int index_ = Integer.valueOf(le.getText());
				if(updateIndex(detail,index_)){
					drawDetailGrid();
				}
			}
		});
		indexContainer.addWidget(le);
		indexContainer.addWidget(orderBtn);
    }
    //更改工时时要刷新工时统计,addWidget(),setParent()两码事？
    private void refreshWh(String qryName,Object param){
			Text whAddupTxt= new Text("");
			List<EntityObject> eolist = dbEntity.getNavListFromGeneral(
					WorkOrderWHSum.class, new Object[]{qryName,param}, 
					-1, -1);
			if(eolist!=null&&eolist.size()>0){
				WorkOrderWHSum wowhs = ((WorkOrderWHSum)eolist.get(0));
				whAddupTxt.setText("预计总工时:"+wowhs.getTyj()+"&nbsp;&nbsp;铣床:"+wowhs.getXcyj()+"&nbsp;&nbsp;磨床:"+wowhs.getMcyj()
						+"&nbsp;&nbsp;放电:"+wowhs.getFdyj()+"&nbsp;&nbsp;cnc:"+wowhs.getCncyj()+"&nbsp;&nbsp;"
						+"实际总工时:"+wowhs.getTsj()+"&nbsp;&nbsp;铣床:"+wowhs.getXcsj()+"&nbsp;&nbsp;磨床:"+wowhs.getMcsj()+"&nbsp;&nbsp;"+
						"放电:"+wowhs.getFdsj()+"&nbsp;&nbsp;cnc:"+wowhs.getCncsj());
				whAddupTxt.setAttributeValue("style", "position: absolute;left: 300px;top: 50px;" +
						"display: block;background-color: #efefef;height: 24px;line-height: 24px;text-align: center;" +
						"color: #247ab6;border-radius: 5px;padding: 0px 10px;border: 1px solid #ddd;");
				this.bindWidget("win1", whAddupTxt);
			}
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
						refreshIndex(detail);
					}else
						Util.infoMessage("Information Please", "设置失败");
				}
				
			}
		});
    	
    	td.addWidget(statusSel);
    };
    private Widget drawTraceHead(Class cls){
    	Field[] tmfields = cls.getDeclaredFields();
		//filterFieldList = new ArrayList<Field>();
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
			 if(ff!=null){
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
				ContainerWidget th = new ContainerWidget(){
	    			@Override
	    			public DomElementType getDomElementType() {
	    				return DomElementType.DomElement_TH;
	    			}
	    		};
	    		
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
	private List<Widget> getChildrenById(WebWidget ww,String id){
		System.out.println("widgetid:"+id+"\tthis.getParent():"+this.getParent());
		List<Widget> childrenW = new ArrayList<Widget>();
		for(int j=0;j<((WebWidget)this.getParent()).getChildren().size();j++){
			System.out.println("ww.child:"+((WebWidget)this.getParent()).getChildren().get(j)+
					"\t childid:"+((WebWidget)this.getParent()).getChildren().get(j).getId());
		}
		if(this.getChildren()!=null){
			for (int i = 0; i < this.getChildren().size(); ++i) {
				Widget result = this.getChildren().get(i).findById(id);
				if (result != null) {
					System.out.println("this.getChildren().get(i):"+this.getChildren().get(i)+"\tresult:"+result);
					childrenW.add(result);
				}
				
			}
		}
		System.out.println("childrenW.size():"+childrenW.size());
		return childrenW;
	}
}
*/