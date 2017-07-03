package cn.mazu.storage.mvc;

import java.util.List;

import cn.mazu.doc.entity.SignDoc;
import cn.mazu.storage.entity.Inventory;
import cn.mazu.storage.entity.InventoryBook;
import cn.mazu.ui.InventoryBookMgr;
import cn.mazu.ui.InventoryInitialMgr;
import cn.mazu.util.EntityObject;
import cn.mazu.util.Util;
import cn.mazu.util.Util.IBookType;
import cn.mazu.util.Util.StoreCategory;
import cn.mazu.util.Util.WorkOrderStatus;
import cn.mazu.util.WidgetUtil;
import cn.mazu.webkit.html.DomElementType;
import cn.mazu.webkit.html.TextFormat;
import cn.mazu.widget.FormView;
import cn.mazu.widget.PaginationNew;
import cn.mazu.widget.PopBox;
import cn.mazu.widget.Widget;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.FormModel;
import cn.mazu.widget.kit.Link;
import cn.mazu.widget.kit.web.form.ComboBox;
import cn.mazu.widget.kit.web.form.DateEdit;
import cn.mazu.widget.kit.web.form.LineEdit;
import cn.mazu.widget.kit.web.form.PushButton;
import cn.mazu.widget.kit.web.interact.Template;
import cn.mazu.widget.kit.web.interact.Text;
import cn.mazu.widget.kit.web.interact.container.Anchor;
import cn.mazu.widget.kit.web.interact.container.ContainerWidget;
import cn.mazu.widget.validator.DoubleValidator;
import cn.mazu.workorder.entity.WorkOrder;

public class InventoryBookForm extends FormView{
    private PopBox pb ;
    private InventoryBookMgr ibMgr;
    private InventoryBook ib;
    private InventoryBookModel ibModel;
    private String title;
    
    private int max_ = 20;
	private int current_page = 0;
	private int invNum = 0;
	
	private Template tcontent;
	private ComboBox storeSel;
	
	private InventoryInitialMgr iim;
	private Template parent;
	
