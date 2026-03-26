// 生成安全的随机字符串
const secureRandomString = (length: number): string => {
  const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
  const array = new Uint32Array(length);
  crypto.getRandomValues(array);
  return Array.from(array, (num) => chars[num % chars.length]).join('');
};

export const genId = (prefix: string = 'id'): string => `${prefix}_${secureRandomString(6)}`

import { STATUS_OPTIONS, STATUS_VALUES } from './constants'

export const isHidden = (status: string): boolean => status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]

export const isReadonly = (status: string, detailMode?: boolean): boolean =>
  status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || !!detailMode

export const computeInteractive = (status: string, runtime: boolean, detailMode?: boolean): boolean =>
  runtime && status !== STATUS_VALUES[STATUS_OPTIONS.READONLY] && !detailMode

export const formItemStyle = (status: string) => ({
  margin: 0,
  opacity: isHidden(status) ? 0.4 : 1
})

export const wrapperStyle = (width: string) => ({
  width,
  display: 'inline-block',
  verticalAlign: 'top',
  paddingRight: 12
})
