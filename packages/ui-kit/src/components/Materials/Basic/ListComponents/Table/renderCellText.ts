import {
  SYSTEM_FIELD_BPM_CURRENT_NODE,
  SYSTEM_FIELD_BPM_INITIATOR_ID,
  SYSTEM_FIELD_BPM_INSTANCE_ID,
  SYSTEM_FIELD_BPM_STATUS,
  SYSTEM_FIELD_BPM_SUBMIT_TIME,
  SYSTEM_FIELD_BPM_TITLE,
  SYSTEM_FIELD_CREATED_TIME,
  SYSTEM_FIELD_UPDATED_TIME
} from '@onebase/common';
import dayjs from 'dayjs';

// 统一的表格文本渲染逻辑，供 XTable 和 DraftBox 复用
export const renderCellText = (columnId: string, v: any) => {
  if (v === null || v === undefined) return '';

  // 处理时间类型字段
  if (
    columnId === SYSTEM_FIELD_CREATED_TIME ||
    columnId === SYSTEM_FIELD_UPDATED_TIME ||
    columnId === SYSTEM_FIELD_BPM_SUBMIT_TIME
  ) {
    return dayjs(v).format('YYYY-MM-DD HH:mm:ss');
  }

  // 处理 BPM 对象类型字段（取 name）
  if (
    columnId === SYSTEM_FIELD_BPM_STATUS ||
    columnId === SYSTEM_FIELD_BPM_CURRENT_NODE ||
    columnId === SYSTEM_FIELD_BPM_INITIATOR_ID
  ) {
    if (v && typeof v === 'object') {
      return (v as any).name || '';
    } else {
      return String(v)
    }
  }

  // 处理 BPM 字符串类型字段
  if (columnId === SYSTEM_FIELD_BPM_TITLE || columnId === SYSTEM_FIELD_BPM_INSTANCE_ID) {
    return String(v);
  }

  // 通用兜底：对象类型优先取 name，name 为空则显示空字符串
  if (typeof v === 'object' && !Array.isArray(v)) {
    return (v as any).name || '';
  }

  return String(v);
};
