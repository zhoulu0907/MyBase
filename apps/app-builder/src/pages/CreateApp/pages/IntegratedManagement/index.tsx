import { useAppStore } from '@/store';
import { Menu } from '@arco-design/web-react';
import { IconBranch, IconPlayCircle, IconRefresh, IconTool } from '@arco-design/web-react/icon';
import React from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import styles from './index.module.less';
import FlowEditorPage from './pages/flowEditor';
import FlowExecuteRecordPage from './pages/flowExecuteRecord';
import FlowManagementPage from './pages/flowManagement';

const MenuItem = Menu.Item;

const IntegratedManagementPage: React.FC = () => {
  const navigate = useNavigate();

  const { curAppId } = useAppStore();

  return (
    <div className={styles.integratedManagementPage}>
      <div className={styles.sider}>
        <div className={styles.title}>主菜单</div>
        <Menu className={styles.menu}>
          <MenuItem
            key="flow"
            onClick={() => navigate(`/onebase/create-app/integrated-management/flow-management?appId=${curAppId}`)}
          >
            <IconBranch /> 流程管理
          </MenuItem>
          {/* <MenuItem
            key="automation"
            onClick={() => navigate(`/onebase/create-app/integrated-management/flow-editor?appId=${curAppId}`)}
          >
            <IconHighlight /> 自动化流编辑
          </MenuItem> */}
          <MenuItem key="debug">
            <IconPlayCircle /> 调试中心
          </MenuItem>
          <MenuItem
            key="record"
            onClick={() => navigate(`/onebase/create-app/integrated-management/flow-execute-record?appId=${curAppId}`)}
          >
            <IconRefresh /> 执行记录
          </MenuItem>
          <MenuItem key="node">
            <IconTool /> 节点与连接器
          </MenuItem>
        </Menu>

        {/* 隐藏系统 */}
        {/* <div className={styles.title}>系统</div>
        <Menu className={styles.menu}>
          <MenuItem key="setting">
            <IconSettings />
            设置
          </MenuItem>
          <MenuItem key="help">
            <IconQuestionCircle />
            帮助中心
          </MenuItem>
        </Menu> */}
      </div>
      <div className={styles.content}>
        <Routes>
          <Route path="/" element={<FlowManagementPage />} />
          <Route path="flow-management" element={<FlowManagementPage />} />
          <Route path="flow-editor" element={<FlowEditorPage />} />
          <Route path="flow-execute-record" element={<FlowExecuteRecordPage />} />
        </Routes>
      </div>
    </div>
  );
};

export default IntegratedManagementPage;
