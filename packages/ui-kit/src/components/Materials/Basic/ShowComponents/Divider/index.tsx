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
    const hexToRgba = (hex: any, alpha = 1) => {
    const cleaned = hex.replace('#', '');
    const full =
      cleaned.length === 3
        ? cleaned
            .split('')
            .map((c: any) => c + c)
            .join('')
        : cleaned;

    const r = parseInt(full.slice(0, 2), 16);
    const g = parseInt(full.slice(2, 4), 16);
    const b = parseInt(full.slice(4, 6), 16);

    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  };

  const titleRender = () => {
    if(!label.display) return null;

    if(styleType === 'style7') {
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

    if(styleType === 'style8') {
      return (
        <div className={`decor-${styleType}`} style={{ backgroundColor: hexToRgba(color, 0.2) }}>
          <span
            className={`decor-title-${styleType}`}
            style={{ backgroundColor: `${color}`, color: `${titleColor}` }}
          >
            {label.text}
          </span>
        </div>
      )
    }

    if(styleType === 'style9') {
      return (
        <div
          className={`decor-${styleType}`}
          style={
            {
              backgroundColor: hexToRgba(color, 0.2),
              '--before-bg': hexToRgba(color, 0.6),
              '--after-bg': hexToRgba(color, 0.6)
            } as React.CSSProperties
          }
        >
          <div className='tabContent'>
            <div className={'tabLeft'} style={{ backgroundColor: `${color}` }}></div>
            <div className={'tabActive'} style={{ backgroundColor: `${color}`, color: `${titleColor}` }}>
              {label.text}
            </div>
            <div className={'tabRight'} style={{ backgroundColor: `${color}`}}></div>
          </div>
        </div>
      )
    }

    if(styleType === 'style10') {
      return (
        <div className={`decor-${styleType}`}>
          <div className={'leftArrows'}>
            <div className={'leftArrow2'} style={{ backgroundColor: hexToRgba(color, 0.2) }} ></div>
            <div className={'leftArrow1'} style={{ backgroundColor: hexToRgba(color, 0.6) }} ></div>
            <div className={'leftArrow'} style={{ backgroundColor: `${color}` }} ></div>
          </div>

          <div className={'center'} style={{ backgroundColor: `${color}`, color: `${titleColor}` }}>
            {label.text}
          </div>

          <div className={'rightArrows'}>
            <div className={'rightArrow'} style={{ backgroundColor: `${color}` }} ></div>
            <div className={'rightArrow1'} style={{ backgroundColor: hexToRgba(color, 0.6) }} ></div>
            <div className={'rightArrow2'} style={{ backgroundColor: hexToRgba(color, 0.2) }} ></div>
          </div>
        </div>
      )
    }

    return (
      <>
        <span className={['style5','style6'].includes(styleType) ? `decor-title-${styleType} title` : 'title'}
            style={{borderLeftColor: `${color}`, 
                    borderBottomColor: `${color}`, 
                    color: `${titleColor}`}}>
            {label.text}
        </span>
        <div className={`decor-${styleType}`} style={{borderTopColor: `${color}`}}></div>
      </>
    )
  }

  // =====  内部状态 & 回显 end =====


  // ===== 外部事件：选择数据 begin =====
  // ===== 外部事件：选择数据 end =====

  return (
    <div className="formWrapper">
        <div className='formDivider'>
          {titleRender()}
          {tooltip?.display && <span className='desc' 
                style={{ color: `${descriptionColor}` }}>{tooltip.text}</span>}
        </div>
    </div>
  );
});

export default XDivider;
