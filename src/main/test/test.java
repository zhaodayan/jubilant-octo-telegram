import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.junit.Test;

/**
 * 
 * @author admin
 *
 */
public class test {
	private char DBC_CHAR_START = 33; // 半角!  
	private char DBC_CHAR_END = 126; // 半角~  
	private char SBC_CHAR_START = 65281; // 全角！  
	private char SBC_CHAR_END = 65374; // 全角～  
	private int CONVERT_STEP = 65248; // 全角半角转换间隔  
	private char SBC_SPACE = 12288; // 全角空格 12288  
	private char DBC_SPACE = ' '; // 半角空格  
    
	private Integer testInt;
	public List<String> one(String fileurl){
		List<String> tblList = new ArrayList<String>();
		try {
			File file = new File(fileurl);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			//System.out.println("reader.readLine():"+reader.readLine());
			while((tempString=reader.readLine())!=null){
				/*if(!tempString.trim().equals("")&&tempString.trim().contains("CREATE TABLE")){
					int endIndex = tempString.trim().lastIndexOf("`");
					tblList.add(tempString.trim().substring(14,endIndex));
				}*/
				if(!tempString.trim().equals("")){
					//System.out.println("tempString:"+tempString);
					tblList.add(tempString.trim());
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tblList;
	}
	@Test
	public void findTbl(){
		List<String> tbl242 = one("D:\\201501\\242.txt");
		List<String> tbl127 = one("D:\\201501\\local.txt");
		/*tbl242.retainAll(tbl127);
		for(String s:tbl242){
			System.out.println("s:"+s);
		}*/
		tbl242.removeAll(tbl127);
		for(String s:tbl242){
			System.out.println("s:"+s);
		}
		}
	@Test
	public void testScratch(){
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		list1.add("1");
		list1.add("2");
		list1.add("3");
		list2 = list1.subList(0, list1.size());
		System.out.println(list2.size());
	}
	@Test
	public void quanBanConvert(){
	}
	@Test
	public void quan2ban(){
	   String quanSrc = "＆";
	   StringBuilder sb = new StringBuilder(quanSrc.length());
	   char[] quanCharArray = quanSrc.toCharArray();
	   for(int i=0;i<quanSrc.length();i++){
		   if(quanCharArray[i]>=SBC_CHAR_START&&quanCharArray[i]<=SBC_CHAR_END){
			   sb.append((char)(quanCharArray[i]-CONVERT_STEP));
			   System.out.println("kim");
		   }else if(quanCharArray[i]==SBC_SPACE){
			   sb.append((char)DBC_SPACE);
			   System.out.println("soo");
		   }else {
			   sb.append((char)quanCharArray[i]);
			   System.out.println("hyun");
		   }
	   }
	   System.out.println("sb.toString():"+sb.toString());
	}
	@Test
	public void ban2quan(){
		String banSrc = "&";
		StringBuilder sb = new StringBuilder(banSrc.length());
		char[] banCharArray = banSrc.toCharArray();
		for (int i=0;i<banCharArray.length;i++){
			 if(banCharArray[i]>=DBC_CHAR_START&&banCharArray[i]<=DBC_CHAR_END){
				   sb.append((char)(banCharArray[i]+CONVERT_STEP));
				   System.out.println("kim");
			   }else if(banCharArray[i]==DBC_SPACE){
				   sb.append((char)SBC_SPACE);
				   System.out.println("soo");
			   }else {
				   sb.append((char)banCharArray[i]);
				   System.out.println("hyun");
			   }
		}
		System.out.println("sb.toString():"+sb.toString());
	}
	@Test
	public void testDash(){
		String selStr = "2019-12-12";
		int dash1 = selStr.indexOf("-");
		int dash2 = selStr.lastIndexOf("-");
		String dashstr1 = selStr.substring(dash1+1,dash2);
		String dashstr2 = selStr.substring(dash2+1);
		System.out.println("dash1:"+dashstr1+"\t dash2:"+dashstr2);
		if(dashstr1.length()==1)
			selStr = selStr.replace("-"+dashstr1, "-0"+dashstr1);
		if(dashstr2.length()==1)
			selStr = selStr.replace("-"+dashstr2, "-0"+dashstr2);
		
		selStr = selStr.replace("-", "");
		System.out.println("dash1:"+dash1+"\tdash2:"+dash2+"\tselStr:"+selStr);
	}
	
	@Test
	public void testSplit(){
		String testStr = "inuy.sha";
		String[] ss = testStr.split("\\.");
		/*for (String s:ss){
			System.out.println("s:"+s);
		}*/
		System.out.println("ss.length:"+ss.length);
	}
	
	@Test
	public void poiPuppy(){      
        try {
        	OutputStream ops = new FileOutputStream("D:\\inuyasha.xls");
            HSSFWorkbook wb = new HSSFWorkbook();      
            HSSFSheet sheet = wb.createSheet("new   sheet");      
            //HSSFCellStyle style = wb.createCellStyle(); // 样式对象      
     
            /*style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直      
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平      
*/            HSSFRow row = sheet.createRow((short) 0);      
            HSSFRow row2 = sheet.createRow((short) 1);      
     
            sheet.addMergedRegion(new Region(0, (short) 0, 1, (short) 0));      
            HSSFCell ce = row.createCell(0);      
            //ce.setEncoding(HSSFCell.ENCODING_UTF_16);// 中文处理      
            ce.setCellValue("项目\\日期"); // 表格的第一行第一列显示的数据      
            //ce.setCellStyle(style); // 样式，居中      
            int num = 0;      
            for (int i = 0; i < 9; i++) { // 循环9次，每一次都要跨单元格显示      
                // 计算从那个单元格跨到那一格      
                int celln = 0;      
                int celle = 0;      
                if (i == 0) {      
                    celln = 0;      
                    celle = 1;      
                } else {      
                    celln = (i * 2);      
                    celle = (i * 2 + 1);      
                }      
                // 单元格合并      
                // 四个参数分别是：起始行，起始列，结束行，结束列      
                sheet.addMergedRegion(new Region(0, (short) (celln + 1), 0,      
                        (short) (celle + 1)));      
                HSSFCell cell = row.createCell((celln + 1));      
                cell.setCellValue("merging" + i); // 跨单元格显示的数据      
                //cell.setCellStyle(style); // 样式      
                // 不跨单元格显示的数据，如：分两行，上一行分别两格为一格，下一行就为两格，“数量”，“金额”      
                HSSFCell cell1 = row2.createCell(celle);      
                HSSFCell cell2 = row2.createCell((celle + 1));      
                //cell1.setEncoding(HSSFCell.ENCODING_UTF_16);      
                cell1.setCellValue("数量");      
                //cell1.setCellStyle(style);      
                //cell2.setEncoding(HSSFCell.ENCODING_UTF_16);      
                cell2.setCellValue("金额");      
                //cell2.setCellStyle(style);      
                num++;      
            }      
     
            // 在后面加上合计百分比      
     
            // 合计 在最后加上，还要跨一个单元格      
            sheet.addMergedRegion(new Region(0, (short) (2 * num + 1), 0,      
                    (short) (2 * num + 2)));      
            HSSFCell cell = row.createCell((2 * num + 1));      
            //cell.setEncoding(HSSFCell.ENCODING_UTF_16);      
            cell.setCellValue("合计");      
            //cell.setCellStyle(style);      
            HSSFCell cell1 = row2.createCell( (2 * num + 1));      
            HSSFCell cell2 = row2.createCell((2 * num + 2));      
            //cell1.setEncoding(HSSFCell.ENCODING_UTF_16);      
            cell1.setCellValue("数量");      
            //cell1.setCellStyle(style);      
            //cell2.setEncoding(HSSFCell.ENCODING_UTF_16);      
            cell2.setCellValue("金额");      
            //cell2.setCellStyle(style);      
     
            // 百分比 同上      
            sheet.addMergedRegion(new Region(0, (short) (2 * num + 3), 0,      
                    (short) (2 * num + 4)));      
            HSSFCell cellb = row.createCell((2 * num + 3));      
            //cellb.setEncoding(HSSFCell.ENCODING_UTF_16);      
             
            cellb.setCellValue("百分比");      
            //cellb.setCellStyle(style);      
              
            HSSFCell cellb1 = row2.createCell( (2 * num + 3));      
            HSSFCell cellb2 = row2.createCell( (2 * num + 4));      
            //cellb1.setEncoding(HSSFCell.ENCODING_UTF_16);      
            cellb1.setCellValue("数量");      
            //cellb1.setCellStyle(style);      
            //cellb2.setEncoding(HSSFCell.ENCODING_UTF_16);      
            cellb2.setCellValue("金额");      
            //cellb2.setCellStyle(style);      
           
            System.out.print("OK");
            wb.write(ops);
        } catch (Exception ex) {      
            ex.printStackTrace();      
        }      
     
    }      
}
