package com.example.demo.security;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

public class CaptchaUtil {

	// 產生隨機的四位數字驗證碼
	public static String generateCaptchaCode() {
		return String.format("%04d", new Random().nextInt(10000)); // 0000~9999 的隨機數
	}

	// 產生包含驗證碼的圖片
	public static BufferedImage getCaptchaImage(String code) throws Exception {

		System.setProperty("java.awt.headless", "true"); // 啟用無頭模式

		int w = 100; // 圖寬
		int h = 38; // 圖高

		// 建立圖像暫存區
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		// 建立畫布
		Graphics g = img.getGraphics();

		// 設定背景顏色
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, w, h);

		// 繪製文字
		g.setColor(Color.CYAN); // 設定顏色
		g.setFont(new Font("Default", Font.BOLD, 30)); // 設定字型
		g.drawString(code, 10, 25);

		// 加入干擾線 20 條
		g.setColor(Color.PINK);
		Random random = new Random();
		for (int i = 1; i <= 20; i++) {
			int x1 = random.nextInt(w);
			int y1 = random.nextInt(h);
			int x2 = random.nextInt(w);
			int y2 = random.nextInt(h);
			g.drawLine(x1, y1, x2, y2);
		}

		return img;
	}

	// 將圖片轉換為 Base64 字串
	public static String imageToBase64(BufferedImage image) throws Exception {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", outputStream);
		byte[] imageBytes = outputStream.toByteArray();
		return Base64.getEncoder().encodeToString(imageBytes);
	}

}
