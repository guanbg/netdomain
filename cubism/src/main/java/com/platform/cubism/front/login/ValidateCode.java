package com.platform.cubism.front.login;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ValidateCode", urlPatterns = { "/sys.validatecode.do" })
public class ValidateCode extends HttpServlet {
	private static final long serialVersionUID = 1384331421602205240L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int picWidth = 120;// 验证码图片的宽度
	private int picHeight = 30;// 验证码图片的高度
	private int fontHeight = 24;// 字体高度
	private int codeCount = 4;// 验证码字符个数
	private int codeY = 0;
	private int x = 0;
	char[] codeSequence = { 
			'0', '1', '2', '3','4','5', '6', '7', '8', '9' , 
			'A','B', 'C', 'D', 'E','F', 'G','H', 'I','J', 'K','L', 'M','N', /*'O',*/'P', 'Q','R', 'S','T', 'U','V', 'W','X', 'Y','Z',
			'a','b', 'c', 'd', 'e','f', 'g','h', 'i',/*'j',*/ 'k',/*'l',*/ 'm','n', /*'o',*/'p', 'q','r', 's','t', 'u','v', 'w','x', 'y','z'};

	/**
	 * 初始化验证图片属性
	 */
	public void init(ServletConfig config) throws ServletException {
		// 从web.xml中获取初始信息
		String strWidth = config.getServletContext().getInitParameter("strWidth");// 宽度
		String strHeight = config.getServletContext().getInitParameter("strHeight");// 高度
		String strCount = config.getServletContext().getInitParameter("strCount");// 字符个数
		String strFont = config.getServletContext().getInitParameter("strFont");// 字体高度
		// 将配置的信息转换成数值
		try {
			if (strWidth != null && strWidth.length() != 0) {
				picWidth = Integer.parseInt(strWidth);
			}
			if (strHeight != null && strHeight.length() != 0) {
				picHeight = Integer.parseInt(strHeight);
			}
			if (strCount != null && strCount.length() != 0) {
				codeCount = Integer.parseInt(strCount);
			}
			if (strFont != null && strFont.length() != 0) {
				fontHeight = Integer.parseInt(strFont);
			}
		} catch (NumberFormatException e) {
		}

		x = (picWidth-codeCount) / (codeCount);//每个字符之间有间隔字符
		codeY = picHeight - 3;
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		Random random = new Random();// 创建一个随机数生成器类
		BufferedImage buffImg = new BufferedImage(picWidth, picHeight, BufferedImage.TYPE_INT_RGB);// 定义图像buffer
		Graphics2D g2D = buffImg.createGraphics();
		
		g2D.setColor(Color.WHITE);// 将图像填充为白色
		g2D.fillRect(0, 0, picWidth, picHeight);
		Font font = new Font("Fixedsys Bold", Font.CENTER_BASELINE, fontHeight);// "Fixedsys"
		g2D.setFont(font);// 设置字体
		g2D.drawRect(0, 0, picWidth, picHeight);
		
		g2D.setColor(Color.gray);// 随机产生干扰线，使图象中的认证码不易被其它程序探测到
		for (int i=0; i<20; i++) { 
			int x = random.nextInt(picWidth), y = random.nextInt(picHeight), 
			xl = random.nextInt(picWidth - x), yl = random.nextInt(picHeight - y); 
			g2D.drawLine(x, y, x + xl, y + yl); 
		}

		g2D.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS, java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2D.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));//设置透明度

		StringBuffer randomCode = new StringBuffer();// randomCode用于保存随机产生的验证码，以便用户登录后进行验证
		for (int i = 0; i < codeCount; i++) {// 随机产生codeCount数字的验证码
			int m = random.nextInt(codeSequence.length - 1);
			String strRand = String.valueOf(codeSequence[m]);// 得到随机产生的验证码数字
			m = random.nextInt(20);// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同
			switch (m % 7) {
				case 0:g2D.setColor(Color.black);break;
				case 1:g2D.setColor(Color.red);break;
				case 2:g2D.setColor(Color.blue);break;
				case 3:g2D.setColor(new Color(146,6,207));break;
				case 4:g2D.setColor(new Color(171,189,9));break;//绿
				case 5:g2D.setColor(new Color(211,168,9));break;//金
				case 6:g2D.setColor(new Color(196,5,153));break;//桃红
				default:g2D.setColor(Color.white);
			}
			if(i==0){
				g2D.drawString(strRand, i * x + 1, codeY);
			}
			else{
				g2D.drawString(strRand, i * x + 2, codeY);
			}
			
			randomCode.append(strRand);// 将产生的四个随机数组合在一起
		}
		g2D.dispose();
		
		// 将四位数字的验证码保存到Session中。
		if(request.getSession(false) != null){
			request.getSession().invalidate();
		}		
		request.getSession(true).setAttribute(Login.VALIDATE_CODE_SESSION, randomCode.toString());

		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpg");

		// 将图像输出到Servlet输出流中。
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(buffImg, "jpg", sos);
		sos.flush();
		response.flushBuffer();
		sos.close();
		
		if (logger.isDebugEnabled()) {
			logger.debug("===ValidateCode===>" + randomCode.toString());
		}
	}
}