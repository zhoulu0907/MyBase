import { newFieldSignal } from '@/store/singals/new_field';
import { useResourceStore } from '@/store/store_resource';
import { Message, Radio, Tag } from '@arco-design/web-react';
import { IconApps, IconCopy, IconMindMapping, IconNav } from '@arco-design/web-react/icon';
import { getDatasourceList } from '@onebase/app';
import dayjs from 'dayjs';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import EntityTable from '../components/EntityTable';
import styles from '../index.module.less';
import { EntityERContainer } from './EntityERContainer';
import { KnowledgeGraphContainer } from './KnowledgeGraphContainer';

export interface DatasourceRecord {
  id: string;
  datasourceName: string;
  code: string;
  datasourceType: string;
  description: string;
  runMode: number;
  applicationId: string;
  creator: string;
  createTime: string;
}

const PAGE_TYPE = {
  ER_CHART: 'ER_CHART',
  ENTITY_TABLE: 'ENTITY_TABLE',
  KNOWLEDGE_GRAPH: 'KNOWLEDGE_GRAPH'
};

export const EntityPageContainer: React.FC<{ applicationId: string; handleMenuClick: (key: string) => void }> = ({
  applicationId,
  handleMenuClick
}) => {
  const [activeTab, setActiveTab] = useState(PAGE_TYPE.ER_CHART);
  const [refreshEntityList, setRefreshEntityList] = useState(false);
  const [onlyUpdateNode, setOnlyUpdateNode] = useState(false);
  const [dsData, setDsData] = useState<DatasourceRecord | null>(null);
  const { setCurDataSourceId, clearCurDataSourceId } = useResourceStore();
  const prevAppIdRef = useRef<string>('');

  const getAppResources = useCallback(
    async (applicationId: string) => {
      if (!applicationId) {
        return;
      }

      try {
        const params = { applicationId };
        const res = await getDatasourceList(params);

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
    [applicationId]
  );

  useEffect(() => {
    console.log('curAppId', applicationId, prevAppIdRef.current);
    if (!applicationId) {
      return;
    }

    if (prevAppIdRef.current && prevAppIdRef.current !== applicationId) {
      console.log('应用切换，清理旧状态');
      setDsData(null);
      clearCurDataSourceId();
      setRefreshEntityList(false);
      setOnlyUpdateNode(false);
      newFieldSignal.clearAllNewFields();
    }

    prevAppIdRef.current = applicationId;

    getAppResources(applicationId);
  }, [applicationId]);

  useEffect(() => {
    return () => {
      newFieldSignal.clearAllNewFields();
    };
  }, []);

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
            <Radio value={PAGE_TYPE.KNOWLEDGE_GRAPH}>
              <IconApps />
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
            dsData={dsData as DatasourceRecord}
            handleMenuClick={handleMenuClick}
          />
        </div>
      )}

      {activeTab === PAGE_TYPE.ENTITY_TABLE && (
        <div className={styles.entityPageContent}>
          <EntityTable />
        </div>
      )}
      {activeTab === PAGE_TYPE.KNOWLEDGE_GRAPH && (
        <div className={styles.entityPageContent}>
          <KnowledgeGraphContainer datasourceId={dsData?.id} />
        </div>
      )}
    </div>
  );
};
