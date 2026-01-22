import { Input, Menu, Message } from '@arco-design/web-react';
import { IconArrowLeft, IconCheck, IconClose, IconEdit } from '@arco-design/web-react/icon';
import {
  getConnectInstance,
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
    const id = getHashQueryParam('id');
    const mode = getHashQueryParam('mode');
    const connectorTypeParam = getHashQueryParam('connectorType');
    const connectorNameParam = getHashQueryParam('connectorName');

    // 检查是否为创建模式
    if (mode === 'create' && connectorTypeParam && !id) {
      setIsCreateMode(true);
      setConnectorType(connectorTypeParam);

      // 获取连接器类型详细信息
      getConnectorTypeInfo(connectorTypeParam)
        .then((typeInfo: any) => {
          if (typeInfo) {
            // 创建一个临时的 baseInfo 对象用于显示
            const tempBaseInfo: ConnectInstance = {
              id: '',
              applicationId: getHashQueryParam('appId') || '',
              connectorUuid: connectorTypeParam,
              connectorName: connectorNameParam || '新连接器实例',
              typeCode: TypeCode.SCRIPT,
              createTime: new Date().toISOString(),
              description: '',
              connectorTypeName: typeInfo.nodeName || connectorNameParam || '未知类型',
              version: typeInfo.version || '1.0.0'
            };
            setBaseInfo(tempBaseInfo);
            setEditName(connectorNameParam || '新连接器实例');
          } else {
            // 接口返回空数据，使用默认值
            const tempBaseInfo: ConnectInstance = {
              id: '',
              applicationId: getHashQueryParam('appId') || '',
              connectorUuid: connectorTypeParam,
              connectorName: connectorNameParam || '新连接器实例',
              typeCode: TypeCode.SCRIPT,
              createTime: new Date().toISOString(),
              description: '',
              connectorTypeName: connectorNameParam || '未知类型',
              version: '1.0.0'
            };
            setBaseInfo(tempBaseInfo);
            setEditName(connectorNameParam || '新连接器实例');
          }
        })
        .catch((error: any) => {
          console.error('Failed to get connector type info:', error);
          // 如果获取失败，使用连接器名称作为类型名称
          const tempBaseInfo: ConnectInstance = {
            id: '',
            applicationId: getHashQueryParam('appId') || '',
            connectorUuid: connectorTypeParam,
            connectorName: connectorNameParam || '新连接器实例',
            typeCode: TypeCode.SCRIPT,
            createTime: new Date().toISOString(),
            description: '',
            connectorTypeName: connectorNameParam || '未知类型',
            version: '1.0.0'
          };
          setBaseInfo(tempBaseInfo);
          setEditName(connectorNameParam || '新连接器实例');
        });
    } else if (id) {
      // 编辑模式：加载现有实例
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

  const handleGetIntanceDetail = async (id: string) => {
    const res = await getConnectInstance(id);
    if (res) {
      setBaseInfo(res);

      // 使用 typeCode 字段来获取连接器类型详细信息
      // 注意：后端的 typeCode 字段存储的是 nodeCode（如 "weaverE9"），而不是类型（如 "script"）
      if (res.typeCode) {
        try {
          const typeInfo = await getConnectorTypeInfo(res.typeCode);
          if (typeInfo) {
            // 更新 baseInfo，添加连接器类型和版本
            setBaseInfo((prev) => ({
              ...prev!,
              connectorTypeName: typeInfo.nodeName || prev?.connectorTypeName,
              version: typeInfo.version || prev?.version
            }));
          }
        } catch (error) {
          console.error('Failed to get connector type info:', error);
          // 接口调用失败时，保持原有数据不变
        }
      }
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
          const id = getHashQueryParam('id');
          if (id) {
            await handleGetIntanceDetail(id);
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
