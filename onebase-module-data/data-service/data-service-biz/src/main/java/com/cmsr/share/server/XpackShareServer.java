package com.cmsr.share.server;

import com.cmsr.api.visualization.request.VisualizationWorkbranchQueryRequest;
import com.cmsr.api.xpack.share.XpackShareApi;
import com.cmsr.api.xpack.share.request.*;
import com.cmsr.api.xpack.share.vo.XpackShareGridVO;
import com.cmsr.api.xpack.share.vo.XpackShareProxyVO;
import com.cmsr.api.xpack.share.vo.XpackShareVO;
import com.cmsr.utils.BeanUtils;
import com.cmsr.share.dao.auto.entity.XpackShare;
import com.cmsr.share.manage.XpackShareManage;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/share")
@RestController
public class XpackShareServer implements XpackShareApi {

    @Resource(name = "xpackShareManage")
    private XpackShareManage xpackShareManage;
    @Override
    public boolean status(Long resourceId) {
        return ObjectUtils.isNotEmpty(xpackShareManage.queryByResource(resourceId));
    }

    @Override
    public void switcher(Long resourceId) {
        xpackShareManage.switcher(resourceId);
    }

    @Override
    public void editExp(XpackShareExpRequest request) {
        xpackShareManage.editExp(request.getResourceId(), request.getExp());
    }

    @Override
    public void editPwd(XpackSharePwdRequest request) {
        xpackShareManage.editPwd(request.getResourceId(), request.getPwd(), request.getAutoPwd());
    }

    @Override
    public XpackShareVO detail(Long resourceId) {
        XpackShare xpackShare = xpackShareManage.queryByResource(resourceId);
        if (ObjectUtils.isEmpty(xpackShare)) return null;
        return BeanUtils.copyBean(new XpackShareVO(), xpackShare);
    }

    @Override
    public List<XpackShareGridVO> query(VisualizationWorkbranchQueryRequest request) {
        return xpackShareManage.query(1, 20, request).getRecords();
    }

    @Override
    public XpackShareProxyVO proxyInfo(XpackShareProxyRequest request) {
        return xpackShareManage.proxyInfo(request);
    }

    @Override
    public boolean validatePwd(XpackSharePwdValidator validator) {
        return xpackShareManage.validatePwd(validator);
    }

    @Override
    public Map<String, String> queryRelationByUserId(Long uid) {
        return xpackShareManage.queryRelationByUserId(uid);
    }

    @Override
    public String editUuid(XpackShareUuidEditor editor) {
        return xpackShareManage.editUuid(editor);
    }
}
