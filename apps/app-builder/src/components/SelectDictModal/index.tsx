import { useEffect, useMemo, useState } from 'react';
import { Button, Collapse, Empty, Input, Modal, Spin, Tabs, Typography, Space } from '@arco-design/web-react';
import type { DictData, DictItem } from '@onebase/platform-center';
import { getAllDictList, getDictDataListByType } from '@onebase/platform-center';
import { TokenManager } from '@onebase/common';
import { useNavigate } from 'react-router-dom';
import { StatusEnum } from '@onebase/platform-center';
import styles from './index.module.less';

export interface SelectDictModalProps {
  appId: string;
  visible: boolean;
  onOk: (dict?: DictItem) => void;
  onCancel: () => void;
  dictTypeId?: string; // 回显参数：传入时自动选中并展开对应字典
  gotoDictPage?: () => void;
}

// 预览展示的最大数量
const PREVIEW_MAX = 5;

const DICT_OWNER_TYPE = {
  APP: 'app',
  TENANT: 'tenant'
};

export default function SelectDictModal({
  appId,
  visible,
  onOk,
  onCancel,
  dictTypeId,
  gotoDictPage
}: SelectDictModalProps) {
  const [activeTab, setActiveTab] = useState<string>(DICT_OWNER_TYPE.APP);
  const [loadingList, setLoadingList] = useState(false);
  const [showMoreMap, setShowMoreMap] = useState<Record<string, boolean>>({});
  const [dictList, setDictList] = useState<DictItem[]>([]);
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [previewMap, setPreviewMap] = useState<Record<string, { loading: boolean; data: DictData[] }>>({});
  const [selectedId, setSelectedId] = useState<string | number>('');
  const [search, setSearch] = useState('');

  const navigate = useNavigate();
  const tenantId = TokenManager.getTenantInfo()?.tenantId;

  const loadList = async (ownerType: string) => {
    setLoadingList(true);
    try {
      const list = await getAllDictList({ dictOwnerType: ownerType, dictOwnerId: getDictOwnerId() });
      const enabledList = list.filter((d) => d.status === StatusEnum.ENABLE);
      setDictList(enabledList || []);
    } finally {
      setLoadingList(false);
    }
  };

  // 弹窗打开或tab切换时重置并加载
  useEffect(() => {
    if (!visible) return;
    if (!dictTypeId) {
      setSelectedId('');
      setExpandedKeys([]);
    }
    setPreviewMap({});
    setShowMoreMap({});
    loadList(activeTab);
  }, [visible, activeTab]);

  // 当 dictTypeId 有值，选中字典
  useEffect(() => {
    if (!dictTypeId || dictList.length === 0 || loadingList) return;

    const targetDict = dictList.find((d) => d.id === dictTypeId);
    if (targetDict) {
      setSelectedId(targetDict.id);
    }
  }, [dictTypeId, dictList]);

  const getDictOwnerId = () => {
    return activeTab === DICT_OWNER_TYPE.APP ? appId : tenantId;
  };
  // 展开时按需加载预览
  const handleExpand = (key: string, keys: string[]) => {
    setExpandedKeys(keys);
    const dict = dictList.find((d) => d.id === key);
    if (!dict) return;
    const cacheKey = dict.type;
    if (previewMap[cacheKey]?.data) return;

    setPreviewMap((prev) => ({ ...prev, [cacheKey]: { loading: true, data: [] } }));
    getDictDataListByType(dict.type)
      .then((data) => {
        setPreviewMap((prev) => ({ ...prev, [cacheKey]: { loading: false, data: data || [] } }));
      })
      .catch(() => {
        setPreviewMap((prev) => ({ ...prev, [cacheKey]: { loading: false, data: [] } }));
      });
  };

  const handleMore = (dictType: string) => {
    setShowMoreMap((prev) => ({
      ...prev,
      [dictType]: !prev[dictType]
    }));
  };

  const filteredList = useMemo(() => {
    if (!search) return dictList;
    return dictList.filter((d) => d.name.includes(search) || d.type.includes(search));
  }, [dictList, search]);

  const footer = (
    <div className={styles.footerBar}>
      <Button
        type="text"
        size="small"
        onClick={gotoDictPage || (() => navigate(`/onebase/create-app/data-factory?appId=${appId || ''}`))}
      >
        数据字典管理
      </Button>
      <Space>
        <Button type="outline" size="small" onClick={onCancel}>
          取消
        </Button>
        <Button
          type="primary"
          size="small"
          onClick={() => onOk(dictList.find((d) => d.id === selectedId))}
          disabled={selectedId === ''}
        >
          确认
        </Button>
      </Space>
    </div>
  );

  return (
    <Modal
      visible={visible}
      footer={null}
      onCancel={onCancel}
      title="选择数据字典"
      className={styles.selectDictModal}
      unmountOnExit
    >
      <Tabs activeTab={activeTab} onChange={(k) => setActiveTab(k as string)} type="line">
        <Tabs.TabPane key={DICT_OWNER_TYPE.APP} title="自定义字典" />
        <Tabs.TabPane key={DICT_OWNER_TYPE.TENANT} title="公共字典" />
      </Tabs>

      <div className={styles.toolbar}>
        <Input.Search value={search} onChange={setSearch} placeholder="输入字典名称" allowClear />
      </div>

      <div className={styles.listArea}>
        {loadingList ? (
          <div className={styles.center}>
            <Spin />
          </div>
        ) : filteredList.length === 0 ? (
          <Empty description="暂无字典" />
        ) : (
          <Collapse activeKey={expandedKeys} onChange={handleExpand} accordion expandIconPosition="left">
            {filteredList.map((item) => {
              const cache = previewMap[item.type];
              const preview = cache?.data?.slice(0, PREVIEW_MAX) || [];
              const remainData = cache?.data?.slice(PREVIEW_MAX) || [];
              const hasMore = remainData.length > 0;
              return (
                <Collapse.Item
                  key={String(item.id)}
                  name={String(item.id)}
                  header={
                    <div className={styles.itemHeader} onClick={() => setSelectedId(item.id)}>
                      <div className={styles.title}>{item.name}</div>
                      <div className={styles.meta}>{item.type}</div>
                    </div>
                  }
                  extra={
                    <input type="radio" checked={selectedId === item.id} onChange={() => setSelectedId(item.id)} />
                  }
                >
                  <div className={styles.previewRow}>
                    {cache?.loading ? (
                      <Spin size={12} />
                    ) : preview.length === 0 ? (
                      <Typography.Text type="secondary">暂无字典值</Typography.Text>
                    ) : (
                      <>
                        <div>
                          {preview.map((d: DictData) => (
                            <div key={d.id} className={styles.previewItem}>
                              <span className={styles.dot} style={{ backgroundColor: d.colorType }} />
                              <span className={styles.label}>{d.label}</span>
                            </div>
                          ))}
                        </div>
                        <div className={`${styles.moreContent} ${showMoreMap[item.type] ? styles.expanded : ''}`}>
                          {remainData.map((d: DictData) => (
                            <div key={d.id} className={styles.previewItem}>
                              <span className={styles.dot} style={{ backgroundColor: d.colorType }} />
                              <span className={styles.label}>{d.label}</span>
                            </div>
                          ))}
                        </div>
                        {hasMore && (
                          <div className={styles.more} onClick={() => handleMore(item.type)}>
                            {showMoreMap[item.type] ? '收起' : '更多'}
                          </div>
                        )}
                      </>
                    )}
                  </div>
                </Collapse.Item>
              );
            })}
          </Collapse>
        )}
      </div>

      {footer}
    </Modal>
  );
}
