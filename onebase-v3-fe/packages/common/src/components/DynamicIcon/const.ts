import {
  //电商财产
  Bank,
  BankCard,
  Blockchain,
  Commodity,
  Consume,
  Coupon,
  ExchangeFour,
  Finance,
  FinancingOne,
  FlashPayment,
  Gift,
  HeavyMetal,
  MallBag,
  RedEnvelope,
  ScanCode,
  Shopping,
  Transaction,
  WaterRate,
  Waves,
  Workbench,

  //建筑
  Application,
  BrdigeThree,
  BuildingOne,
  Castle,
  ChinesePavilion,
  ChurchOne,
  Circus,
  City,
  EiffelTower,
  FerrisWheel,
  GreatWall,
  HomeTwo,
  HospitalTwo,
  MonumentOne,
  MuseumOne,
  PearlOfTheOrient,
  School,
  SignalTowerOne,
  TowerOfPisa,
  WindmillOne,

  // 交通旅游
  Airplane,
  BusOne,
  CableCar,
  CoconutTree,
  CompassOne,
  Cruise,
  Earth,
  Flag,
  Gps,
  HighSpeedRail,
  International,
  Landscape,
  LocalPin,
  MapDraw,
  Sunrise,
  Sunset,
  Theater,
  Taxi,
  RoadSignBoth,
  Sailing,

  // 界面组件
  Carousel,
  Checklist,
  Page,
  SeoFolder,
  PageTemplate,
  Platte,
  SwitchButton,
  TableFile,
  TreeList,

  // 时间日期
  AlarmClock,
  BigClock,
  CalendarDot,
  CalendarThirty,
  Dashboard,
  History,
  Iwatch,
  LoadingThree,
  RadarTwo,
  Speed,
  Stopwatch,
  StopwatchStart,
  Time,
  Watch,

  // 数据
  DataDisplay,
  DataLock,
  DataSwitching,
  DataUser,
  DatabaseAlert,
  DatabaseCode,
  DatabaseConfig,
  DatabaseDownload,
  DatabaseEnter,
  DatabaseFail,
  DatabaseFirst,
  DatabaseForbid,
  DatabaseLock,
  DatabaseNetwork,
  DatabaseNetworkPoint,
  DatabasePoint,
  DatabasePower,
  DatabaseSearch,
  DatabaseSetting,
  DatabaseTime,

  // 数据图表
  ActivitySource,
  Arithmetic,
  BroadcastOne,
  BubbleChart,
  ChartGraph,
  ChartHistogram,
  ChartLine,
  ChartPie,
  ChartRing,
  ChartScatter,
  ChartStock,
  Data,
  DataScreen,
  DataSheet,
  Electrocardiogram,
  Form,
  Histogram,
  KagiMap,
  MaslowPyramids,
  Ranking,

  // 硬件
  AirConditioning,
  BatteryWorkingOne,
  Bike,
  BoltOne,
  BroadcastRadio,
  Calculator,
  CameraFive,
  Car,
  ChargingTreasure,
  Chip,
  CloudStorage,
  Computer,
  Devices,
  Disk,
  DiskOne,
  Drone,
  ElectronicDoorLock,
  Flashlight,
  GameConsole,
  HairDryerOne
} from '@icon-park/react';

export interface MenuItem {
  name: string;
  type: string;
  code: string;
  icon: any;
  [property: string]: any;
}

// 菜单图标类型
export const menuIconType = [
  {
    name: '全部',
    key: 'all'
  },
  {
    name: '电商财产',
    key: 'wealth'
  },
  {
    name: '建筑',
    key: 'building'
  },
  {
    name: '交通旅游',
    key: 'transportationTourism'
  },
  {
    name: '界面组件',
    key: 'interface'
  },
  {
    name: '时间日期',
    key: 'date'
  },
  {
    name: '数据',
    key: 'data'
  },
  {
    name: '数据图表',
    key: 'dataChart'
  },
  {
    name: '硬件',
    key: 'hardware'
  }
];

