import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { COMPONENT_GROUP_NAME, EDITOR_TYPES, PreviewRender, getComponentWidth, usePageEditorSignal, type GridItem } from '@/index';
import { Button, Steps } from '@arco-design/web-react';
import { IconLeft, IconRight } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import { Fragment, memo, useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import './index.css';
import type { XPreviewStepsLayoutConfig } from './schema';

const Step = Steps.Step;

const XPreviewStepsLayout = memo((props: XPreviewStepsLayoutConfig & { detailMode?: boolean, showFromPageData?: Function; refresh?: number; }) => {
  const { id, defaultValue = [], type, colCount, labelPlacement, pageType, detailMode, showFromPageData, refresh } = props;
  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    setShowDeleteButton
  } = usePageEditorSignal(pageType || EDITOR_TYPES.FORM_EDITOR);

  const [current, setCurrent] = useState(0);
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== defaultValue.length) {
      setLayoutSubComponents(
        id,
        Array.from({ length: defaultValue.length }, () => [])
      );
    }
  }, [defaultValue, id, colComponents]);

  const handlePrev = () => {
    if (current > 0) {
      setCurrent(current - 1);
    }
  };

  const handleNext = () => {
    if (current < defaultValue.length - 1) {
      setCurrent(current + 1);
    }
  };

  const handleStepChange = (step: number) => {
    setCurrent(step);
  };

  return (
    <div className={`XPreviewStepsLayout ${pageType === EDITOR_TYPES.LIST_EDITOR ? 'listPreviewStepsLayout' : ''}`}>
      <div className="stepsContainer">
        <Steps
          current={current}
          type={type}
          labelPlacement={labelPlacement}
          onChange={handleStepChange}
        >
          {defaultValue?.map((step, index) => (
            <Step
              key={step.key}
              title={step.title}
              description={step.description}
            />
          ))}
        </Steps>
      </div>

      <div className="contentContainer">
        {defaultValue?.map((step, index) => (
          <div
            key={step.key}
            className={`stepContent ${current === index ? 'active' : ''}`}
            style={{ display: current === index ? 'block' : 'none' }}
          >
            <ReactSortable
              id={`workspace-content-${id}`}
              className="content"
              list={colComponents[index]}
              setList={(newList) => {
                colComponents[index] = newList;
              }}
              sort={false}
              disabled
              group={{
                name: COMPONENT_GROUP_NAME
              }}
              animation={150}
            >
              {colComponents[index] &&
                colComponents[index]?.map((cp: GridItem) => (
                  <Fragment key={cp.id}>
                    {pageComponentSchemas[cp.id]?.config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
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
                          showFromPageData={showFromPageData}
                          runtime={true}
                          refresh={refresh}
                          detailMode={detailMode}
                        />
                      </div>
                    )}
                  </Fragment>
                ))}
            </ReactSortable>
          </div>
        ))}
      </div>

      <div className="buttonContainer">
        <Button
          type="secondary"
          icon={<IconLeft />}
          onClick={handlePrev}
          disabled={current === 0}
        >
          上一步
        </Button>
        <Button
          type="primary"
          onClick={handleNext}
          disabled={current === defaultValue.length - 1}
        >
          下一步
          <IconRight />
        </Button>
      </div>
    </div>
  );
});

export default XPreviewStepsLayout;
