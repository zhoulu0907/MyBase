import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { COMPONENT_GROUP_NAME, PreviewRender, getComponentWidth, usePageEditorSignal, type GridItem } from '@/index';
import { Tabs } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { Fragment, memo, useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import './index.css';
import type { XTabsLayoutConfig } from './schema';

const TabPane = Tabs.TabPane;

const XPreviewTabsLayout = memo((props: XTabsLayoutConfig & { detailMode?: boolean }) => {
  const { id, defaultValue = [], type, colCount, tabPosition, pageType, detailMode } = props;
  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    setShowDeleteButton
  } = usePageEditorSignal(pageType);

  const [activeTab, setActiveTab] = useState('1');
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== defaultValue.length) {
      setLayoutSubComponents(
        id,
        Array.from({ length: defaultValue.length }, () => [])
      );
    }
    console.log('layoutSubComponents---',layoutSubComponents)
    console.log('id---',id)
  }, [defaultValue, id, colComponents]);

  return (
    <Tabs
      className="XPreviewTabsLayout"
      activeTab={activeTab}
      type={type}
      tabPosition={tabPosition}
      onClickTab={(e) => setActiveTab(e)}
    >
      {defaultValue?.map((tab, index) => (
        <TabPane key={tab.key} title={tab.title} style={{ padding: 0 }}>
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
        </TabPane>
      ))}
      /
    </Tabs>
  );
});

export default XPreviewTabsLayout;
