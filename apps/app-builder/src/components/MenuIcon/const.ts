export interface MenuItem {
  name: string;
  type: string;
  icon: string;
}

// 菜单图标类型
export const menuIconType = [
  {
    name: '全部',
    key: 'all'
  },
  {
    name: '基础',
    key: 'base'
  },
  {
    name: '方向',
    key: 'direct'
  },
  {
    name: '操作',
    key: 'operate'
  },
  {
    name: '提示',
    key: 'tips'
  }
];

export const menuIconList: MenuItem[] = [
  {
    name: '向下',
    icon: 'icon-direction-down',
    type: 'direct'
  },
  {
    name: '向左',
    icon: 'icon-direction-left',
    type: 'direct'
  },
  {
    name: '向右',
    icon: 'icon-direction-right',
    type: 'direct'
  },
  {
    name: '向上',
    icon: 'icon-direction-up',
    type: 'direct'
  },
  {
    name: '铃铛',
    icon: 'icon-tips',
    type: 'tips'
  },
  {
    name: '播放',
    icon: 'icon-play',
    type: 'operate'
  },
  {
    name: '查看',
    icon: 'icon-see',
    type: 'operate'
  },
  {
    name: '隐藏',
    icon: 'icon-unsee',
    type: 'operate'
  },
  {
    name: '踩',
    icon: 'icon-cai-l',
    type: 'operate'
  },
  {
    name: '收藏',
    icon: 'icon-shoucang',
    type: 'operate'
  },
  {
    name: '点赞',
    icon: 'icon-zan',
    type: 'operate'
  },
  {
    name: '回收站',
    icon: 'icon-icon_huishouzhan',
    type: 'operate'
  },
  {
    name: '更多',
    icon: 'icon-More',
    type: 'operate'
  },
  {
    name: '更多2',
    icon: 'icon-more',
    type: 'operate'
  },
  {
    name: '下载',
    icon: 'icon-direction-down-circle',
    type: 'operate'
  },
  {
    name: '手机',
    icon: 'icon-phone',
    type: 'base'
  },
  {
    name: '表情',
    icon: 'icon-face',
    type: 'base'
  },
  {
    name: '用户',
    icon: 'icon-user',
    type: 'base'
  },
  {
    name: '分类',
    icon: 'icon-fen_lei2',
    type: 'base'
  },
  {
    name: '邮箱',
    icon: 'icon-email',
    type: 'base'
  },
  {
    name: '按钮区',
    icon: 'icon-menu',
    type: 'base'
  },
  {
    name: '设置',
    icon: 'icon-setting',
    type: 'base'
  },
  {
    name: '书',
    icon: 'icon-book',
    type: 'base'
  },
  {
    name: '动态标签',
    icon: 'icon-tagso',
    type: 'base'
  },
  {
    name: '日历',
    icon: 'icon-rili',
    type: 'base'
  },
  {
    name: '标签',
    icon: 'icon-tag',
    type: 'base'
  },
  {
    name: '对话',
    icon: 'icon-message',
    type: 'base'
  },
  {
    name: '摄影',
    icon: 'icon-Movies',
    type: 'base'
  },
  {
    name: '话筒',
    icon: 'icon-huatong',
    type: 'base'
  },
  {
    name: '监控1',
    icon: 'icon-jiankong',
    type: 'base'
  },
  {
    name: '列表',
    icon: 'icon-list',
    type: 'base'
  },
  {
    name: '链接',
    icon: 'icon-URLguanli',
    type: 'base'
  },
  {
    name: '打印机',
    icon: 'icon-dayinji',
    type: 'base'
  },
  {
    name: '监控2',
    icon: 'icon-mti-jiankong',
    type: 'base'
  }
];
