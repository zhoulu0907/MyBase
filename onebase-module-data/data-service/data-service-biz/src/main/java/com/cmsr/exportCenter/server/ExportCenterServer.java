package com.cmsr.exportCenter.server;

import com.cmsr.api.exportCenter.ExportCenterApi;
import com.cmsr.exportCenter.manage.ExportCenterManage;
import com.cmsr.exportCenter.util.ExportCenterUtils;
import com.cmsr.model.ExportTaskDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exportCenter")
@Transactional(rollbackFor = Exception.class)
public class ExportCenterServer implements ExportCenterApi {
    @Resource
    private ExportCenterManage exportCenterManage;

    @Override
    public List<ExportTaskDTO> exportTasks(String status) {
        return exportCenterManage.exportTasks(status);
    }

    @Override
    public void delete(String id) {
        exportCenterManage.delete(id);
    }

    @Override
    public void delete(List<String> ids) {
        exportCenterManage.delete(ids);
    }

    @Override
    public void deleteAll(String type) {
        exportCenterManage.deleteAll(type);
    }

    @Override
    public void download(String id, HttpServletResponse response) throws Exception {
        exportCenterManage.download(id, response);
    }

    @Override
    public void retry(String id) {
        exportCenterManage.retry(id);
    }

    @Override
    public String exportLimit() {
        return String.valueOf(ExportCenterUtils.getExportLimit("dataset"));
    }
}
