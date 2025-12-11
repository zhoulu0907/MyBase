package com.cmsr.visualization.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmsr.api.visualization.VisualizationBackgroundApi;
import com.cmsr.api.visualization.vo.VisualizationBackgroundVO;
import com.cmsr.i18n.Translator;
import com.cmsr.utils.BeanUtils;
import com.cmsr.visualization.dao.auto.entity.VisualizationBackground;
import com.cmsr.visualization.dao.auto.mapper.VisualizationBackgroundMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : WangJiaHao
 * @date : 2023/6/12 19:31
 */
@RestController
@RequestMapping("/visualizationBackground")
public class VisualizationBackgroundService implements VisualizationBackgroundApi {
    @Resource
    VisualizationBackgroundMapper mapper;

    @Override
    public Map<String, List<VisualizationBackgroundVO>> findAll() {
        List<VisualizationBackground> result = mapper.selectList(new QueryWrapper<>());
        return result.stream().map(vb ->{
            VisualizationBackgroundVO vbVO = new VisualizationBackgroundVO();
            BeanUtils.copyBean(vbVO,vb);
            vbVO.setName(Translator.get("i18n_board")+vbVO.getName());
            return vbVO;
        }).collect(Collectors.groupingBy(VisualizationBackgroundVO::getClassification));
    }
}
