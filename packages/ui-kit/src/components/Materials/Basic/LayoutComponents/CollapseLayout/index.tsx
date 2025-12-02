import { Collapse, Divider, Tooltip } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { memo, useEffect, useState } from 'react';
import { COMPONENT_GROUP_NAME, usePageEditorSignal } from '@/index';
import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import { COLLAPSED_OPTIONS, COLLAPSED_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XCollapseLayoutConfig } from './schema';
import LayoutReactSortable from '../components/layoutReactSortable';

const CollapseItem = Collapse.Item;

const XCollapseLayout = memo((props: XCollapseLayoutConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { id, label, colCount = 1, status, collapseStyle, collapsed, runtime = true } = props;
  useSignals();

  const { pageComponentSchemas, setPageComponentSchemas, layoutSubComponents, setLayoutSubComponents } =
    usePageEditorSignal();

  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);
  const [activeKey, setActiveKey] = useState<string[]>([]);

  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  useEffect(() => {
    setActiveKey(collapsed !== COLLAPSED_VALUES[COLLAPSED_OPTIONS.COLLAPSED] ? ['1'] : []);
  }, [collapsed]);

  // 组件状态同步到子组件
  useEffect(() => {
    if (colComponents[0]) {
      colComponents[0].forEach((comp) => {
        const schema = pageComponentSchemas[comp.id];
        schema.config.status = status;
        setPageComponentSchemas(comp.id, schema);
      });
    }
  }, [status]);

  return (
    <Collapse
      className="XCollapseLayout"
      bordered={false}
      activeKey={activeKey}
      expandIconPosition="right"
      expandIcon={<img src={IconCollapsedDown} alt="" />}
      onChange={(_, key) => {
        if (collapsed !== COLLAPSED_VALUES[COLLAPSED_OPTIONS.DISABLED_COLLAPSED]) {
          setActiveKey(key);
        }
      }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        border: collapseStyle.showBordered ? '1px solid #d4d4d4' : 'none'
      }}
    >
      <CollapseItem
        header={
          <Tooltip content={label.text}>
            <div className="collapse-title">
              <div className="collapse-title-shape" style={{ backgroundColor: collapseStyle.shapeColor }}></div>
              <div className="collapse-title-ellipsis" style={{ color: collapseStyle.titleColor }}>
                {label.text}
              </div>
            </div>
          </Tooltip>
        }
        showExpandIcon={collapsed !== COLLAPSED_VALUES[COLLAPSED_OPTIONS.DISABLED_COLLAPSED]}
        name="1"
        contentStyle={{
          backgroundColor: '#fff',
          paddingLeft: 13,
          paddingTop: 5,
          borderTop: collapseStyle.showDivider ? '1px solid #ccc' : 'none'
        }}
      >
        {colComponents.map((_colComponents, index) => (
          <div className="item" key={index}>
            <LayoutReactSortable
              id={id}
              sortableId={`workspace-content-${id}-${index}`}
              colComponents={colComponents}
              groupName={COMPONENT_GROUP_NAME}
              index={index}
              runtime={runtime}
            ></LayoutReactSortable>
          </div>
        ))}
      </CollapseItem>
    </Collapse>
  );
});

export default XCollapseLayout;
