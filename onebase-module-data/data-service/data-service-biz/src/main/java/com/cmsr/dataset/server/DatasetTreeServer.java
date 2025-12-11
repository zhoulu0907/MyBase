package com.cmsr.dataset.server;

import com.cmsr.api.dataset.DatasetTreeApi;
import com.cmsr.api.dataset.dto.DataSetExportRequest;
import com.cmsr.api.dataset.dto.DatasetNodeDTO;
import com.cmsr.api.dataset.union.DatasetGroupInfoDTO;
import com.cmsr.api.dataset.vo.DataSetBarVO;
import com.cmsr.constant.LogOT;
import com.cmsr.constant.LogST;
import com.cmsr.dataset.manage.DatasetGroupManage;
import com.cmsr.exportCenter.manage.ExportCenterManage;
import com.cmsr.extensions.datasource.dto.DatasetTableDTO;
import com.cmsr.extensions.view.dto.SqlVariableDetails;
import com.cmsr.log.DeLog;
import com.cmsr.model.BusiNodeRequest;
import com.cmsr.model.BusiNodeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("datasetTree")
public class DatasetTreeServer implements DatasetTreeApi {
    @Resource
    private DatasetGroupManage datasetGroupManage;
    @Resource
    private ExportCenterManage exportCenterManage;


    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, st = LogST.DATASET)
    @Override
    public DatasetGroupInfoDTO save(DatasetGroupInfoDTO datasetNodeDTO) throws Exception {
        return datasetGroupManage.save(datasetNodeDTO, false, true);
    }

    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, st = LogST.DATASET)
    @Override
    public DatasetNodeDTO rename(DatasetGroupInfoDTO dto) throws Exception {
        return datasetGroupManage.save(dto, true, false);
    }

    @DeLog(id = "#p0.id", pid = "#p0.pid", ot = LogOT.CREATE, st = LogST.DATASET)
    @Override
    public DatasetNodeDTO create(DatasetGroupInfoDTO dto) throws Exception {
        return datasetGroupManage.save(dto, false, true);
    }

    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, st = LogST.DATASET)
    @Override
    public DatasetNodeDTO move(DatasetGroupInfoDTO dto) throws Exception {
        return datasetGroupManage.move(dto);
    }

    @Override
    public boolean perDelete(Long id) {
        return datasetGroupManage.perDelete(id);
    }

    @DeLog(id = "#p0", ot = LogOT.DELETE, st = LogST.DATASET)
    @Override
    public void delete(Long id) {
        datasetGroupManage.delete(id);
    }


    public List<BusiNodeVO> tree(BusiNodeRequest request) {
        return datasetGroupManage.tree(request);
    }

    @Override
    public DataSetBarVO barInfo(Long id) {
        return datasetGroupManage.queryBarInfo(id);
    }

    @Override
    public DatasetGroupInfoDTO get(Long id) throws Exception {
        return datasetGroupManage.getDatasetGroupInfoDTO(id, "preview");
    }

    @Override
    public DatasetGroupInfoDTO details(Long id) throws Exception {
        return datasetGroupManage.getDetail(id);
    }

    @Override
    public List<DatasetTableDTO> panelGetDsDetails(List<Long> ids) throws Exception {
        return datasetGroupManage.getDetail(ids);
    }

    @Override
    public List<SqlVariableDetails> getSqlParams(List<Long> ids) throws Exception {
        return datasetGroupManage.getSqlParams(ids);
    }

    @Override
    public List<DatasetTableDTO> detailWithPerm(List<Long> ids) throws Exception {
        return datasetGroupManage.getDetailWithPerm(ids);
    }

    @Override
    public void exportDataset(DataSetExportRequest request) throws Exception {
        exportCenterManage.addTask(request.getId(), "dataset", request);
    }

}
