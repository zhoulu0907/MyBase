import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Message, Radio, Tag } from '@arco-design/web-react';
import { IconCopy, IconMindMapping, IconNav } from '@arco-design/web-react/icon';
import { getDatasourceList } from '@onebase/app';
import dayjs from 'dayjs';
import React, { useEffect, useState, useRef, useCallback } from 'react';
import EntityTable from '../components/EntityTable';
import styles from '../index.module.less';
import { EntityERContainer } from './EntityERContainer';

interface DatasourceRecord {
  id: number;
  datasourceName: string;
  code: string;
  datasourceType: string;
  description: string;
  runMode: number;
  appId: string;
  creator: string;
  createTime: string;
}

const PAGE_TYPE = {
  ER_CHART: 'ER_CHART',
  ENTITY_TABLE: 'ENTITY_TABLE'
};

export const EntityPageContainer: React.FC = () => {
  const [activeTab, setActiveTab] = useState(PAGE_TYPE.ER_CHART);
  const [refreshEntityList, setRefreshEntityList] = useState(false);
  const [onlyUpdateNode, setOnlyUpdateNode] = useState(false);
  const [dsData, setDsData] = useState<DatasourceRecord | null>(null);
  const { curAppId } = useAppStore();
  const { setCurDataSourceId, clearCurDataSourceId } = useResourceStore();
  const prevAppIdRef = useRef<string>('');

  const getAppResources = useCallback(
    async (appId: string) => {
      if (!appId) {
        return;
      }

      try {
        console.log(`开始获取数据源，应用ID: ${appId}`);

        const params = { appId };
        const res = await getDatasourceList(params);

        if (appId !== curAppId) {
          console.log('应用ID已变化');
          return;
        }

        if (res?.length > 0) {
          const dataSource = res?.[0];
          setDsData(dataSource);
          // 将数据源ID存储到store中
          setCurDataSourceId(dataSource.id.toString());
        } else {
          console.warn('未获取到数据源列表');
          setDsData(null);
          clearCurDataSourceId();
        }
      } catch (error) {
        console.error('获取数据源失败:', error);
        setDsData(null);
        clearCurDataSourceId();
      }
    },
    [curAppId, setCurDataSourceId, clearCurDataSourceId]
  );

  useEffect(() => {
    if (!curAppId) {
      return;
    }

    if (prevAppIdRef.current && prevAppIdRef.current !== curAppId) {
      console.log('应用切换，清理旧状态');
      setDsData(null);
      clearCurDataSourceId();
      setRefreshEntityList(false);
      setOnlyUpdateNode(false);
    }

    prevAppIdRef.current = curAppId;

    getAppResources(curAppId);
  }, [curAppId]);

  const handleCopy = (text: string | undefined) => {
    if (text) {
      navigator.clipboard.writeText(text);
      Message.success('已复制到剪贴板');
    }
  };

  return (
    <div className={styles.entityPage}>
      <div className={styles.entityPageHeader}>
        <div className={styles.entityPageHeaderLeft}>
          <span className={styles.entityPageHeaderLeftName}>{dsData?.datasourceName}</span>
          <Tag className={styles.entityPageHeaderLeftTag}>
            数据源编码：{dsData?.code}{' '}
            <IconCopy onClick={() => handleCopy(dsData?.code)} className={styles.copyIcon} fontSize={16} />
          </Tag>
          <Tag className={styles.entityPageHeaderLeftTag}>创建人：{dsData?.creator}</Tag>
          <Tag className={styles.entityPageHeaderLeftTag}>
            创建时间：{dayjs(dsData?.createTime).format('YYYY-MM-DD HH:mm:ss')}
          </Tag>
        </div>

        <div className={styles.entityPageHeaderRight}>
          <Radio.Group
            type="button"
            defaultValue={PAGE_TYPE.ER_CHART}
            style={{ marginLeft: 10 }}
            onChange={(value) => {
              setActiveTab(value);
            }}
          >
            <Radio value={PAGE_TYPE.ER_CHART}>
              <IconMindMapping />
            </Radio>
            <Radio value={PAGE_TYPE.ENTITY_TABLE}>
              <IconNav />
            </Radio>
          </Radio.Group>
        </div>
      </div>

      {activeTab === PAGE_TYPE.ER_CHART && (
        <div className={styles.entityPageContent}>
          <EntityERContainer
            refreshEntityList={refreshEntityList}
            setRefreshEntityList={setRefreshEntityList}
            onlyUpdateNode={onlyUpdateNode}
            setOnlyUpdateNode={setOnlyUpdateNode}
          />
        </div>
      )}

      {activeTab === PAGE_TYPE.ENTITY_TABLE && (
        <div className={styles.entityPageContent}>
          <EntityTable />
        </div>
      )}
    </div>
  );
};