export const menuIconList: MenuItem[] = [
  {
    name: '银行',
    code: 'bank',
    icon: Bank,
    type: 'wealth'
  },
  {
    name: '银行卡',
    code: 'bank-card',
    icon: BankCard,
    type: 'wealth'
  },
  {
    name: '区块链',
    code: 'block-chain',
    icon: Blockchain,
    type: 'wealth'
  },
  {
    name: '商品',
    code: 'commodity',
    icon: Commodity,
    type: 'wealth'
  },
  {
    name: '消费',
    code: 'consume',
    icon: Consume,
    type: 'wealth'
  },
  {
    name: '消费券',
    code: 'coupon',
    icon: Coupon,
    type: 'wealth'
  },
  {
    name: '兑换',
    code: 'exchange-four',
    icon: ExchangeFour,
    type: 'wealth'
  },
  {
    name: '金融',
    code: 'finance',
    icon: Finance,
    type: 'wealth'
  },
  {
    name: '理财',
    code: 'financing-one',
    icon: FinancingOne,
    type: 'wealth'
  },
  {
    name: '闪付',
    code: 'flash-payment',
    icon: FlashPayment,
    type: 'wealth'
  },
  {
    name: '礼物',
    code: 'gift',
    icon: Gift,
    type: 'wealth'
  },
  {
    name: '重金属',
    code: 'heavy-metal',
    icon: HeavyMetal,
    type: 'wealth'
  },
  {
    name: '购物袋',
    code: 'mall-bag',
    icon: MallBag,
    type: 'wealth'
  },
  {
    name: '红包',
    code: 'red-envelope',
    icon: RedEnvelope,
    type: 'wealth'
  },
  {
    name: '扫码',
    code: 'scan-code',
    icon: ScanCode,
    type: 'wealth'
  },
  {
    name: '购物车',
    code: 'shoppong',
    icon: Shopping,
    type: 'wealth'
  },
  {
    name: '成交',
    code: 'transaction',
    icon: Transaction,
    type: 'wealth'
  },
  {
    name: '水费',
    code: 'water-rate',
    icon: WaterRate,
    type: 'wealth'
  },
  {
    name: '电波',
    code: 'waves',
    icon: Waves,
    type: 'wealth'
  },
  {
    name: '工作台',
    code: 'workbench',
    icon: Workbench,
    type: 'wealth'
  },

  {
    name: '应用',
    code: 'application',
    icon: Application,
    type: 'building'
  },
  {
    name: '桥',
    code: 'brdige-three',
    icon: BrdigeThree,
    type: 'building'
  },
  {
    name: '建筑',
    code: 'building-one',
    icon: BuildingOne,
    type: 'building'
  },
  {
    name: '城堡',
    code: 'castle',
    icon: Castle,
    type: 'building'
  },
  {
    name: '中国楼阁',
    code: 'chinese-pavilion',
    icon: ChinesePavilion,
    type: 'building'
  },
  {
    name: '教堂',
    code: 'church-one',
    icon: ChurchOne,
    type: 'building'
  },
  {
    name: '马戏团',
    code: 'circus',
    icon: Circus,
    type: 'building'
  },
  {
    name: '城市',
    code: 'city',
    icon: City,
    type: 'building'
  },
  {
    name: '埃菲尔铁塔',
    code: 'eiffel-tower',
    icon: EiffelTower,
    type: 'building'
  },
  {
    name: '摩天轮',
    code: 'ferris-wheel',
    icon: FerrisWheel,
    type: 'building'
  },
  {
    name: '长城',
    code: 'great-wall',
    icon: GreatWall,
    type: 'building'
  },
  {
    name: '首页',
    code: 'home-two',
    icon: HomeTwo,
    type: 'building'
  },
  {
    name: '医院',
    code: 'hospital-two',
    icon: HospitalTwo,
    type: 'building'
  },
  {
    name: '纪念碑',
    code: 'monument-one',
    icon: MonumentOne,
    type: 'building'
  },
  {
    name: '博物馆',
    code: 'museum-one',
    icon: MuseumOne,
    type: 'building'
  },
  {
    name: '东方明珠',
    code: 'pearl-of-the-orient',
    icon: PearlOfTheOrient,
    type: 'building'
  },
  {
    name: '学校',
    code: 'school',
    icon: School,
    type: 'building'
  },
  {
    name: '信号塔',
    code: 'signal-tower-one',
    icon: SignalTowerOne,
    type: 'building'
  },
  {
    name: '比萨斜塔',
    code: 'tower-of-pisa',
    icon: TowerOfPisa,
    type: 'building'
  },
  {
    name: '风车',
    code: 'windmill-one',
    icon: WindmillOne,
    type: 'building'
  },

  {
    name: '飞机',
    code: 'airplane',
    icon: Airplane,
    type: 'transportationTourism'
  },
  {
    name: '公交',
    code: 'bus-one',
    icon: BusOne,
    type: 'transportationTourism'
  },
  {
    name: '缆车',
    code: 'cable-car',
    icon: CableCar,
    type: 'transportationTourism'
  },
  {
    name: '椰子树',
    code: 'coconut-tree',
    icon: CoconutTree,
    type: 'transportationTourism'
  },
  {
    name: '指南针',
    code: 'compass-one',
    icon: CompassOne,
    type: 'transportationTourism'
  },
  {
    name: '航海',
    code: 'cruise',
    icon: Cruise,
    type: 'transportationTourism'
  },
  {
    name: '地球仪',
    code: 'earth',
    icon: Earth,
    type: 'transportationTourism'
  },
  {
    name: '旗子',
    code: 'flag',
    icon: Flag,
    type: 'transportationTourism'
  },
  {
    name: '导航',
    code: 'gps',
    icon: Gps,
    type: 'transportationTourism'
  },
  {
    name: '高铁',
    code: 'high-speed-rail',
    icon: HighSpeedRail,
    type: 'transportationTourism'
  },
  {
    name: '国际化',
    code: 'tnternational',
    icon: International,
    type: 'transportationTourism'
  },
  {
    name: '风景区',
    code: 'landscape',
    icon: Landscape,
    type: 'transportationTourism'
  },
  {
    name: '本地',
    code: 'local-pin',
    icon: LocalPin,
    type: 'transportationTourism'
  },
  {
    name: '地图',
    code: 'map-draw',
    icon: MapDraw,
    type: 'transportationTourism'
  },
  {
    name: '日出',
    code: 'sunrise',
    icon: Sunrise,
    type: 'transportationTourism'
  },
  {
    name: '日落',
    code: 'sunset',
    icon: Sunset,
    type: 'transportationTourism'
  },
  {
    name: '影院',
    code: 'theater',
    icon: Theater,
    type: 'transportationTourism'
  },
  {
    name: '出租车',
    code: 'taxi',
    icon: Taxi,
    type: 'transportationTourism'
  },
  {
    name: '方向标识',
    code: 'road-sign-both',
    icon: RoadSignBoth,
    type: 'transportationTourism'
  },
  {
    name: '航行',
    code: 'sailing',
    icon: Sailing,
    type: 'transportationTourism'
  },

  {
    name: '轮播',
    code: 'carousel',
    icon: Carousel,
    type: 'interface'
  },
  {
    name: '检查列表',
    code: 'checklist',
    icon: Checklist,
    type: 'interface'
  },
  {
    name: '页面',
    code: 'page',
    icon: Page,
    type: 'interface'
  },
  {
    name: '文件夹',
    code: 'seo-folder',
    icon: SeoFolder,
    type: 'interface'
  },
  {
    name: '页面模板',
    code: 'page-template',
    icon: PageTemplate,
    type: 'interface'
  },
  {
    name: '调色盘',
    code: 'platte',
    icon: Platte,
    type: 'interface'
  },
  {
    name: '切换按钮',
    code: 'switch-button',
    icon: SwitchButton,
    type: 'interface'
  },
  {
    name: '表格',
    code: 'table-file',
    icon: TableFile,
    type: 'interface'
  },
  {
    name: '树结构',
    code: 'tree-list',
    icon: TreeList,
    type: 'interface'
  },

  {
    name: '闹钟',
    code: 'alarm-clock',
    icon: AlarmClock,
    type: 'date'
  },
  {
    name: '大钟表',
    code: 'big-clock',
    icon: BigClock,
    type: 'date'
  },
  {
    name: '日历',
    code: 'calendar-dot',
    icon: CalendarDot,
    type: 'date'
  },
  {
    name: '日历2',
    code: 'calendar-thirty',
    icon: CalendarThirty,
    type: 'date'
  },
  {
    name: '仪表盘',
    code: 'dashboard',
    icon: Dashboard,
    type: 'date'
  },
  {
    name: '历史记录',
    code: 'history',
    icon: History,
    type: 'date'
  },
  {
    name: '智能手表',
    code: 'iwatch',
    icon: Iwatch,
    type: 'date'
  },
  {
    name: '加载',
    code: 'loading-three',
    icon: LoadingThree,
    type: 'date'
  },
  {
    name: '雷达',
    code: 'radar-two',
    icon: RadarTwo,
    type: 'date'
  },
  {
    name: '速度',
    code: 'speed',
    icon: Speed,
    type: 'date'
  },
  {
    name: '秒表',
    code: 'stopwatch',
    icon: Stopwatch,
    type: 'date'
  },
  {
    name: '计时器',
    code: 'stopwatch-start',
    icon: StopwatchStart,
    type: 'date'
  },
  {
    name: '时间',
    code: 'time',
    icon: Time,
    type: 'date'
  },
  {
    name: '手表',
    code: 'watch',
    icon: Watch,
    type: 'date'
  },

  {
    name: '数据显示',
    code: 'data-display',
    icon: DataDisplay,
    type: 'data'
  },
  {
    name: '数据锁定',
    code: 'data-lock',
    icon: DataLock,
    type: 'data'
  },
  {
    name: '数据切换',
    code: 'data-switching',
    icon: DataSwitching,
    type: 'data'
  },
  {
    name: '数据用户',
    code: 'data-user',
    icon: DataUser,
    type: 'data'
  },
  {
    name: '数据库警示',
    code: 'database-alert',
    icon: DatabaseAlert,
    type: 'data'
  },
  {
    name: '数据库代码',
    code: 'database-code',
    icon: DatabaseCode,
    type: 'data'
  },
  {
    name: '数据库配置',
    code: 'database-config',
    icon: DatabaseConfig,
    type: 'data'
  },
  {
    name: '数据库下载',
    code: 'database-download',
    icon: DatabaseDownload,
    type: 'data'
  },
  {
    name: '数据库进入',
    code: 'database-enter',
    icon: DatabaseEnter,
    type: 'data'
  },
  {
    name: '数据库错误',
    code: 'database-fail',
    icon: DatabaseFail,
    type: 'data'
  },
  {
    name: '数据库第一',
    code: 'database-first',
    icon: DatabaseFirst,
    type: 'data'
  },
  {
    name: '数据库禁止',
    code: 'database-forbid',
    icon: DatabaseForbid,
    type: 'data'
  },
  {
    name: '数据库锁定',
    code: 'database-lock',
    icon: DatabaseLock,
    type: 'data'
  },
  {
    name: '数据库网络',
    code: 'database-network',
    icon: DatabaseNetwork,
    type: 'data'
  },
  {
    name: '数据库网络节点',
    code: 'database-network-point',
    icon: DatabaseNetworkPoint,
    type: 'data'
  },
  {
    name: '数据库节点',
    code: 'database-point',
    icon: DatabasePoint,
    type: 'data'
  },
  {
    name: '数据库电源',
    code: 'database-power',
    icon: DatabasePower,
    type: 'data'
  },
  {
    name: '数据库搜索',
    code: 'database-search',
    icon: DatabaseSearch,
    type: 'data'
  },
  {
    name: '数据库设置',
    code: 'database-setting',
    icon: DatabaseSetting,
    type: 'data'
  },
  {
    name: '数据库时间',
    code: 'database-time',
    icon: DatabaseTime,
    type: 'data'
  },

  {
    name: '活动源',
    code: 'activity-source',
    icon: ActivitySource,
    type: 'dataChart'
  },
  {
    name: '计算',
    code: 'arithmetic',
    icon: Arithmetic,
    type: 'dataChart'
  },
  {
    name: '广播',
    code: 'broadcast-one',
    icon: BroadcastOne,
    type: 'dataChart'
  },
  {
    name: '气泡图',
    code: 'bubble-chart',
    icon: BubbleChart,
    type: 'dataChart'
  },
  {
    name: '关系图',
    code: 'chart-graph',
    icon: ChartGraph,
    type: 'dataChart'
  },
  {
    name: '条形图',
    code: 'chart-histogram',
    icon: ChartHistogram,
    type: 'dataChart'
  },
  {
    name: '折线图',
    code: 'chart-line',
    icon: ChartLine,
    type: 'dataChart'
  },
  {
    name: '饼图',
    code: 'chart-pie',
    icon: ChartPie,
    type: 'dataChart'
  },
  {
    name: '环形图',
    code: 'chart-ring',
    icon: ChartRing,
    type: 'dataChart'
  },
  {
    name: '散点图',
    code: 'chart-scatter',
    icon: ChartScatter,
    type: 'dataChart'
  },
  {
    name: 'K线图',
    code: 'chart-stock',
    icon: ChartStock,
    type: 'dataChart'
  },
  {
    name: '数据库',
    code: 'data',
    icon: Data,
    type: 'dataChart'
  },
  {
    name: '数据看板',
    code: 'data-screen',
    icon: DataScreen,
    type: 'dataChart'
  },
  {
    name: '数据表',
    code: 'data-sheet',
    icon: DataSheet,
    type: 'dataChart'
  },
  {
    name: '心电图',
    code: 'electrocardiogram',
    icon: Electrocardiogram,
    type: 'dataChart'
  },
  {
    name: '表格',
    code: 'form',
    icon: Form,
    type: 'dataChart'
  },
  {
    name: '直方图',
    code: 'histogram',
    icon: Histogram,
    type: 'dataChart'
  },
  {
    name: 'kaji图',
    code: 'Kagi-map',
    icon: KagiMap,
    type: 'dataChart'
  },
  {
    name: '马斯洛金字塔',
    code: 'maslow-pyramids',
    icon: MaslowPyramids,
    type: 'dataChart'
  },
  {
    name: '排行榜',
    code: 'ranking',
    icon: Ranking,
    type: 'dataChart'
  },

  {
    name: '空调',
    code: 'air-conditioning',
    icon: AirConditioning,
    type: 'hardware'
  },
  {
    name: '电池',
    code: 'battery-working-one',
    icon: BatteryWorkingOne,
    type: 'hardware'
  },
  {
    name: '自行车',
    code: 'bike',
    icon: Bike,
    type: 'hardware'
  },
  {
    name: '插头',
    code: 'bolt-one',
    icon: BoltOne,
    type: 'hardware'
  },
  {
    name: '收音机',
    code: 'broadcast-radio',
    icon: BroadcastRadio,
    type: 'hardware'
  },
  {
    name: '计算器',
    code: 'calculator',
    icon: Calculator,
    type: 'hardware'
  },
  {
    name: '摄像头',
    code: 'camera-five',
    icon: CameraFive,
    type: 'hardware'
  },
  {
    name: '汽车',
    code: 'car',
    icon: Car,
    type: 'hardware'
  },
  {
    name: '充电宝',
    code: 'charging-treasure',
    icon: ChargingTreasure,
    type: 'hardware'
  },
  {
    name: '芯片',
    code: 'chip',
    icon: Chip,
    type: 'hardware'
  },
  {
    name: '云储存',
    code: 'cloud-storage',
    icon: CloudStorage,
    type: 'hardware'
  },
  {
    name: '电脑',
    code: 'computer',
    icon: Computer,
    type: 'hardware'
  },
  {
    name: '设备',
    code: 'devices',
    icon: Devices,
    type: 'hardware'
  },
  {
    name: '磁盘',
    code: 'disk',
    icon: Disk,
    type: 'hardware'
  },
  {
    name: 'U盘',
    code: 'disk-one',
    icon: DiskOne,
    type: 'hardware'
  },
  {
    name: '无人机',
    code: 'drone',
    icon: Drone,
    type: 'hardware'
  },
  {
    name: '智能门锁',
    code: 'electronic-door-lock',
    icon: ElectronicDoorLock,
    type: 'hardware'
  },
  {
    name: '手电筒',
    code: 'flashlight',
    icon: Flashlight,
    type: 'hardware'
  },
  {
    name: '游戏机',
    code: 'game-console',
    icon: GameConsole,
    type: 'hardware'
  },
  {
    name: '吹风机',
    code: 'hair-dryer-one',
    icon: HairDryerOne,
    type: 'hardware'
  }
];
