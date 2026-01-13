import { Collapse } from '@arco-design/mobile-react';
import {
  COLLAPSED_OPTIONS,
  COLLAPSED_VALUES,
  COMPONENT_GROUP_NAME,
  LayoutSchema,
  STATUS_OPTIONS,
  STATUS_VALUES,
  usePageEditorSignal
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { memo, useEffect, useState } from 'react';
import LayoutReactSortable from '../components/layoutReactSortable';
import './index.css';

type XCollapseLayoutConfig = typeof LayoutSchema.XCollapseLayoutSchema.config;

const XCollapseLayout = memo(
  (props: XCollapseLayoutConfig & { runtime?: boolean; editPreview?: boolean; useStoreSignals?: any }) => {
    const {
      id,
      label,
      colCount = 1,
      status,
      collapsed,
      collapseStyle,
      editPreview,
      useStoreSignals,
      runtime = true
    } = props;
    useSignals();

    const { pageComponentSchemas, layoutSubComponents, setLayoutSubComponents, setPageComponentSchemas } =
      runtime && !editPreview ? usePageEditorSignal() : useStoreSignals;

    const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);
    const [activeKey, setActiveKey] = useState<boolean>();

    useEffect(() => {
      const currentColumns = layoutSubComponents[id];
      if (!currentColumns || currentColumns.length !== colCount) {
        const newColumns = Array.from({ length: colCount }, () => []);
        setLayoutSubComponents(id, newColumns);
      }
    }, [colCount, id, colComponents]);

    useEffect(() => {
      setActiveKey(collapsed !== COLLAPSED_VALUES[COLLAPSED_OPTIONS.COLLAPSED]);
    }, [collapsed]);

    // 组件状态同步到子组件
    useEffect(() => {
      if (colComponents[0]) {
        colComponents[0].forEach((comp: any) => {
          const schema = pageComponentSchemas[comp.id];
          schema.config.status = status;
          setPageComponentSchemas(comp.id, schema);
        });
      }
    }, [status]);

    return (
      <Collapse
        className="XCollapseLayout"
        value={id}
        active={activeKey}
        hideIcon={collapsed === COLLAPSED_VALUES[COLLAPSED_OPTIONS.DISABLED_COLLAPSED]}
        onCollapse={() =>
          collapsed !== COLLAPSED_VALUES[COLLAPSED_OPTIONS.DISABLED_COLLAPSED] && setActiveKey((prev) => !prev)
        }
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
          border: collapseStyle.showBordered ? '1px solid #d4d4d4' : 'none'
        }}
        header={
          label.display || label.display === null ? (
            <div style={{ width: '100%' }}>
              <div className="collapse-title">
                <div className="collapse-title-shape" style={{ backgroundColor: collapseStyle.shapeColor }}></div>
                <div className="collapse-title-ellipsis" style={{ color: collapseStyle.titleColor }}>
                  {label.text}
                </div>
              </div>
            </div>
          ) : (
            <div className="collapse-title"></div>
          )
        }
        content={colComponents.map((_colComponents: any, index: number) => (
          <div className="item" key={index}>
            <LayoutReactSortable
              id={id}
              sortableId={`workspace-content-${id}-${index}`}
              colComponents={colComponents}
              groupName={COMPONENT_GROUP_NAME}
              index={index}
              runtime={runtime}
              editPreview={editPreview}
              useStoreSignals={useStoreSignals}
            ></LayoutReactSortable>
          </div>
        ))}
      />
    );
  }
);

export default XCollapseLayout;
