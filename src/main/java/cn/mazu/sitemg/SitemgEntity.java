package cn.mazu.sitemg;

import java.util.List;

import javax.persistence.Query;

import cn.mazu.mysql.AbstractDatabase.Transaction;
import cn.mazu.mysql.Entity;
import cn.mazu.sitemg.entity.Company;
import cn.mazu.sitemg.entity.Consult;
import cn.mazu.sitemg.entity.Contact;
import cn.mazu.sitemg.entity.Device;
import cn.mazu.sitemg.entity.Notice;
import cn.mazu.sitemg.entity.Product;
import cn.mazu.sitemg.entity.QuotLibrary;
import cn.mazu.sitemg.entity.Quotation;
import cn.mazu.sitemg.entity.Repair;
import cn.mazu.sitemg.mvc.ContactModel;
import cn.mazu.sitemg.mvc.DeviceModel;
import cn.mazu.sitemg.mvc.ProductModel;
import cn.mazu.sitemg.mvc.QuolibModel;
import cn.mazu.util.Util.NoticeType;
import cn.mazu.util.Util.QuotaType;
import cn.mazu.util.Util.RapidOrNot;
import cn.mazu.widget.kit.FormModel;

public class SitemgEntity extends Entity{
	
	public List<Company> getCompanyList(){
		Query query = this.getEntityManager().createQuery("select company from Company company order by company.generateDate desc");
		return query.getResultList();
	}
	/*public boolean updateCompany(Company c){
		Transaction t = this.getDatabase().startTransaction();
		try {
			this.getEntityManager().merge(c);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
*/
	public List<Contact> getContactList(){
		Query query = this.getEntityManager().createQuery("select c from Contact c order by c.generateDate desc");
		return query.getResultList();
	}
	/*public boolean updateContact(Contact c,ContactModel cm){
		Transaction t = this.getDatabase().startTransaction();
		try {
			c.setAddress(cm.valueText(ContactModel.addrField));
			c.setCname(cm.valueText(ContactModel.manField));
			c.setcQQ(cm.valueText(ContactModel.QQField));
			c.setEmail(cm.valueText(ContactModel.emailField));
			c.setFax(cm.valueText(ContactModel.faxField));
			c.setSiteaddr(cm.valueText(ContactModel.siteField));
			c.setTele(cm.valueText(ContactModel.teleField));
			this.getEntityManager().merge(c);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}*/
	public boolean removeQuotation(Quotation q){
		Transaction t = this.getDatabase().startTransaction();
		try {
			this.getEntityManager().remove(q);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean removeConsult(Consult c){
		Transaction t = this.getDatabase().startTransaction();
		try {
			this.getEntityManager().remove(c);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean removeRepair(Repair r){
		Transaction t = this.getDatabase().startTransaction();
		try {
			this.getEntityManager().remove(r);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	/*public boolean updateProduct(Product p,ProductModel pModel){
		Transaction t = this.getDatabase().startTransaction();
		try {
			p.setPname(pModel.valueText(ProductModel.namefield));
			p.setPintro(pModel.valueText(ProductModel.introfield));
			p.setRapidOrNot((RapidOrNot)(pModel.getValue(ProductModel.rapidfield)));
			this.getEntityManager().merge(p);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}*/
	public boolean removeProduct(Product p){
		Transaction t = this.getDatabase().startTransaction();
		
		try {
			this.getEntityManager().remove(p);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	/*public boolean updateDevice(Device p,DeviceModel pModel){
		Transaction t = this.getDatabase().startTransaction();
		try {
			p.setDname(pModel.valueText(DeviceModel.namefield));
			p.setDintro(pModel.valueText(DeviceModel.introfield));
			
			this.getEntityManager().merge(p);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}*/
	public boolean removeDevice(Device d){
		Transaction t = this.getDatabase().startTransaction();
		
		try {
			this.getEntityManager().remove(d);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public List<QuotLibrary> getQuotLibrary(QuotaType qt){
		Query query = this.getEntityManager().createQuery("select quot from QuotLibrary quot where quot.type= :qt order by quot.generateDate desc");
		query.setParameter("qt", qt);	
		return query.getResultList();
	}
	
	public List<Notice> getNoticeList(NoticeType nt){
		Query query = this.getEntityManager().createQuery("select notice from Notice notice where notice.type= :nt order by notice.generateDate desc");
		query.setParameter("nt", nt);	
		return query.getResultList();
	}
}
