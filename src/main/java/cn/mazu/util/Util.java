package cn.mazu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;

import com.sun.image.codec.jpeg.TruncatedFileException;

import cn.mazu.WApplication;
import cn.mazu.utils.WLength;
import cn.mazu.widget.event.Signal;
import cn.mazu.widget.kit.composite.popup.Icon;
import cn.mazu.widget.kit.composite.popup.StandardButton;
import cn.mazu.widget.kit.composite.popup.WMessageBox;
//enum类都重写了toString方法，为WidgetUtil中由枚举类生成下拉选项用，间接地valueOf方法也被改写
public class Util {
	//公告分类：内部公告，外部公告
	public enum NoticeType{
		INNERNT(0,"内部公告"),OUTERNT(1,"外部公告");
		public String name;
		private int index;
		private NoticeType(int index,String name){
			this.index = index;
			this.name = name;
		}
		public String getName(){
			return this.name;
		}
		@Override
		public String toString() {
		   return this.name;
		}
	}
	
	//快速报价产品
	public enum RapidOrNot{
		RAPIDNO(0,"否"),RAPIDYES(1,"是");
		public String name;
		private int index;
		private RapidOrNot(int index,String name){
			this.name = name;
			this.index = index;
		}
		public String getName(){
			return this.name;
		}
		@Override
		public String toString() {
		   return this.name;
		}
	}
	//用户性别
	public enum UserGender{
		FEMALE(0,"女"),MALE(1,"男");
		public String name;
		private int index;
		private UserGender(int index,String name){
			this.name = name;
		    this.index = index;
		}
		public String getName(){
			return this.name;
		}
		@Override
		public String toString() {
		   return this.name;
		}
	}
	
	public static String isLogin(){
    	String loginid = Util.URLDString(WApplication.getInstance().getEnvironment().getCookie("loginid"));
    	if((loginid!=null&&loginid.equals(""))||loginid==null){
			if(WApplication.getInstance().changedInternalPath("/login_page")){
				WApplication.getInstance().setInternalPath("/login_page");
			}
		}
    	return loginid;
    }
	
	/**
	 * 随机生成密码
	 * @return
	 */
	public static String randomPassword() {
		Random r = new Random();
		String code = "";

		for (int i = 0; i < 9; ++i) {
			if (i % 2 == 0) // 偶数位生产随机整数
			{
				code = code + r.nextInt(10);
			} else// 奇数产生随机字母包括大小写
			{
				int temp = r.nextInt(52);
				char x = (char) (temp < 26 ? temp + 97 : (temp % 26) + 65);
				code += x;
			}
		}
		return code;
	}
	
	 public static void skipPage(String page){
	    	if(!page.startsWith("/"))page="/"+page;
	    	if(WApplication.getInstance().changedInternalPath(page)){
				WApplication.getInstance().setInternalPath(page);
			}
	 }
	 
	//删除一个目录下的所有文件
	    
