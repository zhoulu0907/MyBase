import { useAppStore } from '@/store';
import { Menu } from '@arco-design/web-react';
import { IconBranch, IconCommon, IconLink, IconPlayCircle, IconRefresh, IconTool } from '@arco-design/web-react/icon';
import React, { useMemo } from 'react';
import { Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import styles from './index.module.less';
import ConnectorPage from './pages/connector/connectorNode';
import ConnectorDetailPage from './pages/connector/detail';
import ConnectorInstancesPage from './pages/connector/instance';
import FlowEditorPage from './pages/flowEditor';
import FlowExecuteRecordPage from './pages/flowExecuteRecord';
import FlowManagementPage from './pages/flowManagement';

const MenuItem = Menu.Item;

// 路径常量
const ROUTE_PATHS = {
  FLOW_MANAGEMENT: 'flow-management',
  FLOW_EXECUTE_RECORD: 'flow-execute-record',
  FLOW_EDITOR: 'flow-editor',
  CONNECTOR: 'connector',
  CONNECTOR_INSTANCES: 'connector-instances',
  CONNECTOR_DETAIL: 'connector-detail'
} as const;

const IntegratedManagementPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { curAppId } = useAppStore();

  // 根据路径映射到菜单 key
  const { selectedKeys, openKeys } = useMemo(() => {
    const pathname = location.pathname;
    let selectedKey: string = 'flow';
    let openKeysList: string[] = [];

    if (pathname.includes(ROUTE_PATHS.FLOW_MANAGEMENT)) {
      selectedKey = 'flow';
    } else if (pathname.includes(ROUTE_PATHS.FLOW_EXECUTE_RECORD)) {
      selectedKey = 'record';
    } else if (pathname.includes(ROUTE_PATHS.CONNECTOR_INSTANCES)) {
      selectedKey = 'connector-instances';
      openKeysList = ['connectors'];
    } else if (pathname.includes(ROUTE_PATHS.CONNECTOR)) {
      selectedKey = 'connectors-list';
      openKeysList = ['connectors'];
    }

    return {
      selectedKeys: [selectedKey],
      openKeys: openKeysList.length > 0 ? openKeysList : ['connectors']
    };
  }, [location.pathname]);

  return (
    <div className={styles.integratedManagementPage}>
      <div className={styles.sider}>
        <Menu className={styles.menu} selectedKeys={selectedKeys} openKeys={openKeys}>
          <MenuItem
            key="flow"
            onClick={() =>
              navigate(`/onebase/create-app/integrated-management/${ROUTE_PATHS.FLOW_MANAGEMENT}?appId=${curAppId}`)
            }
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
            onClick={() =>
              navigate(`/onebase/create-app/integrated-management/${ROUTE_PATHS.FLOW_EXECUTE_RECORD}?appId=${curAppId}`)
            }
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
              onClick={() =>
                navigate(`/onebase/create-app/integrated-management/${ROUTE_PATHS.CONNECTOR}?appId=${curAppId}`)
              }
            >
              <IconLink />
              连接器
            </Menu.Item>
            <Menu.Item
              key="connector-instances"
              onClick={() =>
                navigate(
                  `/onebase/create-app/integrated-management/${ROUTE_PATHS.CONNECTOR_INSTANCES}?appId=${curAppId}`
                )
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
          <Route path={ROUTE_PATHS.FLOW_MANAGEMENT} element={<FlowManagementPage />} />
          <Route path={ROUTE_PATHS.FLOW_EDITOR} element={<FlowEditorPage />} />
          <Route path={ROUTE_PATHS.FLOW_EXECUTE_RECORD} element={<FlowExecuteRecordPage />} />
          <Route path={ROUTE_PATHS.CONNECTOR} element={<ConnectorPage />} />
          <Route path={ROUTE_PATHS.CONNECTOR_INSTANCES} element={<ConnectorInstancesPage />} />
          <Route path={ROUTE_PATHS.CONNECTOR_DETAIL} element={<ConnectorDetailPage />} />
        </Routes>
      </div>
    </div>
  );
};

export default IntegratedManagementPage;
