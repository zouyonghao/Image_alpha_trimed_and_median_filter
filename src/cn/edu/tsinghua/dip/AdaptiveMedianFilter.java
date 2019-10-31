package cn.edu.tsinghua.dip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AdaptiveMedianFilter {

    private static final int MAX_FILTER_SIZE = 7;

    public static void main(String[] args) throws IOException {
        BufferedImage srcImg = ImageIO.read(new File("img/2.tif"));
        BufferedImage destImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        int initialSize = 5;

        for (int i = 0; i < srcImg.getWidth(); i++) {
            for (int j = 0; j < srcImg.getHeight(); j++) {
                int pixel = filterA(i, j, srcImg, initialSize);
                destImg.setRGB(i, j, new Color(pixel, pixel, pixel).getRGB());
            }
        }

        ImageIO.write(destImg, "png", new File("img/2_result.png"));
    }

    private static int filterA(int i, int j, BufferedImage srcImg, int filterSize) {
        int left = i - filterSize / 2;
        int right = i + filterSize / 2;
        int top = j - filterSize / 2;
        int down = j + filterSize / 2;
        int[] result = new int[filterSize * filterSize];
        int count = 0;
        int z_min = Integer.MAX_VALUE;
        int z_max = 0;
        for (int a = left; a <= right; a++) {
            for (int b = top; b <= down; b++) {
                if (a >= 0 && a < srcImg.getWidth() && b >= 0 && b < srcImg.getHeight()) {
                    int pixel = srcImg.getRaster().getSample(a, b, 0);
                    result[count++] = pixel;
                    z_max = Math.max(z_max, pixel);
                    z_min = Math.min(z_min, pixel);
                }
            }
        }
        Arrays.sort(result);
        int z_med = result[count / 2];
        int a_1 = z_med - z_min;
        int a_2 = z_med - z_max;
        // 如果中值不是极值，进入过程B
        if (a_1 > 0 && a_2 < 0) {
            int z_xy = srcImg.getRaster().getSample(i, j, 0);
            return filterB(z_xy, z_min, z_max, z_med);
        }
        // 否则增大窗口
        if (filterSize <= MAX_FILTER_SIZE) {
            return filterA(i, j, srcImg, filterSize + 2);
        }
        // 如果超过最大窗口直接输出中值
        return z_med;
    }

    private static int filterB(int z_xy, int z_min, int z_max, int z_med) {
        int b_1 = z_xy - z_min;
        int b_2 = z_xy - z_max;
        // 如果当前像素不是极值，输出当前像素
        if (b_1 > 0 && b_2 < 0) {
            return z_xy;
        }
        // 否则输出中值
        return z_med;
    }
}
