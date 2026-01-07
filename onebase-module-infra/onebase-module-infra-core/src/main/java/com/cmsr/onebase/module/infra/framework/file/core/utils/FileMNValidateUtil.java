package com.cmsr.onebase.module.infra.framework.file.core.utils;

import cn.hutool.core.io.FileMagicNumber;
import com.cmsr.onebase.module.infra.enums.file.FileUploadCheckConstants;

/**
 * 功能概要：文件魔数(Magic Number)验证
 *
 */
public final class FileMNValidateUtil {

    /**
     * 校验文件头魔数是否匹配
     * @param content 文件内容
     * @param extension 文件扩展名
     * @return 是否匹配
     */
    public static boolean isValidDefaultMagicNumber(byte[] content, String extension) {
        return switch (extension) {
            case FileUploadCheckConstants.PDF -> FileMagicNumber.PDF.match(content);
            case FileUploadCheckConstants.DOC -> FileMagicNumber.DOC.match(content);
            case FileUploadCheckConstants.DOCX -> FileMagicNumber.DOCX.match(content);
            case FileUploadCheckConstants.XLS -> FileMagicNumber.XLS.match(content);
            case FileUploadCheckConstants.XLSX -> FileMagicNumber.XLSX.match(content);
            case FileUploadCheckConstants.PPT -> FileMagicNumber.PPT.match(content);
            case FileUploadCheckConstants.PPTX -> FileMagicNumber.PPTX.match(content);
            case FileUploadCheckConstants.JPG, FileUploadCheckConstants.JPEG -> FileMagicNumber.JPEG.match(content) || FileMagicNumber.PNG.match(content);
            case FileUploadCheckConstants.PNG -> FileMagicNumber.PNG.match(content);
            case FileUploadCheckConstants.GIF -> FileMagicNumber.GIF.match(content);
            // 可添加更多文件类型的魔数校验
            default -> false;
        };
    }

    /**
     * 校验文件头魔数是否匹配
     * @param sourceBytes 文件内容
     * @param expectMagicNumber 文件扩展名
     */
    public static void isValidCustomMagicNumber(byte[] sourceBytes, String expectMagicNumber,String ext) {
        int byteLen = expectMagicNumber.length() % 2;
        if (byteLen != 0) {
            throw new IllegalArgumentException("扩展名【" + ext + "】的魔数定义错误！魔数必须定义为偶数！");
        }

        // 期望魔数值的字节长度
        int expectMNLen = expectMagicNumber.length() / 2;
        if (sourceBytes.length < expectMNLen) {
            // 文件总字节数小于该文件类型的期望魔数值的字节长度时，拒绝上传
            throw new IllegalArgumentException("文件无效");
        }
        // 获取期望魔数字节等长度的实际文件的魔数值
        byte[] buff = new byte[expectMNLen];
        //如果文件为mp4做一个特殊处理，将之前魔数从第0位开始取到第7位，改成了魔数从第4位开始取到第7位。
        if (FileUploadCheckConstants.MP4.equals(ext)) {
            System.arraycopy(sourceBytes, 4, buff, 0, expectMNLen);
        } else {
            System.arraycopy(sourceBytes, 0, buff, 0, expectMNLen);
        }
        // 把实际魔数值转换成16进制
        String actualMagicNumber = bytesToHexString(buff);
        // 比较实际魔数值和期望魔数值
        if (!expectMagicNumber.equalsIgnoreCase(actualMagicNumber)) {
            throw new IllegalArgumentException("上传失败，文件可能已被篡改");
        }
    }

    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray 字节数组
     * @return 16进制大写字符串
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


}