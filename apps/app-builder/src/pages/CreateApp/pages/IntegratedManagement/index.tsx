import { useAppStore } from '@/store';
import { Menu } from '@arco-design/web-react';
import { IconBranch, IconCommon, IconLink, IconPlayCircle, IconRefresh, IconTool } from '@arco-design/web-react/icon';
import React, { useMemo } from 'react';
import { Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom';
import styles from './index.module.less';
import ConnectorPage from './pages/connector/connectorNode';
import ConnectorDetailPage from './pages/connector/detail';
import ConnectorInstancesPage from './pages/connector/instance';
import ConnectorCreateWizard from './pages/connector/createWizard';
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
  CONNECTOR_DETAIL: 'connector-detail',
  CONNECTOR_CREATE: 'connector-create'
} as const;

const IntegratedManagementPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { tenantId } = useParams();

  const { curAppId } = useAppStore();

  // 根据路径映射到菜单 key
  const { selectedKeys, openKeys } = useMemo(() => {
    const pathname = location.pathname;
    let selectedKey: string = 'flow';
    let openKeysList: string[] = [];

    // 检查URL参数来判断是否为创建模式
    // 尝试从 hash 和 search 中获取参数
    let searchParams = new URLSearchParams();
    try {
      // 从 location.hash 中解析参数（格式：#xxx?appId=123&mode=select）
      const hashQuery = location.hash.split('?')[1];
      // 从 location.search 中解析参数（格式：?appId=123&mode=select）
      const searchQuery = location.search.substring(1);

      searchParams = new URLSearchParams(hashQuery || searchQuery || '');
    } catch (e) {
      console.error('Failed to parse URL params:', e);
    }

    const isCreateMode = searchParams.get('mode') === 'select' || searchParams.get('mode') === 'create';

    console.log('Menu activation check:', {
      pathname,
      hash: location.hash,
      search: location.search,
      isCreateMode,
      selectedKey: selectedKey
    });

    if (pathname.includes(ROUTE_PATHS.FLOW_MANAGEMENT)) {
      selectedKey = 'flow';
    } else if (pathname.includes(ROUTE_PATHS.FLOW_EXECUTE_RECORD)) {
      selectedKey = 'record';
    } else if (pathname.includes(ROUTE_PATHS.CONNECTOR_INSTANCES)) {
      // 连接器实例列表页
      selectedKey = 'connector-instances';
      openKeysList = ['connectors'];
    } else if (pathname.includes(ROUTE_PATHS.CONNECTOR_CREATE)) {
      // 连接器创建向导页 - 应该高亮"连接器实例"
      selectedKey = 'connector-instances';
      openKeysList = ['connectors'];
    } else if (pathname.includes(ROUTE_PATHS.CONNECTOR_DETAIL)) {
      // 连接器详情页：根据模式判断高亮哪个菜单
      // 如果有 id 参数（编辑现有实例）或有 mode=create（从实例列表创建）或有 connectorUuid，高亮"连接器实例"
      const hasId = searchParams.get('id');
      const hasUuid = searchParams.get('connectorUuid');
      const isFromInstances = isCreateMode || hasId || hasUuid;

      if (isFromInstances) {
        selectedKey = 'connector-instances';
      } else {
        selectedKey = 'connectors-list';
      }
      openKeysList = ['connectors'];
    } else if (pathname.includes(ROUTE_PATHS.CONNECTOR)) {
      // 连接器类型页
      if (isCreateMode) {
        // 如果是从实例列表创建，高亮"连接器实例"
        selectedKey = 'connector-instances';
      } else {
        // 否则高亮"连接器"
        selectedKey = 'connectors-list';
      }
      openKeysList = ['connectors'];
    }

    return {
      selectedKeys: [selectedKey],
      openKeys: openKeysList.length > 0 ? openKeysList : ['connectors']
    };
  }, [location.pathname, location.hash, location.search]);

  return (
    <div className={styles.integratedManagementPage}>
      <div className={styles.sider}>
        <Menu className={styles.menu} selectedKeys={selectedKeys} openKeys={openKeys}>
          <MenuItem
            key="flow"
            onClick={() =>
              navigate(
                `/onebase/${tenantId}/home/create-app/integrated-management/${ROUTE_PATHS.FLOW_MANAGEMENT}?appId=${curAppId}`
              )
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
              navigate(
                `/onebase/${tenantId}/home/create-app/integrated-management/${ROUTE_PATHS.FLOW_EXECUTE_RECORD}?appId=${curAppId}`
              )
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
                navigate(
                  `/onebase/${tenantId}/home/create-app/integrated-management/${ROUTE_PATHS.CONNECTOR}?appId=${curAppId}`
                )
              }
            >
              <IconLink />
              连接器
            </Menu.Item>
            <Menu.Item
              key="connector-instances"
              onClick={() =>
                navigate(
                  `/onebase/${tenantId}/home/create-app/integrated-management/${ROUTE_PATHS.CONNECTOR_INSTANCES}?appId=${curAppId}`
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
          <Route path={ROUTE_PATHS.CONNECTOR_CREATE} element={<ConnectorCreateWizard />} />
        </Routes>
      </div>
    </div>
  );
};

export default IntegratedManagementPage;
