import {
  FORM_COMPONENT_TYPES,
  LAYOUT_COMPONENT_TYPES,
  LIST_COMPONENT_TYPES,
  SHOW_COMPONENT_TYPES
} from './componentTypes';

/**
 * 组件 type 到 displayName 的静态映射
 * 手动维护，确保与模板配置保持一致
 */
export const COMPONENT_TYPE_DISPLAY_NAME_MAP: Record<string, string> = {
  // 布局组件
  [LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT]: '分栏布局',
  [LAYOUT_COMPONENT_TYPES.TABS_LAYOUT]: '页签组件',
  [LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT]: '折叠组件',

  // 表单组件
  [FORM_COMPONENT_TYPES.INPUT_TEXT]: '单行文本',
  [FORM_COMPONENT_TYPES.INPUT_TEXTAREA]: '多行文本',
  [FORM_COMPONENT_TYPES.INPUT_EMAIL]: '邮箱',
  [FORM_COMPONENT_TYPES.INPUT_PHONE]: '电话',
  [FORM_COMPONENT_TYPES.INPUT_NUMBER]: '数字录入',
  [FORM_COMPONENT_TYPES.DATE_PICKER]: '日期',
  [FORM_COMPONENT_TYPES.DATE_RANGE_PICKER]: '日期区间',
  [FORM_COMPONENT_TYPES.DATE_TIME_PICKER]: '日期时间',
  [FORM_COMPONENT_TYPES.STATIC_TEXT]: '静态文本',
  [FORM_COMPONENT_TYPES.TIME_PICKER]: '时间',
  [FORM_COMPONENT_TYPES.SWITCH]: '开关',
  [FORM_COMPONENT_TYPES.RADIO]: '单选框',
  [FORM_COMPONENT_TYPES.CHECKBOX]: '复选框',
  [FORM_COMPONENT_TYPES.SELECT_ONE]: '下拉单选',
  [FORM_COMPONENT_TYPES.SELECT_MUTIPLE]: '下拉多选',
  [FORM_COMPONENT_TYPES.USER_SELECT]: '人员选择',
  [FORM_COMPONENT_TYPES.DEPT_SELECT]: '部门选择',
  [FORM_COMPONENT_TYPES.FILE_UPLOAD]: '文件上传',
  [FORM_COMPONENT_TYPES.IMG_UPLOAD]: '图片上传',
  [FORM_COMPONENT_TYPES.AUTO_CODE]: '自动编号',
  [FORM_COMPONENT_TYPES.RELATED_FORM]: '关联表单',
  [FORM_COMPONENT_TYPES.RICH_TEXT]: '富文本',
  [FORM_COMPONENT_TYPES.CAROUSEL_FORM]: '轮播图',
  [FORM_COMPONENT_TYPES.SUB_TABLE]: '子表单',
  [FORM_COMPONENT_TYPES.DATA_SELECT]: '选择数据',

  // 列表组件
  [LIST_COMPONENT_TYPES.TABLE]: '表格',
  [LIST_COMPONENT_TYPES.CALENDAR]: '日历',
  [LIST_COMPONENT_TYPES.TIMELINE]: '时间轴',
  [LIST_COMPONENT_TYPES.COLLAPSE]: '看板',
  [LIST_COMPONENT_TYPES.CAROUSEL]: '图片轮播',
  [LIST_COMPONENT_TYPES.LIST]: '画布列表',

  // 展示组件
  [SHOW_COMPONENT_TYPES.DIVIDER]: '分隔符',
  [SHOW_COMPONENT_TYPES.INFO_NOTICE]: '信息公告',
  [SHOW_COMPONENT_TYPES.TEXT]: '静态文本',
  [SHOW_COMPONENT_TYPES.IMAGE]: '静态图片',
  [SHOW_COMPONENT_TYPES.FILE]: '静态文件',
  [SHOW_COMPONENT_TYPES.WEB_VIEW]: '网页组件',
  [SHOW_COMPONENT_TYPES.PLACEHOLDER]: '占位符'
};

