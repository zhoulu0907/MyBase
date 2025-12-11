package com.cmsr.api.sync.datasource.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cmsr.api.sync.datasource.dto.DBTableDTO;
import com.cmsr.api.sync.datasource.dto.DatasourceGridRequest;
import com.cmsr.api.sync.datasource.dto.GetDatasourceRequest;
import com.cmsr.api.sync.datasource.dto.SyncDatasourceDTO;
import com.cmsr.api.sync.datasource.vo.SyncDatasourceVO;
import com.cmsr.auth.DeApiPath;
import com.cmsr.exception.DEException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static com.cmsr.constant.AuthResourceEnum.SYNC_DATASOURCE;

/**
 * @author fit2cloud
 * @date 2023/11/20 10:14
 **/
@DeApiPath(value = "/sync/datasource", rt = SYNC_DATASOURCE)
public interface SyncDatasourceApi {

    @PostMapping("/source/pager/{goPage}/{pageSize}")
    IPage<SyncDatasourceVO> sourcePager(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody DatasourceGridRequest request);

    @PostMapping("/target/pager/{goPage}/{pageSize}")
    IPage<SyncDatasourceVO> targetPager(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody DatasourceGridRequest request);

    @PostMapping("/save")
    void save(@RequestBody SyncDatasourceDTO dataSourceDTO) throws DEException;

    @PostMapping("/update")
    Map<String, Object> update(@RequestBody SyncDatasourceDTO dataSourceDTO) throws DEException;

    @PostMapping("/delete/{datasourceId}")
    void delete(@PathVariable("datasourceId") String datasourceId) throws DEException;

    @GetMapping("/types")
    Object datasourceTypes() throws DEException;

    @PostMapping("/validate")
    String validate(@RequestBody SyncDatasourceDTO dataSourceDTO) throws DEException;

    @PostMapping("/getSchema")
    List<String> getSchema(@RequestBody SyncDatasourceDTO dataSourceDTO) throws DEException;

    @GetMapping("/validate/{datasourceId}")
    SyncDatasourceDTO validate(@PathVariable("datasourceId") String datasourceId) throws DEException;

    @PostMapping("/latestUse/{sourceType}")
    List<String> latestUse(@PathVariable("sourceType") String sourceType);

    @GetMapping("/get/{datasourceId}")
    SyncDatasourceDTO get(@PathVariable("datasourceId") String datasourceId) throws DEException;

    @PostMapping("/batchDel")
    void batchDel(@RequestBody List<String> ids) throws DEException;

    @PostMapping("/fields")
    Map<String, Object> getFields(@RequestBody GetDatasourceRequest getDsRequest) throws DEException;

    @GetMapping("/list/{type}")
    List<SyncDatasourceDTO> listByType(@PathVariable("type") String type) throws DEException;

    @GetMapping("/table/list/{dsId}")
    List<DBTableDTO> getTableList(@PathVariable("dsId") String dsId) throws DEException;


}
