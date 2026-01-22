import { Input, Menu, Message } from '@arco-design/web-react';
import { IconArrowLeft, IconCheck, IconClose, IconEdit } from '@arco-design/web-react/icon';
import {
  getConnectInstance,
  getConnectorByUuid,
  updateConnectInstance,
  createConnectInstance,
  getConnectorTypeInfo,
  TypeCode,
  type ConnectInstance,
  type UpdateConnectInstanceReq
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ScriptActionListPage from '../action/list';
import ConnectorBaseInfo from './components/baseInfo';
import styles from './index.module.less';

interface ConnectorInstanceDetailProps {}

const ConnectorDetailPage: React.FC<ConnectorInstanceDetailProps> = ({}) => {
  const navigate = useNavigate();
  const { tenantId } = useParams();
  const [activeKey, setActiveKey] = useState<'base' | 'env' | 'action' | 'logic' | 'logs'>('base');

  const [baseInfo, setBaseInfo] = useState<ConnectInstance>();
  const [isEditingName, setIsEditingName] = useState(false);
  const [editName, setEditName] = useState('');
  const [isCreateMode, setIsCreateMode] = useState(false); // 是否为创建模式
  const [connectorType, setConnectorType] = useState<string>(''); // 连接器类型
  const nameInputRef = useRef<any>(null);

  useEffect(() => {
    const connectorUuid = getHashQueryParam('connectorUuid');
    const id = getHashQueryParam('id');
    const mode = getHashQueryParam('mode');
    const connectorTypeParam = getHashQueryParam('connectorType');
    const connectorNameParam = getHashQueryParam('connectorName');

    // 创建临时的 baseInfo 对象
    const createTempBaseInfo = (typeName?: string, version?: string): ConnectInstance => ({
      id: '',
      applicationId: getHashQueryParam('appId') || '',
      connectorUuid: connectorTypeParam,
      connectorName: connectorNameParam || '新连接器实例',
      typeCode: TypeCode.SCRIPT,
      createTime: new Date().toISOString(),
      description: '',
      connectorTypeName: typeName || connectorNameParam || '未知类型',
      version: version || '1.0.0'
    });

    // 检查是否为创建模式
    if (mode === 'create' && connectorTypeParam && !connectorUuid && !id) {
      setIsCreateMode(true);
      setConnectorType(connectorTypeParam);

      // 获取连接器类型详细信息
      getConnectorTypeInfo(connectorTypeParam)
        .then((typeInfo: any) => {
          const tempBaseInfo = createTempBaseInfo(
            typeInfo?.nodeName,
            typeInfo?.version
          );
          setBaseInfo(tempBaseInfo);
          setEditName(connectorNameParam || '新连接器实例');
        })
        .catch((error: any) => {
          console.error('Failed to get connector type info:', error);
          const tempBaseInfo = createTempBaseInfo();
          setBaseInfo(tempBaseInfo);
          setEditName(connectorNameParam || '新连接器实例');
        });
    } else if (connectorUuid) {
      // 编辑模式：使用 connectorUuid 加载现有实例
      handleGetIntanceDetail(connectorUuid);
    } else if (id) {
      // 兼容旧模式：使用 id 加载现有实例
      handleGetIntanceDetail(id);
    }
  }, []);

  useEffect(() => {
    if (isEditingName && nameInputRef.current) {
      nameInputRef.current.focus();
    }
  }, [isEditingName]);

  useEffect(() => {
    if (baseInfo) {
      setEditName(baseInfo.connectorName);
    }
  }, [baseInfo]);

  const handleGetIntanceDetail = async (param: string) => {
    // 判断参数格式，UUID 格式使用 connectorUuid，否则使用 id
    const isUuid = param.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
    const res = isUuid ? await getConnectorByUuid(param) : await getConnectInstance(param);

    if (res) {
      setBaseInfo(res);
      // 后端接口 /connector/get 已经返回完整的连接器信息，不需要再次调用 type-info 接口
    }
  };

  const handleStartEdit = () => {
    if (baseInfo) {
      setEditName(baseInfo.connectorName);
      setIsEditingName(true);
    }
  };

  const handleCancelEdit = () => {
    if (baseInfo) {
      setEditName(baseInfo.connectorName);
    }
    setIsEditingName(false);
  };

  const handleSaveName = async () => {
    if (!baseInfo || !editName.trim()) {
      Message.warning('连接器名称不能为空');
      return;
    }

    try {
      if (isCreateMode) {
        // 创建模式：调用创建接口
        const appId = getHashQueryParam('appId');
        const res = await createConnectInstance({
          applicationId: appId || '',
          connectorName: editName.trim(),
          description: baseInfo.description,
          typeCode: connectorType as any
        });

        if (res) {
          Message.success('创建成功');
          setIsCreateMode(false);
          setIsEditingName(false);

          // 跳转到编辑模式（使用新创建的实例ID）
          navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${appId}&id=${res}`);
        }
      } else {
        // 编辑模式：调用更新接口
        const res = await updateConnectInstance({
          id: baseInfo.id,
          connectorName: editName.trim(),
          description: baseInfo.description
        } as UpdateConnectInstanceReq);

        if (res) {
          Message.success('更新成功');
          setIsEditingName(false);
          // 刷新数据
          const connectorUuid = getHashQueryParam('connectorUuid');
          const id = getHashQueryParam('id');
          const param = connectorUuid || id;
          if (param) {
            await handleGetIntanceDetail(param);
          }
        }
      }
    } catch (error) {
      console.error('Failed to save connector:', error);
      Message.error(isCreateMode ? '创建失败' : '更新失败');
    }
  };

  const handleGoBack = () => {
    const appId = getHashQueryParam('appId');
    navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-instances?appId=${appId}`);
  };

  return (
    <div className={styles.connectorInstanceDetail}>
      <div className={styles.sider}>
        <div className={styles.siderHeader}>
          <IconArrowLeft onClick={handleGoBack} className={styles.backIcon} />
          <div className={styles.connectorName}>
            {isEditingName ? (
              <Input
                ref={nameInputRef}
                value={editName}
                onChange={(value) => setEditName(value)}
                className={styles.nameInput}
                size="small"
                onPressEnter={handleSaveName}
                placeholder={isCreateMode ? '请输入连接器实例名称' : ''}
              />
            ) : (
              <span>{baseInfo?.connectorName || (isCreateMode ? '新连接器实例' : '')}</span>
            )}
          </div>
          {isEditingName ? (
            <div className={styles.editActions}>
              <IconCheck className={styles.icon} onClick={handleSaveName} />
              <IconClose className={styles.icon} onClick={handleCancelEdit} />
            </div>
          ) : (
            <IconEdit className={styles.icon} onClick={handleStartEdit} />
          )}
        </div>
        <div className={styles.sideMenu}>
          <Menu
            style={{ width: '100%' }}
            selectedKeys={[activeKey]}
            onClickMenuItem={(key) => setActiveKey(key as 'base' | 'env' | 'action' | 'logic' | 'logs')}
          >
            <Menu.Item key="base">基本信息</Menu.Item>
            <Menu.Item key="env">环境配置</Menu.Item>
            <Menu.Item key="action">动作配置</Menu.Item>
            <Menu.Item key="logic">关联逻辑流</Menu.Item>
            <Menu.Item key="logs">请求日志</Menu.Item>
          </Menu>
        </div>
      </div>
      <div className={styles.content}>
        {activeKey === 'base' && (
          <div className={styles.baseInfoContainer}>
            {baseInfo && <ConnectorBaseInfo baseInfo={baseInfo} />}
            {!baseInfo && isCreateMode && (
              <div className={styles.loadingContainer}>加载中...</div>
            )}
          </div>
        )}
        {activeKey === 'env' && (
          <div className={styles.envContainer}>
            {!isCreateMode ? (
              <div>环境配置</div>
            ) : (
              <div className={styles.emptyState}>
                <p>请先保存连接器实例基本信息后，再配置环境</p>
              </div>
            )}
          </div>
        )}
        {activeKey === 'action' && (
          <div className={styles.actionContainer}>
            {!isCreateMode ? (
              <ScriptActionListPage />
            ) : (
              <div className={styles.emptyState}>
                <p>请先保存连接器实例基本信息后，再配置动作</p>
              </div>
            )}
          </div>
        )}
        {activeKey === 'logic' && (
          <div className={styles.logicContainer}>
            {!isCreateMode ? (
              <div>关联逻辑流</div>
            ) : (
              <div className={styles.emptyState}>
                <p>请先保存连接器实例基本信息后，再配置关联逻辑流</p>
              </div>
            )}
          </div>
        )}
        {activeKey === 'logs' && (
          <div className={styles.logsContainer}>
            {!isCreateMode ? (
              <div>请求日志</div>
            ) : (
              <div className={styles.emptyState}>
                <p>请先保存连接器实例基本信息后，再查看请求日志</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ConnectorDetailPage;
