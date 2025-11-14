import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Layout } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { Fragment, useEffect } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { getComponentWidth } from 'src/components/Materials/schema';
import PreviewRender from 'src/components/render/PreviewRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { COMPONENT_GROUP_NAME, EDITOR_TYPES, type GridItem } from 'src/utils/const';
import './index.css';
import { type XColumnLayoutConfig } from './schema';

const XPreviewColumnLayout = (props: XColumnLayoutConfig) => {
  const { colCount, id, pageType } = props;

  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    setShowDeleteButton
  } = usePageEditorSignal(pageType || EDITOR_TYPES.FORM_EDITOR);

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      setLayoutSubComponents(
        id,
        Array.from({ length: colCount }, () => [])
      );
    }
  }, [colCount, id, colComponents]);

  return (
    <Layout className="XPreviewColumnLayout">
      {colComponents.map((_colComponents, index) => (
        <div key={index} className="item">
          <ReactSortable
            id={`workspace-content-${id}-${index}`}
            list={colComponents[index]}
            setList={(newList) => {
              colComponents[index] = newList;
            }}
            group={{
              name: COMPONENT_GROUP_NAME
            }}
            animation={150}
            className="content"
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
                        console.log('点击组件: ', cp.id);
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
                        detailMode={true}
                      />
                    </div>
                  )}
                </Fragment>
              ))}
          </ReactSortable>
        </div>
      ))}
    </Layout>
  );
};

export default XPreviewColumnLayout;
