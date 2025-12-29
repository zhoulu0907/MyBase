//应用主题色
export const appThemeColor = ['#009E9E', '#24B28F', '#1979FF', '#7E5AEA', '#EB693A', '#EBBC00'];

// 应用图标
export const appIcon: string[] = [
  'bike',
  'bullet-map',
  'church-one',
  'data-display',
  'fingerprint-two',
  'platte',
  'arc-de-triomphe',
  'brdige-three',
  'bridge-two',
  'chinese-pavilion',
  'city-gate',
  'city-one',
  'elevator',
  'escalators',
  'factory-building',
  'monument-one',
  'link-cloud-faild',
  'network-tree',
  'link-break',
  'left-branch',
  'connection-point-two',
  'branch-one',
  'assembly-line',
  'bezier-curve',
  'anchor-round',
  'anchor',
  'good',
  'four',
  'five',
  'cool',
  'clap',
  'bad',
  'bye',
  'come',
  'easy',
  'concept-sharing',
  'data-lock',
  'database-code',
  'data-user',
  'database-download',
  'database-enter',
  'database-lock',
  'database-position',
  'database-time',
  'database-sync',
  'database-fail',
  'slide',
  'negative-dynamics',
  'parabola',
  'pie',
  'maslow-pyramids',
  'kagi-map',
  'histogram',
  'chart-histogram',
  'chart-graph',
  'change-date-sort',
  'phonograph',
  'phone',
  'mouse',
  'microphone',
  'memory',
  'master',
  'laptop',
  'lamp',
  'kettle',
  'Hdd',
  'gamepad',
  'flashlight',
  'gopro',
  'hardDiskOne',
  'open',
  'press',
  'yep',
  'zoom',
  'six',
  'pagoda',
  'palace',
  'shop',
  'school',
  'tower'
];

// 图标背景色
export const appIconColor: string[] = ['#009E9E', '#24B28F', '#1979FF', '#7E5AEA', '#EB693A', '#EBBC00'];

export interface Options {
  label: string;
  value: string;
}

// 创建大屏类型
export enum DashBoardCreateType {
  DashboardNew = 'dashboardNew', // 新建大屏
  DashboardTemplate = 'dashboardTemplate', // 大屏模版
  DashboardLink = 'dashboardLink' // 关联已创建大屏
}
