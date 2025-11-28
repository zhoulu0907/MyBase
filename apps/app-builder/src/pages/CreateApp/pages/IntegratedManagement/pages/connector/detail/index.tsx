import { Input, Menu, Message } from '@arco-design/web-react';
import { IconArrowLeft, IconCheck, IconClose, IconEdit } from '@arco-design/web-react/icon';
import {
  getConnectInstance,
  updateConnectInstance,
  type ConnectInstance,
  type UpdateConnectInstanceReq
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ScriptActionListPage from '../action/list';
import ConnectorBaseInfo from './components/baseInfo';
import styles from './index.module.less';

interface ConnectorInstanceDetailProps {}

const ConnectorDetailPage: React.FC<ConnectorInstanceDetailProps> = ({}) => {
  const navigate = useNavigate();
  const [activeKey, setActiveKey] = useState<'base' | 'action' | 'logic'>('base');

  const [baseInfo, setBaseInfo] = useState<ConnectInstance>();
  const [isEditingName, setIsEditingName] = useState(false);
  const [editName, setEditName] = useState('');
  const nameInputRef = useRef<any>(null);

  useEffect(() => {
    const id = getHashQueryParam('id');

    if (id) {
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
    console.log('res :', res);
    if (res) {
      setBaseInfo(res);
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
      const res = await updateConnectInstance({
        connectorId: baseInfo.connectorId,
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
    } catch (error) {
      console.error('Failed to update connector name:', error);
    }
  };

  const handleGoBack = () => {
    const appId = getHashQueryParam('appId');
    navigate(`/onebase/create-app/integrated-management/connector-instances?appId=${appId}`);
  };

  return (
    <div className={styles.connectorInstanceDetail}>
      <div className={styles.sider}>
        <div className={styles.siderHeader}>
          <IconArrowLeft onClick={handleGoBack} className={styles.backIcon} />
          {isEditingName ? (
            <Input
              ref={nameInputRef}
              value={editName}
              onChange={(value) => setEditName(value)}
              className={styles.nameInput}
              size="small"
              onPressEnter={handleSaveName}
            />
          ) : (
            <div className={styles.connectorName}>{baseInfo?.connectorName}</div>
          )}
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
            defaultSelectedKeys={['base']}
            onClickMenuItem={(key) => setActiveKey(key as 'base' | 'action' | 'logic')}
          >
            <Menu.Item key="base">基本信息</Menu.Item>
            <Menu.Item key="action">动作配置</Menu.Item>
            <Menu.Item key="logic">关联逻辑流</Menu.Item>
          </Menu>
        </div>
      </div>
      <div className={styles.content}>
        {activeKey === 'base' && <div>{baseInfo && <ConnectorBaseInfo baseInfo={baseInfo} />}</div>}
        {activeKey === 'action' && (
          <div>
            <ScriptActionListPage />
          </div>
        )}
        {activeKey === 'logic' && <div>关联逻辑流</div>}
      </div>
    </div>
  );
};

export default ConnectorDetailPage;
