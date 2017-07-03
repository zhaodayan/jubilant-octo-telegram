package cn.mazu.doc.mvc;

import java.text.SimpleDateFormat;

import cn.mazu.base.entity.Supplier;
import cn.mazu.doc.SignDocEntity;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.util.Util.DOCType;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.DateValidator;
import cn.mazu.widget.validator.RegExpValidator;
import cn.mazu.widget.validator.Validator;
import cn.mazu.workorder.entity.WorkOrder;

public class SignDocModel extends FormModel{
	//地址
	   public static final String ADDRESS = "address-field";
	   //订单号码
	   public static final String ORDERNO = "orderno-field";
	   //工单号码
	   public static final String  WORDERNO= "worder-field";
	   //联系人
	   public static final String MYLINKMAN = "mylinkman-field";
	   //订单日期
	   public static final String  ORDERDATE = "orderdate-field";
	   //电话
	   public static final String MYTEL = "mytel-field";
	   //传真
	   public static final String MYFAX = "myfax-field";
	   //供方名称,供货商名称
	   public static final String SUPPLIER = "supplier-field";
	   //供方联系人
	   public static final String SLINKMAN = "slinkman-field";
	   //供方电话
	   public static final String STEL = "stel-field";
	   //供方传真
	   public static final String SFAX = "sfax-field";
	  /* //条款
	   public static final String TERMS = "terms-field";*/
	   //上传附件
	 /*  public static final String ADDITION = "addition-field";*/
	   //合同类型
	   public static final String DOCTYPE = "type-field";
	   //工艺
	   public static final String TECHARR = "tech-field";
	   
	   public static final String rcid = "id-field";
	   public static final String typeStatus = "typeStatus-field";
	   private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	   
	   public SignDocModel() {
	      Validator generalV = new Validator();
		  
	      RegExpValidator mytelv = new RegExpValidator();
//	      RegExpValidator myfaxv = new RegExpValidator();
	      mytelv.setInvalidNoMatchText(tr("badform"));
//	      myfaxv.setInvalidNoMatchText(tr("badform"));
	      mytelv.setRegExp("(\\d{3,4}-)?(\\d{7,8})|(13[0-9]{9}|15[0-9]{9}|18[0-9]{9})");
//	      myfaxv.setRegExp("(\\d{3,4}-)?(\\d{7,8})");
	      
	      DateValidator ORDERDATEResult = new DateValidator();
	      
		  this.addField(ADDRESS);
//		  this.setValidator(ADDRESS, generalV);
		  
		  this.addField(ORDERNO);
		  this.setValidator(ORDERNO, generalV);
		  
		  this.addField(MYLINKMAN);
//		  this.setValidator(MYLINKMAN, generalV);
		  
		  this.addField(WORDERNO);
//		  this.setValidator(WORDERNO, generalV);

		  this.addField(ORDERDATE);
//		  this.setValidator(ORDERDATE, ORDERDATEResult);
		  
		  this.addField(MYTEL);
//		  this.setValidator(MYTEL, mytelv);
		  
		  this.addField(MYFAX);
//		  this.setValidator(MYFAX, myfaxv);

		  this.addField(SUPPLIER);
//		  this.setValidator(SUPPLIER, generalV);
		  
		  this.addField(SLINKMAN);
//		  this.setValidator(SLINKMAN,generalV);
		  
		  this.addField(STEL);
//		  this.setValidator(STEL, mytelv);

		  this.addField(SFAX);
//		  this.setValidator(SFAX, myfaxv);

		  this.addField(DOCTYPE);
		  this.setValidator(DOCTYPE, generalV);
		  this.addField(typeStatus);
		  this.setValidator(typeStatus, generalV);
		  
		  this.addField(rcid);
		  this.addField(TECHARR);
	}
	   
	   public boolean saveOrUpdate(SignDoc sd,Entity sdEntity){
//		   Transaction t = sdEntity.getDatabase().startTransaction();
		   if(this.getValue(WORDERNO)!=null)
			   sd.setWorkOrder((WorkOrder)this.getValue(WORDERNO));
		   if(this.getValue(SUPPLIER)!=null)
			   sd.setSupplier((Supplier)this.getValue(SUPPLIER));
		   if(this.getValue(DOCTYPE)!=null)
			   sd.setDocType((DOCType)this.getValue(DOCTYPE));
		   sd.setWostatus((WorkOrderStatus)this.getValue(typeStatus));
		   
		    sd.setProduceFax(this.valueText(SignDocModel.MYFAX));
			sd.setProAddr(this.valueText(SignDocModel.ADDRESS));
			sd.setProContactor(this.valueText(SignDocModel.MYLINKMAN));
			sd.setProduceFax(this.valueText(SignDocModel.MYFAX));
			sd.setProduceTele(this.valueText(SignDocModel.MYTEL));
			sd.setOrderno(this.valueText(SignDocModel.ORDERNO));
			sd.setSupTele(this.valueText(SignDocModel.STEL));
			sd.setSupContactor(this.valueText(SignDocModel.SLINKMAN));
			sd.setSupFax(this.valueText(SignDocModel.SFAX));
			sd.setTechStr(this.getValue(TECHARR).toString());
		   try{
			   if(this.getValue(ORDERDATE)!=null&&!this.getValue(ORDERDATE).toString().equals("")){
				   sd.setOrderDate(sdf.parse(this.valueText(SignDocModel.ORDERDATE)));
			   }
			  /* sdEntity.getEntityManager().merge(sd);
			   t.commit();
			   return true;*/
		   }catch(Exception e){
//			   t.rollback();
			   e.printStackTrace();
		   }
//		   return false;
		   return sdEntity.saveOrUpdate(sd);
	   }
	
}
