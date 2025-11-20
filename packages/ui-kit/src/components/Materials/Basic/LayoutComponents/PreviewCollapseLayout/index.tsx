import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import { COMPONENT_GROUP_NAME, EDITOR_TYPES, PreviewRender, getComponentWidth, usePageEditorSignal, type GridItem } from '@/index';
import { Collapse } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { Fragment, memo, useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { COLLAPSED_OPTIONS, COLLAPSED_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XCollapseLayoutConfig } from './schema';

const CollapseItem = Collapse.Item;

const XPreviewCollapseLayout = memo((props: XCollapseLayoutConfig & { detailMode?: boolean }) => {
  const { id, label, colCount = 1, status, collapsed, pageType, detailMode } = props;
  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    setShowDeleteButton
  } = usePageEditorSignal(pageType || EDITOR_TYPES.FORM_EDITOR);

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
    setActiveKey(collapsed === COLLAPSED_VALUES[COLLAPSED_OPTIONS.EXPOSED] ? ['1'] : []);
  }, [collapsed]);

  return (
    <Collapse
      className="XPreviewCollapseLayout"
      bordered={false}
      activeKey={activeKey}
      expandIconPosition="right"
      expandIcon={<img src={IconCollapsedDown} alt="" />}
      onChange={(_, key) => setActiveKey(key)}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      <CollapseItem
        header={label.text}
        name="1"
        contentStyle={{ backgroundColor: '#fff', paddingLeft: 13, paddingTop: 20, borderTop: '1px solid #ccc' }}
      >
        {colComponents.map((_colComponents, index) => (
          <div className="item" key={index}>
            <ReactSortable
              id={`workspace-content-${id}-${index}`}
              className="content"
              list={colComponents[index]}
              setList={(newList) => {
                colComponents[index] = newList;
              }}
              group={{
                name: COMPONENT_GROUP_NAME
              }}
              animation={150}
            >
              {colComponents[index] &&
                colComponents[index].map((cp: GridItem) => (
                  <Fragment key={cp.id}>
                    {pageComponentSchemas[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                      <div
                        key={cp.id}
                        data-cp-type={cp.type}
                        data-cp-displayname={cp.displayName}
                        data-cp-id={cp.id}
                        className="componentItem"
                        style={{
                          width: `calc(${getComponentWidth(pageComponentSchemas[cp.id], cp.type)} - 8px)`,
                          margin: '4px'
                        }}
                        onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                          e.stopPropagation();
                          setCurComponentID(cp.id);
                          const curComponentSchema = pageComponentSchemas[cp.id];
                          setCurComponentSchema(curComponentSchema);
                          setShowDeleteButton(true);
                        }}
                      >
                        <PreviewRender
                          cpId={cp.id}
                          cpType={cp.type}
                          pageComponentSchema={pageComponentSchemas[cp.id]}
                          runtime={true}
                          detailMode={detailMode}
                        />
                      </div>
                    )}
                  </Fragment>
                ))}
            </ReactSortable>
          </div>
        ))}
      </CollapseItem>
    </Collapse>
  );
});

export default XPreviewCollapseLayout;
