import { Card } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XCanvasCardConfig } from './schema';
import CanvasCardType1 from './components/CanvasCardType1';
import CanvasCardType2 from './components/CanvasCardType2';
import './index.css';

const XCanvasCard = memo((props: XCanvasCardConfig & { runtime?: boolean; detailMode?: boolean; componentName?: string }) => {
  const { status, runtime = true, config, componentName = 'CanvasCardType1' } = props;

  // 根据组件名字动态渲染
  const renderComponent = () => {
    switch (componentName) {
      case 'CanvasCardType1':
        return <CanvasCardType1 {...props} />;
      case 'CanvasCardType2':
        return <CanvasCardType2 {...props} />;
      default:
        return <CanvasCardType1 {...props} />;
    }
  };

  return (
    <div className="canvas-card-container" style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}>
      <Card className="XCanvasCard">
        {renderComponent()}
      </Card>
    </div>
  );
});

export default XCanvasCard;