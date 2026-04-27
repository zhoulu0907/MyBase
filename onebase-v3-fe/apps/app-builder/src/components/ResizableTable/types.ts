import type { TableProps } from '@arco-design/web-react';
import type { ReactNode } from 'react';

export interface ResizableTableProps extends TableProps {
  /** 是否启用列宽拖拽调整，默认 true */
  resizable?: boolean;
  /** 最小列宽，默认 50 */
  minWidth?: number;
  /** 最大列宽，默认 500 */
  maxWidth?: number;
  emptyContent?: ReactNode;
}

export interface ResizeHandleProps {
  handleAxis?: string;
  [key: string]: any;
}

export interface ResizableTitleProps {
  width?: number;
  onResize?: (e: React.SyntheticEvent, data: { size: { width: number } }) => void;
  [key: string]: any;
}