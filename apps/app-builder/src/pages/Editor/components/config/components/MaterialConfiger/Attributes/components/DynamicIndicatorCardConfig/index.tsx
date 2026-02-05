import { Checkbox, Form, Input, Button } from '@arco-design/web-react';
import { IconDragDotVertical, IconEdit, IconDelete } from '@arco-design/web-react/icon';
import { useEffect, useRef, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicIndicatorCardConfig = ({ handlePropsChange, item, configs, id }: Props) => {
  const indicatorKey = 'indicatorList';

  const [indicatorList, setIndicatorList] = useState<any[]>(configs[indicatorKey] || []);

  return (
    <Form.Item className={styles.formItem} label="指标配置">
      <Form.List field={`${id}-'indicatorList'`}>
        {(_fields, { remove }) => (
          <div>
            <ReactSortable
              list={indicatorList}
              setList={setIndicatorList}
              group={{
                name: 'indicator-col-item'
              }}
              swap
              sort={true}
              handle=".indicator-col-item-handle"
              className={styles.componentCollapseContent}
              forceFallback={true}
              animation={150}
              onAdd={(e) => {
                console.log('onAdd: ', e);
              }}
              onSort={(e) => {
                console.log(e);
                const newList = [...configs[indicatorKey]];
                console.log('configs[columnsKey]', configs[indicatorKey]);
                // 根据 onSort 事件中的 oldIndex 和 newIndex 交换数组元素
                const { oldIndex, newIndex } = e;
                if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
                  // 复制一份新数组
                  const movedList = [...newList];
                  // 取出被移动的元素
                  const [movedItem] = movedList.splice(oldIndex, 1);
                  // 插入到新位置
                  movedList.splice(newIndex, 0, movedItem);
                  // 更新属性
                  handlePropsChange(indicatorKey, movedList);
                }
              }}
            >
              {indicatorList.map((_col: any, idx: number) => (
                <div key={idx}>
                  <IconDragDotVertical
                    // 支持拖拽的图标，别误删了：）
                    className="indicator-col-item-handle"
                    style={{
                      cursor: 'move',
                      color: '#555'
                    }}
                  />
                  <Input value={_col.name} />
                </div>
              ))}
            </ReactSortable>
          </div>
        )}
      </Form.List>
    </Form.Item>
  );
};

export default DynamicIndicatorCardConfig;

registerConfigRenderer(CONFIG_TYPES.INDICATOR_CARD_CONFIG, ({ handlePropsChange, item, configs, id }) => (
  <DynamicIndicatorCardConfig handlePropsChange={handlePropsChange} item={item} configs={configs} id={id} />
));
