import type { CSSProperties } from 'react';
import { memo, useEffect, useState } from 'react';
import { Empty, Tabs } from '@arco-design/web-react';
import { IconRight } from '@arco-design/web-react/icon';
import dayjs from 'dayjs';
import { TokenManager } from '@onebase/common';
import { getTodoPageList, getDonePageList, getMyCreatePageList, getMyCCPageList } from '@onebase/app/src/services/app_runtime';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS, DATA_CONFIG_NAME_MAP } from '../../core/constants';
import { useJump } from '../../hooks/useJump';
import type { XTodoListConfig, ITodoItem } from './schema';
import { pendingListDefault } from './schema';
import styles from './index.module.css';

const statusMap: Record<string, string> = {
  timeout: '超时',
  normal: '正常',
  completed: '已完成',
  cancelled: '已取消',
  in_approval: '审批中'
}

const XTodoList = memo((props: XTodoListConfig & { runtime?: boolean }) => {
  const { label, dataConfig, theme, status, runtime, dataCount, userAvatar, userName, showMore, showMoreLink } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

  const [pendingList, setPendingList] = useState<ITodoItem[]>(runtime ? [] : pendingListDefault);
  const [createdList, setCreatedList] = useState<ITodoItem[]>([]);
  const [handledList, setHandledList] = useState<ITodoItem[]>([]);
  const [ccList, setCcList] = useState<ITodoItem[]>([]);
  const [contentHeight, setContentHeight] = useState<number | undefined>(undefined);

  const { handleJump } = useJump();

  const getListMap = {
    showPending: pendingList,
    showCreated: createdList,
    showHandled: handledList,
    showCc: ccList
  }

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  // 切换tab获取对应的数据（仅在 runtime 模式下调用接口）
  const fetchListData = async (tabKey: string) => {
    if (!runtime) {
      return;
    }

    const appId = TokenManager.getCurAppId();

    try {
      let res;

      const params = {
        pageNo: 1,
        pageSize: dataCount,
        appId: appId || ''
      }

      switch (tabKey) {
        case 'showPending':
          res = await getTodoPageList(params);
          if (res && res.list) {
            setPendingList(res.list);
          }
          break;
        case 'showHandled':
          res = await getDonePageList(params);
          if (res && res.list) {
            setHandledList(res.list);
          }
          break;
        case 'showCreated':
          res = await getMyCreatePageList(params);
          if (res && res.list) {
            setCreatedList(res.list);
          }
          break;
        case 'showCc':
          res = await getMyCCPageList(params);
          if (res && res.list) {
            setCcList(res.list);
          }
          break;
        default:
          break;
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取第一个展示的 tab key
  const getFirstVisibleTabKey = (): string | null => {
    const entries = Object.entries(dataConfig);
    const firstVisibleTab = entries.find(([_, value]) => value === true);
    return firstVisibleTab ? firstVisibleTab[0] : null;
  };

  useEffect(() => {
    if (runtime) {
      const firstTabKey = getFirstVisibleTabKey();
      if (firstTabKey) {
        fetchListData(firstTabKey);
      }
    }
  }, [runtime, dataCount]);

  // 编辑态中，根据dataCount调整pendingList数据条数
  useEffect(() => {
    if (!runtime) {
      const currentLength = pendingList.length;
      const targetCount = dataCount;

      if (targetCount > currentLength) {
        // 新增数据
        const newItems: ITodoItem[] = [];
        for (let i = currentLength; i < targetCount; i++) {
          const templateIndex = i % pendingListDefault.length;
          const template = pendingListDefault[templateIndex];
          newItems.push({
            ...template,
            id: `${i + 1}`,
            processTitle: `${template.initiator.name}发起的流程表单测试_表单`,
            arrivalTime: Date.now() - i * 60000,
            submitTime: Date.now() - i * 60000 + 200,
            taskId: `${1446542543792771072 + i}`,
            instanceId: `${1446542538365341696 + i}`
          });
        }
        setPendingList([...pendingList, ...newItems]);
      } else if (targetCount < currentLength) {
        // 减少数据
        setPendingList(pendingList.slice(0, targetCount));
      }
    }
  }, [dataCount, runtime]);

  // 根据dataCount计算固定高度
  useEffect(() => {
    const itemHeight = 60;
    const itemsPadding = 24;
    const baseHeight = 128;
    const calculatedHeight = baseHeight + itemsPadding + itemHeight * dataCount;
    setContentHeight(calculatedHeight);
  }, [dataCount]);

  const handleShowMoreClick = () => {
    if (!runtime) {
      return;
    }
    handleJump({
      menuUuid: undefined,
      linkAddress: showMoreLink,
      runtime
    });
  };
  const containerStyle: CSSProperties = runtime && contentHeight 
    ? { height: contentHeight, overflow: 'hidden', display: 'flex', flexDirection: 'column' }
    : {};

  const contentStyle: CSSProperties = runtime && contentHeight
    ? { flex: 1, overflow: 'auto' }
    : {};

  return (
    <div className={styles.containerStyle} style={containerStyle}>

      <div className={styles.todoListHeader}>
        {label?.display && (
          <span className={styles.todoListHeaderTitle}>{label?.text}</span>
        )}
        {showMore && (
          <a href={showMoreLink} className={styles.showMore} onClick={() => handleShowMoreClick()}>更多<IconRight /></a>
        )}
      </div>

      <div className={styles.todoListContent} style={contentStyle}>
        <Tabs style={{ width: '100%', height: '100%' }} onChange={runtime ? fetchListData : undefined}>
          {Object.entries(dataConfig).map(([key, value]: [string, boolean], index: number) => (
            value &&
            (<Tabs.TabPane key={key} title={DATA_CONFIG_NAME_MAP[key] || key}>
              <div className={styles.todoListContentList}>
                {getListMap[key as keyof typeof getListMap].length > 0 && getListMap[key as keyof typeof getListMap]?.slice(0, dataCount)?.map((item: ITodoItem, index: number) => (
                  <div key={item.id || index} className={styles.todoListContentItem}>
                    <div className={styles.todoListContentItemLeft} style={{ display: theme === WORKBENCH_THEME_OPTIONS.THEME_1 ? 'flex' : 'none' }}>
                      {item?.initiator?.avatar ? (
                        <img src={item.initiator.avatar} alt={item.initiator.name} className={styles.userAvatar} />
                      ) : (
                        <div className={styles.userAvatar}>{item.initiator.name?.charAt(0)}</div>
                      )}
                    </div>
                    <div className={styles.todoListContentItemRight}>
                      <div className={styles.todoListContentItemRightTop}>
                        <div className={styles.todoListTitle}>{item?.processTitle}</div>
                        <div className={item?.flowStatus === 'timeout' ? styles.todoListStatueTimeout : styles.todoListStatueNormal}>
                          {statusMap[item?.flowStatus] || item?.flowStatus}
                        </div>
                      </div>
                      <div className={styles.todoListContentItemRightBottom}>
                        <div className={styles.todoListInitiator}>发起人：{item?.initiator?.name}</div>
                        {/* <div className={styles.todoListSourceSystem}>表单摘要：{item.formSummary}</div> */}
                        <div className={styles.todoListCreateTime}>创建时间：{dayjs(item?.submitTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                      </div>
                    </div>
                  </div>
                ))}

                {/* {getListMap[key as keyof typeof getListMap].length === 0 && (
                  <div className={styles.todoListContentItemEmpty}>
                    <Empty description="暂无数据" />
                  </div>
                )} */}
              </div>
            </Tabs.TabPane>
            )))}
        </Tabs>
      </div>
    </div>
  );
});

export default XTodoList;

