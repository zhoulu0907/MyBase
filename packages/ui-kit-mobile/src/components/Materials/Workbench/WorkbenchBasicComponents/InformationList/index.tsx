import { Divider, Cell } from '@arco-design/mobile-react';
import { IconArrowIn } from '@arco-design/mobile-react/esm/icon';
import { memo, useMemo } from 'react';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS, workbenchSchema, type InformationListItem } from '@onebase/ui-kit';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';

type XInformationListConfig = typeof workbenchSchema.XInformationList.config;

const XInformationList = memo((props: XInformationListConfig & { runtime?: boolean }) => {
  const {
    status,
    runtime,
    label,
    theme,
    dataCount,
    dataSourceMode,
    informationListConfig,
    staticInformationList,
    imageField,
    titleField,
    subtitleField,
    dateField,
    linkField,
    showMore,
    jumpType,
    jumpPageId,
    jumpExternalUrl
  } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];
  const { handleJump } = useJump();

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  const dataSource = useMemo<InformationListItem[]>(() => {
    const safeCount =
      typeof dataCount === 'number' && Number.isFinite(dataCount) ? Math.max(0, dataCount) : Number.POSITIVE_INFINITY;

    // 静态模式：优先使用 schema 指定的静态列表字段；兼容旧字段 informationListConfig
    if (dataSourceMode !== 'dynamic') {
      const list = Array.isArray(staticInformationList)
        ? staticInformationList
        : Array.isArray(informationListConfig)
          ? informationListConfig
          : [];
      return list.slice(0, safeCount);
    }

    // 动态模式：此处不负责拉取数据，只负责按字段映射渲染；
    // 运行时数据若已注入到 informationListConfig，则按映射转换后展示。
    const rawList = Array.isArray(informationListConfig) ? informationListConfig : [];
    const mapped = rawList.map((row: any, index: number) => {
      const image = imageField ? row?.[imageField] : row?.image;
      const title = titleField ? row?.[titleField] : row?.title;
      const subtitle = subtitleField ? row?.[subtitleField] : row?.subtitle;
      const date = dateField ? row?.[dateField] : row?.date;
      const link = linkField ? row?.[linkField] : undefined;

      return {
        id: row?.id ?? `${index}`,
        image,
        title,
        subtitle,
        date,
        ...(typeof link === 'string' && link
          ? { linkType: 'external' as const, url: link }
          : null)
      } satisfies InformationListItem;
    });
    return mapped.slice(0, safeCount);
  }, [
    dataSourceMode,
    staticInformationList,
    informationListConfig,
    dataCount,
    imageField,
    titleField,
    subtitleField,
    dateField,
    linkField
  ]);

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
      runtime,
    });
  };
  return (
    <div className={styles.container}>
      <div className={styles.informationListHeader}>
        {label?.display && (
          <span className={styles.informationListHeaderTitle}>{label?.text}</span>
        )}
        {showMore && (
          <a className={styles.showMore} onClick={() => handleShowMoreClick()}>更多<IconArrowIn /></a>
        )}
      </div>

      <div className={styles.informationListContent}>
        <Cell.Group
          className={styles.list}
          bordered={false}
        >
          {dataSource.map((item) => (
            <Cell
              key={item.id}
              className={styles.listItem}
              onClick={() => handleItemClick(item)}
            >
                {theme === WORKBENCH_THEME_OPTIONS.THEME_1 && <div className={styles.cover}>
                  {item.image ? (
                    <img src={item.image} alt="cover" className={styles.coverImg} />
                  ) : (
                    <div className={styles.coverPlaceholder} />
                  )}
                </div>}

                <div className={styles.meta}>
                  <div className={styles.title}>{item.title || ''}</div>
                  <div className={styles.subtitle}>{item.subtitle || ''}</div>
                  <div className={styles.timeContainer}>
                    <div className={styles.author}>{item.author || ''}</div>
                    {item.title && item.date && <div className={styles.divider} />}
                    <div className={styles.time}>{item.date || ''}</div>
                  </div>
                </div>
            </Cell>
          ))}
        </Cell.Group>
      </div>
    </div>
  );
});

export default XInformationList;

