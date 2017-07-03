package cn.mazu.ui;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.mazu.annotation.DisplayField;
import cn.mazu.cost.entity.CostConf;
import cn.mazu.cost.entity.WOCostDetailModel;
import cn.mazu.cost.entity.WOCostGather;
import cn.mazu.doc.entity.Goods;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.mysql.Entity;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.DOCType;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.AlignmentFlag;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.workorder.entity.WorkOrder;
import cn.mazu.workorder.entity.WorkOrderTraceDetail;

public class WOCostMgr extends MiddleTpl{
    private Entity dbEntity;
    private Map<Field,String> totalMap = new HashMap<Field,String>();//此Map做为纵向合计字段用
    private Map<SurfaceDetail,String> sMap = new TreeMap<SurfaceDetail,String>();//map
    private Map<EntityObject,List<WOCostDetailModel>> wocdMap = new HashMap<EntityObject,List<WOCostDetailModel>>();//统计各个明细用，key中String为GeneralElement的id
    private DecimalFormat df = new DecimalFormat(".##");
    private List<Field> docFieldList = new ArrayList<Field>();
    //private Map<String,List<WOCostDetailModel>> wocdMap = new HashMap<String,List<WOCostDetailModel>>();//统计各个明细用，key中String为GeneralElement的id
    private WorkOrder selectedWo ;
    //private Template tpl;
    private PopBox wbx;
    private EntityObject eo;
    private Widget costDetailHead, costDetailBody,closeBtn;
	public WOCostMgr() {
	  super("cn.mazu.cost.entity.WOCostGather",new Object[]{"WoCostQuery"});
	  setNavSqlFlag(true);
	  dbEntity = getDBEntity();
   }
   public void getDetailTblBody(final Template tpl, PopBox wbx, String title,
			EntityObject eo, String dename){
	   //this.tpl = tpl;
	   this.wbx = wbx;
	   this.eo = eo;
	   totalMap.clear();
       wocdMap.clear();
       for (Field field:getDeCls().getDeclaredFields()){
       	totalMap.put(field, "0.0");
       }
       //工单
       selectedWo = getDBEntity().getWOByOrderno(((WOCostGather)getHeadButton().getDeliveryObj()).getWorkorderno());
       
       //工单成本配置信息
       CostConf costConf = null;
       List<EntityObject> traceList  = null,signDocList = null;
       //Goods goods = null;
       if(selectedWo!=null){
         traceList = getDBEntity().getSubListQBC(WorkOrderTraceDetail.class, new Object[]{"equal","workOrder",selectedWo,"and"}, -1, -1);
         signDocList = getDBEntity().getSubListQBC(SignDoc.class, new Object[]{"equal","workOrder",selectedWo,"and"}, -1, -1);
         costConf = (CostConf)getDBEntity().getCostConfByWO(selectedWo);
       }
       //来自图面明细
       for (EntityObject wotd:traceList){
    	   WorkOrderTraceDetail traceDetail = (WorkOrderTraceDetail)wotd;
    	   
    	   WOCostDetailModel wdModel = new WOCostDetailModel();
    	   wdModel.setGoodName(traceDetail.getSurfaceDetail().getGeneralElement().getName());
    	   wdModel.setDrawingCode(traceDetail.getSurfaceDetail().getDetailcode());
    	   wdModel.setCncwh(traceDetail.getCncsjwh());
    	   wdModel.setXcwh(traceDetail.getXcsjwh());
    	   wdModel.setMcwh(traceDetail.getMcsjwh());
    	   wdModel.setFdwh(traceDetail.getFdsjwh());
    	   wdModel.setTotalwh(traceDetail.getCncsjwh()+traceDetail.getXcsjwh()+traceDetail.getMcsjwh()+traceDetail.getFdsjwh());
    	    
    	   if(costConf!=null){
    		   wdModel.setMccb(traceDetail.getMcsjwh()*costConf.mccost);
    		   wdModel.setFdcb(traceDetail.getFdsjwh()*costConf.fdcost);
    		   wdModel.setXccb(traceDetail.getXcsjwh()*costConf.xccost);
    		   wdModel.setCnccb(traceDetail.getCncsjwh()*costConf.cnccost);
    		   wdModel.setTotalwhcb(wdModel.getMccb()+wdModel.getXccb()+wdModel.getCnccb()+wdModel.getFdcb());
    	   }
    	   List<EntityObject> geoList = getDBEntity().getSubListQBC(Goods.class, new Object[]{"equal","surfaceDetail",traceDetail.getSurfaceDetail(),"and"}, -1, -1);
    	   for (EntityObject geo:geoList){
    		   Goods goods = (Goods)geo;
    		   if(goods.getSignDoc().getDocType().name.equals(DOCType.AUTHORIZE.name)||
        			   goods.getSignDoc().getDocType().name.equals(DOCType.GAUTHORIZE.name)||goods.getSignDoc().getDocType().name.equals(DOCType.KGMAUTHORIZE.name))//面积算法托工单、一般托工单、公斤材质算法托工单
        		   wdModel.setAcost(wdModel.getAcost()+goods.getTotalPrice());
       		  else if(goods.getSignDoc().getDocType().name.equals(DOCType.BOOKING.name))
       		      wdModel.setBcost(wdModel.getBcost()+goods.getTotalPrice());
       		  else if(goods.getSignDoc().getDocType().name.equals(DOCType.PURCHASE.name)||goods.getSignDoc().getDocType().name.equals(DOCType.GPURCHASE.name))
       			  wdModel.setPcost(wdModel.getPcost()+goods.getTotalPrice());
    		   
    		   wdModel.setOutcost(wdModel.getAcost()+wdModel.getPcost()+wdModel.getBcost());//外发总成本
    	   }
    	   
    	   wdModel.setTotalCb(wdModel.getTotalwhcb()+wdModel.getOutcost());//总成本
    	   
    	   SurfaceDetail sDetail = traceDetail.getSurfaceDetail();
    	   List<WOCostDetailModel> modelList = wocdMap.get(sDetail);
    	   if(modelList==null){
    		   modelList = new LinkedList<WOCostDetailModel>();
    	   }
    	   modelList.add(wdModel);
    	   wocdMap.put(sDetail, modelList);
       }
       //来自库存
       for (EntityObject sdEo:signDocList){
    	   SignDoc signDoc = (SignDoc)sdEo;
    	   List<EntityObject> goodsList = getDBEntity().getIvGoodsList(signDoc,selectedWo);//.getSubListQBC(Goods.class, new Object[]{"equal","signDoc",signDoc,"and","notEqual","inventory",null,"and"}, -1, -1);
    	   for(EntityObject geo:goodsList){
    		   Goods inGoods = (Goods)geo;
    		   WOCostDetailModel wdModel = new WOCostDetailModel();
    		   wdModel.setGoodName(inGoods.getGoodsName());
    		   wdModel.setDrawingCode(inGoods.getInventory().getSpec());
    		   if(inGoods.getSignDoc().getDocType().name.equals(DOCType.AUTHORIZE.name)||
    				   inGoods.getSignDoc().getDocType().name.equals(DOCType.GAUTHORIZE.name)||inGoods.getSignDoc().getDocType().name.equals(DOCType.KGMAUTHORIZE.name))//面积算法托工单、一般托工单、公斤材质算法托工单
        		   wdModel.setAcost(inGoods.getTotalPrice());
       		  else if(inGoods.getSignDoc().getDocType().name.equals(DOCType.BOOKING.name))
       		      wdModel.setBcost(inGoods.getTotalPrice());
       		  else if(inGoods.getSignDoc().getDocType().name.equals(DOCType.PURCHASE.name)||inGoods.getSignDoc().getDocType().name.equals(DOCType.GPURCHASE.name))
       			  wdModel.setPcost(inGoods.getTotalPrice());
    		   wdModel.setOutcost(wdModel.getAcost()+wdModel.getPcost()+wdModel.getBcost());//外发总成本
    		   wdModel.setTotalCb(wdModel.getOutcost()+wdModel.getTotalwhcb());
    		   //System.out.println("inGoods.getInventory():"+inGoods.getInventory().getId()+"\tinGoods.getTotalPrice():"+inGoods.getTotalPrice());
    		   List<WOCostDetailModel> modelList = wocdMap.get(inGoods.getInventory());
    		   if(modelList==null)
    			   modelList = new LinkedList<WOCostDetailModel>();
    		   modelList.add(wdModel);
    		   wocdMap.put(inGoods.getInventory(), modelList);
    	   }
       }
       ContainerWidget tbody = new ContainerWidget(){//模板体
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TBODY;
			}
		};
		Field[] mfields = getDeCls().getDeclaredFields();
		int modelIndex = 0;
		for (Iterator iterator = wocdMap.entrySet().iterator();iterator.hasNext();){
			 modelIndex++;
			 Map.Entry<EntityObject, List<WOCostDetailModel>> costEntry = (Map.Entry<EntityObject, List<WOCostDetailModel>>)iterator.next();
			 List<WOCostDetailModel> modelList = (List)costEntry.getValue();
			 final EntityObject eoKey = (EntityObject)costEntry.getKey();
			 ContainerWidget tr = new ContainerWidget(){
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_TR;
					}
				};
				for (final Field field : mfields){
					ContainerWidget td = new ContainerWidget(){
						@Override
						public DomElementType getDomElementType() {
							return DomElementType.DomElement_TD;
						}
					};
					td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
					Object modelVal = null;
					if(field.getName().equals("goodName")||field.getName().equals("drawingCode")){
						if(eoKey!=null&&eoKey.getClass().getName().equals("cn.mazu.surface.entity.SurfaceDetail")&&field.getName().equals("goodName")){
							modelVal = ((SurfaceDetail)eoKey).getGeneralElement().getName();
						}else if(eoKey!=null&&eoKey.getClass().getName().equals("cn.mazu.storage.entity.Inventory")&&field.getName().equals("goodName")){
							modelVal = ((Inventory)eoKey).getName();
						}else if(eoKey!=null&&eoKey.getClass().getName().equals("cn.mazu.storage.entity.Inventory")&&field.getName().equals("drawingCode")){
							modelVal = ((Inventory)eoKey).getSpec();
						}else if(eoKey!=null&&eoKey.getClass().getName().equals("cn.mazu.surface.entity.SurfaceDetail")&&field.getName().equals("drawingCode")){
							modelVal = ((SurfaceDetail)eoKey).getDetailcode();
						}
					}else{
						modelVal = df.format(accumulateModelVal(modelList,field));	
					}
					
					if(field.getName().equals("acost")||field.getName().equals("pcost")||field.getName().equals("bcost")){//
						Anchor costAnchor = new Anchor(new Link(),modelVal==null?"":modelVal.toString(),td);
						if(!modelVal.toString().equals(".0")){
							costAnchor.setAttributeValue("style", "color:red;cursor: pointer;text-decoration: underline;");
							costAnchor.clicked().addListener(this, new Signal.Listener() {
								@Override
								public void trigger() {
									PopBox win = new PopBox("单证明细") {
										@Override
										public Widget createFormTbl(String title, PopBox wbx) {
											try {
												//return createSelectDrawingForm(wbx,fdeo);
												return generateDocInfo(wbx,eoKey,field);
											} catch (Exception e) {
												e.printStackTrace();
												return null;
											}
										}
									};
									WOCostMgr.this.bindWidget("win1", win);
									drawGrid();
									WOCostMgr.this.doJavaScript("$('#bctable').datagrid();");
								}
							});
						}
					}else if(field.getName().equals("seqno")){
						Text text = new Text(modelIndex+"",td);
					}else{
						Text text = new Text(modelVal==null?"":modelVal.toString(),td);
					}
					tr.addWidget(td);
					
					if(field.getName().equals("seqno")){
						totalMap.put(field, wocdMap.size()+1+"");
					}else if(field.getName().equals("goodName")){
						totalMap.put(field, tr("summation").toString());
					}else if(field.getName().equals("drawingCode")){
						totalMap.put(field, "--");
					}else{
						totalMap.put(field,df.format(Double.valueOf(totalMap.get(field).toString())+(modelVal==null?0.0:(Double.valueOf(modelVal.toString())))));
					}
				}
				tbody.addWidget(tr);
			 
		}
		//合计
		ContainerWidget hjtr = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TR;
			}
		};
		for (Field field:getDeCls().getDeclaredFields()){
			ContainerWidget hjtd = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TD;
				}
			};
			hjtd.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);
			hjtd.addWidget(new Text(totalMap.get(field)));
			hjtr.addWidget(hjtd);
       }
		tbody.addWidget(hjtr);
		tpl.bindWidget("tbody", tbody);
        tpl.bindString("detailPagi", "");//无分页
   }
	//显示一下！
	@Override
	public Widget generateDetailWidget(Field field,
			Map<Field, FormWidget> fieldWMap, EntityObject deo, EntityObject eo) {
		Text w = new Text();
		//System.out.println("kim");
		w.setTextFormat(TextFormat.XHTMLUnsafeText);
		
		//System.out.println("soo");
		Object fieldVal = WidgetUtil.getValFromProperty(deo, field);
		//System.out.println("ban:"+fieldVal.toString()+"\tquan:"+Util.ban2quan(fieldVal.toString()));
		 w.setText(fieldVal==null?"":Util.ban2quan(fieldVal.toString()));
		return w;
	}
	//按照需要对model中某些字段值进行累加
	private Double accumulateModelVal(List<WOCostDetailModel> modelList,Field modelField){
		Double accumulateResult = 0.0;
		for (WOCostDetailModel costModel:modelList){
			Object propertyVal = WidgetUtil.getValFromProperty(costModel,modelField);
			accumulateResult += Double.valueOf(propertyVal==null?"0.0":propertyVal.toString());
		}
		return accumulateResult;
	}
	private Widget generateDocInfo(final PopBox wbx,EntityObject eoKey,Field field){
		getDocField();
		Template docT = new Template();
		docT.setTemplateText("<div class=\"tcontainer\"><table class='detailtb'>" +
				"${thead}"+
				"${tbody}</table></div>${box_button}", TextFormat.XHTMLUnsafeText);
		Widget trHead = getDetailHead("cn.mazu.doc.entity.SignDoc");
		docT.bindWidget("thead", trHead);
		List<SignDoc> docList = new LinkedList<SignDoc>();
		List<EntityObject> goodsList = new ArrayList<EntityObject>();
		
		if(eoKey.getClass().getName().equals("cn.mazu.surface.entity.SurfaceDetail")){
			SurfaceDetail relatedSD = (SurfaceDetail)eoKey;
			goodsList = getDBEntity().getSubListQBC(Goods.class, new Object[]{"equal","surfaceDetail",relatedSD,"and"}, -1, -1);
		}else if(eoKey!=null&&eoKey.getClass().getName().equals("cn.mazu.storage.entity.Inventory")){
			Inventory inventory = (Inventory)eoKey;
			goodsList = getDBEntity().getSubListQBC(Goods.class, new Object[]{"equal","inventory",inventory,"and"}, -1, -1);
		}
		for (EntityObject geo:goodsList){
			Goods goods = (Goods)geo;
			//System.out.println("fieldname:"+field.getName()+"\tdoctypename:"+goods.getSignDoc().getDocType().getName());
			if(field.getName().equals("acost")&&goods.getSignDoc().getDocType().getName().contains("托工单")||
					field.getName().equals("bcost")&&goods.getSignDoc().getDocType().getName().contains("订购单")||
					field.getName().equals("pcost")&&goods.getSignDoc().getDocType().getName().contains("采购单")){
				docList.add(goods.getSignDoc());
			}else 
				continue;
		}
		List<SignDoc> arrSDList = new ArrayList(docList);
		ContainerWidget tbody = new ContainerWidget(){//模板体
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TBODY;
			}
		};
		
		for (int i=0;i<arrSDList.size();i++){//逐行显示  数据库中查询出的记录
			EntityObject deo = arrSDList.get(i);
			tbody.addWidget(drawDetaiTrContent(null,deo,wbx,"",i,docT));
		}
		
		docT.bindWidget("tbody", tbody);
	    
	    
	    Template box_button = new Template();		
		box_button.setStyleClass("box_button");
		box_button.setTemplateText("${returnbtn}", TextFormat.XHTMLUnsafeText);
	    
	    
	    Anchor returnbtn = new Anchor(new Link(""), "返回");
	    returnbtn.setStyleClass("close");
	    returnbtn.clicked().addListener(this,new Signal.Listener() {
			@Override
			public void trigger() {
				wbx.setAttributeValue("style", "display:none");
			}
		});
	    box_button.bindWidget("returnbtn", returnbtn);
	    docT.bindWidget("box_button", box_button);
	    return docT;
	}
	
	@Override
	public Widget drawDetaiTrContent(EntityObject eo, EntityObject deo,
			PopBox wbx, String title, int i, Template tpl) {
	    ContainerWidget tr = new ContainerWidget(){
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_TR;
			}
		};
		for(int j=0;j<docFieldList.size();j++){//逐列显示
			final Field detailField = docFieldList.get(j);//取到字段field
			ContainerWidget td = new ContainerWidget(){//画一个td
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_TD;
				}
			};
			td.setContentAlignment(AlignmentFlag.AlignCenter, AlignmentFlag.AlignMiddle);//设置td的对齐方式
			td.setAttributeValue("colspan",((DisplayField)detailField.getAnnotation(DisplayField.class)).colspan());
			tr.addWidget(td);
			        Text detailWidget = new Text();
					if(detailField.getName().equals("seqno")){
						detailWidget.setText(getDetailStart()*getDetailMax()+(i+1)+"");
					}else if(detailField.getName().equals("workOrder")){
						Object parentO = WidgetUtil.getValFromProperty(deo, detailField);
						Object kidO = parentO==null?"":WidgetUtil.getValFromProperty((EntityObject)parentO,"orderno");
						detailWidget.setText(kidO+"");
					}else if(detailField.getName().equals("supplier")){
						Object parentO = WidgetUtil.getValFromProperty(deo, detailField);
						Object kidO = parentO==null?"":WidgetUtil.getValFromProperty((EntityObject)parentO,"name");
						detailWidget.setText(kidO+"");
					}else{
						detailWidget.setText(WidgetUtil.getValFromProperty(deo, detailField)==null?"":WidgetUtil.getValFromProperty(deo, detailField).toString());
					}
					td.addWidget(detailWidget);
	    	tr.addWidget(td);
		}
		return tr;
	}
	private void getDocField(){
		docFieldList.clear();
		Field[] docFieldArr = SignDoc.class.getDeclaredFields();
		for (Field f:docFieldArr){
			if(!f.getName().equals("oper")&&!f.getName().equals("goodsList"))
				docFieldList.add(f);
			else
				getDetailHiddenList().add(f);
		}
	}
	
}
