package com.cmsr.onebase.module.app.build.service.custombutton;

import com.cmsr.onebase.module.app.build.vo.custombutton.*;

public interface AppCustomButtonService {

    CustomButtonPageRespVO page(CustomButtonPageReqVO reqVO);

    CustomButtonDetailRespVO get(Long id);

    Long create(CustomButtonSaveReqVO reqVO);

    Boolean update(CustomButtonSaveReqVO reqVO);

    Boolean delete(Long id);

    Boolean updateStatus(CustomButtonStatusReqVO reqVO);

    Boolean sort(CustomButtonSortReqVO reqVO);
}
