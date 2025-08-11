import { IconApps, IconArrowDown, IconArrowLeft, IconArrowRight, IconArrowUp, IconBug, IconCalendar, IconCheckCircle, IconClockCircle, IconCloseCircle, IconCompass, IconDelete, IconDownload, IconEdit, IconEmail, IconEye, IconFile, IconImage, IconInfoCircle, IconLaunch, IconLayout, IconList, IconLoading, IconLocation, IconMoon, IconNotification, IconPhone, IconPrinter, IconPushpin, IconRobot, IconStorage, IconSubscribeAdd, IconSubscribed, IconSun, IconTag, IconTrophy, IconUnlock, IconUser, IconWifi } from '@arco-design/web-react/icon';

export interface MenuItem {
  name: string;
  type: string;
  icon: React.ComponentType<{ className?: string }>; // 允许传递 className 属性
}

// 菜单图标类型
export const menuIconType = [
  {
    name: '全部',
    key: 'all',
  },
  {
    name: '基础',
    key: 'base',
  },
  {
    name: '方向',
    key: 'direct',
  },
  {
    name: '操作',
    key: 'operate',
  },
  {
    name: '提示',
    key: 'tips',
  },
];

export const menuIconList: MenuItem[] = [
  {
    name: '向下',
    icon: IconArrowDown,
    type: 'direct',
  },
  {
    name: '向左',
    icon: IconArrowLeft,
    type: 'direct',
  },
  {
    name: '向右',
    icon: IconArrowRight,
    type: 'direct',
  },
  {
    name: '向上',
    icon: IconArrowUp,
    type: 'direct',
  },
  {
    name: '对号（圆形）',
    icon: IconCheckCircle,
    type: 'tips',
  },
  {
    name: '时间',
    icon: IconClockCircle,
    type: 'tips',
  },
  {
    name: '关闭',
    icon: IconCloseCircle,
    type: 'tips',
  },
  {
    name: '信息',
    icon: IconInfoCircle,
    type: 'tips',
  },
  {
    name: '下载',
    icon: IconDownload,
    type: 'operate',
  },
  {
    name: '查看',
    icon: IconEye,
    type: 'operate',
  },
  {
    name: '目录',
    icon: IconList,
    type: 'operate',
  },
  {
    name: '分享',
    icon: IconLaunch,
    type: 'operate',
  },
  {
    name: '编辑',
    icon: IconEdit,
    type: 'operate',
  },
  {
    name: '删除',
    icon: IconDelete,
    type: 'operate',
  },
  {
    name: '应用',
    icon: IconApps,
    type: 'base',
  },
  {
    name: '缺陷',
    icon: IconBug,
    type: 'base',
  },
  {
    name: '日历',
    icon: IconCalendar,
    type: 'base',
  },
  {
    name: '指南针',
    icon: IconCompass,
    type: 'base',
  },
  {
    name: '邮箱',
    icon: IconEmail,
    type: 'base',
  },
  {
    name: '文件',
    icon: IconFile,
    type: 'base',
  },
  {
    name: '人员',
    icon: IconUser,
    type: 'base',
  },
  {
    name: 'Wifi',
    icon: IconWifi,
    type: 'base',
  },
  {
    name: '解锁',
    icon: IconUnlock,
    type: 'base',
  },
  {
    name: '奖杯',
    icon: IconTrophy,
    type: 'base',
  },
  {
    name: '标签',
    icon: IconTag,
    type: 'base',
  },
  {
    name: '太阳',
    icon: IconSun,
    type: 'base',
  },
  {
    name: '订阅',
    icon: IconSubscribed,
    type: 'base',
  },
  {
    name: '添加订阅',
    icon: IconSubscribeAdd,
    type: 'base',
  },
  {
    name: '存储',
    icon: IconStorage,
    type: 'base',
  },
  {
    name: '机器人',
    icon: IconRobot,
    type: 'base',
  },
  {
    name: '图钉',
    icon: IconPushpin,
    type: 'base',
  },
  {
    name: '打印机',
    icon: IconPrinter,
    type: 'base',
  },
  {
    name: '电话',
    icon: IconPhone,
    type: 'base',
  },
  {
    name: '月亮',
    icon: IconMoon,
    type: 'base',
  },
  {
    name: '通知',
    icon: IconNotification,
    type: 'base',
  },
  {
    name: '位置',
    icon: IconLocation,
    type: 'base',
  },
  {
    name: '加载中',
    icon: IconLoading,
    type: 'base',
  },
  {
    name: '布局',
    icon: IconLayout,
    type: 'base',
  },
  {
    name: '图片',
    icon: IconImage,
    type: 'base',
  },
];