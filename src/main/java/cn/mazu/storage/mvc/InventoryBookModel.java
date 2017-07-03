package cn.mazu.storage.mvc;

import java.text.SimpleDateFormat;

import cn.mazu.WApplication;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.mysql.Entity;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.storage.entity.InventoryBook;
import cn.mazu.util.Util;
import cn.mazu.util.Util.IBookType;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DateValidator;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.widget.validator.Validator;
import cn.mazu.workorder.entity.WorkOrder;

public class InventoryBookModel extends FormModel{
   public static final String itemname = "name-field";//货品名称
   public static final String itemspec = "spec-field";//货品规格
   public static final String itemcount = "count-field";//数量
   public static final String bookdate = "bdate-field";//记录日期
   public static final String eid = "id-field";
   public static final String itemcode = "code-field";//货品编码
   public static final String inventory= "inventory-field";//货品
   //public static final String recorder = "recorder-field";//记录人
   public static final String workorder = "worder-field";//工单
   public static final String signdoc = "signdoc-field";//非销售合同
   public static final String itype = "type-field";
   
   public static final String sc = "sc-field";
   public static final String traceDetail = "sd-field";
   
   private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   public InventoryBookModel() {
      this.addField(itemcode);
      Validator generalV = new Validator();
	  this.setValidator(itemcode, generalV);
		
      this.addField(itemcount);
	  DoubleValidator itemvtor = new DoubleValidator();
	 
      this.setValidator(itemcount, itemvtor);
		
      this.addField(bookdate);
      DateValidator bookdateResult = new DateValidator();//Validator双击后日期上不去，必须得是DateValidtor?
	  this.setValidator(bookdate, bookdateResult);
		
      this.addField(eid);
      
      this.addField(itemname);
	  this.setValidator(itemname, generalV);

      this.addField(inventory);
      this.addField(itemspec);
      this.addField(workorder);
	  
	  this.addField(signdoc);
	  
	  this.addField(itype);
	  this.addField(sc);
   }
   
   public boolean saveOrUpdate(Entity entity,InventoryBook iBook){
	   IBookType ibt = IBookType.valueOf(this.getValue(itype).toString());
	   iBook.setBookamt(Double.valueOf(this.getValue(itemcount).toString()));
	   iBook.setInventory((Inventory)this.getValue(inventory));
	   iBook.setSc((StoreCategory)this.getValue(sc));
	   String logname = Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("username"));
	   iBook.setRecorder(logname);//设当前登录的用户名为记录人
	   if(ibt.equals(IBookType.INBOOK))
		   iBook.setSd((SignDoc)this.getValue(signdoc));
	   else if(ibt.equals(IBookType.OUTBOOK)){
		   iBook.setWorkOrder((WorkOrder)this.getValue(workorder));
		   if(Double.valueOf(this.getValue(itemcount).toString())>0)
			   iBook.setBookamt(0-Double.valueOf(this.getValue(itemcount).toString()));
		   }
	   iBook.setIbType(ibt);

	   try{
		   iBook.setBookDate(sdf.parse(this.getValue(bookdate).toString()));
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return entity.saveOrUpdate(iBook);
   }
   public boolean saveOrUpdateFromWO(Entity entity,InventoryBook iBook){
	   iBook.setBookamt(Double.valueOf(this.getValue(itemcount).toString()));
	   return entity.saveOrUpdate(iBook);
   }
}
