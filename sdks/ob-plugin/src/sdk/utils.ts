/**
 * 生成唯一 ID
 * @param prefix 前缀
 * @returns unique id
 */
export const genId = (prefix: string = 'id'): string => {
  return `${prefix}_${Math.random().toString(36).slice(2, 8)}`;
};

import { STATUS_OPTIONS, STATUS_VALUES } from './constants';

export const isHidden = (status: string): boolean => status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN];

export const isReadonly = (status: string, detailMode?: boolean): boolean =>
  status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || !!detailMode;

export const computeInteractive = (status: string, runtime: boolean, detailMode?: boolean): boolean =>
  runtime && status !== STATUS_VALUES[STATUS_OPTIONS.READONLY] && !detailMode;

export const formItemStyle = (status: string) => ({
  margin: 0,
  opacity: isHidden(status) ? 0.4 : 1
});

export const wrapperStyle = (width: string) => ({
  width,
  display: 'inline-block',
  verticalAlign: 'top',
  paddingRight: 12
});
