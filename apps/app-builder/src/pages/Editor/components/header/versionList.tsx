import { useEffect, useState, forwardRef, useImperativeHandle, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { useFlowEditorStor } from '@/store/index';
import { Breadcrumb, Button, Form, Message, Tabs, Select } from '@arco-design/web-react';
import { IconArrowLeft, IconSettings } from '@arco-design/web-react/icon';
import { getVersionMgmt, getByBusinessId } from '@onebase/app';
import type { VersionType } from '../constants';
import { VersionStatus } from '../constants';
import styles from './index.module.less';
const Option = Select.Option;

export const VersionListSelect = forwardRef(
  ({ setManageVisible }: { setManageVisible: (value: boolean) => void }, ref) => {
    const [versionList, setVersionList] = useState<VersionType[]>([]);
    const { setBusinessId, setCurrnetFlowId, currentFlowId } = useFlowEditorStor();
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const pageSetId = searchParams.get('pageSetId') || '';

    // 获取版本信息列表
    const getVersionMgmtData = async () => {
      console.log('进来查询了');
      const params = { businessId: pageSetId, sortType: 'create_time' };
      const { list } = await getVersionMgmt(params);
      console.log(list);
      
      setVersionList(list);
    };

    // 获取当前版本信息
    const getCurrentVersion = async () => {
      const { id } = await getByBusinessId({ businessId: pageSetId });
      setCurrnetFlowId(id);
    };
    useEffect(() => {
      getCurrentVersion();
      setBusinessId(pageSetId);
    }, []);

    useEffect(() => {
      getVersionMgmtData();
    }, [currentFlowId]);

    const changeCurrentFlow = (value: string) => {
      if (value !== VersionStatus.MANAGE) {
        setCurrnetFlowId(value);
      } else {
        setManageVisible(true);
      }
    };

    // 使用 useImperativeHandle 暴露方法给父组件
    useImperativeHandle(ref, () => ({
      getVersionMgmtData
    }));
    return (
      <Select
        placeholder="选择流程版本"
        style={{ width: 154 }}
        triggerProps={{
          autoAlignPopupWidth: false,
          autoAlignPopupMinWidth: true,
          position: 'bl'
        }}
        value={currentFlowId}
        arrowIcon={null}
        className={styles.versionSelect}
        onChange={(value) => changeCurrentFlow(value)}
      >
        {versionList.map((item) => (
          <Option key={item.id} value={item.id}>
            <div className={styles.versionOption}>
              <span className={styles.versionName}>
                {item.versionAlias || '未命名'}
                {item.version}
              </span>
              <span
                className={`${styles.versionStatus} ${
                  item.versionStatus === VersionStatus.DESIGNING
                    ? styles.designing
                    : item.versionStatus === VersionStatus.PUBLISHED
                      ? styles.published
                      : styles.history
                }`}
              >
                {item.versionStatus}
              </span>
            </div>
          </Option>
        ))}
        <Option key="manage" value={VersionStatus.MANAGE} className={styles.manageOption}>
          <IconSettings /> 流程版本管理
        </Option>
      </Select>
    );
  }
);
