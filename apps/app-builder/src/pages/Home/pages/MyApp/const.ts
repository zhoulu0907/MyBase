export const defaultTheme = '#4FAE7B';
const applicationWidth = 332;
const applicationHeight = 206;
const applicationGutterX = 24;
const applicationGutterY = 28;

export function calculateMaxItems(containerWidth: number, containerHeight: number) {
  const cols = Math.floor((containerWidth + applicationGutterX) / (applicationWidth + applicationGutterX));
  const rows = Math.floor((containerHeight + applicationGutterY) / (applicationHeight + applicationGutterY));
  return {
    cols,
    rows,
    total: cols * rows
  };
}

export const appOptions = [
  {
    label: '全部应用',
    value: 0
  },
  {
    label: '我创建的',
    value: 1
  }
];

export const createTimeOptions = [
  {
    label: '按创建时间排序',
    value: 'create'
  },
  {
    label: '按更新时间排序',
    value: 'update'
  }
];

export const statusOptions = [
  {
    label: '全部状态',
    value: ''
  },
  {
    label: '开发中',
    value: 0
  },
  {
    label: '已发布',
    value: 1
  }
];

export enum TagColor {
  'arcoblue',
  'red',
  'orangered'
}

export const avatarBgColor = ['#009e9e', '#24B28F', '#1979FF', '#7E5AEA', '#EB693A', '#EBBC00'];
