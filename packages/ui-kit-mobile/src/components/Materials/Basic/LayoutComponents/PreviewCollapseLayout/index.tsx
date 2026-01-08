import { Fragment, memo, useEffect, useState } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import { ReactSortable } from 'react-sortablejs';
import { Collapse } from '@arco-design/mobile-react';
import { PreviewRender } from '@/components/render';
import { COLLAPSED_OPTIONS, COLLAPSED_VALUES, COMPONENT_GROUP_NAME, EDITOR_TYPES, getComponentWidth, GridItem, LayoutSchema, STATUS_OPTIONS, STATUS_VALUES, usePageEditorSignal } from '@onebase/ui-kit';
import './index.css';

type XCollapseLayoutConfig = typeof LayoutSchema.XPreviewCollapseLayoutSchema.config;

const XPreviewCollapseLayout = memo((props: XCollapseLayoutConfig & { detailMode?: boolean; runtime?: boolean; }) => {
  const { id, label, colCount = 1, status, collapsed, collapseStyle, pageType, detailMode, runtime } = props;
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

  return (
    <Collapse
      className='XPreviewCollapseLayout'
      header={
        label.display || label.display === null ?
          <div style={{ width: '100%' }}>
            <div className="collapse-title">
              <div className="collapse-title-shape" style={{ backgroundColor: collapseStyle.shapeColor }}></div>
              <div className="collapse-title-ellipsis" style={{ color: collapseStyle.titleColor }}>
                {label.text}
              </div>
            </div>
          </div> : <div className="collapse-title"></div>
      }
      value={id}
      defaultActive
      active={activeKey}
      hideIcon={collapsed === COLLAPSED_VALUES[COLLAPSED_OPTIONS.DISABLED_COLLAPSED]}
      onCollapse={() => collapsed !== COLLAPSED_VALUES[COLLAPSED_OPTIONS.DISABLED_COLLAPSED] && setActiveKey(prev => !prev)}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        // border: collapseStyle.showBordered ? '1px solid #d4d4d4' : 'none'
      }}
      content={
        colComponents.map((_colComponents: any, index: number) => (
          <div className="item" key={index} style={{ paddingTop: !runtime ? '0.36rem' : 0 }}>
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
              disabled
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
                          width: `calc(${getComponentWidth(pageComponentSchemas[cp.id], cp.type)} - 0.16rem)`,
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
                          {...props}
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
    />
  );
});

export default XPreviewCollapseLayout;
