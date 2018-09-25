package com.ssource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ssource.SFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 2017/9/8.
 */
public class SQRCode {

    private Integer width;
    private Integer height;
    private String format;

    public Integer getWidth() {
        if (this.width == null){
            this.width = 200;
        }
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        if (this.height == null){
            this.height = 200;
        }
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getFormat() {
        if (this.format == null){
            this.format = "png";
        }
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }



    public File creatQrcodeImage(String codeString,String fileName,String pathLocal){
        String content = codeString;
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = null;// 生成矩阵
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, getWidth(), getHeight(), hints);
        } catch (WriterException e) {
            return null;
        }
        boolean isOk = SFile.creatDir(pathLocal + File.separator + "bufferImage" + File.separator);
        if (isOk == false){
            return null;
        }
        File outputFile = new File(pathLocal+ File.separator + "bufferImage" + File.separator + fileName + "." +getFormat());
        Path path = outputFile.toPath();
        try {
            MatrixToImageWriter.writeToPath(bitMatrix, getFormat(), path);// 输出图像
        } catch (IOException e) {
            return null;
        }
        return outputFile;
    }

    public boolean clearAllBufferImage(){
        String dirPath = "." + File.separator + "bufferImage";
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        File dirFile = new File(dirPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();// 获得传入路径下的所有文件
        for (int i = 0; i < files.length; i++) {// 循环遍历删除文件夹下的所有文件(包括子目录)
            if (files[i].isFile()) {// 删除子文件
                flag = SFile.deleteFile(files[i].getAbsolutePath());
                System.out.println(files[i].getAbsolutePath() + " 删除成功");
                if (!flag)
                    break;// 如果删除失败，则跳出
            } else {// 运用递归，删除子目录
                flag = SFile.deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;// 如果删除失败，则跳出
            }
        }
        if (!flag)
            return false;
        if (dirFile.delete()) {// 删除当前目录
            return true;
        } else {
            return false;
        }
    }


}
