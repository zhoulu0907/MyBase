// ===== 导入 begin =====
import { memo } from 'react';

import type { XDividerConfig } from './schema';

import '../index.css';
import './index.css';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XDivider = memo((props: XDividerConfig) => {
  // ===== 外部 props begin =====
  const {
    label,
    tooltip,
    styleType,
    color,
    titleColor,
    descriptionColor,
  } = props;
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====
  const titleRender = () => {
    if(!label.display) return null;

    if(styleType !== 'style7') {
      return (
        <span className={['style5','style6'].includes(styleType) ? `decor-title-${styleType} title` : 'title'}
            style={{borderLeftColor: `${color}`, 
                    borderBottomColor: `${color}`, 
                    color: `${titleColor}`}}>
            {label.text}
        </span>
      )
    }

    return (
      <div className={`decor-title-${styleType}`} style={{ borderBottomColor: `${color}` }}>
        <div className={'label'} style={{ backgroundColor: `${color}`,color: `${titleColor}` }}>
            {label.text}
        </div>
        <div className={'decorator'} style={{ backgroundColor: `${color}` }}></div>
        <div className={'decorator1'} style={{ backgroundColor: `${color}` }}></div>
        <div className={'decorator2'} style={{ backgroundColor: `${color}` }}></div>
        <div className={'decorator3'} style={{ backgroundColor: `${color}` }}></div>
      </div>
    )
  }

  // =====  内部状态 & 回显 end =====


  // ===== 外部事件：选择数据 begin =====
  // ===== 外部事件：选择数据 end =====

  return (
    <div className="formWrapper">
        <div className='formDivider'>
          {titleRender()}
          <div className={`decor-${styleType}`} style={{borderTopColor: `${color}`}}></div>
            {tooltip?.display && <span className='desc' 
                style={{ color: `${descriptionColor}` }}>{tooltip.text}</span>}
        </div>
    </div>
  );
});

export default XDivider;
