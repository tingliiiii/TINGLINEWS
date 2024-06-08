package com.example.demo.security;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

public class CaptchaUtil {

	public static String generateCaptchaCode() {
		return String.format("%04d", new Random().nextInt(10000)); // 0000~9999 的隨機數
	}

	public static BufferedImage getCaptchaImage(String code) {

		int w = 100; // 圖寬
		int h = 38; // 圖高
		// 建立圖像暫存區
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		try {
			// 建立畫布
			Graphics g = img.getGraphics();
			// 設定顏色
			g.setColor(Color.YELLOW);
			// 塗滿背景
			g.fillRect(0, 0, w, h);
			// 繪製文字
			g.setColor(Color.CYAN); // 設定顏色
			g.setFont(new Font("新細明體", Font.BOLD, 30)); // 設定字型
			g.drawString(code, 10, 25);
			// 加入干擾線
			g.setColor(Color.RED);
			Random random = new Random();
			for (int i = 1; i <= 20; i++) {
				int x1 = random.nextInt(w);
				int y1 = random.nextInt(h);
				int x2 = random.nextInt(w);
				int y2 = random.nextInt(h);
				g.drawLine(x1, y1, x2, y2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return img;
	}

	public static String imageToBase64(BufferedImage image) throws IOException {

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			ImageIO.write(image, "JPEG", bos);
			byte[] imageBytes = bos.toByteArray();
			return Base64.getEncoder().encodeToString(imageBytes);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
