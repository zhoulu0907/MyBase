export const defaultTheme = '#4FAE7B';
const applicationWidth = 332;
const applicationHeight = 206;
const applicationGutterX = 24;
const applicationGutterY = 24;

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
    label: '迭代中',
    value: 1
  },
  {
    label: '已发布',
    value: 2
  }
];

export enum TagColor {
  'arcoblue',
  'red',
  'orangered'
}

export enum ApplicationStatus {
  ITERATE = 'iterate'
}

export enum ApplicationStatusLabel {
  ITERATE = '迭代中'
}

export const avatarBgColor = ['#009E9E', '#24B28F', '#1979FF', '#7E5AEA', '#EB693A', '#EBBC00'];

type themeColor = string;
type bgColor = string;
export const ThemeColorMap: Record<themeColor, bgColor> = {
  '#009E9E': '#E8FFEF',
  '#24B28F': '#E8F1FF',
  '#1979FF': '#E8F5FF',
  '#7E5AEA': '#F2E8FF',
  '#EB693A': '#FFF2E8',
  '#EBBC00': '#FFFEE8',
  '#4FAE7B': '#E8FFEF'
};
