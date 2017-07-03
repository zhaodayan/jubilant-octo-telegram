package cn.mazu.workorder.mvc;

import java.text.SimpleDateFormat;

import cn.mazu.base.entity.Client;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DateValidator;
import cn.mazu.widget.validator.IntValidator;
import cn.mazu.widget.validator.Validator;
import cn.mazu.workorder.entity.WorkOrder;


public class WorkOrderModel extends FormModel {
   public static final String orderno = "orderno-field";
   public static final String name = "name-field";
   public static final String corpname = "corpname-field";
   public static final String amount = "amt-field";
   public static final String eid = "id-field";
   public static final String deliveryDate = "deliveryd-field";
   public static final String orderDate = "orderd-field";
   public static final String realDate = "reald-field";
   public static final String shipDate = "shipd-field";
   public static final String predictDate = "predictd-field";
   public static final String remark = "remark-field";
   public static final String surface = "surface-field";
   //public static final String wostatus = "status-field";
   //上传附件
   public static final String additionC = "additionC-field";
   public static final String additionT = "additionT-field";
   
   private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   
   public WorkOrderModel() {
	  Validator generalV = new Validator();

	  IntValidator amtResult = new IntValidator();
	  amtResult.setRange(Short.MIN_VALUE, Short.MAX_VALUE);
	  amtResult.setInvalidTooLargeText(tr("too large"));
	  amtResult.setInvalidTooSmallText(tr("too small"));
	  
	  this.addField(orderDate);
//	  this.setValidator(orderDate, generalV);
	  
	  this.addField(orderno);
	  this.setValidator(orderno, generalV);
	  
	  this.addField(amount);
//	  this.setValidator(amount, amtResult);
	  
	  DateValidator dateV = new DateValidator();
	  this.addField(deliveryDate);
//	  this.setValidator(deliveryDate, dateV);
	  
	  this.addField(orderDate);
//	  this.setValidator(orderDate, dateV);
	  
	  this.addField(realDate);
//	  this.setValidator(realDate, dateV);
	  
	  this.addField(predictDate);
//	  this.setValidator(predictDate,dateV);

	  this.addField(shipDate);
//	  this.setValidator(shipDate, dateV);
	  
	  this.addField(corpname);
//	  this.setValidator(corpname, dateV);
	  
	  this.addField(name);
//	  this.setValidator(name, generalV);
	  
	  /*this.addField(wostatus);
	  this.setValidator(wostatus, dateV);*/
	  
	  this.addField(eid);
	  this.addField(remark);
	  this.addField(surface);
	  this.addField(additionC);
	  this.addField(additionT);
   }
   public boolean saveOrUpdate(Entity entity,WorkOrder workOrder){
	   if(this.getValue(corpname)!=null)
	      workOrder.setClient((Client)this.getValue(corpname));
	   /*if(this.getValue(wostatus)!=null)
   	      workOrder.setWostatus((WorkOrderStatus)this.getValue(wostatus));*/
	   
	   workOrder.setAmount(Double.valueOf(this.getValue(amount).toString()));
	   workOrder.setOrderno(this.getValue(orderno).toString());
	   workOrder.setName(this.getValue(name).toString());
	   workOrder.setRemark(this.getValue(remark).toString());
	   workOrder.setWostatus(WorkOrderStatus.ACTIVATED);
//	   Transaction t = entity.getDatabase().startTransaction();
	   try{
		   if(this.getValue(deliveryDate)!=null&&!this.getValue(deliveryDate).toString().equals("")){
			   workOrder.setDeliveryDate(sdf.parse(this.getValue(deliveryDate).toString()));
		   }
		   if(this.getValue(predictDate)!=null&&!this.getValue(predictDate).toString().equals("")){
			   workOrder.setPredictDate(sdf.parse(this.getValue(predictDate).toString()));
		   }
		   if(this.getValue(shipDate)!=null&&!this.getValue(shipDate).toString().equals("")){
			   workOrder.setShipDate(sdf.parse(this.getValue(shipDate).toString()));
		   }
		   if(this.getValue(realDate)!=null&&!this.getValue(realDate).toString().equals("")){
			   workOrder.setRealDate(sdf.parse(this.getValue(realDate).toString()));
		   }
		   if(this.getValue(orderDate)!=null&&!this.getValue(orderDate).toString().equals("")){
			   workOrder.setOrderDate(sdf.parse(this.getValue(orderDate).toString()));
		   }
		 /*  entity.getEntityManager().merge(workOrder);
		   t.commit();*/
	   }catch(Exception e){
//		   t.rollback();
		   e.printStackTrace();
	   }
	   return entity.saveOrUpdate(workOrder);
   }
}
