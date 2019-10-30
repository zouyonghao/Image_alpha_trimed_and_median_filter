package cn.edu.tsinghua.dip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AlphaTrimmedMeanFilter {

    private static int alpha = 12;
    private static int filterSize = 5;

    public static void main(String[] args) throws IOException {
        BufferedImage srcImg = ImageIO.read(new File("img/1.tif"));
        BufferedImage destImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < srcImg.getWidth(); i++) {
            for (int j = 0; j < srcImg.getHeight(); j++) {
                int pixel = filter(i, j, srcImg);
                destImg.setRGB(i, j, new Color(pixel, pixel, pixel).getRGB());
            }
        }

        ImageIO.write(destImg, "png", new File("img/1_result.png"));
    }

    private static int filter(int i, int j, BufferedImage srcImg) {
        int left = i - filterSize / 2;
        int right = i + filterSize / 2;
        int top = j - filterSize / 2;
        int down = j + filterSize / 2;
        int[] result = new int[filterSize * filterSize];
        int count = 0;
        for (int a = left; a <= right; a++) {
            for (int b = top; b <= down; b++) {
                if (a < 0 || a >= srcImg.getWidth() || b < 0 || b >= srcImg.getHeight()) {
                    // pass
                } else {
                    result[count++] = srcImg.getRaster().getSample(a, b, 0);
                }
            }
        }
        Arrays.sort(result);
        int mean = 0;
        for (int a = alpha; a < count - alpha; a++) {
            mean += result[a];
        }
        if (count - 2 * alpha <= 0) {
            return mean / count;
        }
        return mean / (count - 2 * alpha);
    }
}
