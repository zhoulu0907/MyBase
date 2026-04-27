import { Divider, List, Space } from '@arco-design/web-react';
import { IconRight } from '@arco-design/web-react/icon';
import { memo, useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import { dataMethodPageV2, menuSignal, PageMethodV2Params, attachmentDownload } from '@onebase/app';
import { isRuntimeEnv } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS } from '../../core/constants';
import type { InformationListItem, XInformationListConfig } from './schema';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';

const XInformationList = memo((props: XInformationListConfig & { runtime?: boolean }) => {
  useSignals();
  const {
    status,
    runtime,
    label,
    theme,
    dataCount,
    dataSourceMode,
    staticInformationList,
    contentSource,
    filterCondition,
    imageField,
    titleField,
    subtitleField,
    authorField,
    dateField,
    linkField,
    showMore,
    jumpType,
    jumpPageId,
    jumpExternalUrl
  } = props;

  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];
  const { handleJump } = useJump();
  const { curMenu } = menuSignal;
  const [dynamicData, setDynamicData] = useState<InformationListItem[]>([]);

  const safeCount = useMemo(() => {
    if (typeof dataCount === 'number' && Number.isFinite(dataCount)) {
      return Math.max(0, dataCount);
    }
    return 10;
  }, [dataCount]);

  const getFieldDisplayName = (field: unknown) => {
    if (typeof field === 'object' && field) {
      return (field as { displayName?: string }).displayName;
    }

    return typeof field === 'string' ? field : undefined;
  };


  const getFieldyName = (field: unknown) => {
    if (typeof field === 'object' && field) {
      return (field as { fieldName?: string }).fieldName;
    }

    return typeof field === 'string' ? field : undefined;
  };

  const getNormalizedValue = (value: unknown): string => {
    if (value === null || value === undefined) {
      return '';
    }
    if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') {
      return String(value);
    }
    if (typeof value === 'object') {
      const obj = value as { name?: string; id?: string };
      return obj.name || obj.id || '';
    }
    return '';
  };

  const getDateValue = (value: unknown): string => {
    const normalized = getNormalizedValue(value);
    if (!normalized) {
      return '';
    }

    const date = dayjs(normalized);
    if (!date.isValid()) {
      return normalized;
    }

    return date.format('YYYY-MM-DD');
  };

  // 运行态时获取动态数据
  useEffect(() => {
    const fetchDynamicData = async () => {
      if (!runtime || !isRuntimeEnv() || dataSourceMode !== 'dynamic') {
        return;
      }

      if (!contentSource?.tableName) {
        console.warn('InformationList: 未配置 tableName');
        return;
      }

      try {
        const req: PageMethodV2Params = {
          pageNo: 1,
          pageSize: safeCount,
          filters: filterCondition && Object.keys(filterCondition).length > 0 ? filterCondition : undefined
        };

        const res = await dataMethodPageV2(contentSource.tableName, curMenu.value?.id, req);

        const { list } = res;

        const fieldKeys = {
          image: getFieldyName(imageField),
          title: getFieldyName(titleField),
          subtitle: getFieldyName(subtitleField),
          author: getFieldyName(authorField),
          date: getFieldyName(dateField),
          link: getFieldyName(linkField)
        };

        const normalizedList = await Promise.all(
          (list || []).slice(0, safeCount).map(async (row: any, index: number) => {
            const rowId = row?.id ?? `${index}`;
            const imageFieldName = fieldKeys.image;
            const fileId = imageFieldName ? row?.[imageFieldName]?.[0]?.id : undefined;
            const linkValue = fieldKeys.link ? getNormalizedValue(row?.[fieldKeys.link]) : '';

            let imgUrl = '';
            if (fileId) {
              imgUrl = await attachmentDownload(contentSource?.tableName || '', {
                menuId: curMenu.value.id,
                id: rowId,
                fieldName: imageFieldName,
                fileId: fileId
              });
            }

            const item: InformationListItem = {
              id: String(rowId),
              image: imgUrl,
              title: getNormalizedValue(fieldKeys.title ? row?.[fieldKeys.title] : row?.title),
              subtitle: getNormalizedValue(fieldKeys.subtitle ? row?.[fieldKeys.subtitle] : row?.subtitle),
              author: getNormalizedValue(fieldKeys.author ? row?.[fieldKeys.author] : row?.author),
              date: getDateValue(fieldKeys.date ? row?.[fieldKeys.date] : row?.date)
            };

            if (linkValue) {
              item.linkType = 'external';
              item.url = linkValue;
            }

            return item;
          })
        );

        setDynamicData(normalizedList);
      } catch (error) {
        console.error('InformationList fetchDynamicData error:', error);
        setDynamicData([]);
      }
    };

    fetchDynamicData();
  }, [
    runtime,
    dataSourceMode,
    contentSource,
    filterCondition,
    safeCount,
    imageField,
    titleField,
    subtitleField,
    authorField,
    dateField,
    linkField,
    curMenu.value?.id
  ]);

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  const dataSource = useMemo<InformationListItem[]>(() => {
    if (dataSourceMode !== 'dynamic') {
      const list = Array.isArray(staticInformationList) ? staticInformationList : [];
      return list.slice(0, safeCount);
    }

    if (runtime && dynamicData.length > 0) {
      return dynamicData.slice(0, safeCount);
    }

    const previewCount = Math.min(safeCount, 3);
    const previewNames = {
      title: getFieldDisplayName(titleField) || '标题字段',
      subtitle: getFieldDisplayName(subtitleField) || '副标题字段',
      author: getFieldDisplayName(authorField) || '作者字段',
      date: getFieldDisplayName(dateField) || '日期字段'
    };


    return Array.from({ length: previewCount }, (_, index) => ({
      id: `preview-${index}`,
      title: previewNames.title,
      subtitle: previewNames.subtitle,
      author: previewNames.author,
      date: previewNames.date
    }));
  }, [dataSourceMode, staticInformationList, runtime, dynamicData, safeCount, titleField, subtitleField, authorField, dateField]);

  const handleItemClick = (item: any) => {
    if (!runtime) {
      return;
    }
    handleJump({
      menuUuid: item?.linkType === 'internal' ? item?.internalPageId : undefined,
      linkAddress: item?.linkType === 'external' ? item?.url : undefined,
      runtime
    });
  };

  const handleShowMoreClick = () => {
    if (!runtime) {
      return;
    }
    handleJump({
      menuUuid: jumpType === 'internal' ? jumpPageId : undefined,
      linkAddress: jumpType === 'external' ? jumpExternalUrl : undefined,
      runtime
    });
  };

  return (
    <div className={styles.container}>
      <div className={styles.informationListHeader}>
        {label?.display && <span className={styles.informationListHeaderTitle}>{label?.text}</span>}
        {showMore && (
          <a className={styles.showMore} onClick={handleShowMoreClick}>
            更多
            <IconRight />
          </a>
        )}
      </div>

      <div className={styles.informationListContent}>
        <List
          className={styles.list}
          dataSource={dataSource}
          render={(item: any) => (
            <List.Item key={item.id} className={styles.listItem} onClick={() => handleItemClick(item)}>
              <div className={styles.row} data-runtime={runtime ? 'true' : 'false'}>
                {theme === WORKBENCH_THEME_OPTIONS.THEME_1 && (
                  <div className={styles.cover}>
                    {item.image ? (
                      <img src={item.image} alt="cover" className={styles.coverImg} />
                    ) : (
                      <div className={styles.coverPlaceholder} />
                    )}
                  </div>
                )}

                <div className={styles.meta}>
                  <div className={styles.title}>{item.title || ''}</div>
                  <div className={styles.subtitle}>{item.subtitle || ''}</div>
                  <Space
                    split={item.author && item.date ? <Divider type="vertical" /> : undefined}
                    className={styles.timeContainer}
                  >
                    <div className={styles.author}>{item.author || ''}</div>
                    <div className={styles.time}>{item.date || ''}</div>
                  </Space>
                </div>
              </div>
            </List.Item>
          )}
        />
      </div>
    </div>
  );
});

export default XInformationList;
