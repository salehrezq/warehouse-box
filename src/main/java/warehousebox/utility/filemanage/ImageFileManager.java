/*
 * The MIT License
 *
 * Copyright 2024 Saleh.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package warehousebox.utility.filemanage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Saleh
 */
public class ImageFileManager {

    private static final String HOME = System.getProperty("user.home");
    private static final String slash = File.separator;
    private static final String appImagesPath = HOME + slash + "warehouse-box-images";

    public static boolean copyFileUsingJava7Files(File file, String newGeneratedName, String directoryName) {
        boolean success = false;
        String path = appImagesPath + slash + directoryName;
        Path destPath = Paths.get(path);
        Optional<String> extension = getExtensionByStringHandling(file.getName());
        Path destFile = Paths.get(path + slash + newGeneratedName);
        try {
            Files.createDirectories(destPath);
            Files.copy(file.toPath(), destFile, StandardCopyOption.REPLACE_EXISTING);
            success = !Files.exists(destFile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    public static boolean saveBufferedImageToFileSystem(
            BufferedImage bufferedImage,
            String newGeneratedName,
            String directoryName) {
        boolean success = false;
        String path = appImagesPath + slash + directoryName;
        Path destPath = Paths.get(path);
        try {
            Files.createDirectories(destPath);
            File outputfile = new File(path + slash + newGeneratedName);
            success = ImageIO.write(bufferedImage, getExtensionByStringHandling(newGeneratedName).get(), outputfile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    public static boolean delete(String filename, String directoryName) {
        boolean success = false;
        String path = appImagesPath + slash + directoryName;
        try {
            Path destFile = Paths.get(path + slash + filename);
            Files.delete(destFile);
            success = !Files.exists(destFile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    public static String generateImageName(File file) {
        Optional<String> extension = getExtensionByStringHandling(file.getName());
        UUID fileNewName = generateType1UUID();
        return fileNewName + "." + extension.get();
    }

    private static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    private static long get64MostSignificantBitsForVersion1() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long version = 1 << 12;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | version | time_hi;
    }

    public static UUID generateType1UUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits);
    }

    public static BufferedImage loadImageToBufferedImage(String imageName, String directoryName) {
        String path = appImagesPath + slash + directoryName;
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path + slash + imageName));
        } catch (IOException ex) {
            System.err.println("Issue image name " + imageName);
            Logger.getLogger(ImageFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }
}
