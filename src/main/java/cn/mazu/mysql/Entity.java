package cn.mazu.mysql;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import cn.mazu.WApplication;
import cn.mazu.auth.PasswordHash;
import cn.mazu.auth.Session;
import cn.mazu.base.entity.GeneralElement;
import cn.mazu.doc.entity.SignDoc;
import cn.mazu.drawing.entity.Drawing;
import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.storage.entity.InventoryBook;
import cn.mazu.sys.entity.AccountPermission;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.IBookType;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.util.Util.WOTraceDetailStatus;
import cn.mazu.util.WidgetUtil;
import cn.mazu.workorder.entity.WorkOrder;
import cn.mazu.workorder.entity.WorkOrderTraceDetail;



public class Entity implements Serializable{
	private static final long serialVersionUID = 5483093702785813252L;
	private String id_;
	private AbstractDatabase db_;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public Entity(){
		this("",DbFactory.createDatabase());
	}
	
	public Entity(String id, AbstractDatabase userdDatabase){
		this.id_ = id;
		this.db_ = userdDatabase;
	}
	
	public AbstractDatabase getDatabase(){
		return this.db_;
	}

	public String getId_() {
		return id_;
	}
	
	public boolean equals(Entity entity){
		return this.id_.equals(entity.id_)&&this.db_==entity.db_;
	}
	
	public EntityManager getEntityManager(){
		return this.db_.entityManager_;
	}
	
