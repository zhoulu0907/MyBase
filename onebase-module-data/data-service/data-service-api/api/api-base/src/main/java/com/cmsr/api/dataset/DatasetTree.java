package com.cmsr.api.dataset;

import com.cmsr.api.dataset.dto.DataSetExportRequest;
import com.cmsr.api.dataset.dto.DatasetNodeDTO;
import com.cmsr.api.dataset.union.DatasetGroupInfoDTO;
import com.cmsr.api.dataset.vo.DataSetBarVO;
import com.cmsr.auth.DeApiPath;
import com.cmsr.auth.DePermit;
import com.cmsr.extensions.datasource.dto.DatasetTableDTO;
import com.cmsr.extensions.view.dto.SqlVariableDetails;
import com.cmsr.model.BusiLeafVO;
import com.cmsr.model.BusiNodeRequest;
import com.cmsr.model.BusiNodeVO;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static com.cmsr.constant.AuthResourceEnum.DATASET;

@Tag(name = "数据集:树")
@ApiSupport(order = 980)
@DeApiPath(value = "/datasetTreeN", rt = DATASET)
public interface DatasetTree {
    /**
     * 编辑
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @Operation(summary = "保存数据集", hidden = true)
    @DePermit({"#p0.id+':manage'"})
    @PostMapping("save")
    DatasetNodeDTO save(@RequestBody DatasetGroupInfoDTO dto) throws Exception;

    @Operation(summary = "重命名数据集")
    @DePermit({"#p0.id+':manage'"})
    @PostMapping("rename")
    DatasetNodeDTO rename(@RequestBody DatasetGroupInfoDTO dto) throws Exception;

    /**
     * 新建
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @Operation(summary = "创建数据集")
    @DePermit({"#p0.pid+':manage'"})
    @PostMapping("create")
    DatasetNodeDTO create(@RequestBody DatasetGroupInfoDTO dto) throws Exception;

    @Operation(summary = "移动数据集")
    @DePermit({"#p0.id+':manage'", "#p0.pid+':manage'"})
    @PostMapping("move")
    DatasetNodeDTO move(@RequestBody DatasetGroupInfoDTO dto) throws Exception;

    @Operation(summary = "是否有仪表板、大屏正在使用此数据集")
    @DePermit({"#p0+':manage'"})
    @PostMapping("perDelete/{id}")
    boolean perDelete(@PathVariable("id") Long id);

    @Operation(summary = "删除数据集")
    @DePermit({"#p0+':manage'"})
    @PostMapping("delete/{id}")
    void delete(@PathVariable("id") Long id);

    @Operation(summary = "查询文件夹以及数据集tree")
    @PostMapping("tree")
    List<BusiNodeVO> tree(@RequestBody BusiNodeRequest request);

    @Operation(summary = "查询数据集")
    @PostMapping("leaf")
    public List<BusiLeafVO> getLeaf(@RequestBody BusiNodeRequest request);

    @Operation(summary = "查询数据集对应用户信息")
    @GetMapping("/barInfo/{id}")
    DataSetBarVO barInfo(@PathVariable("id") Long id);

    @Operation(summary = "查询数据集")
    @PostMapping("get/{id}")
    DatasetGroupInfoDTO get(@PathVariable("id") Long id) throws Exception;

    @Operation(summary = "获取数据集详情")
    @PostMapping("details/{id}")
    DatasetGroupInfoDTO details(@PathVariable("id") Long id) throws Exception;

    @Operation(summary = "获取数据集详情")
    @PostMapping("dsDetails")
    Map panelGetDsDetails(@RequestBody List<Long> ids) throws Exception;

    @Operation(summary = "获取SQL参数")
    @PostMapping("getSqlParams")
    List<SqlVariableDetails> getSqlParams(@RequestBody List<Long> ids) throws Exception;

    @Operation(summary = "带权限查询数据集详情")
    @PostMapping("detailWithPerm")
    List<DatasetTableDTO> detailWithPerm(@RequestBody List<Long> ids) throws Exception;

    @DePermit(value = {"#p0.id+':export'"})
    @Operation(summary = "数据集导出")
    @PostMapping("/exportDataset")
    void exportDataset(@RequestBody DataSetExportRequest request) throws Exception;

}
