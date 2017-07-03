package cn.mazu.surface.mvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;

import cn.mazu.WApplication;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.surface.entity.Surface;
import cn.mazu.surface.entity.SurfaceDetail;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.validator.Validator;
import cn.mazu.workorder.entity.WorkOrder;

public class SurfaceModel extends FormModel {
   public static final String eid = "id-field";
   public static final String companyname = "cname-field";
   //public static final String drawing = "drawing-field";
   public static final String chart = "chart-field";
   public static final String check = "check-field";
   public static final String approval = "approval-field";
   public static final String name = "name-field";
   public static final String copyface = "copy-field";
   public static final String code = "code-field";
   public static final String workorder = "wo-field";
 
   public SurfaceModel() {
	  this.addField(eid);
	  this.addField(companyname);
	  Validator generalV = new Validator();
//		this.setValidator(companyname, generalV);
	  this.addField(code);
//		this.setValidator(code, generalV);
	  this.addField(chart);
//		this.setValidator(chart, generalV);
	  this.addField(check);
//		this.setValidator(check, generalV);
	  this.addField(approval);
//		this.setValidator(approval, generalV);
	  this.addField(name);
		this.setValidator(name, generalV);
	  this.addField(copyface);
	  this.addField(workorder);
   }
   public boolean saveOrUpdate(Entity entity,Surface surface){
	   Surface copysurface  = (Surface)this.getValue(copyface);
	   WorkOrder wo =  (WorkOrder)this.getValue(workorder);
	   
	   surface.setApprover(this.getValue(approval).toString());
	   surface.setChecker(this.getValue(check).toString());
	   surface.setCompanyName(this.getValue(companyname).toString());
	   surface.setLister(this.getValue(chart).toString());
	   surface.setName(this.getValue(name).toString());
	   surface.setCode(this.getValue(code).toString());
	   Transaction t = entity.getDatabase().startTransaction();
	   try{
		   surface.setDataCode(Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")));
		   entity.getEntityManager().merge(surface);
		   t.commit();
 		   //若修改了图面信息中除“图面复制”的其他数据，为防止图面复制累加，若该图面下已经有图面明细数据，则不复制
		   Query query = entity.getEntityManager().createQuery("from SurfaceDetail sd where sd.surface=:surface");
		   query.setParameter("surface", surface);
		   int detailSize = query.getResultList().size();
		   
		   if(detailSize==0&&copysurface!=null){
			   Transaction t2 = entity.getDatabase().startTransaction();
			   if(copyFromSurface(entity,copysurface,surface))
				   t2.commit();
			   else 
				   t2.rollback();
		   }
		   if(this.getValue(workorder)!=null){
			   Transaction t3 = entity.getDatabase().startTransaction();
			   wo.setSurface(surface);
			   entity.getEntityManager().merge(wo);
			   t3.commit();
		   }
		   
	   }catch(Exception e){
		   t.rollback();
		   e.printStackTrace();
	   }
	   return true;
   }
   private boolean copyFromSurface(Entity entity,Surface copysurface,Surface targetsurface){
	   List<EntityObject> cdetailList = entity.getSubListQBC(SurfaceDetail.class, 
			   new Object[]{"equal","surface",copysurface,"and"}, -1, -1);
	   try{
		   for (EntityObject eo:cdetailList){
			   SurfaceDetail detail = (SurfaceDetail)eo;
			   SurfaceDetail targetDetail = detail.clone();
			   targetDetail.setSurface(targetsurface);
			   targetDetail.setId(UUID.randomUUID().toString());
			   targetDetail.setGenerateDate(new Date());
			   entity.saveOrUpdate(targetDetail);
		   }
	   }catch(Exception e){
		   e.printStackTrace();
		   return false;
	   }
	   return true;
   }
}
