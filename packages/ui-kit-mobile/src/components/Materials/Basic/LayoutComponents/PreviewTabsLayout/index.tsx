import { Fragment, memo, useEffect } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { Tabs } from '@arco-design/mobile-react';
import { useSignals } from '@preact/signals-react/runtime';
import { PreviewRender } from '@/components/render';
import { STATUS_OPTIONS, STATUS_VALUES, usePageEditorSignal, EDITOR_TYPES, COMPONENT_GROUP_NAME, GridItem, LayoutSchema } from '@onebase/ui-kit';
import './index.css';

type TabsType = 'line' | 'card' | 'tag' | 'line-divide' | 'tag-divide';
type XTabsLayoutConfig = typeof LayoutSchema.XTabsLayoutSchema.config;

const mobileTypes = ["line", "card", "line-divide", "tag", "tag-divide"];

const XPreviewTabsLayout = memo((props: XTabsLayoutConfig & { detailMode?: boolean }) => {
  const { id, defaultValue = [], type, colCount, tabPosition, detailMode } = props;
  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    setShowDeleteButton
  } = usePageEditorSignal(EDITOR_TYPES.FORM_EDITOR);

  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  const finalType: TabsType =
    mobileTypes.includes(type as TabsType)
      ? (type as TabsType)
      : 'line';

  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== defaultValue.length) {
      setLayoutSubComponents(
        id,
        Array.from({ length: defaultValue.length }, () => [])
      );
    }
  }, [defaultValue, id, colComponents]);

  return (
    <Tabs
      className="XPreviewTabsLayout"
      tabs={defaultValue?.map((tab: any) => ({ title: tab.title }))}
      type={finalType}
      tabBarPosition={tabPosition}
      defaultActiveTab={0}
      tabBarHasDivider={false}
    >
      {defaultValue?.map((_tab: any, index: number) => (
        <div className="item" key={index}>
          <ReactSortable
            id={`workspace-content-${id}`}
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
              colComponents[index]?.map((cp: GridItem) => (
                <Fragment key={cp.id}>
                  {pageComponentSchemas[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                    <div
                      key={cp.id}
                      data-cp-type={cp.type}
                      data-cp-displayname={cp.displayName}
                      data-cp-id={cp.id}
                      className="componentItem"
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
    </Tabs>
  );
});

export default XPreviewTabsLayout;
