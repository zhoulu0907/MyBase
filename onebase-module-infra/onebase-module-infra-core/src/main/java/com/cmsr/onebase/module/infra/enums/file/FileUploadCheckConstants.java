package com.cmsr.onebase.module.infra.enums.file;


public interface FileUploadCheckConstants {

    //文件上传安全配置类别
    Long FILE_UPLOAD_CATEGORY = 7L;

    //默认允许上传的文件大小，单位：MB
    Integer FILE_DEFAULT_SIZE = 10;

    //默认允许的文件名长度，单位：字符
    Integer FILE_NAME_DEFAULT_LENGTH = 120;

    //默认放行文件MIMETYPE/magicNum类型
    String UNCHECK = "unCheck";

    //查询默认配置的magicNumber
    String DEFAULT ="default";

    //默认文件名
    String DEFAULT_FILE_NAME = "file";

    //文件类型
    String PDF ="pdf";
    String DOC ="doc";
    String DOCX ="docx";
    String XLS ="xls";
    String XLSX ="xlsx";
    String PPT ="ppt";
    String PPTX ="pptx";
    String JPG ="jpg";
    String JPEG ="jpeg";
    String PNG ="png";
    String GIF ="gif";
    String MP4 ="mp4";

}
