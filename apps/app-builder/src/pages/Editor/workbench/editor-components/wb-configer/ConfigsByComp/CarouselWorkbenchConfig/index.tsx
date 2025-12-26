import { Collapse } from '@arco-design/web-react';
import { useState, useMemo } from 'react';
import WorkbenchCarouselContentConfig from './WorkbenchCarouselContentConfig';
import {
  WorkbenchAttributes,
  UseWorkbenchAttributeContext,
  PanelContentStyle
} from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';
import type { CarouselContentMeta } from './types';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  TITLE: 'title',
  CAROUSEL: 'carousel',
  CONTENT: 'content'
} as const;

const CarouselWorkbenchConfig = () => {
  const { editData, configs, handlePropsChange, renderEditItem } = UseWorkbenchAttributeContext();
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.TITLE, SECTION_KEYS.CONTENT]);

  const configItems = useMemo(() => {
    return {
      label: findItem(editData, 'label'),
      interval: findItem(editData, 'interval'),
      carouselConfig: findItem(editData, 'carouselConfig'),
      carouselContent: findItem(editData, 'carouselContent')
    };
  }, [editData]);

  return (
    <WorkbenchAttributes
      renderPanels={({ cpID }) => (
        <Collapse
          activeKey={activeKeys}
          onChange={(_key, keys) => setActiveKeys(keys)}
          accordion={false}
          bordered={false}
          expandIconPosition="right"
          className={styles.collapseConfigs}
        >
          <CollapseItem header="标题配置" name={SECTION_KEYS.TITLE} contentStyle={PanelContentStyle}>
            {configItems.label && <div>{renderEditItem(configItems.label)}</div>}
          </CollapseItem>
          <CollapseItem header="轮播配置" name={SECTION_KEYS.CAROUSEL} contentStyle={PanelContentStyle}>
            {configItems.interval && <div>{renderEditItem(configItems.interval)}</div>}
            {configItems.carouselConfig && <div>{renderEditItem(configItems.carouselConfig)}</div>}
          </CollapseItem>
          <CollapseItem header="轮播内容" name={SECTION_KEYS.CONTENT} contentStyle={PanelContentStyle}>
            {cpID && configItems.carouselContent && (
              <WorkbenchCarouselContentConfig
                id={cpID}
                item={configItems.carouselContent.item}
                configs={configs}
                handlePropsChange={handlePropsChange}
              />
            )}
          </CollapseItem>
        </Collapse>
      )}
    />
  );
};

export default CarouselWorkbenchConfig;