const allTemplate = {
  base: [
    // TODO(Mickey): 导航组件为空，先隐藏
    // {
    //     category: 'navigate',
    //     items: [

    //     ]
    // },
    {
      category: 'layout',
      items: [
        {
          type: LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT],
          icon: 'col_layout_cp.svg',
          category: 'base'
        },
        {
          type: LAYOUT_COMPONENT_TYPES.TABS_LAYOUT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LAYOUT_COMPONENT_TYPES.TABS_LAYOUT],
          icon: 'tabs_layout_cp.svg',
          category: 'base'
        },
        {
          type: LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT],
          icon: 'colpase_layout_cp.svg',
          category: 'base'
        }
      ]
    },
    {
      category: 'form',
      items: [
        {
          type: FORM_COMPONENT_TYPES.INPUT_TEXT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.INPUT_TEXT],
          icon: 'text_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_TEXTAREA,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.INPUT_TEXTAREA],
          icon: 'textarea_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.RICH_TEXT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.RICH_TEXT],
          icon: 'rich_text_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_EMAIL,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.INPUT_EMAIL],
          icon: 'email_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_PHONE,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.INPUT_PHONE],
          icon: 'phone_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_NUMBER,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.INPUT_NUMBER],
          icon: 'number_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DATE_PICKER,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.DATE_PICKER],
          icon: 'date_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DATE_RANGE_PICKER,
          h: 36,
          w: 118,
          displayName: '日期区间',
          icon: 'date_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DATE_TIME_PICKER,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.DATE_TIME_PICKER],
          icon: 'time_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.TIME_PICKER,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.TIME_PICKER],
          icon: 'time_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SWITCH,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.SWITCH],
          icon: 'switch_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.RADIO,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.RADIO],
          icon: 'radio_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.CHECKBOX,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.CHECKBOX],
          icon: 'checkbox_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SELECT_ONE,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.SELECT_ONE],
          icon: 'select_one_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SELECT_MUTIPLE,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.SELECT_MUTIPLE],
          icon: 'select_mutiple_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.USER_SELECT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.USER_SELECT],
          icon: 'user_select_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DEPT_SELECT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.DEPT_SELECT],
          icon: 'dept_select_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.FILE_UPLOAD,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.FILE_UPLOAD],
          icon: 'upload_file_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.IMG_UPLOAD,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.IMG_UPLOAD],
          icon: 'upload_image_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.AUTO_CODE,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.AUTO_CODE],
          icon: 'readonly_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.RELATED_FORM,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.RELATED_FORM],
          icon: 'related_form_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.CAROUSEL_FORM,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.CAROUSEL_FORM],
          icon: 'carousel_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SUB_TABLE,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.SUB_TABLE],
          icon: 'sub_table_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DATA_SELECT,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[FORM_COMPONENT_TYPES.DATA_SELECT],
          icon: 'data_select_cp.svg',
          category: 'base'
        }
      ]
    },
    {
      category: 'list',
      items: [
        {
          type: LIST_COMPONENT_TYPES.TABLE,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LIST_COMPONENT_TYPES.TABLE],
          icon: 'table_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.CALENDAR,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LIST_COMPONENT_TYPES.CALENDAR],
          icon: 'calendar_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.TIMELINE,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LIST_COMPONENT_TYPES.TIMELINE],
          icon: 'timeline_cp.svg',
          category: 'base'
        },
        // {
        //     type: LIST_COMPONENT_TYPES.TABLE,
        //     h: 48,
        //     w: 68,
        //     displayName: '目录',
        //     icon: 'directory_cp.svg',
        //     category: 'base'
        // },
        {
          type: LIST_COMPONENT_TYPES.COLLAPSE,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LIST_COMPONENT_TYPES.COLLAPSE],
          icon: 'kanban_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.CAROUSEL,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LIST_COMPONENT_TYPES.CAROUSEL],
          icon: 'carousel_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.LIST,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[LIST_COMPONENT_TYPES.LIST],
          icon: 'canvas_list_cp.svg',
          category: 'base'
        }
        // {
        //     type: LIST_COMPONENT_TYPES.TABLE,
        //     h: 48,
        //     w: 68,
        //     displayName: '列表容器',
        //     icon: 'list_container_cp.svg',
        //     category: 'base'
        // },
      ]
    },
    {
      // TODO(Mickey): 假的，配合演示使用，后续需要填坑：）
      category: 'show',
      items: [
        {
          type: SHOW_COMPONENT_TYPES.INFO_NOTICE,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.INFO_NOTICE],
          icon: 'info_notice_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.TEXT,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.TEXT],
          icon: 'static_text_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.IMAGE,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.IMAGE],
          icon: 'static_image_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.FILE,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.FILE],
          icon: 'static_file_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.WEB_VIEW,
          h: 48,
          w: 68,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.WEB_VIEW],
          icon: 'web_component_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.DIVIDER,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.DIVIDER],
          icon: 'divider_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.PLACEHOLDER,
          h: 36,
          w: 118,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[SHOW_COMPONENT_TYPES.PLACEHOLDER],
          icon: 'placeholder_cp.svg',
          category: 'base'
        }
      ]
    }
  ]
};

export { allTemplate };
