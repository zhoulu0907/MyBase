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
    name: '页面',
    icon: 'icon-yemian',
    type: 'base'
  },
  {
    name: '熊猫',
    icon: 'icon-xiongmao_panda',
    type: 'base'
  },
  {
    name: '压缩',
    icon: 'icon-yasuo_compression',
    type: 'base'
  },
  {
    name: '熊',
    icon: 'icon-xiong_bear',
    type: 'base'
  },
  {
    name: '蜜蜂',
    icon: 'icon-mifeng_bee',
    type: 'base'
  },
  {
    name: '全部应用',
    icon: 'icon-quanbu_all-application',
    type: 'base'
  },
  {
    name: '法案',
    icon: 'icon-faan_bill',
    type: 'base'
  },
  {
    name: '蝴蝶',
    icon: 'icon-hudie_butterfly',
    type: 'base'
  },
  {
    name: '下投影',
    icon: 'icon-xiatouying_drop-shadow-down',
    type: 'base'
  },
  {
    name: '蝙蝠',
    icon: 'icon-bianfu_bat',
    type: 'base'
  },
  {
    name: '鸭子',
    icon: 'icon-yazi_duck',
    type: 'base'
  },
  {
    name: '鹿',
    icon: 'icon-lu_deer',
    type: 'base'
  },
  {
    name: '鹰',
    icon: 'icon-ying_eagle',
    type: 'base'
  },
  {
    name: '海豚',
    icon: 'icon-haitun_dolphin',
    type: 'base'
  },
  {
    name: '工程车',
    icon: 'icon-gongchengche_engineering-vehicle',
    type: 'base'
  },
  {
    name: '领带',
    icon: 'icon-lingdai_necktie',
    type: 'base'
  },
  {
    name: '猴',
    icon: 'icon-hou_monkey',
    type: 'base'
  },
  {
    name: '顶栏',
    icon: 'icon-hou_monkey',
    type: 'base'
  },
  {
    name: '主题',
    icon: 'icon-zhuti_theme',
    type: 'base'
  },
  {
    name: '文档失败',
    icon: 'icon-wendangshibai_doc-fail',
    type: 'base'
  },
  {
    name: '鸟',
    icon: 'icon-niao_bird',
    type: 'base'
  },
  {
    name: '搜索文件夹',
    icon: 'icon-sousuowenjianjia_folder-search',
    type: 'base'
  },
  {
    name: '牛',
    icon: 'icon-niu_cattle',
    type: 'base'
  },
  {
    name: '礼服长裙',
    icon: 'icon-lifuchangqun_full-dress-longuette',
    type: 'base'
  },
  {
    name: '反转镜头',
    icon: 'icon-fanzhuanjingtou_invert-camera',
    type: 'base'
  },
  {
    name: '工程牌',
    icon: 'icon-gongchengpai_engineering-brand',
    type: 'base'
  },
  {
    name: '删除',
    icon: 'icon-shanchu_delete-one',
    type: 'base'
  },
  {
    name: '轮播视频',
    icon: 'icon-lunboshipin_carousel-video',
    type: 'base'
  },
  {
    name: '鸽子',
    icon: 'icon-gezi_pigeon',
    type: 'base'
  },
  {
    name: '兔子',
    icon: 'icon-tuzi_rabbit',
    type: 'base'
  },
  {
    name: '工作台',
    icon: 'icon-gongzuotai_workbench',
    type: 'base'
  },
  {
    name: '文字',
    icon: 'icon-wenzi_text',
    type: 'base'
  },
  {
    name: '泡泡',
    icon: 'icon-paopao_soap-bubble',
    type: 'base'
  },
  {
    name: '狗',
    icon: 'icon-gou_dog',
    type: 'base'
  },
  {
    name: '文件夹',
    icon: 'icon-wenjianjia_seo-folder',
    type: 'base'
  },
  {
    name: '钱包',
    icon: 'icon-qianbao_wallet',
    type: 'base'
  },
  {
    name: '自然模式',
    icon: 'icon-ziranmoshi_natural-mode',
    type: 'base'
  },
  {
    name: '鱼',
    icon: 'icon-yu_fish-one',
    type: 'base'
  },
  {
    name: '电钻',
    icon: 'icon-dianzuan_electric-drill',
    type: 'base'
  },
  {
    name: '电位器',
    icon: 'icon-dianweiqi_potentiometer',
    type: 'base'
  },
  {
    name: '青蛙',
    icon: 'icon-qingwa_frog',
    type: 'base'
  },
  {
    name: '复古包',
    icon: 'icon-fugubao_retro-bag',
    type: 'base'
  },
  {
    name: '转变',
    icon: 'icon-zhuanbian_transform',
    type: 'base'
  },
  {
    name: '闪电',
    icon: 'icon-shandian_lightning',
    type: 'base'
  },
  {
    name: '反转相机',
    icon: 'icon-fanzhuanxiangji_reverse-lens-one',
    type: 'base'
  },
  {
    name: '短裙',
    icon: 'icon-duanqun_short-skirt',
    type: 'base'
  },
  {
    name: '河马',
    icon: 'icon-hema_hippo',
    type: 'base'
  },
  {
    name: '猫头鹰',
    icon: 'icon-maotouying_owl',
    type: 'base'
  },
  {
    name: '趋势',
    icon: 'icon-maotouying_owl',
    type: 'base'
  },
  {
    name: '鲸鱼',
    icon: 'icon-jingyu_whale',
    type: 'base'
  },
  {
    name: '背心',
    icon: 'icon-beixin_vest',
    type: 'base'
  },
  {
    name: 'LED灯',
    icon: 'icon-faguangerjiguan_led-diode',
    type: 'base'
  },
  {
    name: '线轮',
    icon: 'icon-xianlun_reel',
    type: 'base'
  },
  {
    name: '灭火器',
    icon: 'icon-miehuoqi_fire-extinguisher-one',
    type: 'base'
  },
  {
    name: '星星',
    icon: 'icon-xingxing_star',
    type: 'base'
  },
];
