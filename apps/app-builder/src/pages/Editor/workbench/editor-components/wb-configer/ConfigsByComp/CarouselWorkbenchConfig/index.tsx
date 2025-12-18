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

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  TITLE: 'title',
  CONTENT: 'content'
} as const;

const CarouselWorkbenchConfig = () => {
  const { editData, configs, handlePropsChange, renderEditItem } = UseWorkbenchAttributeContext();
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.TITLE, SECTION_KEYS.CONTENT]);

  const labelItem = useMemo(() => {
    return findItem(editData, 'label');
  }, [editData]);

  const carouselContentItem = useMemo(() => {
    return findItem(editData, 'carouselContent');
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
            {labelItem && <div>{renderEditItem(labelItem.item, labelItem.index)}</div>}
          </CollapseItem>
          <CollapseItem header="轮播内容" name={SECTION_KEYS.CONTENT} contentStyle={PanelContentStyle}>
            {cpID && carouselContentItem && (
              <WorkbenchCarouselContentConfig
                id={cpID}
                item={carouselContentItem.item}
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