    public InventoryBookForm(String title,InventoryBook eib,PopBox pb,InventoryBookMgr ibMgr,InventoryInitialMgr iim) {
		this.ib = eib;
		this.pb = pb;
		this.ibMgr = ibMgr;
		this.title = title;
		this.iim = iim;
		ibModel = new InventoryBookModel();		
		//this.ib = new InventoryBook();
		if(title.equals("新建入库")){
			this.ib = new InventoryBook();
			ibModel.removeField(ibModel.workorder);//验证用
			this.setCondition("if:isin", true);
			ibModel.setValue(ibModel.itype, IBookType.INBOOK);
		}else if(title.equals("新建出库")){
			this.ib = new InventoryBook();
			ibModel.removeField(ibModel.signdoc);//验证用
			this.setCondition("if:isout", true);
			ibModel.setValue(ibModel.itype, IBookType.OUTBOOK);
		}else if(title.equals("编辑")){//完结工单来的明细
			updateClientView();
		}
		tcontent = new Template();
		tcontent.setTemplateText("${selInput}${selSearch}${addInv}${invList}",TextFormat.XHTMLUnsafeText);
		tcontent.setStyleClass("selWrap");
	}
    //判断是编辑还是新建从而分走不同的逻辑
    @Override
    public Widget draw(Template parent, String... args) {
    	this.parent = parent;
    	storeSel = WidgetUtil.initSelFromEnum(StoreCategory.class);
    	    if(title.equals("编辑")){
    	    	this.setTemplateText(tr("inventoryb-editf"), TextFormat.XHTMLUnsafeText);
    	    	
    	    	Anchor submitbtn = new Anchor(new Link(""), "保存");
    	    	submitbtn.setStyleClass("close");
    	    	submitbtn.clicked().addListener(this, new Signal.Listener() {
					@Override
					public void trigger() {
						InventoryBookForm.this.updateModel(ibModel);
						if(ibModel.saveOrUpdateFromWO(ibMgr.getDBEntity(),ib)){
							if(ibMgr.getSelectTr()!=null&&ibMgr.getSelectTr().getId().equals(ib.getId())){//选中某一行，确实执行的是【编辑】操作
								ibMgr.refreshSelTr(ib);
							}
						}else{
							Util.infoMessage(tr("Information Please").toString(), "失败");
						}
						pb.setAttributeValue("style", "display:none");
					}
				});
    	    	this.bindWidget("submit", submitbtn);
    	    	
    	    	Anchor closebtn = new Anchor(new Link(""), "关闭");
    			closebtn.setStyleClass("close");
    			closebtn.clicked().addListener(this, new Signal.Listener() {
    				@Override
    				public void trigger() {
    					pb.setAttributeValue("style", "display:none");
    				}
    			});
    	    	this.bindWidget("close", closebtn);
    	    	
    	    	this.updateView(ibModel);
    	    	
    	    }else{
    			this.setTemplateText(tr("inventoryb-form"),TextFormat.XHTMLUnsafeText);
    			Anchor submitbtn = new Anchor(new Link(""), "保存");
    			submitbtn.setStyleClass("close");
    			submitbtn.clicked().addListener(this, new Signal.Listener() {
    				@Override
    				public void trigger() {
    					InventoryBookForm.this.updateModel(ibModel);
    					if(validationPassed(ibModel)){
    						if(ibModel.saveOrUpdate(ibMgr.getDBEntity(),ib)){
    							if(ibMgr.getSelectTr()!=null&&!ibMgr.getSelectTr().getId().equals(ib.getId())||ibMgr.getSelectTr()==null){//选中了某一行但却执行【新建】操作|直接点【新建】
    								ibMgr.drawGrid();
    								ibMgr.doJavaScript("$('#bctable').datagrid(options={pagiStart:"+ibMgr.getPaginationStart()*ibMgr.getPaginationMax()+"});");
    							}else if(ibMgr.getSelectTr()!=null&&ibMgr.getSelectTr().getId().equals(ib.getId())){//选中某一行，确实执行的是【编辑】操作
    								ibMgr.refreshSelTr(ib);
    							}
    						}else{
    							Util.infoMessage(tr("Information Please").toString(), "失败");
    						}
    						pb.setAttributeValue("style", "display:none");
    					}
    				}
    			});
    			Anchor closebtn = new Anchor(new Link(""), "关闭");
    			closebtn.setStyleClass("close");
    			closebtn.clicked().addListener(this, new Signal.Listener() {
    				@Override
    				public void trigger() {
    					pb.setAttributeValue("style", "display:none");
    				}
    			});
    			if(title.equals("查看")){
    				this.bindString("submit", "");
    			}else
    				this.bindWidget("submit", submitbtn);
    			this.bindWidget("close", closebtn);
    			this.updateView(ibModel);
    			this.doJavaScript("$(document).ready(function() {" +
    					"var $tag = $('.regionbox #hotTags a');" +
    					"$tag.click(function(e) {" +
    						"$(this).parents('.regionbox').find('#hotTags a').removeClass('selected');" +
    						"$(this).parents('.regionbox').find('#hotTags .deleteX').css('display','none');" +
    						"$(this).addClass('selected');$(this).siblings('.deleteX').css('display','inline-block');});" +
    					"});");
    	    }
		return this;
    }
    @Override
    protected Widget createFormWidget(String field) {
    	Widget le = null;
		  if(field.equals(ibModel.itemname)||field.equals(ibModel.itemcode)||field.equals(ibModel.itemspec)){
				le = new LineEdit();
				((LineEdit)le).setReadOnly(true);
		  }else if(field.equals(ibModel.sc)&&title.equals("编辑")){
			  storeSel.setDisabled(true);
			  le = storeSel;
		  }else if(field.equals(ibModel.sc)&&!title.equals("编辑")){
			  le = storeSel;
		  }else if(field.equals(ibModel.bookdate)){
			  DateEdit d = new DateEdit();
			  d.setFormat("yyyy-MM-dd");
			  d.setReadOnly(true);
			  le = d;
		  }else if(field.equals(ibModel.eid)||field.equals(ibModel.itype)){//字段隐藏
			  le =new LineEdit();
			  le.hide();
		  }else if(field.equals(ibModel.inventory)){//选货品	  	
				try {
					final LineEdit selInput = new LineEdit();
					selInput.setEmptyText(tr("input key word"));
					PushButton selSearch = new PushButton(tr("search"));
					selSearch.setStyleClass("selSearch editcls");
					selSearch.clicked().addListener(this, new Signal.Listener() {
						@Override
						public void trigger() {
							tcontent.bindWidget("invList",
									drawInvList(selInput.getText(),0));
						}
					});
					tcontent.bindWidget("selInput", selInput);
					tcontent.bindWidget("selSearch", selSearch);
					
					PushButton addInv = new PushButton(tr("addInv"));
					addInv.setStyleClass("selSearch editcls");
					addInv.clicked().addListener(this, new Signal.Listener() {
						@Override
						public void trigger() {
							PopBox win = new PopBox("添加货品") {
								@Override
								public Widget createFormTbl(String title, PopBox wbx) {
									try {
										return addInventory(wbx);
									} catch (Exception e) {
										e.printStackTrace();
										return null;
									}
								}
							};
							ibMgr.bindWidget("win1", win);
							ibMgr.drawGrid();
							ibMgr.doJavaScript("$('#bctable').datagrid();");
						}
					});
					tcontent.bindWidget("addInv", addInv);
					
					tcontent.bindWidget("invList", drawInvList("",0));
        le = tcontent;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }else if(field.equals(ibModel.workorder)&&!title.equals("编辑")){//选工单
	          le = drawDoc(WorkOrder.class,field);
		  }else if(field.equals(ibModel.workorder)&&title.equals("编辑")){//修改完结工单带过来的明细
	          //le = drawDoc(WorkOrder.class,field);
			  le = new LineEdit();
			  ((LineEdit)le).setReadOnly(true);
		  }else if(field.equals(ibModel.signdoc)){//选非销售单
			  le = drawDoc(SignDoc.class,field);
		  }else if(field.equals(ibModel.itemcount)){
			  LineEdit input = new LineEdit();
			  DoubleValidator iv = new DoubleValidator();//保证只能输入数字
			  input.setMaxLength(7);//设定输入长度，包括符号位，防止超出short表示范围而得到溢出的数值如66666666666666666，--》-385439062，反而提示输入值小于下限。
			  input.setValidator(iv);
			  input.setEmptyText("请填写数字");
			  le = input;
		  }
			return le;
    }
 
    private Widget drawInvList(final String keyword,int cpage){
    	Template invList = new Template();
    	invList.setTemplateText("${tagbox}${pagi}",TextFormat.XHTMLUnsafeText);
    	ContainerWidget tr_region = new ContainerWidget() {
			@Override
			public DomElementType getDomElementType() {
				return DomElementType.DomElement_DIV;
			}
		};

		tr_region.setStyleClass("regionbox");
		List<EntityObject> inventoryList;
		List<EntityObject> invAllList;
		if(keyword.equals("")){
			inventoryList = ibMgr.getDBEntity().getSubListQBC(
					Inventory.class, new Object[] { "like", "dataCode", "___","and"},
					cpage*max_, max_);
			invAllList = ibMgr.getDBEntity().getSubListQBC(
					Inventory.class, new Object[] { "like", "dataCode", "___","and"},
					-1, -1);
		}else{
			inventoryList = ibMgr.getDBEntity().getSubListQBC(
					Inventory.class, new Object[] { "like", "dataCode", "___","and","like","name","%"+keyword+"%","and","like","spec","%"+keyword+"%","or"},
					cpage*max_, max_);
			invAllList = ibMgr.getDBEntity().getSubListQBC(
					Inventory.class, new Object[] { "like", "dataCode", "___","and","like","name","%"+keyword+"%","and","like","spec","%"+keyword+"%","or"},
					-1, -1);
		}
		invNum = invAllList.size();		
		if(invNum<1){
			tr_region.addWidget(new Text(tr("there is no goods")));
		}else{
			for (EntityObject eo : inventoryList) {
				final Inventory inventory = (Inventory) eo;
				ContainerWidget itemDiv = new ContainerWidget() {
					@Override
					public DomElementType getDomElementType() {
						return DomElementType.DomElement_DIV;
					}
				};
				itemDiv.setId("hotTags");
				final Anchor item = new Anchor(new Link(""), inventory.getName()
						+ "-" + Util.ban2quan(inventory.getSpec()));
				final PushButton deleteTag = new PushButton();//规格中半角特殊符号要转换成全角
				deleteTag.setStyleClass("deleteX");
				deleteTag.setAttributeValue("style", "display:none;");
				deleteTag.clicked().addListener(deleteTag, new Signal.Listener() {
					@Override
					public void trigger() {
						deleteTag.setAttributeValue("style", "display:none;");
						item.removeStyleClass("selected");
						InventoryBookForm.this.bindWidget("name-field",
								new LineEdit());
						InventoryBookForm.this.bindWidget("code-field",
								new LineEdit());
						InventoryBookForm.this.bindWidget("spec-field",
								new LineEdit());
					}
				});
				itemDiv.addWidget(deleteTag);
				item.clicked().addListener(item, new Signal.Listener() {
					@Override
					public void trigger() {
						deleteTag.setAttributeValue("style",
								"display:inline-block;");
						item.setStyleClass("selected");
						InventoryBookForm.this.bindWidget("name-field-info",
								new Text());
						InventoryBookForm.this.bindWidget("code-field-info",
								new Text());
						InventoryBookForm.this.bindWidget("spec-field-info",
								new Text());
						InventoryBookForm.this.bindWidget("name-field",
								new LineEdit(inventory.getName()));
						InventoryBookForm.this.bindWidget("code-field",
								new LineEdit(inventory.getCode()));
						InventoryBookForm.this.bindWidget("spec-field",
								new LineEdit(inventory.getSpec()));
						ibModel.setValue(ibModel.inventory, inventory);
					}
				});
				Inventory inventoryVal = ib.getInventory();
				if (inventoryVal != null && inventoryVal.equals(inventory)) {
					deleteTag.setAttributeValue("style", "display:inline-block;");
					item.setStyleClass("selected");
					ibModel.setValue(ibModel.inventory, inventory);
				}
				itemDiv.addWidget(item);
				tr_region.addWidget(itemDiv);
			}
		}
		invList.bindWidget("tagbox", tr_region);
		PaginationNew pagi = new PaginationNew(invNum, cpage, max_){
			@Override
			protected void selectPage(int page_id) {
				super.selectPage(page_id);
				current_page = page_id;
				tcontent.bindWidget("invList",drawInvList(keyword,current_page));
			}
		};
		pagi.setAttributeValue("style", "text-align: right;");
		if(invNum>max_)
			invList.bindWidget("pagi", pagi);
		else
			invList.bindString("pagi","");
		return invList;
    }
    //显示入库时候的非销售合同、出库时候的销售合同 的单号
    private Widget drawDoc(Class cls,final String field){
    	ContainerWidget wrapper = new ContainerWidget(){
			  @Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_DIV;
				}
		  };
		  ContainerWidget tr_region = new ContainerWidget(){
				@Override
				public DomElementType getDomElementType() {
					return DomElementType.DomElement_DIV;
				}
			};
			
			tr_region.setStyleClass("regionbox");
			List<EntityObject> docList ;
			if(cls.getSimpleName().equals("SignDoc"))
				docList = ibMgr.getDBEntity().getSubListQBC(cls, new Object[]{"like","dataCode","___","and","equal","wostatus",WorkOrderStatus.ACTIVATED,"and"},-1,-1);
			else
			   docList = ibMgr.getDBEntity().getSubListQBC(cls, new Object[]{"like","dataCode","___","and"},-1,-1);
			
			 for (final EntityObject eo:docList){
	        	  ContainerWidget itemDiv = new ContainerWidget(){
						@Override
						public DomElementType getDomElementType() {
							return DomElementType.DomElement_DIV;
						}
					};
					itemDiv.setId("hotTags");
					final Anchor item = new Anchor(new Link(""),WidgetUtil.getValFromProperty(eo,"orderno").toString());
					final PushButton deleteTag = new PushButton();
		            deleteTag.setStyleClass("deleteX");
		            deleteTag.setAttributeValue("style", "display:none;");
		            deleteTag.clicked().addListener(deleteTag, new Signal.Listener() {
						@Override
						public void trigger() {
							deleteTag.setAttributeValue("style", "display:none;");
							item.removeStyleClass("selected");
						}
					});
		            itemDiv.addWidget(deleteTag);
					item.clicked().addListener(item,new Signal.Listener() {
						@Override
						public void trigger() {
							deleteTag.setAttributeValue("style", "display:inline-block;");
							item.setStyleClass("selected");
							ibModel.setValue(field,eo);
						}
					});
					itemDiv.addWidget(item);					
					tr_region.addWidget(itemDiv);
					wrapper.addWidget(tr_region);
	          }
			 wrapper.setStyleClass("selWrap");
			 return wrapper;
    }
    private void updateClientView(){
    	ibModel.setValue(ibModel.eid, ib.getId());
    	ibModel.setValue(ibModel.bookdate, ib.getBookDate());
    	ibModel.setValue(ibModel.itemcode, ib.getTraceDetail().getSurfaceDetail().getGeneralElement().getCode());
    	ibModel.setValue(ibModel.itemcount, ib.getTraceDetail().getSurfaceDetail().getAmount());
    	ibModel.setValue(ibModel.itemname, ib.getTraceDetail().getSurfaceDetail().getGeneralElement().getName());
    	ibModel.setValue(ibModel.sc, ib.getSc());
    	ibModel.setValue(ibModel.workorder, ib.getTraceDetail().getWorkOrder().getOrderno());
    	ibModel.setValue(ibModel.itemspec, ib.getSpec());
    }
    @Override
	public void updateModelField(FormModel model,String field) {
	    if(field.equals(ibModel.sc))
	    	ibModel.setValue(field, WidgetUtil.getSelEO(storeSel));
	    else
		   super.updateModelField(model, field);
	}
    private Widget addInventory(PopBox wbx){
    	return new InventoryInitialForm("新建", null, wbx, iim).draw(this.parent, null);
    }
}

