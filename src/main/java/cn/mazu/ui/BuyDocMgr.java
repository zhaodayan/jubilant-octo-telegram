package cn.mazu.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import cn.mazu.WApplication;
import cn.mazu.annotation.DisplayField;
import cn.mazu.annotation.FilterField;
import cn.mazu.doc.entity.Goods;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.doc.mvc.SignDocForm;
import cn.mazu.mysql.Entity;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.surface.entity.Surface;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.DOCType;
import cn.mazu.util.WidgetUtil;
import cn.mazu.utils.WDate;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.webkit.servlet.WebResponse;
import cn.mazu.widget.HeadButton;
import cn.mazu.widget.MiddleTpl;
import cn.mazu.widget.PaginationWidget;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.FormWidget;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.widget.validator.DoubleValidator;

//采购单--包括采购合同、托工合同和订购合同
public class BuyDocMgr extends MiddleTpl {
	/*private Double countVal;
	private double unitpriceVal,unitWeight;*/
	private LineEdit subtotal;
	private Entity dbEntity;
	private SimpleDateFormat sdfslash = new SimpleDateFormat("MM/dd");
	private SimpleDateFormat sdfdot = new SimpleDateFormat("yyyy.MM.dd");
	private SignDoc sd ;
	private Template filterTpl,qryTpl, detailFilterTpl;
	private List<String> filterParamList = new ArrayList<String>();
	private PopBox qryWbx;
	private String qryTitle, qryDename;
	private EntityObject qryEo; 
	private double summation;
	private DecimalFormat df = new DecimalFormat(".##");
	public BuyDocMgr() {
		super("cn.mazu.doc.entity.SignDoc",new Object[]{"like","dataCode","___","and"});
		dbEntity = getDBEntity();
		try {
			getHiddenList().add(getCurCls().getField("proContactor"));
			getHiddenList().add(getCurCls().getField("produceTele"));
			getHiddenList().add(getCurCls().getField("produceFax"));
			getHiddenList().add(getCurCls().getField("proAddr"));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
	}
	
		@Override
		public Widget generateRelevantForm(String title, PopBox p) {
			HeadButton hb = getHeadButton();
			sd = (SignDoc) hb.getDeliveryObj();
	    	return new SignDocForm(title, sd, p, BuyDocMgr.this).draw(getMyParent(), null);
		}
		@Override
		public Widget getSpecialTextWidget(EntityObject val, Field field) {
			Text retText = new Text();
			    if(field.getType().getName().equals("cn.mazu.workorder.entity.WorkOrder"))
			    	retText.setText(WidgetUtil.getValFromProperty(val,"orderno").toString());
			    else if(field.getType().getName().equals("cn.mazu.base.entity.Supplier"))
			    	retText.setText(WidgetUtil.getValFromProperty(val,"name").toString());
		    return retText;
		}
		@Override
		public Widget getDetailHead(String dename) {
			setDetailDisplayField(dename);
			return super.getDetailHead(dename);
		}
		
		//过滤要过滤两个部分：已经入库的goods信息，未入库的图面带过来的信息也要过滤，否则会把已入库的但是被过滤掉的重新生成一条空记录，继而覆盖原有已入库的记录
		@Override
		public void getDetailTblBody(final Template tpl, final PopBox wbx, final String title,
				final EntityObject eo, final String dename) {
			qryTpl = tpl;
			qryWbx = wbx;
			qryTitle = title;
			qryEo = eo;
			qryDename = dename;
			Class dCls;
			setSummation((SignDoc)eo);
			
			try {
				dCls = Class.forName(dename);
				Field[] df_ar = dCls.getFields();
				List<Field> detailAllFieldList = new ArrayList<Field>(Arrays.asList(df_ar));
				detailAllFieldList.removeAll(getDetailFieldList());
				Object[] objDetailArr = new Object[]{"like","dataCode",Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")),"and","","","","",
					"","","","",
					"","","",""};
				for (Field f:detailAllFieldList){
					if(eo.getClass().isAssignableFrom(f.getType())){
						objDetailArr[4] = "equal";
						objDetailArr[5] = f.getName();
						objDetailArr[6] = eo;
						objDetailArr[7] = "and";
						break;
					}
				}
				//添加过滤条件
				for (int j=0;j<filterParamList.size();j++){
					objDetailArr[8+j] = filterParamList.get(j);
				}
				
				List<EntityObject> totalDetailList = dbEntity.getSubListQBC(dCls, objDetailArr, -1, -1);//查询明细类的记录---计算总条数
				
				List<EntityObject> detaildataList = dbEntity.getSubListQBC(dCls, objDetailArr, getDetailStart()*getDetailMax(),getDetailMax());//查询明细类的记录--显示的记录数目
				
				String techStr = ((SignDoc)eo).getTechStr();
				techStr = techStr==null?"":techStr;
				List<String> doctechList =  Arrays.asList(techStr.split(","));
				
				List<EntityObject> overlaps = new ArrayList<EntityObject>();//工艺重叠的图面明细
	            //得到该工单的图面
				Surface surface = null;
				List<EntityObject> surfaceDetailList = new ArrayList<EntityObject>();
				//得到该图面的明细列表
				Object[] surfaceDetailArr = null;
				if(((SignDoc)eo).getWorkOrder()!=null){
					surface = ((SignDoc)eo).getWorkOrder().getSurface();
					surfaceDetailArr = new Object[]{"equal","surface",surface,"and","","","","","","","",""};
				}
				if(filterParamList.size()>0){
					for (int j=0;j<filterParamList.size();j++){
						surfaceDetailArr[4+j] = filterParamList.get(j);
					}
					surfaceDetailArr[5] = "generalElement.name";
					surfaceDetailArr[9] = "detailcode";
				}
				
				
				
				if(surface!=null)surfaceDetailList = dbEntity.getSubListQBC(SurfaceDetail.class,surfaceDetailArr, -1, -1);//图面明细该过滤的也要过滤掉，从源头过滤掉
						
				
				
				//获取重叠工艺的工单图面明细
				for (EntityObject eotity:surfaceDetailList){
					 SurfaceDetail surfaceDetail = (SurfaceDetail)eotity;
					 String detailtech = surfaceDetail.getGeneralElement().getTechnologyStr();
					 List<String> dtechList = Arrays.asList(detailtech.split(","));
					 for (String s:dtechList){
						 if(doctechList.contains(s)){
							 overlaps.add(surfaceDetail);//overlaps是工艺重合的所有图面明细,即要取到的图面明细
							 break;
						 }
					 }
				}
				List<EntityObject> overlapList = new LinkedList<EntityObject>();//工艺重叠的图面明细中已经保存进数据库的货品集合
				//double summation = 0.0;
				for(EntityObject sdl:overlaps){
					SurfaceDetail sd = (SurfaceDetail) sdl;
					for(EntityObject ddl:totalDetailList){
						Goods gd = (Goods) ddl;
						/*summation+=gd.getTotalPrice();
						System.out.println();*/
						if(gd.getSurfaceDetail()!=null&&gd.getSurfaceDetail().getId().equals(sd.getId())){
							overlapList.add(sd);
							break;
						}
					}					
				}
				//tpl.bindString("summation",summation+"");
				overlaps.removeAll(overlapList);//减去已保存到采购明细的货品
				
				int detail_display_count = totalDetailList.size();//该采购单下明细中保存到数据库的所有货品的数量
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
				List<EntityObject> pagiSurfaceDetailList = null; 
				if((getDetailStart()+1)*getDetailMax()>detail_display_count){ //判断保存到数据库中的明细是否查询完毕，
	    			if(Math.floor(detail_display_count/getDetailMax())==getDetailStart()){//交叉
	    				if(overlaps.size()>getDetailMax()-detail_display_count%getDetailMax()){
	    					pagiSurfaceDetailList = overlaps.subList(0, getDetailMax()-detail_display_count%getDetailMax());
	    					createSurfacedetail(tpl, wbx, title, eo, detaildataList,tbody, pagiSurfaceDetailList);
	    				}else{
	    					pagiSurfaceDetailList = overlaps.subList(0, overlaps.size());
	    					createSurfacedetail(tpl, wbx, title, eo, detaildataList,tbody, pagiSurfaceDetailList);
	    					//最后新添加的那一条,货品从库存盘点的录入中选取，旧为手动输入
		    				EntityObject neweo;
		    				try {
		    					neweo = (EntityObject)Class.forName("cn.mazu.doc.entity.Goods").newInstance();
		    					tbody.addWidget(drawDetaiTrContent(eo,neweo,wbx,title,detaildataList.size()+pagiSurfaceDetailList.size(),tpl));
		    				} catch (InstantiationException e) {
		    					e.printStackTrace();
		    				} catch (IllegalAccessException e) {
		    					e.printStackTrace();
		    				} catch (ClassNotFoundException e) {
		    					e.printStackTrace();
		    				}
	    				}	
	    				
	    			}else if(getDetailStart()<Math.ceil((detail_display_count+overlaps.size())/getDetailMax())){//中间页面getDetailStart()<Math.ceil(detail_count/getDetailMax())
	    				int start = getDetailMax()-detail_display_count%getDetailMax()
	    						+(getDetailStart()-1-(int)Math.floor(detail_display_count/getDetailMax()))*getDetailMax();
	    				pagiSurfaceDetailList = overlaps.subList(start, start+getDetailMax());
	    				createSurfacedetail(tpl, wbx, title, eo, detaildataList,tbody, pagiSurfaceDetailList);
	    			}else{//末页
	    				int start = getDetailMax()-detail_display_count%getDetailMax()
	    						+(getDetailStart()-1-(int)Math.floor(detail_display_count/getDetailMax()))*getDetailMax();
	    				pagiSurfaceDetailList = overlaps.subList(start,overlaps.size());
	    				
	    				createSurfacedetail(tpl, wbx, title, eo, detaildataList,tbody, pagiSurfaceDetailList);
	    				//最后新添加的那一条,货品从库存盘点的录入中选取，旧为手动输入
	    				EntityObject neweo;
	    				try {
	    					neweo = (EntityObject)Class.forName("cn.mazu.doc.entity.Goods").newInstance();
	    					tbody.addWidget(drawDetaiTrContent(eo,neweo,wbx,title,detaildataList.size()+pagiSurfaceDetailList.size(),tpl));
	    				} catch (InstantiationException e) {
	    					e.printStackTrace();
	    				} catch (IllegalAccessException e) {
	    					e.printStackTrace();
	    				} catch (ClassNotFoundException e) {
	    					e.printStackTrace();
	    				}
	    			}    			
	    		}
				
				tpl.bindWidget("tbody", tbody);		
				
				PaginationWidget pagi = new PaginationWidget(detail_display_count+overlaps.size()+1, getDetailStart(),getDetailMax()){
					@Override
					protected void selectPage(int page_id) {
						super.selectPage(page_id);
						setDetailStart(page_id);//js的原因，表头也得重新画一次，优化？
						tpl.bindWidget("thead", getDetailHead(dename));
						getDetailTblBody(tpl,wbx,title,eo,dename);
					}
				};
				tpl.bindWidget("detailPagi", pagi);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void createSurfacedetail(final Template tpl, final PopBox wbx,
				final String title, final EntityObject eo,
				List<EntityObject> detaildataList, ContainerWidget tbody,
				List<EntityObject> pagiSurfaceDetailList) {
			for (int k=0;k<pagiSurfaceDetailList.size();k++){
				SurfaceDetail detaileo = (SurfaceDetail) pagiSurfaceDetailList.get(k);
				tbody.addWidget(drawDetaiTrContent(eo,detaileo,wbx,title,detaildataList.size()+k,tpl));
			}
		}
		
		  //根据图面明细生成的记录
		
		@Override
		public Widget generateDetailWidget(Field field,final Map<Field,FormWidget> fMap,
				final EntityObject deo,EntityObject eo){
			Object fieldVal = null;
			//若是图面明细
			if(deo.getClass().getSimpleName().equals("SurfaceDetail")){
				if(field.getName().equals("goodsName"))
					fieldVal = ((SurfaceDetail)deo).getGeneralElement().getName();
				else if(field.getName().equals("amount"))
					fieldVal =  ((SurfaceDetail)deo).getAmount();
				else if(field.getName().equals("drawingcode")){
					fieldVal = deo;
				}
            //正常数据库中查询来的,以前用这个是因为有手动录入的 名称，现在名称来源两部分：图面带来的+库存录入的，所以不会手动填写的				
			}else{
				String getName = "get"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
				try {
					Method m = deo.getClass().getMethod(getName);
					fieldVal = m.invoke(deo);
				}catch (Exception e) {
					e.printStackTrace(); 
				}
					
			}
			
			//小计
			FormWidget w = null;
			
	    	if(field.getName().equals("goodsName")){//品名
	    		if(fieldVal!=null){
	    			w = new LineEdit();
	    			((LineEdit)w).setText(fieldVal.toString());
	    		}else{
	    			List<EntityObject> inventoryList = dbEntity.getSubListQBC(Inventory.class, new Object[]{"like","dataCode","___","and"}, -1, -1);
	    		    w = WidgetUtil.initSelFromList(inventoryList, "name,spec");
	    		}
	    		fMap.put(field, w);
	    	}else if(field.getName().equals("remark")||field.getName().equals("material")){//备注、材质
	    		w = new LineEdit();
	    		((LineEdit)w).setText(fieldVal!=null?fieldVal.toString():"");
	    		fMap.put(field, w);
	    	}else if(field.getName().equals("amount")){//数量，数量变化会引起总价变化或者总重变化
	    		LineEdit amtInput = new LineEdit();
	    		addRestriction(amtInput);
	    		amtInput.setText(fieldVal==null?"0.0":fieldVal.toString());
	    		w = amtInput;
	    		fMap.put(field, w);
	    		Map<String,String> operatorMap = new HashMap<String,String>();
	    		List<String> resetFieldList = new ArrayList<String>();
	    		if(sd.getDocType().getName().equals(DOCType.PURCHASE.name)){
	    			operatorMap.put("unitWeight", "*");
		    		operatorMap.put("unitprice", "*");
		    		operatorMap.put("processCost", "+");
		    		resetFieldList.add("totalWeight");
		    		resetFieldList.add("totalPrice");
	    		}/*else if(!sd.getDocType().getName().equals(DOCType.KGMAUTHORIZE.name)){//按照公斤材质算法的托工单【数量】不纳入总价的计算公式
	    			operatorMap.put("unitprice", "*");
	    			resetFieldList.add("totalPrice");
	    		}*/
	    		
	    		bindValueChangeEvent(field.getName(),amtInput,operatorMap,fMap,resetFieldList);
	    	}else if(field.getName().equals("unitprice")){//单价,单价变化会引起总价变化
	    		LineEdit upInput = new LineEdit();
	    		addRestriction(upInput);
	    		upInput.setText(fieldVal==null?"0.0":fieldVal.toString());
	    		w = upInput;
	    		fMap.put(field, w);
	    		Map<String,String> operatorMap = new HashMap<String,String>();
	    		List<String> resetFieldList = new ArrayList<String>();
	    		if(sd.getDocType().getName().equals(DOCType.PURCHASE.name)){
	    			operatorMap.put("totalWeight", "*");
		    		operatorMap.put("processCost", "+");
	    		}else if(sd.getDocType().getName().equals(DOCType.KGMAUTHORIZE.name)){
	    			operatorMap.put("weight", "*");
	    		}else{
	    			operatorMap.put("amount", "*");
	    		}
	    		resetFieldList.add("totalPrice");
	    		bindValueChangeEvent(field.getName(),upInput,operatorMap,fMap,resetFieldList);
	    	}else if(field.getName().equals("weight")){//重量,重量发生变化会引起总价变化，只有按照公斤材质算法的托工单有【重量】，所以总价=重量*单价
	    		LineEdit upInput = new LineEdit();
	    		addRestriction(upInput);
	    		upInput.setText(fieldVal==null?"0.0":fieldVal.toString());
	    		w = upInput;
	    		fMap.put(field, w);
	    		Map<String,String> operatorMap = new HashMap<String,String>();
	    		operatorMap.put("unitprice", "*");
	    		List<String> resetFieldList = new ArrayList<String>();
	    		resetFieldList.add("totalPrice");
	    		bindValueChangeEvent(field.getName(),upInput,operatorMap,fMap,resetFieldList);
	    	}else if(field.getName().equals("drawingcode")){
	    		LineEdit le = new LineEdit();
	    		if(fieldVal!=null&&SurfaceDetail.class.isAssignableFrom(fieldVal.getClass())){
	    			String detialcode = ((SurfaceDetail)fieldVal).getDetailcode();
	    			le.setText(detialcode.equals("")?"无图号":detialcode);
	    			le.setReadOnly(true);
	    		}else if(fieldVal!=null){
	    			le.setText(fieldVal.toString());
	    		}
	    		w = le;
	    		fMap.put(field, w);
	    	}else if(field.getName().equals("totalPrice")){//总价
	    		subtotal = new LineEdit();
	    		subtotal.setReadOnly(true);
	    		subtotal.setAttributeValue("style","width:30px;");
	    		subtotal.setText(fieldVal==null?"0.0":df.format(Double.valueOf(fieldVal.toString())));//总价进行格式化
	    		w = subtotal;
	    		fMap.put(field, w);
	    	}else if(field.getName().equals("deliverDate")){
	    		DateEdit d = new DateEdit();
				d.setFormat("yyyy-MM-dd");
				d.setDate(fieldVal==null?new WDate(new Date()):new WDate((Date)fieldVal));
				d.setAttributeValue("style", "width:105px;");
				w = d;
				fMap.put(field, w);
	    	}else if(field.getName().equals("size")){
	    		LineEdit le = new LineEdit();
	    		le.setText(fieldVal==null?"":fieldVal.toString());
	    		w = le;
	    		fMap.put(field, w);
	    	}else if(field.getName().equals("unitWeight")){//单重，单重变化会引起总重变化
	    		LineEdit le = new LineEdit();
	    		addRestriction(le);
	    		le.setText(fieldVal==null?"0.0":fieldVal.toString());
	    		w = le;
	    		fMap.put(field, w);
	    		
	    		Map<String,String> operatorMap = new HashMap<String,String>();
	    		operatorMap.put("amount", "*");
	    		operatorMap.put("unitprice", "*");
	    		operatorMap.put("processCost", "+");
	    		List<String> resetFieldList = new ArrayList<String>();
	    		resetFieldList.add("totalPrice");
	    		resetFieldList.add("totalWeight");
	    		bindValueChangeEvent(field.getName(),le,operatorMap,fMap,resetFieldList);
	    	}else if(field.getName().equals("totalWeight")){//总重，总重由单重和数量决定，只读
	    	   LineEdit le = new LineEdit();
	    	   le.setText(fieldVal==null?"0.0":fieldVal.toString());
	    	   le.setReadOnly(true);
	    	   w = le;
	    	   fMap.put(field, w);
	    	}else if(field.getName().equals("processCost")){
	    		LineEdit le = new LineEdit();
	    		addRestriction(le);
	    		le.setText(fieldVal==null?"0.0":fieldVal.toString());
	    		w = le;
	    		fMap.put(field, w);

	    		Map<String,String> operatorMap = new HashMap<String,String>();
	    		operatorMap.put("amount", "*");
	    		operatorMap.put("unitprice", "*");
	    		operatorMap.put("unitWeight", "*");
	    		List<String> resetFieldList = new ArrayList<String>();
	    		resetFieldList.add("totalPrice");
	    		bindValueChangeEvent(field.getName(),le,operatorMap,fMap,resetFieldList);
	    	}
	    	return w;
		}
		
		@Override
		public boolean setPropertyFromWidget(EntityObject deo,Map<Field,FormWidget> fieldWMap,EntityObject eo) {
			try {
				if(deo.getClass().getSimpleName().equals("SurfaceDetail")){//如果是图面明细带过来的，则把他反向设置到Goods的新实例上。
                    SurfaceDetail tempDetail = (SurfaceDetail)deo;
					deo = (EntityObject) Class.forName("cn.mazu.doc.entity.Goods").newInstance();
					Method setm = deo.getClass().getMethod("setSurfaceDetail", SurfaceDetail.class);
					setm.invoke(deo, tempDetail);
				}
					
				
				for (Iterator iterator = fieldWMap.entrySet().iterator();iterator.hasNext();){
					 Map.Entry<Field,FormWidget> fw = (Entry<Field, FormWidget>) iterator.next();
					 Field setField = fw.getKey();
					 Object setWidget = fw.getValue();
					 String setNative = "set"+setField.getName().substring(0, 1).toUpperCase()+setField.getName().substring(1);
					 Method m = deo.getClass().getMethod(setNative, setField.getType());
					 //名称另外处理
					 if(ComboBox.class.isAssignableFrom(setWidget.getClass())){
						 m.invoke(deo, WidgetUtil.getSelDisplayStr((ComboBox)setWidget));
						 //为了统计用，设置一下Inventory
						 deo.getClass().getMethod("setInventory", Inventory.class)
						               .invoke(deo, (EntityObject)WidgetUtil.getSelEO((ComboBox)setWidget));
					 }else if(setField.getType().getName().equals("java.lang.String")){
						 m.invoke(deo, ((LineEdit)setWidget).getText());
					 }else if(setField.getType().getName().equals("java.lang.Double")){
						 Double d = Double.valueOf(((LineEdit)setWidget).getText());
						 m.invoke(deo, d);
					 }else if(setField.getType().getName().equals("java.lang.Integer")){
						 Integer d = Integer.valueOf(((LineEdit)setWidget).getText());
						 m.invoke(deo, d);
					 }else if(setField.getType().getName().equals("java.util.Date")){
						 WDate wDate = ((DateEdit)setWidget).getDate();
						 m.invoke(deo, wDate.getDate());
					 }
				}
			    String setObj = "set"+eo.getClass().getSimpleName().substring(0,1).toUpperCase()+eo.getClass().getSimpleName().substring(1);
			    Method setObjM = deo.getClass().getMethod(setObj, eo.getClass());
			    setObjM.invoke(deo, eo);
			    return dbEntity.saveOrUpdate(deo);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return false;
		}
		
		@Override
		public boolean validatePassed(Map fMap,EntityObject deo){
			boolean retValue = true;
			try {
				for (Iterator iterator = fMap.entrySet().iterator(); iterator
						.hasNext();) {
					Map.Entry<Field, FormWidget> fw = (Entry<Field, FormWidget>) iterator
							.next();
					Field setField = fw.getKey();
					Widget setWidget = fw.getValue();
					//要么从图面带过来，要么从库存盘点的下拉框中选出来,图面明细带过来的不需要验证，库存盘点下拉+已经入库的需要验证
					if ((setField.getName().equals("goodsName")
							&&!deo.getClass().getSimpleName().equals("SurfaceDetail"))||setField.getName().equals("size")) {//品名、尺寸
						Text vinfo = new Text();
						vinfo.setStyleClass("span");
						//vinfo.setAttributeValue("style", "color:red");
						ContainerWidget cww = new ContainerWidget(){
							@Override
							public DomElementType getDomElementType() {
							   return DomElementType.DomElement_DIV;
							}
						};
						cww.setStyleClass("nulltxttip");
						//cww.addWidget(vinfo);
						ContainerWidget cw = null;
						Widget previousInfo = null;
						if(setWidget.getClass().getSimpleName().equals("LineEdit")){//数据库中带来
							cw = (ContainerWidget)((LineEdit) setWidget).getParent();
							if(cw.getChildren().size()-1!=cw.getIndexOf(((Widget)setWidget))){
								previousInfo = cw.getWidget(cw.getIndexOf(setWidget)+1);
								cw.removeWidget(previousInfo);
							}
							
							if(((LineEdit)setWidget).getText().trim().equals("")||((LineEdit) setWidget).getText()==null){//备注可以为不填
								vinfo.setText(tr("inputmust"));
								cww.addWidget(vinfo);
								//cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, vinfo);
								cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, cww);
								retValue = false;
							}	
						}else if(setWidget.getClass().getSimpleName().equals("ComboBox")){//库存带来
							cw = (ContainerWidget)((ComboBox) setWidget).getParent();
							if(cw.getChildren().size()-1!=cw.getIndexOf(((Widget)setWidget))){
								previousInfo = cw.getWidget(cw.getIndexOf(setWidget)+1);
								cw.removeWidget(previousInfo);
							}
							if(WidgetUtil.getSelEO((ComboBox) setWidget)==null){
								vinfo.setText(tr("inputmust"));
								cww.addWidget(vinfo);
								//cw.insertWidget(cw.getIndexOf(((ComboBox) setWidget))+1, vinfo);
								cw.insertWidget(cw.getIndexOf(((ComboBox) setWidget))+1, cww);
								retValue = false;
							}
						}
					}else if(setField.getName().equals("amount")||setField.getName().equals("unitprice")
				            ||setField.getName().equals("unitWeight")/*||setField.getName().equals("processCost")*/){//数量、单价、单重，//加工费,所给样例中加工费可以为0.0，故加工费不做要求
						Text vinfo = new Text();
						vinfo.setStyleClass("span");
						ContainerWidget cww = new ContainerWidget(){
							@Override
							public DomElementType getDomElementType() {
							   return DomElementType.DomElement_DIV;
							}
						};
						cww.setStyleClass("nulltxttip");
						
						//vinfo.setAttributeValue("style", "color:red");
						ContainerWidget cw = (ContainerWidget)((LineEdit) setWidget).getParent();
						if(cw.getChildren().size()-1!=cw.getIndexOf(((LineEdit) setWidget))){
							Widget previousInfo = cw.getWidget(cw.getIndexOf(((LineEdit) setWidget))+1);
							cw.removeWidget(previousInfo);
						}
						Double d = Double.valueOf(((LineEdit) setWidget).getText().equals("")?"0.0":((LineEdit) setWidget).getText());
						if(d==0){//为空
							vinfo.setText(tr("inputmust"));
							cww.addWidget(vinfo);
							cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, cww);
							retValue = false;
						}else if(d<Double.MIN_VALUE){
							vinfo.setText(tr("too small"));
							cww.addWidget(vinfo);
							cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, cww);
							retValue = false;
						}else if(d>Double.MAX_VALUE){
							vinfo.setText(tr("too large"));
							cww.addWidget(vinfo);
							cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, cww);
							retValue = false;
						}
						//cw.insertWidget(cw.getIndexOf(((LineEdit) setWidget))+1, vinfo);
						
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retValue;
		}
		
		@Override
		public Object exportCustomDoc(WebResponse resp) {
			filterParamList.clear();
			sd = (SignDoc) getHeadButton().getDeliveryObj();
			setSummation(sd);
			String dename = "cn.mazu.doc.entity.Goods";
			setDetailDisplayField(dename);
			//getDetailHead(dename);

			List<EntityObject> goodsList = dbEntity.getSubListQBC(Goods.class, new Object[]{"equal","signDoc",sd,"and"}, -1, -1);
			FileOutputStream fos = null;
			XWPFDocument doc = new XWPFDocument();
			Util.deleteFiles(tr("base.filepath")+"/"+sd.getId(), "*");
			String fileUrl = null;
			try {
				fileUrl = sd.getOrderno()+"-"+Util.formatDateyyyyMMdd(new Date())+".docx";
				fos = new FileOutputStream(tr("base.filepath")+"/"+fileUrl);
				
				XWPFParagraph titleP = doc.createParagraph();
				titleP.setAlignment(ParagraphAlignment.CENTER);
				XWPFRun titleRun = titleP.createRun();
				titleRun.setFontSize(18);
				titleRun.setText(tr("corptitle").toString());
				titleRun.setBold(true);
				
				
				XWPFParagraph doctypeP = doc.createParagraph();
				doctypeP.setAlignment(ParagraphAlignment.CENTER);
				XWPFRun doctypeRun = doctypeP.createRun();
				doctypeRun.setFontSize(18);
				doctypeRun.setText(sd.getDocType().name);
				doctypeRun.setBold(true);
				
				
				
				XWPFParagraph p1 = doc.createParagraph();
				XWPFRun addressRun = p1.createRun();
				addressRun.setText("地址："+tr("corpaddr").toString());
				XWPFRun spaceR1 = p1.createRun();
				spaceR1.setText("                    ");
				XWPFRun ordernoRun = p1.createRun();
				ordernoRun.setText("订单号码："+sd.getOrderno());
				
				XWPFParagraph p2 = doc.createParagraph();
				XWPFRun linkrun1 = p2.createRun();
				linkrun1.setText("联系人："+sd.getProContactor());
				p2.createRun().setText("                    ");
				XWPFRun orderdr = p2.createRun();
				if(sd.getOrderDate()!=null)
					orderdr.setText("订单日期："+sdfdot.format(sd.getOrderDate()));
				else
					orderdr.setText("订单日期："+"");
				
				XWPFParagraph p3 = doc.createParagraph();
				XWPFRun jiatel = p3.createRun();
				jiatel.setText("电话："+sd.getProduceTele());
				p3.createRun().setText("     ");
				XWPFRun jiafax = p3.createRun();
				jiafax.setText("传真："+sd.getProduceFax());
				p3.createRun().setText("     ");
				XWPFRun worder = p3.createRun();
				if(sd.getWorkOrder()!=null)
					worder.setText("工单号："+sd.getWorkOrder().getOrderno());
				else 	worder.setText("工单号："+"");
				
				XWPFParagraph p4 = doc.createParagraph();
				XWPFRun supname = p4.createRun();
				if(sd.getSupplier()!=null)	supname.setText("供方名称："+sd.getSupplier().getName());
				else supname.setText("供方名称："+"");
				
				XWPFParagraph p5 = doc.createParagraph();
				XWPFRun yilink = p5.createRun();
				yilink.setText("联系人："+sd.getSupContactor());
				
				XWPFParagraph p6 = doc.createParagraph();
				XWPFRun yitel = p6.createRun();
				yitel.setText("电话："+sd.getSupTele());
				p6.createRun().setText("                    ");
				XWPFRun yifax = p6.createRun();
				yifax.setText("传真："+sd.getSupFax());
				
				XWPFParagraph descP = doc.createParagraph();
				XWPFRun headtitle = descP.createRun(); 
				headtitle.setText(tr("goodstitle").toString());
				
				
				
				Field[] fieldarr = Goods.class.getDeclaredFields();
				
				List<Field> collist = new ArrayList<Field>();
				for (Field field : fieldarr){
					DisplayField colanno = field.getAnnotation(DisplayField.class);
					if(colanno!=null&&colanno.exportflag()&&!getDetailHiddenList().contains(field))
						collist.add(field);
					
				}
				
				XWPFTable detailTbl = doc.createTable(goodsList.size()+1,collist.size());
				
				List<XWPFTableRow> rows = detailTbl.getRows();
				for (XWPFTableRow row : rows) {
					//set height,work?
					row.setHeight(100);
					int rownum = rows.indexOf(row);
					List<XWPFTableCell> cells = row.getTableCells();
					for (XWPFTableCell cell : cells) {
						cell.setVerticalAlignment(XWPFVertAlign.BOTTOM);
						int colnum = cells.indexOf(cell);
						XWPFParagraph para = cell.getParagraphs().get(0);
						XWPFRun rh = para.createRun();
						if(rownum==0){
							rh.setText(collist.get(colnum).getAnnotation(DisplayField.class).cname());
						}else if(colnum==0){
							rh.setText(rownum+"");
						}else{
							String getname = "get"+collist.get(colnum).getName().substring(0, 1).toUpperCase()+collist.get(colnum).getName().substring(1);
							Method methodname = Goods.class.getDeclaredMethod(getname);
							Object val = methodname.invoke(((Goods)goodsList.get(rownum-1)));
							//System.out.println("val:"+val+"\tfieldname:"+collist.get(colnum).getName());
							//很奇怪的事，刚保存的那条，时间记录居然是java.util.Date,之前保存的记录是java.sql.Date
							if(val.getClass().getName().equals("java.sql.Date")||val.getClass().getName().equals("java.util.Date")){
								rh.setText(sdfslash.format((Date)val));
								}
							else
							    rh.setText(val.toString());
						}
					}
				}
				
				/*for (int i=1;i<15;i++){
					XWPFParagraph remarkP = doc.createParagraph();
					XWPFRun remarkR = remarkP.createRun();
					remarkR.setText(tr("remark"+i).toString());
				}*/
				
				XWPFParagraph summationP = doc.createParagraph();
				XWPFRun summationR = summationP.createRun();
				summationR.setText("总价：                "+df.format(summation));
				
				XWPFParagraph remarkP = doc.createParagraph();
				XWPFRun remarkR = remarkP.createRun();
				remarkR.setText(tr("authorizermk").toString());
				
				XWPFParagraph nameP = doc.createParagraph();
				XWPFRun jianameR = nameP.createRun();
				jianameR.setText(tr("corptitle").toString());
				jianameR.setFontSize(15);
				
				XWPFRun spaceR = nameP.createRun();
				spaceR.setText("             ");
				
				XWPFRun yinameR = nameP.createRun();
				yinameR.setText("有限公司");
				yinameR.setFontSize(15);
			
				
				XWPFParagraph signP = doc.createParagraph();
				XWPFRun jiasignR = signP.createRun();
				jiasignR.setText("签名（盖章）：");
				
				XWPFRun signspaceP = signP.createRun();
				signspaceP.setText("               ");
				
				XWPFRun yisignR = signP.createRun();
				yisignR.setText("签名（盖章）：");
				resp.setContentType("application/msword");
				resp.addHeader("Content-Disposition", "attachment;filename="+fileUrl);
				doc.write(resp.getOutputStream());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return tr("www.file")+"/"+fileUrl;
		}
		//单价、数量、单重，加工费变化引发变化,变动条件和影响条件要匹配好
		//operateParam<String1,String2> String1为字段名称，String2为字段操作符,构建时将二级乘除运算写在一级加减运算前面 
		private void bindValueChangeEvent(final String changeFieldName,
				final LineEdit le,final Map<String,String> operateParam,
				final Map<Field,FormWidget> fieldWidgetMap,final List<String> resetFieldList){
			le.changed().addListener(this,new Signal.Listener() {
				@Override
				public void trigger() {
					resetValueTriggerChange(changeFieldName,le,operateParam,fieldWidgetMap,resetFieldList);
					
				}
			});
		}
		private void resetValueTriggerChange(String changeFieldName,
				LineEdit le,Map<String,String> operateParam,
				Map<Field,FormWidget> fieldWidgetMap,List<String> resetFieldList){
			try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine se = manager.getEngineByName("js");
			//得到当前变化的值
			Double changeVal = Double.valueOf(le.getText().trim());
			//找到要重新设值得组件,总价+？
			FormWidget totalWeightW = null,totalPriceW = null,unitWeightW = null,amoutW = null;
			
			int searchToken = 0;//总价是肯定要变的,若重设字段中有总重,则总重也是要变的,单重和数量是影响总重的两个变量,所以要把组件取出来
			for(Iterator iterator = fieldWidgetMap.entrySet().iterator();iterator.hasNext();){
				Entry<Field,FormWidget> entry = (Entry<Field, FormWidget>)iterator.next();
					if(resetFieldList.contains(entry.getKey().getName())&&entry.getKey().getName().equals("totalWeight")){//总重
						totalWeightW = entry.getValue();
						searchToken++;
					}else if(resetFieldList.contains(entry.getKey().getName())&&entry.getKey().getName().equals("totalPrice")){//总价
						totalPriceW = entry.getValue();
						searchToken++;
				    }else if(entry.getKey().getName().equals("unitWeight")){//单重
				    	unitWeightW = entry.getValue();
				    	searchToken++;
				    }else if(entry.getKey().getName().equals("amount")){//数量
				    	amoutW = entry.getValue();
				    	searchToken++;
				    }
					if(searchToken==4)
						break;
			}
			
				Double totalPrice = 0.0;
				String opertorStr = "1.0";//乘法在前，所以初始为1.0 ,单重变化
				if(changeFieldName.equals("unitWeight")){//单重--->单重变则总重变
					totalWeightW.setValueText(changeVal*Double.valueOf(amoutW.getValueText())+"");
				}else if(changeFieldName.equals("amount")&&resetFieldList.contains("totalWeight")){//数量--->数量变且重设字段中由总重,则总重变;对数量变引起的总价变见下
					totalWeightW.setValueText(changeVal*Double.valueOf(unitWeightW.getValueText())+"");
				}
				//拼接操作map中传来各个参数成字符串,字符串形式为： 操作符+操作数值
					for(Iterator iterator = operateParam.entrySet().iterator();iterator.hasNext();){
						Entry<String,String> entry = (Entry<String, String>)iterator.next();
						String operatorFieldName = entry.getKey();//操作字段
						String operator = entry.getValue().toString();//操作符
						Double operatorVal = 1.0;//操作数值
						//获取该字段对应的组件--->从组件中拿到操作数
						for(Iterator iterator2 = fieldWidgetMap.entrySet().iterator();iterator2.hasNext();){
							Entry<Field,FormWidget> entry2 = (Entry<Field, FormWidget>)iterator2.next();
							if(entry2.getKey().getName().equals(operatorFieldName)){
								operatorVal = Double.valueOf(entry2.getValue().getValueText());
								break;}
						}
						opertorStr +=operator+operatorVal;
					}
					//若整个字符串中出现了 +操作数 的字符串，若出现在中间要人为地将其移动到字符串的最末端，保证运算的正确顺序。
					int addIndex = opertorStr.indexOf("+");
					if(addIndex>0){
						int multipleIndex = opertorStr.substring(addIndex).indexOf("*");
						String addStr = "";
						if(multipleIndex>0){
							addStr = opertorStr.substring(addIndex, addIndex+multipleIndex);
							opertorStr = opertorStr.replace(addStr, "");
							opertorStr+=addStr;
						}
					}
					//变化的字段，若是加工费，则直接加上即可
					if(changeFieldName.equals("processCost")){//加工费是用加的
						opertorStr += "+"+changeVal;
					}else {//若是加工费以外的乘法字段，同上，要先取出已经有的加法串，将其移动到字符串末尾
						int addIndexStart = opertorStr.indexOf("+");
						if(addIndexStart>0){
							int addIndexEnd = opertorStr.substring(addIndexStart).indexOf("*");
							String addStr = "";
							if(addIndexEnd>0){
								addStr = opertorStr.substring(addIndexStart,addIndexEnd);
							}else{
								addStr = opertorStr.substring(addIndexStart);
							}
							opertorStr = opertorStr.replace(addStr, "");
							opertorStr += "*"+changeVal+addStr;
						}else
							opertorStr += "*"+changeVal;
					}
					//计算总价，重新设定总价输入框的值
					totalPrice = (Double)se.eval(opertorStr);
					//System.out.println("totalPriceW:"+totalPriceW);
					if(totalPriceW!=null)//公斤材质算法托工单中，数量发生变化的时候，总价是不发生变化的，【数量】不是变量，此乃特殊情况
					  totalPriceW.setValueText(totalPrice+"");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//添加输入限制，全角时不起作用，只能验证半角条件下输入
		private void addRestriction(FormWidget fw){
			DoubleValidator dv = new DoubleValidator();
    		dv.setMandatory(true);
    		fw.setValidator(dv);
    		fw.setAttributeValue("style","width:30px;");
		}
		@Override
		public Widget drawDetailFilterForm() {
				String filterText = "<ul class='form_ul' style='margin-bottom: 0px;'>";
				detailFilterTpl = new Template();
				Field[] fields = getDeCls().getDeclaredFields();
				final List<Field> filterFieldList = new ArrayList<Field>();
				for (Field field:fields){
					FilterField ff = field.getAnnotation(FilterField.class);
					if(ff!=null){
						filterFieldList.add(field);	
						filterText += "<li><label>${"+ff.fname()+"}</label>"+"${"+ff.fname()+"inputVal}"+"</li>";
					}
				}
				
				filterText+="<div style='clear:both;'></div></ul>";
				filterText+="<div class='butbox'>${queryBtn}${resetBtn}</div>";
				detailFilterTpl.setTemplateText(filterText, TextFormat.XHTMLUnsafeText);
				for (Field filterField:filterFieldList){
					LineEdit le = new LineEdit("");
					FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
					detailFilterTpl.bindString(ff.fname(),ff.fname());
					detailFilterTpl.bindWidget(ff.fname()+"inputVal", le);
				}
				PushButton resetBtn = new PushButton(tr("reset"));
				PushButton queryBtn = new PushButton(tr("query"));
				resetBtn.setStyleClass("btn-warning");
				queryBtn.setStyleClass("editcls");

				queryBtn.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						filterParamList.clear();
						for (Field filterField:filterFieldList){
							FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
							LineEdit inputLe = (LineEdit)detailFilterTpl.resolveWidget(ff.fname()+"inputVal");
							filterParamList.add("like");
							filterParamList.add(ff.o2fname());
							filterParamList.add("%"+inputLe.getText()+"%");
							filterParamList.add("and");
						}
						//qryTpl.bindWidget("tbody", );
						qryTpl.bindWidget("thead", getDetailHead(qryDename));
						getDetailTblBody(qryTpl,qryWbx,qryTitle,qryEo,qryDename);
						qryTpl.bindWidget("summation", getSummation());
					}
				});
				resetBtn.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						for (Field filterField:filterFieldList){
							FilterField ff = (FilterField)filterField.getAnnotation(FilterField.class);
							LineEdit inputLe = (LineEdit)detailFilterTpl.resolveWidget(ff.fname()+"inputVal");
							inputLe.setText("");
						}
						
					}
				});
				detailFilterTpl.bindWidget("resetBtn", resetBtn);
				detailFilterTpl.bindWidget("queryBtn", queryBtn);
				return detailFilterTpl;
		}
		
		@Override
		public Widget getSummation() {
			//System.out.println("qryEo.getId():"+qryEo.getId());
			setSummation((SignDoc)qryEo);
			return new Text("总价：                "+df.format(summation));
		}
		//设置总价,总价有两个来源，一个是图面明细一个是库存
		private void setSummation(SignDoc sd){
			summation = 0.0;	
			//List<Goods> goodsList = ((SignDoc)sd).getGoodsList();//刚保存进去的记录刷不出来
			List<EntityObject> goodsList = getDBEntity().getGoodsListBySD(sd);
			for (EntityObject eo:goodsList){
				 Goods good = (Goods)eo;
				if(filterParamList.size()>0&&good.getGoodsName().contains(filterParamList.get(2).replace("%", ""))&&good.getDrawingcode().contains(filterParamList.get(6).replace("%", ""))){
					summation+=good.getTotalPrice();
				}else if(filterParamList.size()==0){
					summation+=good.getTotalPrice();				
				}
			}
		}
		//根据选中的记录，判断明细表隐藏哪些字段
		private void setDetailDisplayField(String dename){
			sd = (SignDoc) getHeadButton().getDeliveryObj();
			getDetailHiddenList().clear();
			try {
				if(sd!=null&&sd.getDocType().getName().equals(DOCType.BOOKING.name)){
					getDetailHiddenList().add(Class.forName(dename).getField("unitWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("totalWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("processCost"));
					getDetailHiddenList().add(Class.forName(dename).getField("weight"));
					getDetailHiddenList().add(Class.forName(dename).getField("material"));
					getDetailHiddenList().add(Class.forName(dename).getField("drawingcode"));
				}else if(sd!=null&&sd.getDocType().getName().equals(DOCType.PURCHASE.name)){//下料采购
					getDetailHiddenList().add(Class.forName(dename).getField("weight"));
				}else if(sd!=null&&sd.getDocType().getName().equals(DOCType.GPURCHASE.name)){//一般采购
					getDetailHiddenList().add(Class.forName(dename).getField("size"));
					getDetailHiddenList().add(Class.forName(dename).getField("material"));
					getDetailHiddenList().add(Class.forName(dename).getField("unitWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("totalWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("processCost"));
					getDetailHiddenList().add(Class.forName(dename).getField("weight"));
				}else if(sd!=null&&sd.getDocType().getName().equals(DOCType.AUTHORIZE.name)){//面积算法托工单
					getDetailHiddenList().add(Class.forName(dename).getField("unitWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("totalWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("processCost"));
					getDetailHiddenList().add(Class.forName(dename).getField("material"));
					getDetailHiddenList().add(Class.forName(dename).getField("weight"));
				}else if(sd!=null&&sd.getDocType().getName().equals(DOCType.GAUTHORIZE.name)){//一般托工单
					getDetailHiddenList().add(Class.forName(dename).getField("unitWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("totalWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("processCost"));
					getDetailHiddenList().add(Class.forName(dename).getField("material"));
					getDetailHiddenList().add(Class.forName(dename).getField("size"));
					getDetailHiddenList().add(Class.forName(dename).getField("weight"));
				}else if(sd!=null&&sd.getDocType().getName().equals(DOCType.KGMAUTHORIZE.name)){//公斤材质托工单
					getDetailHiddenList().add(Class.forName(dename).getField("unitWeight"));
					getDetailHiddenList().add(Class.forName(dename).getField("processCost"));
					getDetailHiddenList().add(Class.forName(dename).getField("size"));
					getDetailHiddenList().add(Class.forName(dename).getField("totalWeight"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
			
}
