import { SYSTEM_FIELD_CREATED_TIME, SYSTEM_FIELD_UPDATED_TIME } from '@onebase/common';
import dayjs from 'dayjs';

// 统一的表格文本渲染逻辑，供 XTable 和 DraftBox 复用
export const renderCellText = (columnId: string, v: any) => {
  if (v === null || v === undefined) return '';

  if (typeof v === 'object') {
    console.log(v);
    if ('name' in v && typeof (v as any).name !== 'undefined') return (v as any).name;
    return '';
  }

  if (columnId === SYSTEM_FIELD_CREATED_TIME || columnId === SYSTEM_FIELD_UPDATED_TIME) {
    return dayjs(v).format('YYYY-MM-DD HH:mm:ss');
  }

  return v as any;
};
