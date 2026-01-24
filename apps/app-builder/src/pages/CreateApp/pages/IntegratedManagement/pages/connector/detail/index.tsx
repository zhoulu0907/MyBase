import { Input, Menu, Message, Button } from '@arco-design/web-react';
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

interface ConnectorInstanceDetailProps { }

const ConnectorDetailPage: React.FC<ConnectorInstanceDetailProps> = ({ }) => {
  const navigate = useNavigate();
  const { tenantId } = useParams();
  const [activeKey, setActiveKey] = useState<'base' | 'env' | 'action' | 'logic' | 'logs'>('base');

  const [baseInfo, setBaseInfo] = useState<ConnectInstance>();
  const [isEditingName, setIsEditingName] = useState(false);
  const [editName, setEditName] = useState('');
  const [isCreateMode, setIsCreateMode] = useState(false); // 是否为创建模式
  const [createName, setCreateName] = useState(''); // 创建模式下的名称
  const [connectorType, setConnectorType] = useState<string>(''); // 连接器类型
  const nameInputRef = useRef<any>(null);

  useEffect(() => {
    const id = getHashQueryParam('id');
    const mode = getHashQueryParam('mode');
    const connectorTypeParam = getHashQueryParam('connectorType');
    const connectorNameParam = getHashQueryParam('connectorName');
    const instanceCountParam = getHashQueryParam('instanceCount');

    // 创建临时的 baseInfo 对象
    const createTempBaseInfo = (typeName?: string, version?: string): ConnectInstance => ({
      id: '',
      applicationId: getHashQueryParam('appId') || '',
      connectorUuid: connectorTypeParam || '',
      connectorName: connectorNameParam || '新连接器实例',
      typeCode: TypeCode.SCRIPT,
      createTime: new Date().toISOString(),
      description: '',
      connectorTypeName: typeName || connectorNameParam || '未知类型',
      version: version || '1.0.0'
    });

    // 检查是否为创建模式
    if (mode === 'create' && connectorTypeParam && !id) {
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
          // 默认名称规则：{连接器类型}实例-{实例数量 + 1}
          const count = parseInt(instanceCountParam || '0', 10);
          const initialName = `${connectorNameParam || '连接器'}实例-${count + 1}`;

          setEditName(initialName);
          setCreateName(initialName);
        })
        .catch((error: any) => {
          console.error('Failed to get connector type info:', error);
          const tempBaseInfo = createTempBaseInfo();
          setBaseInfo(tempBaseInfo);

          const count = parseInt(instanceCountParam || '0', 10);
          const initialName = `${connectorNameParam || '连接器'}实例-${count + 1}`;
          setEditName(initialName);
          setCreateName(initialName);
        });
    } else if (id) {
      // 编辑模式：使用 id 加载现有实例
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
    // 统一使用 ID 获取详情
    const res = await getConnectInstance(id);

    if (res) {
      setBaseInfo(res);
      // 后端接口已经返回完整的连接器信息，不需要再次调用 type-info 接口
    }
  };

  const handleStartEdit = () => {
    if (isCreateMode) {
      setEditName(createName);
      setIsEditingName(true);
    } else if (baseInfo) {
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
    if ((isCreateMode && !createName.trim()) || (!isCreateMode && (!baseInfo || !editName.trim()))) {
      Message.warning('连接器名称不能为空');
      return;
    }

    if (isCreateMode) {
      setCreateName(editName.trim());
      setIsEditingName(false);
      return;
    }

    try {
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
        if (baseInfo?.id) {
          await handleGetIntanceDetail(baseInfo.id);
        }
      }
    } catch (error) {
      console.error('Failed to save connector:', error);
      Message.error('更新失败');
    }
  };

  const handleCreate = async (data: { description: string }) => {
    try {
      const appId = getHashQueryParam('appId');
      const res = await createConnectInstance({
        applicationId: appId || '',
        connectorName: createName,
        description: data.description,
        typeCode: connectorType as any
      });

      if (res) {
        Message.success('创建成功');
        setIsCreateMode(false);
        // 跳转到编辑模式（使用新创建的实例ID）
        navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${appId}&id=${res}`);
      }
    } catch (error) {
      console.error('Failed to create connector:', error);
      Message.error('创建失败');
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
                maxLength={30}
                showWordLimit
              />
            ) : (
              <span>{isCreateMode ? (createName || ' ') : (baseInfo?.connectorName || '')}</span>
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
            <Menu.Item key="env" disabled={isCreateMode}>环境配置</Menu.Item>
            <Menu.Item key="action" disabled={isCreateMode}>动作配置</Menu.Item>
            <Menu.Item key="logic" disabled={isCreateMode}>关联逻辑流</Menu.Item>
            <Menu.Item key="logs" disabled={isCreateMode}>请求日志</Menu.Item>
          </Menu>
        </div>
      </div>
      <div className={styles.content}>
        {activeKey === 'base' && (
          <div className={styles.baseInfoContainer}>
            {baseInfo && (
              <ConnectorBaseInfo
                baseInfo={baseInfo}
                isCreateMode={isCreateMode}
                createName={createName}
                onCreateNameChange={setCreateName}
                onCreate={handleCreate}
              />
            )}
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
              <div className={styles.envContainer}>
                <div className={styles.content}>
                  <div>环境配置内容</div>
                  <div className={styles.footer}>
                    <Button onClick={() => setActiveKey('base')}>上一步</Button>
                    <Button type="primary" onClick={() => setActiveKey('action')}>
                      下一步
                    </Button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
        {activeKey === 'action' && (
          <div className={styles.actionContainer}>
            {!isCreateMode ? (
              <ScriptActionListPage />
            ) : (
              <div className={styles.actionContainer}>
                <div className={styles.content}>
                  <div>动作配置内容</div>
                  <div className={styles.footer}>
                    <Button onClick={() => setActiveKey('env')}>上一步</Button>
                    <Button type="primary" onClick={() => setActiveKey('logic')}>
                      下一步
                    </Button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
        {activeKey === 'logic' && (
          <div className={styles.logicContainer}>
            {!isCreateMode ? (
              <div>关联逻辑流</div>
            ) : (
              <div className={styles.logicContainer}>
                <div className={styles.content}>
                  <div>关联逻辑流内容</div>
                  <div className={styles.footer}>
                    <Button onClick={() => setActiveKey('action')}>上一步</Button>
                    <Button type="primary" onClick={() => setActiveKey('logic')}>
                      完成
                    </Button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
        {activeKey === 'logs' && (
          <div className={styles.logsContainer}>
            <div>请求日志</div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ConnectorDetailPage;
