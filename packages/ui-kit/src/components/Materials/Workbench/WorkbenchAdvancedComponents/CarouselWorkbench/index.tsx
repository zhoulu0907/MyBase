import { Carousel } from '@arco-design/web-react';
import { attachmentDownload, dataMethodPageV2, menuSignal } from '@onebase/app';
import type { PageMethodV2Params } from '@onebase/app';
import { isRuntimeEnv } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { memo, useEffect, useMemo, useState } from 'react';
import { useJump } from '../../hooks/useJump';
import type { XCarouselConfig } from './schema';
import styles from './index.module.css';

const XCarousel = memo((props: XCarouselConfig & { runtime?: boolean }) => {
  useSignals();

  const {
    label,
    interval,
    carouselConfig,
    runtime,
    autoplay,
    fillStyle,
    dataSourceMode,
    contentSource,
    imageField,
    linkField,
    filterCondition,
    displayCount
  } = props;

  const { handleJump } = useJump();
  const { curMenu } = menuSignal;
  const [dynamicData, setDynamicData] = useState<any[]>([]);

  const safeCount = useMemo(() => {
    if (typeof displayCount === 'number' && Number.isFinite(displayCount)) {
      return Math.max(1, displayCount);
    }
    return 10;
  }, [displayCount]);

  const fieldNames = useMemo(() => {
    const resolveFieldName = (field: unknown) => {
      if (typeof field === 'object' && field) {
        const currentField = field as { fieldName?: string; displayName?: string };
        return currentField.fieldName || currentField.displayName;
      }
      return typeof field === 'string' ? field : undefined;
    };

    return {
      image: resolveFieldName(imageField),
      link: resolveFieldName(linkField)
    };
  }, [imageField, linkField]);

  useEffect(() => {
    const fetchDynamicData = async () => {
      if (!runtime || !isRuntimeEnv() || dataSourceMode !== 'dynamic') {
        return;
      }

      if (!contentSource?.tableName) {
        setDynamicData([]);
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

        const imageFieldName = fieldNames.image;
        const linkFieldName = fieldNames.link;

        const normalizedList = await Promise.all(
          (list || []).slice(0, safeCount).map(async (row: any, index: number) => {
            const rowId = row?.id ?? `${index}`;
            const fileId = imageFieldName ? row?.[imageFieldName]?.[0]?.id : undefined;

            let imageUrl = '';
            if (fileId && imageFieldName && curMenu.value?.id) {
              const fileUrl = await attachmentDownload(contentSource.tableName || '', {
                menuId: curMenu.value.id,
                id: rowId,
                fieldName: imageFieldName,
                fileId
              });
              imageUrl = typeof fileUrl === 'string' ? fileUrl : '';
            }

            const linkValue = linkFieldName ? row?.[linkFieldName] : undefined;

            return {
              id: String(rowId),
              image: imageUrl,
              ...(typeof linkValue === 'string' && linkValue ? { linkType: 'external' as const, url: linkValue } : {})
            };
          })
        );

        setDynamicData(normalizedList);
      } catch (error) {
        console.error('CarouselWorkbench fetchDynamicData error:', error);
        setDynamicData([]);
      }
    };

    fetchDynamicData();
  }, [
    runtime,
    dataSourceMode,
    contentSource,
    fieldNames,
    filterCondition,
    safeCount,
    curMenu.value?.id
  ]);

  const dataSource = useMemo(() => {
    if (dataSourceMode === 'dynamic') {
      return dynamicData;
    }
    return Array.isArray(carouselConfig) ? carouselConfig : [];
  }, [dataSourceMode, dynamicData, carouselConfig]);

  const handleImgClick = (item: any) => {
    handleJump({
      menuUuid: item.linkType === 'internal' ? item.internalPageId : undefined,
      linkAddress: item.linkType === 'external' ? item.url : undefined,
      runtime
    });
  };

  return (
    <div className={styles.carouselWrapper}>
      {label?.display && label?.text && <div className={styles.title}>{label?.text}</div>}

      <Carousel
        className={styles.carousel}
        autoPlay={autoplay ? { interval: interval ? interval * 1000 : 4 * 1000 } : false}
        style={{ pointerEvents: runtime ? 'unset' : 'none' }}
        indicatorPosition="bottom"
      >
        {dataSource.map((item: any, index: number) => (
          <div className={styles.imageWrapper} key={item?.id || index} onClick={() => handleImgClick(item)}>
            <img className={styles.image} src={item.image} alt="carousel" style={{ objectFit: fillStyle }} />
          </div>
        ))}
      </Carousel>
    </div>
  );
});

export default XCarousel;
