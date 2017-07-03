/*package cn.mazu.auth.mvc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.mazu.webkit.servlet.WebRequest;
import cn.mazu.webkit.servlet.WebResponse;
import cn.mazu.widget.kit.Resource;

public class AuthCode extends Resource {

	@Override
	protected void handleRequest(WebRequest request, WebResponse response)
			throws IOException {
		//Setting the corresponding type, tell the browser output for the content of the picture
		response.setContentType("image/jpeg");
		//set the response headers, tell the browser not to cache the content
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        // output image method
        getRandcode(request, response);
	}
	
	//In the session key
	public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";
    private Random random = new Random();
    //Random string
    private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    //Image width
    private int width = 80;
    //Image height
    private int height = 26;
    //Interference lines number
    private int lineSize = 40;
    //Random number of characters
    private int stringNum = 4;
    
     * Getting font
     
    private Font getFont(){
        return new Font("Fixedsys",Font.CENTER_BASELINE,18);
    }
    
     * Getting color
     
    private Color getRandColor(int fc,int bc){
        if(fc > 255)
            fc = 255;
        if(bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc-fc-16);
        int g = fc + random.nextInt(bc-fc-14);
        int b = fc + random.nextInt(bc-fc-18);
        return new Color(r,g,b);
    }
    *//**
     * Generate random pictures
     *//*
    public void getRandcode(HttpServletRequest request,
            HttpServletResponse response) {
        HttpSession session = request.getSession();
        //BufferedImage is a buffer Image, the Image is used to describe the Image information
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();//����Image�����Graphics����,�Ķ��������ͼ���Ͻ��и��ֻ��Ʋ���
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman",Font.ROMAN_BASELINE,18));
        g.setColor(getRandColor(110, 133));
        //Draw line interference
        for(int i=0;i<=lineSize;i++){
            drowLine(g);
        }
        //Draw random characters
        String randomString = "";
        for(int i=1;i<=stringNum;i++){
            randomString=drowString(g,randomString,i);
        }
        session.removeAttribute(RANDOMCODEKEY);
        session.setAttribute(RANDOMCODEKEY, randomString);
        //System.out.println(randomString);
        g.dispose();
        try {
            ImageIO.write(image, "JPEG", response.getOutputStream());//���ڴ��е�ͼƬͨ��������ʽ������ͻ���
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     * Draw the string
     
    private String drowString(Graphics g,String randomString,int i){
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101),random.nextInt(111),random.nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString.length())));
        randomString +=rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13*i, 16);
        return randomString;
    }
    
     * Draw line interference
     
    private void drowLine(Graphics g){
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x+xl, y+yl);
    }
    
     * Access to random characters
     
    public String getRandomString(int num){
        return String.valueOf(randString.charAt(num));
    }
}
*/