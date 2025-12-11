package com.cmsr.font.server;

import com.cmsr.api.font.api.FontApi;
import com.cmsr.api.font.dto.FontDto;
import com.cmsr.exception.DEException;
import jakarta.annotation.Resource;
import com.cmsr.font.manage.FontManage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/typeface")
public class FontServer implements FontApi {

    @Resource
    private FontManage fontManage;

    @Override
    public List<FontDto> list(FontDto fontDto) {
        return fontManage.list(fontDto);
    }

    @Override
    public FontDto create(FontDto fontDto) {
        return fontManage.create(fontDto);
    }

    @Override
    public FontDto edit(FontDto fontDto) {
        return fontManage.edit(fontDto);
    }

    @Override
    public void delete(Long id) {
        fontManage.delete(id);
    }

    @Override
    public void changeDefault(FontDto fontDto) {
        fontManage.changeDefault(fontDto);
    }

    @Override
    public FontDto upload(MultipartFile file) throws DEException {
        return fontManage.upload(file);
    }

    @Override
    public void download(String file, HttpServletResponse response) throws DEException {
        fontManage.download(file, response);
    }

    @Override
    public List<FontDto> defaultFont() throws DEException {
        return fontManage.defaultFont();
    }
}
