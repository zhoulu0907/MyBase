import { useAppStore } from '@/store';
import { Menu } from '@arco-design/web-react';
import { IconBranch, IconCommon, IconLink, IconPlayCircle, IconRefresh, IconTool } from '@arco-design/web-react/icon';
import React from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import styles from './index.module.less';
import ConnectorPage from './pages/connector';
import ConnectorInstancesPage from './pages/connectorInstance';
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
        <Menu className={styles.menu} autoOpen>
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
          <Menu.SubMenu
            key="connectors"
            title={
              <span>
                <IconTool /> 连接器中心
              </span>
            }
          >
            <Menu.Item
              key="connectors-list"
              onClick={() => navigate(`/onebase/create-app/integrated-management/connector?appId=${curAppId}`)}
            >
              <IconLink />
              连接器
            </Menu.Item>
            <Menu.Item
              key="connector-instances"
              onClick={() =>
                navigate(`/onebase/create-app/integrated-management/connector-instances?appId=${curAppId}`)
              }
            >
              <IconCommon />
              连接器实例
            </Menu.Item>
          </Menu.SubMenu>
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
          <Route path="connector" element={<ConnectorPage />} />
          <Route path="connector-instances" element={<ConnectorInstancesPage />} />
        </Routes>
      </div>
    </div>
  );
};

export default IntegratedManagementPage;