	    public static void deleteFiles(String baseDirName, String targetFileName) {
			String tempName = null;
			// 判断目录是否存在
			File baseDir = new File(baseDirName);
			if (!baseDir.exists() || !baseDir.isDirectory()) {
				/*System.out.println("search file fail：" + baseDirName
						+ " is not direct！");*/
			} else {
				String[] filelist = baseDir.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(baseDirName + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						tempName = readfile.getName();
						if (wildcardMatch(targetFileName, tempName)) {
							readfile.getAbsoluteFile().delete();
							if(i==filelist.length-1){//删除该目录下的最后一个文件后，将该目录也删除掉
								baseDir.delete();
							}
						}
					} else if (readfile.isDirectory()) {
						deleteFiles(baseDirName + "\\" + filelist[i], targetFileName);
					} 
				}
			}
		}
	    /**
		 * 通配符匹配
		 * 
		 * @param pattern
		 *            通配符模式
		 * @param str
		 *            待匹配的字符串
		 * @return 匹配成功则返回true，否则返回false
		 */
		private static boolean wildcardMatch(String pattern, String str) {
			int patternLength = pattern.length();
			int strLength = str.length();
			int strIndex = 0;
			char ch;
			for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
				ch = pattern.charAt(patternIndex);
				if (ch == '*') {
					// 通配符星号*表示可以匹配任意多个字符
					while (strIndex < strLength) {
						if (wildcardMatch(pattern.substring(patternIndex + 1),
								str.substring(strIndex))) {
							return true;
						}
						strIndex++;
					}
				} else if (ch == '?') {
					// 通配符问号?表示匹配任意一个字符
					strIndex++;
					if (strIndex > strLength) {
						// 表示str中已经没有字符匹配?了。
						return false;
					}
				} else {
					if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
						return false;
					}
					strIndex++;
				}
			}
			return (strIndex == strLength);
		}
		
		// 复制文件
	    public static void copyFile(File sourceFile, File targetFile) throws IOException {
	        BufferedInputStream inBuff = null;
	        BufferedOutputStream outBuff = null;
	        try {
	            // 新建文件输入流并对它进行缓冲
	            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

	            // 新建文件输出流并对它进行缓冲
	            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

	            // 缓冲数组
	            byte[] b = new byte[1024 * 5];
	            int len;
	            while ((len = inBuff.read(b)) != -1) {
	                outBuff.write(b, 0, len);
	            }
	            // 刷新此缓冲的输出流
	            outBuff.flush();
	        } finally {
	            // 关闭流
	            if (inBuff != null)
	                inBuff.close();
	            if (outBuff != null)
	                outBuff.close();
	        }
	    }
	 public static void infoMessage(String caption,String info){
	    	final WMessageBox msbox = new WMessageBox(caption,info,Icon.Information,EnumSet.of(StandardButton.Ok));
	    	msbox.setWidth(new WLength(200));
	    	msbox.buttonClicked().addListener(null,new Signal.Listener() {
				public void trigger() {
					if (msbox != null)
						msbox.remove();
				}
			});
	    	msbox.show();
	    }
	 public static void infoMessage(String caption,String info,final String url){
	    	final WMessageBox msbox = new WMessageBox(caption,info,Icon.Information,EnumSet.of(StandardButton.Ok));
	    	msbox.setWidth(new WLength(200));
	    	msbox.buttonClicked().addListener(null,new Signal.Listener() {
				public void trigger() {
					if (msbox != null)
						msbox.remove();
					Util.skipPage(url);
				}
			});
	    	msbox.show();
	    }
	 
	 public static String formatDateyyyyMMdd(Date date){
    	SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    	return formatDate.format(date);
    }
    public static String formatDateyyyyMMddHHMMSS(Date date){
    	//SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmss");
    	return formatDate.format(date);
    }
	    
	    /**
		 * 递归查找文件
		 * 
		 * @param baseDirName
		 *            查找的文件夹路径
		 * @param targetFileName
		 *            需要查找的文件名
		 * @param fileList
		 *            查找到的文件集合
		 */
		public static void findFiles(String baseDirName, String targetFileName,
				Map<String, File> fileList) {
			/**
			 * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
			 * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
			 */
			String tempName = null;
			// 判断目录是否存在
			File baseDir = new File(baseDirName);
			if (!baseDir.exists() || !baseDir.isDirectory()) {
				return ;
			} else {
				String[] filelist = baseDir.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(baseDirName + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						tempName = readfile.getName();
						if (wildcardMatch(targetFileName, tempName)) {
							fileList.put(tempName, readfile.getAbsoluteFile());
						}
					} else if (readfile.isDirectory()) {
						findFiles(baseDirName + "\\" + filelist[i], targetFileName,
								fileList);
					}
				}
			}
		}
		/**
	     * 查找图片文件
	     */
	    static public String findImage(String baseDir, String targetFile){
	    	Map<String, File> fileList = new TreeMap<String, File>();
	    	findFiles(baseDir,targetFile+"*",fileList);
	    	fileList.remove("Thumbs.db");
	    	if(fileList.size()>0){
	    		return fileList.keySet().toArray()[0].toString();
//	    	        return fileList.keySet().iterator().next();
	    	}
	    	/*for(Iterator iterator = fileList.keySet().iterator();iterator.hasNext();){
	    		 String key = iterator.next().toString();
	    		 System.out.println("iterator-key:"+key);
	    		 if(!key.equals("Thumbs.db")&&iterator.hasNext())
		    	     return iterator.next().toString();
	    		 
	    	}*/
	    	return "";
	    }
	    
	    public static String URLEncoder(String href,String...params){
	    	if(!href.startsWith("/"))href="/"+href;
			if (params == null)
				return href;
			try {
				String param = "?";
				for (int i = 0; i < params.length;) {
					param += params[i] + "=" + params[i + 1] + "&";
					i += 2;
				}
				param = java.net.URLEncoder.encode(
						param.substring(0, param.length() - 1), "UTF-8");
				href += param;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return href;
	    }
	    public static String URLEString(String original){
	    	try {
				original = java.net.URLEncoder.encode(original, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return original;
	    }
	    public static String URLDString(String original){
	    	try {
				original = java.net.URLDecoder.decode(original, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return original;
	    }
	    
	  
	    public static enum MainMenu{
	    	FRONTMT("fronttmt","前台维护",1,null,""),
	    	INFOPUBLISH("InfoPublish","公司介绍",2,FRONTMT,"setting"),
	    	COMMENTINFO("CommentInfo","公司公告",3,FRONTMT,"create,edit,delete,view,usedc"),
	    	PRICEPOST("GridTable","报价库",4,FRONTMT,"setting"),
	    	ASKPRICEINFO("Askpriceinfo","询价信息",5,FRONTMT,"view,delete"),
	    	REPAIRAPPLY("Repairapply","维修申请",6,FRONTMT,"view,delete"),
	    	INQURYINFO("Inqueryinfo","咨询信息",7,FRONTMT,"view,delete"),
	    	CONTACTINFO("Contactinfo","联系我们",8,FRONTMT,"setting"),
	    	
	    	BASEDATAMAN("BasedataMgr","基础数据管理",9,null,""),
	    	CLIENTMAN("ClientMgr","客户管理",10,BASEDATAMAN,"create,edit,delete,view,usedc,filtrate"),
	    	SUPPLIERMAN("SupplierMgr","供应商管理",11,BASEDATAMAN,"create,edit,delete,view,usedc,filtrate"),
	    	SERVERMAN("StaffMgr","员工管理",12,BASEDATAMAN,"create,edit,delete,view,usedc,filtrate"),
	    	DEPTMAN("DeptMgr","部门管理",13,BASEDATAMAN,"create,edit,delete,view,usedc"),
	    	FILETMAN("GeneralElementMgr","零件通用字典",14,BASEDATAMAN,"create,edit,delete,view,usedc"),
	    	
	    	CONTRACTMAN("ContractMgr","合同管理",15,null,""),
	        SALECONTRACT("SaleDocMgr","销售合同",16,CONTRACTMAN,"create,edit,delete,view,usedc,filtrate"),
	    	BUYCONTRACT("BuyDocMgr","采购合同",17,CONTRACTMAN,"create,edit,delete,view,export,filtrate,usedc"),
	    	
	    	
	    	STOREMAN("StorkMgr","库存管理",24,null,""),
	    	STORECHECK("InventoryInitialMgr","库存盘点",25,STOREMAN,"create,edit,delete,view,filtrate,usedc"),
	    	INPUTMAN("InventoryBookMgr","库存流水",26,STOREMAN,"in_store,out_store,edit,delete,filtrate,usedc"),

	    	STOREWARN("InventoryWarnMgr","库存预警",27,STOREMAN,"setting"),
	    	QUERYCENSUS("InventorySumMgr","查询统计",28,STOREMAN,"setting"),
	    	
	    	SYSMAINTAIN("SysMaintain","系统维护",29,null,""),
	    	APMGR("AccountPMgr","账号权限",34,SYSMAINTAIN,"create,edit,delete,view"),
	    	/*MPMGR("ModifyPwdMgr","修改密码",18,SYSMAINTAIN,false),*/
	    	
	    	WORKODER("WORKORDER","生产管理",30,null,""),
	    	WORKODERMGR("WorkOrderMgr","工单管理",31,WORKODER,"setting,finish,vdetail,edetail,filtrate,export"),
	    	DRAWINGMGR("DrawingMgr","图纸管理",35,WORKODER,"create,edit,delete,view,usedc"),
	    	SURFACEMGR("SurfaceMgr","图面管理",36,WORKODER,"create,edit,delete,view,usedc"),
	    	
	    	//WODETAILMGR("WOdetailMgr","工单明细",55,WORKODER,"setting,finish,vdetail,edetail"),
	    	
	    	PRUDCTINGO("Productinfo","产品信息",32,FRONTMT,"create,edit,delete,view,usedc"),
	    	DEVICETINGO("Deviceinfo","设备信息",33,FRONTMT,"create,edit,delete,view,usedc"),
	    	
	    	COSTMGR("CostMgr","成本管理",37,null,""),
            COSTCONF("CostConfMgr","成本配置",39,COSTMGR,"create,edit,delete,view"),
	    	WOCOSTMGR("WOCostMgr","工单成本信息",38,COSTMGR,"setting");
	    	/*
	    	SETTINGUP("SETTINGUP","安全设置",40,null,false),
	    	PSWDRESET("pswdReset","修改密码",41,SETTINGUP,false),
	    	LOGOUT("logoutBtn","退出登录",42,SETTINGUP,false);*/
	    	
	    	public String name;
	    	public String desc;
	    	public int index_;
	    	public MainMenu parent;
	    	//public boolean dcuse;
	    	public String permission;
	    	private MainMenu(String name,String desc,int index_,MainMenu parent,String permission){
	    	   this.name = name;
	    	   this.desc = desc;
	    	   this.index_ = index_;
	    	   this.parent = parent;
	    	   this.permission = permission;
	    	}
	    	//找到某一菜单下的所有子菜单
	    	public List<MainMenu> getDescendant(){
		    	List<MainMenu> desL = new ArrayList<MainMenu>();
		    	for(MainMenu m :MainMenu.values()){
		    		if(m.parent!=null && m.parent.desc.equals(this.desc))
		    			desL.add(m);
		    	}
		    	return desL;
	    	}
	    	
	    	public static List<MainMenu> getAllDescendant(){
	    		List<MainMenu> desL = new ArrayList<MainMenu>();
	    		for(MainMenu m :MainMenu.values()){
		    		if(m.parent!=null)
		    			desL.add(m);
		    	}
	    		return desL;
	    	}
	    	public static List<MainMenu> getParent(){
	    		List<MainMenu> parentL = new ArrayList<MainMenu>();
	    		for(MainMenu mm:MainMenu.values()){
	    			if(mm.parent==null)
	    				parentL.add(mm);
	    		}
	    		return parentL;
	    	}
	    	public static String getMainMenuDesc(String name){
	    		for(MainMenu mm:MainMenu.values())
	    			if(mm.name!=null&&mm.name.equals(name))
	    				return mm.desc;
	    		
	    		return null;
	    	}
	    	public static String getMainMenuName(String desc){
	    		for(MainMenu mm:MainMenu.values())
	    			if(mm.desc.equals(desc))
	    				return mm.name;
	    		
	    		return "";
	    	}
	    	public String getName(){
				return this.name;
			}
	    	@Override
			public String toString() {
			   return this.name;
			}
	    	//根据汉字名字获取枚举对象
	    	public static MainMenu getMainMenuByDesc(String desc){
	    		for(MainMenu mm:MainMenu.values()){
	    			if(mm.desc.equals(desc))
	    				return mm;
	    		}
	    		return null;
	    	}
	    	//根据编码获取枚举对象
	    	public static MainMenu getMainMenuByName(String name){
	    		for(MainMenu mm:MainMenu.values()){
	    			if(mm.name.trim().equals(name))
	    				return mm;
	    		}
	    		return null;
	    	}
	    	//根据子级编码获取父级菜单
	    	public static MainMenu getParentMenuByName(String name){
	    		MainMenu mm = MainMenu.getMainMenuByName(name);
	    		return mm.parent;
	    	}
	    	//根据子级名称获取父级菜单
	    	public static MainMenu getParentMenuByDesc(String desc){
	    		MainMenu mm = MainMenu.getMainMenuByDesc(desc);
	    		return mm.parent;
	    	}
	    }
	    //单证类型
		public enum DOCType{
			PURCHASE("下料采购单",0),GPURCHASE("一般采购单",3),
			BOOKING("订购单",1),
			AUTHORIZE("面积算法托工单",2),
			GAUTHORIZE("一般托工单",4),
			KGMAUTHORIZE("公斤材质算法托工单",5);
			public String name;
			private int index;
			private DOCType(String name,int index){
				this.name = name;
				this.index = index;
			}
			public int getIndex(){
				return index;			
			}
			public String getName(){
				return this.name;
			}
			@Override
			public String toString() {
			   return this.name;
			}
		}
		//零件加工工艺
		public static enum ElementTechnology{
			OUTERASSIST("外协"),
			FEEDING("下料"),
			MILLING("铣床"),
			HORT("H/T"),
			GRIND("磨床"),
			DISCHARGE("放电"),
			CNC("CNC"),
			WORC("W/C"),
			COATING("表面处理"),
			QORC("Q/C");
			public String name;
			private ElementTechnology(String name){
				this.name = name;
			}
			
			public String getName(){
				return this.name;
			}
			@Override
			public String toString() {
			   return this.name;
			}
		}
		
		   public enum QuotaType{
				TRADI("传统模具",0),MGP("MGP模具",1);
				public String name;
				private int index;
				private QuotaType(String name,int index){
					this.name = name;
				    this.index = index;
				}
				public String getName(){
					return this.name;
				}
				/*@Override
				public String toString() {
				   return this.name;
				}*/
			}
		   //图纸种类
		   public enum DrawingType{
			   FACEDRAWING("图面图纸",0),
			   DETAILDRAWING("图面明细图纸",1);
			   public String name;
			   public int index_;
			   private DrawingType(String name,int index_){
				   this.name = name;
				   this.index_ = index_;
			   }
			   public String getName(){
				   return this.name;
			   }
			  /* @Override
				public String toString() {
				   return this.name;
				}*/
		   }
		   //工单状态
		   public enum WorkOrderStatus{
			   //BEREADY("准备中",0),
			   ACTIVATED("活动中",0),
			   BEDONE("已完结",1);
			   private String name;
			   private int index_;
			   private WorkOrderStatus(String name,int index_){
				   this.name = name;
				   this.index_ = index_;
			   }
			   public String getName(){
				   return this.name;
			   }
			   @Override
				public String toString() {
				   return this.name;
				}
		   }
		   //工单明细状态
		   public enum WOTraceDetailStatus{
			   TRACKING("TRACKING","跟踪中",1),
			   FINISHED("FINISHED","已完成",2);
			   private String name;
			   private String desc_;
			   private int index_;
			   private WOTraceDetailStatus(String name,String desc_,int index_){
				  this.name = name;
				  this.desc_= desc_;
				  this.index_ = index_;
			   }
			   @Override
			   public String toString() {
				 return this.desc_;
			   }
			   public String getName(){
				   return this.name;
			   }
			   public int getIndex(){
				   return this.index_;
			   }
			   public static WOTraceDetailStatus getStatus(String name){
				   for (WOTraceDetailStatus wds:WOTraceDetailStatus.values()){
					   if(wds.getName().equals(name))
						   return wds;
				   }
				   return null;
			   }
			   
		   }
		   //菜单权限
		   public enum PermissionType{
			   ADD("新增",0),
			   EDIT("编辑",1),
			   DELETE("删除",2),
			   VIEW("查看",3),
			   FILTER("筛选",4),
			   DISPLAYALL("显示全部",5),
			   HIDEONE("隐藏",6);
			   public String name;
			   public int index_;
			   private PermissionType(String name,int index_){
				   this.name = name;
				   this.index_ = index_;
			   }
			   public String getName(){
				   return this.name;
			   }
			  /* @Override
				public String toString() {
				   return this.name;
				}*/
		   }
		   //库存流水单类型：入库单、出库单
		   public enum IBookType{
			   INBOOK("入库",0),
			   OUTBOOK("出库",1);
			   public String name;
			   public int index_;
			   private IBookType(String name,int index_){
				   this.name = name;
				   this.index_ = index_;
			   }
			   public String getName(){
				   return this.name;
			   }
			   /*@Override
				public String toString() {
				   return this.name;
				}*/
		   }
		   //库存仓库类别---简称仓别
		   public enum StoreCategory{
			   GUANGLIAOAREA("光料区",0),
			   BANCHENGPINAREA("半成品区",1),
			   PEIJIANAREA("配件区",2),
			   CHENGPINAREA("成品区",3),
			   WORKORDER("工单区",4);
			   public String name;
			   public int index_;
			   private StoreCategory(String name,int index_){
				   this.name = name;
				   this.index_ = index_;
			   }
			   public String getName(){
				   return this.name;
			   }
			   @Override
				public String toString() {
				   return this.name;
				}
		   }
		   
		   public static String ban2quan(String banSrc){
			    char DBC_CHAR_START = 33; // 半角!  
				char DBC_CHAR_END = 126; // 半角~  
				char SBC_CHAR_START = 65281; // 全角！  
				char SBC_CHAR_END = 65374; // 全角～  
				int CONVERT_STEP = 65248; // 全角半角转换间隔  
				char SBC_SPACE = 12288; // 全角空格 12288  
				char DBC_SPACE = ' '; // 半角空格  
				StringBuilder sb = new StringBuilder(banSrc.length());
				char[] banCharArray = banSrc.toCharArray();
				for (int i=0;i<banCharArray.length;i++){
					 if(banCharArray[i]>=DBC_CHAR_START&&banCharArray[i]<=DBC_CHAR_END){
						   sb.append((char)(banCharArray[i]+CONVERT_STEP));
					   }else if(banCharArray[i]==DBC_SPACE){
						   sb.append((char)SBC_SPACE);
					   }else {
						   sb.append((char)banCharArray[i]);
					   }
				}
			   return sb.toString();
		   }
}
