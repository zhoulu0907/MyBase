import { useFlowEditorStor } from '@/store/index';
import { Select } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { getByBusinessUuid, getVersionMgmt } from '@onebase/app';
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react';
import { useLocation } from 'react-router-dom';
import type { VersionType } from '../constants';
import { VersionStatus } from '../constants';
import { BpmVersionStatus } from './bpm/constants';
import styles from './index.module.less';
const Option = Select.Option;

export const VersionListSelect = forwardRef(
  ({ setManageVisible, menuUuid }: { setManageVisible: (value: boolean) => void; menuUuid: string }, ref) => {
    const [versionList, setVersionList] = useState<VersionType[]>([]);
    const { setBusinessId, setCurrnetFlowId, currentFlowId } = useFlowEditorStor();
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const pageSetId = searchParams.get('pageSetId') || '';

    // 获取版本信息列表
    const getVersionMgmtData = async () => {
      const params = { businessUuid: menuUuid, sortType: 'create_time' };
      const { list } = await getVersionMgmt(params);
      setVersionList(list);
    };

    // 获取当前版本信息
    const getCurrentVersion = async () => {
      const { id } = await getByBusinessUuid({ businessUuid: menuUuid });
      setCurrnetFlowId(id);
    };
    useEffect(() => {
      menuUuid && getCurrentVersion();
      setBusinessId(pageSetId);
    }, [menuUuid]);

    useEffect(() => {
      menuUuid && getVersionMgmtData();
    }, [currentFlowId, menuUuid]);

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
                {item.bpmVersionAlias || '未命名'}
                {item.bpmVersion}
              </span>
              <span
                className={`${styles.versionStatus} ${
                  item.bpmVersionStatus === BpmVersionStatus.DESIGNING.VALUE
                    ? styles.designing
                    : item.bpmVersionStatus === BpmVersionStatus.PUBLISHED.VALUE
                      ? styles.published
                      : styles.history
                }`}
              >
                {Object.values(BpmVersionStatus).find((status) => status.VALUE === item.bpmVersionStatus)?.LABEL ||
                  item.bpmVersionStatus}
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
