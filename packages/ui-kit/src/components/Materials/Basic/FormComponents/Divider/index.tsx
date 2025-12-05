// ===== 导入 begin =====
import { Form } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XFormDividerConfig } from './schema';

import '../index.css';
import './index.css';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XFormDivider = memo((props: XFormDividerConfig) => {
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

  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====

  // ===== 表单上下文与字段名与值读取 end =====

  // ===== 外部事件：选择数据 begin =====
  // ===== 外部事件：选择数据 end =====

  return (
    <div className="formWrapper">
      {/* <Form.Item
        label={
          label.display &&
          label.text && <span className={['style5','style6'].includes(styleType) ? `decor-title-${styleType} tooltipLabelText` : 'tooltipLabelText'}>{label.text}</span>
        }
        field={`${FORM_COMPONENT_TYPES.FORMDIVIDER}_${props.id}`}
        labelCol={{ style: { width: 200, flex: 'unset' } }}
        layout="vertical"
        style={{
          flex: 1,
          margin: 0
        }}
      > */}
        <div className='formDivider'>
            {label.display && 
                <span className={['style5','style6'].includes(styleType) ? `decor-title-${styleType} title` : 'title'}
                    style={{borderLeftColor: `${color}`, 
                            borderBottomColor: `${color}`, 
                            color: `${titleColor}`}}>
                    {label.text}
                </span>
            }
            <div className={`decor-${styleType}`} style={{borderTopColor: `${color}`}}></div>
            {tooltip?.display && <span className='desc' 
                style={{ color: `${descriptionColor}` }}>{tooltip.text}</span>}
        </div>
      {/* </Form.Item> */}
    </div>
  );
});

export default XFormDivider;
