package com.cmsr.dataset.server;

import com.cmsr.api.dataset.DatasetTree;
import com.cmsr.api.dataset.dto.DataSetExportRequest;
import com.cmsr.api.dataset.dto.DatasetNodeDTO;
import com.cmsr.api.dataset.union.DatasetGroupInfoDTO;
import com.cmsr.api.dataset.vo.DataSetBarVO;
import com.cmsr.dataset.dao.ext.po.DataSetNodePO;
import com.cmsr.dataset.manage.DatasetGroupManage;
import com.cmsr.extensions.datasource.dto.DatasetTableDTO;
import com.cmsr.extensions.datasource.dto.DatasetTableFieldDTO;
import com.cmsr.extensions.view.dto.SqlVariableDetails;
import com.cmsr.model.BusiLeafVO;
import com.cmsr.model.BusiNodeRequest;
import com.cmsr.model.BusiNodeVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("datasetTreeN")
@Service("datasetTreeGet")
public class DatasetTreeImpl implements DatasetTree {

    @Resource
    private DatasetGroupManage datasetGroupManage;

    @Override
    public DatasetNodeDTO save(DatasetGroupInfoDTO dto) throws Exception {
        return null;
    }

    @Override
    public DatasetNodeDTO rename(DatasetGroupInfoDTO dto) throws Exception {
        return null;
    }

    @Override
    public DatasetNodeDTO create(DatasetGroupInfoDTO dto) throws Exception {
        return null;
    }

    @Override
    public DatasetNodeDTO move(DatasetGroupInfoDTO dto) throws Exception {
        return null;
    }

    @Override
    public boolean perDelete(Long id) {
        return false;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<BusiNodeVO> tree(BusiNodeRequest request) {
        return datasetGroupManage.tree(request);
    }

    @Override
    public List<BusiLeafVO> getLeaf(BusiNodeRequest request) {
        return datasetGroupManage.getLeaf(request);
    }

    @Override
    public DataSetBarVO barInfo(Long id) {
        return null;
    }

    @Override
    public DatasetGroupInfoDTO get(Long id) throws Exception {
        return null;
    }

    @Override
    public DatasetGroupInfoDTO details(Long id) throws Exception {
        return null;
    }

    @Override
    public Map panelGetDsDetails(List<Long> ids) throws Exception {
        List<DatasetTableDTO> detail = datasetGroupManage.getDetail(ids);
        Iterator<DatasetTableDTO> iterator = detail.iterator();
        List<Map> list = new ArrayList();
        Map resMap = new HashMap();
        Map map = null;
        while (iterator.hasNext()) {
            DatasetTableDTO next = iterator.next();
            map = new HashMap();
            map.put("datasetName",next.getName());
            map.put("datasetId",next.getId());
            Map<String, List<DatasetTableFieldDTO>> fields = next.getFields();
            List<DatasetTableFieldDTO> dimensionList = fields.get("dimensionList");
            Iterator<DatasetTableFieldDTO> iterator1 = dimensionList.iterator();
            List list1 = new ArrayList();
            List list3 = new ArrayList();
            while (iterator1.hasNext()) {
                Map map1 = new HashMap();
                DatasetTableFieldDTO next1 = iterator1.next();
                map1.put("id",next1.getId());
                map1.put("datasourceId",next1.getDatasourceId());
                map1.put("datasetTableId",next1.getDatasetTableId());
                map1.put("datasetGroupId",next1.getDatasetGroupId());
                map1.put("name",next1.getName());
                map1.put("dataeaseName",next1.getDataeaseName());
                map1.put("deType",next1.getDeType());
                list1.add(next1.getName());
                list3.add(map1);
            }
            map.put("dimension",list1);
            resMap.put("dimensions",list3);
            List<DatasetTableFieldDTO> quotaList = fields.get("quotaList");
            Iterator<DatasetTableFieldDTO> iterator2 = quotaList.iterator();
            List list2 = new ArrayList();
            List list4 = new ArrayList();
            while (iterator2.hasNext()) {
                Map map1 = new HashMap();
                DatasetTableFieldDTO next1 = iterator2.next();
                map1.put("id",next1.getId());
                map1.put("datasourceId",next1.getDatasourceId());
                map1.put("datasetTableId",next1.getDatasetTableId());
                map1.put("datasetGroupId",next1.getDatasetGroupId());
                map1.put("name",next1.getName());
                map1.put("dataeaseName",next1.getDataeaseName());
                map1.put("deType",next1.getDeType());

                //map1.put("")
                list2.add(next1.getName());
                list4.add(map1);
            }
            map.put("metrics",list2);
            resMap.put("metricsMaps",list4);
            list.add(map);
        }
        resMap.put("list",list);
        System.out.println("***");
            //detail.stream().map(datasetTableDTO -> datasetTableDTO.getFields())
        return resMap;
    }

    @Override
    public List<SqlVariableDetails> getSqlParams(List<Long> ids) throws Exception {
        return List.of();
    }

    @Override
    public List<DatasetTableDTO> detailWithPerm(List<Long> ids) throws Exception {
        return List.of();
    }

    @Override
    public void exportDataset(DataSetExportRequest request) throws Exception {

    }
}
