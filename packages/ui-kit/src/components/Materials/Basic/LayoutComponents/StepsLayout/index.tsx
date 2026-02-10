import { Button, Steps } from '@arco-design/web-react';
import { IconLeft, IconRight } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { COMPONENT_GROUP_NAME, usePageEditorSignal } from '@/index';
import { useSignals } from '@preact/signals-react/runtime';
import './index.css';
import type { XStepsLayoutConfig } from './schema';
import LayoutReactSortable from '../components/layoutReactSortable';

const Step = Steps.Step;

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth;

const XStepsLayout = memo((props: XStepsLayoutConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { id, defaultValue = [], type, colCount, labelPlacement, showDescription = false, color, runtime = true } = props;
  useSignals();

  const { layoutSubComponents, setLayoutSubComponents } = usePageEditorSignal();

  const [current, setCurrent] = useState(1);
  const colComponents = layoutSubComponents[id] || Array.from({ length: defaultValue.length }, () => []);

  useEffect(() => {
    if (!id) {
      return;
    }
    const currentColumns = layoutSubComponents[id] || [];
    const newLength = defaultValue.length;

    if (currentColumns.length !== newLength) {
      let updatedColumns;

      if (newLength > currentColumns.length) {
        const diff = newLength - currentColumns.length;
        const newEmptyArrays = Array.from({ length: diff }, () => []);
        updatedColumns = [...currentColumns, ...newEmptyArrays];
      } else {
        updatedColumns = currentColumns.slice(0, newLength);
      }

      setLayoutSubComponents(id, updatedColumns);
    }
  }, [defaultValue, id]);

  const handlePrev = () => {
    if (current > 1) {
      setCurrent(current - 1);
    }
  };

  const handleNext = () => {
    if (current < defaultValue.length) {
      setCurrent(current + 1);
    }
  };

  const handleStepChange = (step: number) => {
    setCurrent(step);
  };

  return (
    <div 
      className="XStepsLayout"
      style={{
        '--primary-6': color || undefined
      } as React.CSSProperties}
    >
      <div className="stepsContainer">
        <Steps
          current={current}
          type={type}
          labelPlacement={labelPlacement}
          onChange={handleStepChange}
          style={{
            maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`
          }}
        >
          {defaultValue?.map((step, index) => (
            <Step
              key={step.key}
              title={step.title}
              description={showDescription ? step.description : undefined}
            />
          ))}
        </Steps>
      </div>

      <div className="contentContainer">
        {defaultValue?.map((step, index) => (
          <div
            key={step.key}
            className={`stepContent ${current === index + 1 ? 'active' : ''}`}
            style={{ display: current === index + 1 ? 'block' : 'none' }}
          >
            <LayoutReactSortable
              id={id}
              sortableId={`workspace-content-${id}-${index}`}
              colComponents={colComponents}
              groupName={COMPONENT_GROUP_NAME}
              index={index}
              runtime={runtime}
            />
          </div>
        ))}
      </div>

      <div className="buttonContainer">
        <Button
          type="secondary"
          icon={<IconLeft />}
          onClick={handlePrev}
          style={{ display: current === 1 ? 'none' : undefined }}
        >
          上一步
        </Button>
        <Button
          type="outline"
          onClick={handleNext}
          style={{ display: current === defaultValue.length ? 'none' : undefined }}
        >
          下一步
          <IconRight />
        </Button>
      </div>
    </div>
  );
});

export default XStepsLayout;