	//获取总条数hql
	public int getTotalNumber(Class cls,Object[] obj_arr){
		TypedQuery<EntityObject> typedQuery;
		try {
			CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
			CriteriaQuery<EntityObject> cq = cb.createQuery(cls);
			Subquery<Number> sq = cq.subquery(Number.class);
			Root<EntityObject> eo = cq.from(cls);
			Predicate p = cb.like(eo.<String>get("id"), "%%");
			List<Predicate> predicatesList = new ArrayList<Predicate>();
			for (int i=0;i<obj_arr.length;i+=4){
				String[] attrArr = obj_arr[i+1].toString().split("\\.");
				Path path = null;
				if(!obj_arr[i+1].toString().contains("asc")&&!obj_arr[i+1].toString().contains("desc")&&!obj_arr[i].equals("")){
					path = eo.get(attrArr[0]);
					for (int j=1;j<attrArr.length;j++)
						path = path.get(attrArr[j]);
				}
				
				if(obj_arr[i].toString().equals("like")&&obj_arr[i+3].toString().equals("and")){//.<String>只有在like时用到,equal等不用,传递对象的时候类型转换出错
					p = cb.and(p,cb.like(path, obj_arr[i+2].toString()));
				}
				else if(obj_arr[i].toString().equals("like")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(p, cb.like(path, obj_arr[i+2].toString()));
				}
				else if(obj_arr[i].toString().equals("equal")&&obj_arr[i+3].toString().equals("and")){//get(参数1为属性，可以toString,参数2不要toString()
					p = cb.and(p,cb.equal(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("isnull")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(p,cb.isNull(path));
				}
				else if(obj_arr[i].toString().equals("equal")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(cb.equal(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("notEqual")&&obj_arr[i+3].toString().equals("and")){
					p = cb.and(p,cb.notEqual(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("notEqual")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(p,cb.notEqual(path, obj_arr[i+2]));
				}
			}
			typedQuery = this.getEntityManager().createQuery(cq.where(p));
			return typedQuery.getResultList().size(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public int getTotalNumberNative(Class cls,Object[] obj){
		Query query = this.getEntityManager().createNamedQuery(obj[0].toString());
	    List resultList = query.getResultList();
	    if(resultList!=null)
	    	return resultList.size();
		return 0;
	}
	public List<EntityObject> getSubListQBC(Class cls, Object[] obj_arr,int start_,int max_){
		try {
			CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
			CriteriaQuery<EntityObject> cq = cb.createQuery(cls);
			Root<EntityObject> eo = cq.from(cls);
			Predicate p = cb.like(eo.<String>get("id"), "%%");
			List<Predicate> predicatesList = new ArrayList<Predicate>();
			cq.orderBy(cb.desc(eo.get("generateDate")));
			for (int i=0;i<obj_arr.length;i+=4){
				String[] attrArr = obj_arr[i+1].toString().split("\\.");
				Path path = null;
				if(!obj_arr[i+1].toString().contains("asc")&&!obj_arr[i+1].toString().contains("desc")&&!obj_arr[i].equals("")){
					path = eo.get(attrArr[0]);
					for (int j=1;j<attrArr.length;j++)
						path = path.get(attrArr[j]);
				}

				if(obj_arr[i].toString().equals("like")&&obj_arr[i+3].toString().equals("and")){//.<String>只有在like时用到,equal等不用,传递对象的时候类型转换出错
					p = cb.and(p,cb.like(path, obj_arr[i+2].toString()));
				}
				else if(obj_arr[i].toString().equals("like")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(p, cb.like(path, obj_arr[i+2].toString()));
				}
				else if(obj_arr[i].toString().equals("equal")&&obj_arr[i+3].toString().equals("and")){//get(参数1为属性，可以toString,参数2不要toString()
					p = cb.and(p,cb.equal(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("equal")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(cb.equal(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("isnull")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(p,cb.isNull(path));
				}
				else if(obj_arr[i].toString().equals("notEqual")&&obj_arr[i+3].toString().equals("and")){
					p = cb.and(p,cb.notEqual(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("notEqual")&&obj_arr[i+3].toString().equals("or")){
					p = cb.or(p,cb.notEqual(path, obj_arr[i+2]));
				}
				else if(obj_arr[i].toString().equals("orderBy")&&obj_arr[i+1].toString().equals("asc")){
					cq.orderBy(cb.asc(cb.coalesce(eo.get(obj_arr[i+2].toString()), obj_arr[i+3].toString())));
				}
				else if(obj_arr[i].toString().equals("orderBy")&&obj_arr[i+1].toString().equals("desc"))
				    cq.orderBy(cb.desc(eo.get(obj_arr[i+2].toString())));
			}
			TypedQuery<EntityObject> typedQuery = this.getEntityManager().createQuery(cq.where(p)); 
			if(start_>-1)typedQuery.setFirstResult(start_);
			if(max_>-1)typedQuery.setMaxResults(max_);
			return typedQuery.getResultList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	public List<EntityObject> getNavListFromGeneral(Class<? extends EntityObject> cls,Object[] objArr,int start_,int max_){
		
		Query query = this.getEntityManager().createNamedQuery(objArr[0].toString());
		if(start_>=0)query.setFirstResult(start_);
		if(max_>=0)query.setMaxResults(max_);

		if(objArr.length>1&&objArr[1]!=null){
			query.setParameter(3, objArr[1]);
		}
		
		//sql语句查询后得出list
		List objList = query.getResultList();
		//将sql记录包装成EntityObject，添加到list中返回
		List<EntityObject> eoList = new ArrayList<EntityObject>();
		//取到页面上的FieldResult注解，含列名信息，且出现顺序与sql中的查询的列名顺序一致
		Annotation[] annoArr = cls.getDeclaredAnnotations();
		List<ColumnResult> crList = new ArrayList<ColumnResult>();
		for (Annotation anno:annoArr){
			if(SqlResultSetMappings.class.isAssignableFrom(anno.annotationType())){
				SqlResultSetMappings srsms = (SqlResultSetMappings)anno;
				SqlResultSetMapping ssrm = srsms.value()[0];
				crList.addAll(Arrays.asList(ssrm.columns()));
				break;
			}
		}
		try {
			if(crList.size()>0){
			   for (int i=0;i<objList.size();i++){
				   Object[] obj = (Object[])objList.get(i);
				   EntityObject eo = cls.newInstance();
				   
				   for (int k=0;k<crList.size();k++){
					   String fieldName = crList.get(k).name();
					   Object fieldVal = obj[k+1];//namedQuery会查询出实体对象作为第一列，所以要跳过下
			
					   if(fieldVal instanceof byte[])//防止数据库中返回的不是utf-8
						   fieldVal = new String((byte[])obj[k+1],"UTF-8");
					   
					   String clsName = crList.get(k).type().getName();
					   if(fieldVal!=null){
						   if(clsName.equals("java.lang.String")){
							   fieldVal = fieldVal.toString();
						   }else if(clsName.equals("java.lang.Short")){
							   fieldVal = Short.valueOf(fieldVal.toString());
						   }else if(clsName.equals("java.lang.Integer")){
							   fieldVal = Integer.valueOf(fieldVal.toString());
						   }else if(clsName.equals("java.lang.Double")){
							   fieldVal = Double.valueOf(fieldVal.toString());
						   }else if(clsName.equals("java.sql.Date")){
							   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							   fieldVal = sdf.format(fieldVal.toString());
						   }else if(clsName.equals("java.util.Date")){
							   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//|.m
							   fieldVal = sdf.parse(fieldVal.toString().substring(0,19));
						   }
						   WidgetUtil.setValForProperty(eo, fieldName, fieldVal);
					   }
				   }
				   eoList.add(eo);
			   }   
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return eoList;
	}
	
	public boolean saveOrUpdate(EntityObject eo){
		Transaction t = this.getDatabase().startTransaction();
		try{
			eo.setDataCode( Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")));
			this.getEntityManager().merge(eo);
			
			t.commit();
		}catch(Exception e){
			t.rollback();
			e.printStackTrace();
		}
		return true;
	}
	public boolean removeEO(EntityObject eo){
		Transaction t = this.getDatabase().startTransaction();
		try{
			this.getEntityManager().remove(eo);
			t.commit();
			return true;
		}catch(Exception e){
			t.rollback();
			e.printStackTrace();
			return false;
		}
	}
	public boolean saveOrUpdateBatch(List<EntityObject> eoList){
		Transaction t = this.getDatabase().startTransaction();
		try{
			for (EntityObject eo:eoList){
				 this.getEntityManager().merge(eo);
			}
			t.commit();
		}catch(Exception e){
			t.rollback();
			e.printStackTrace();
		}
		return true;
	}
	//是否为持久化对象
	public boolean isPersistObject(EntityObject eo){
		return this.getEntityManager().find(eo.getClass(), eo.getId())!=null;
	}
	//判断是否有关联关系
	public boolean hasAssociation(EntityObject peo,EntityObject deo){
		Field[] fields = deo.getClass().getDeclaredFields();
		for (Field field:fields){
			if(field.getType().getName().equals(peo.getClass().getName()))
				return true;
		}
		return false;
	}
	/**
	 * get widget
	 */
	public List<GeneralElement> getWidget(){
		Query query = this.getEntityManager().createQuery("from "+GeneralElement.class.getSimpleName());
		return query.getResultList();
	}
	public List<Drawing> getDrawingOfWidget(String code){
		Query query = this.getEntityManager().createQuery("from "+Drawing.class.getSimpleName()+" where code like '%"+code+"%'");
		return query.getResultList();
	}
	public List<Drawing> getDrawingOfWidget(GeneralElement ge){
		Query query = this.getEntityManager().createQuery("from Drawing where generalElement.id='"+ge.getId()+"'");
		return query.getResultList();
	}
	//图面是不是被设置在工单上了
	//public boolean Surface
	//得到新建图面的编号
	public String getMaxCode(){
		Query query = this.getEntityManager().createQuery("select coalesce(max(code),0)+1 from Surface");
		return query.getSingleResult().toString();
	}
	//得到新建工单的编号
	public String getWOCount(){
		String likeym = Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1);
		Query query = this.getEntityManager().createQuery("select coalesce(count(id),0)+1 from WorkOrder where orderno like '"+likeym+"%'");
		return likeym+"-"+query.getSingleResult().toString();
		
	}
	public SimpleDateFormat getSdf() {
	    return this.sdf;
	}
    //登录验证
    public Object validateLoginPass(Class ap,String username,String pwd,Session _session){
    	CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<EntityObject> cq = cb.createQuery(ap);
    	Root<EntityObject> eo = cq.from(ap);
    	TypedQuery<EntityObject> typedQuery;
    	List<EntityObject> resultList;
    	cq.select(eo).where(cb.equal(eo.get("username"),username));
    	typedQuery = this.getEntityManager().createQuery(cq);
    	resultList = typedQuery.getResultList();
    	if(resultList==null||resultList!=null&&resultList.size()==0)//用户名不存在
    		return new Integer(1);
        else{
    	   AccountPermission loginAp = (AccountPermission)resultList.get(0);
    	   PasswordHash ph = new PasswordHash(loginAp.getPasswordMethod(),loginAp.getPasswordSalt(),loginAp.getPasswordHash());
    	   if(_session.getPasswordAuth().getVerifier().verify(pwd, ph))//验证用户名和密码是否匹配
    		   return loginAp;
    	   else 
    		   return new Integer(2); 
       } 
    	   
    }
    //得到当前登陆账号对象AP
    public AccountPermission getCurrentAP(){
    	CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<AccountPermission> cq = cb.createQuery(AccountPermission.class);
    	Root<AccountPermission> eo = cq.from(AccountPermission.class);
    	cq.select(eo).where(cb.equal(eo.get("username"),
    			 Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("username"))));
    	TypedQuery<AccountPermission> typedQuery = this.getEntityManager().createQuery(cq);
    	List<AccountPermission> resultList = typedQuery.getResultList();
    	if(resultList!=null&&resultList.size()>0)
    		return resultList.get(0);
    	else 
    		return null;
    }
    //判断对象是否为持久化对象
    public boolean isPersist(EntityObject eo){
    	return this.getEntityManager().find(eo.getClass(), eo.getId())==null?false:true;
    }
    @Deprecated//由已经开始跟踪但是没有跟踪完的明细 ||或者是还没有开始跟踪的图面明细,工单跟踪明细状态后被取消，所以将该方法废弃 20150409
    public boolean hasTracingDetail(WorkOrder wo){
    	int sdSize = wo.getSurface().getSdList().size();
    	CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<WorkOrderTraceDetail> cq = cb.createQuery(WorkOrderTraceDetail.class);
    	Root<WorkOrderTraceDetail> eo = cq.from(WorkOrderTraceDetail.class);
    	Predicate p = cb.and(cb.equal(eo.get("workOrder"),wo),cb.equal(eo.get("detailStatus"),
    			WOTraceDetailStatus.valueOf(WOTraceDetailStatus.FINISHED.getName())));
    	TypedQuery<WorkOrderTraceDetail> typedQuery = this.getEntityManager().createQuery(cq.where(p));
    	if(typedQuery.getResultList()!=null&&typedQuery.getResultList().size()<sdSize)
    		return true;
    	else return false;
    	 
    }
    //新建编辑采购合同，工单选项连带订单号码变化。
    public String getSDOrderno(String workorderNo,String signdocId){
			String retStr = workorderNo;
			int dash1 = retStr.indexOf("-");
			int dash2 = retStr.lastIndexOf("-");
			String dashstr1 = retStr.substring(dash1+1,dash2);
			String dashstr2 = retStr.substring(dash2+1);
			if(dashstr1.length()==1)
				retStr = retStr.replace("-"+dashstr1, "-0"+dashstr1);
			if(dashstr2.length()==1)
				retStr = retStr.replace("-"+dashstr2, "-0"+dashstr2);
			retStr = retStr.replace("-", "");
			String maxNumStr = this.getEntityManager().createQuery("select coalesce(max(orderno),1) from SignDoc sd where sd.id<>'"+signdocId+"' and sd.workOrder.orderno='"+workorderNo+"'").getSingleResult().toString();
			//String availableSizeStr = maxNum.toString();
			int dash3 = maxNumStr.lastIndexOf("-");
			if(dash3>0)
			   return retStr+"-"+(Integer.valueOf(maxNumStr.substring(dash3+1))+1+"");
			else
			  return retStr+"-"+Integer.valueOf(maxNumStr);
    }
    //通过工单号查找对应工单
    public WorkOrder getWOByOrderno(String workorderno){
    	WorkOrder wo = null;
    	Query query = this.getEntityManager().createQuery("from WorkOrder wo where wo.orderno=:workorderno");
    	query.setParameter("workorderno", workorderno);
    	List qryList = query.getResultList();
    	if(qryList!=null&&qryList.size()>0)
    		wo = (WorkOrder)qryList.get(0);
    	return wo;
    }
    public List<EntityObject> getGoodsListBySD(SignDoc sd){
    	Query query = this.getEntityManager().createQuery("from Goods gs where gs.signDoc=:sd");
    	query.setParameter("sd", sd);
    	return query.getResultList();
    }
    //获取该工单的成本配置信息
    public Object getCostConfByWO(WorkOrder wo){
    	Query query = this.getEntityManager().createQuery("from CostConf cc where cc.startDate<=:wogeDate and cc.endDate>=:wogeDate");
    	query.setParameter("wogeDate", wo.getGenerateDate());
    	List resultList = query.getResultList();
    	if(resultList!=null&&resultList.size()>0)
    		return resultList.get(0);
    	else return null;
    }
    
    public List<EntityObject> getIvGoodsList(SignDoc sd,WorkOrder wo){
    	Query query = this.getEntityManager().createQuery("from Goods g where g.inventory is not null and g.signDoc=:sd and g.signDoc.workOrder=:wo");
    	query.setParameter("sd", sd);
    	query.setParameter("wo", wo);
    	return query.getResultList();
    }
    
    //工单完结，修改工单状态，将该工单下所有入库跟踪记录入库
    public boolean endWorkOrder(WorkOrder workOrder){
    	List<InventoryBook> inputBookList = new LinkedList<InventoryBook>();
    	if(saveOrUpdate(workOrder)){
    		for (WorkOrderTraceDetail traceDetail:workOrder.getWotracelist()){
    			 InventoryBook inputInventory = new InventoryBook();
    			 inputInventory.setBookamt(Double.valueOf(traceDetail.getQuantity()));
    			 //inputInventory.setInvName(traceDetail.getSurfaceDetail().getGeneralElement().getName());
    			 //inputInventory.setInvNo(workOrder.getOrderno()+"-"+traceDetail.getSurfaceDetail().getGeneralElement().getCode());
    			 //inputInventory.setSpec("workorder");
    			 inputInventory.setIbType(IBookType.INBOOK);
    			 inputInventory.setSc(StoreCategory.WORKORDER);
    			 inputInventory.setBookDate(new Date());
    			 inputInventory.setDataCode(Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("dataCode")));
    			 inputInventory.setGenerateDate(new Date());
    			 inputInventory.setRecorder(Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("username")));
    			 inputInventory.setTraceDetail(traceDetail);
    			 inputBookList.add(inputInventory);
    		}
    		//this.getEntityManager().
    	    for (InventoryBook ib:inputBookList){
    	    	if(!saveOrUpdate(ib))
    	    		return false;
    	    }
    	    return true;
    	}else
    		return false;
    }
    
}
