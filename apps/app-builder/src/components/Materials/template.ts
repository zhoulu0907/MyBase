import {
  FORM_COMPONENT_TYPES,
  LAYOUT_COMPONENT_TYPES,
  LIST_COMPONENT_TYPES,
  SHOW_COMPONENT_TYPES
} from '@/constants/componentTypes';

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
          h: 48,
          w: 68,
          displayName: '分栏布局',
          icon: 'col_layout_cp.svg',
          category: 'base'
        }
      ]
    },
    {
      category: 'form',
      items: [
        {
          type: FORM_COMPONENT_TYPES.INPUT_TEXT,
          h: 48,
          w: 68,
          displayName: '单行文本',
          icon: 'text_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_TEXTAREA,
          h: 48,
          w: 68,
          displayName: '多行文本',
          icon: 'textarea_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_EMAIL,
          h: 48,
          w: 68,
          displayName: '邮箱输入',
          icon: 'email_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_PHONE,
          h: 48,
          w: 68,
          displayName: '手机号输入',
          icon: 'phone_input_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.INPUT_NUMBER,
          h: 48,
          w: 68,
          displayName: '数字输入',
          icon: 'number_input_cp.svg',
          category: 'base'
        },
        // {
        //     type: FORM_COMPONENT_TYPES.DATE_RANGE_PICKER,
        //     h: 48,
        //     w: 68,
        //     displayName: '时间段',
        //     icon: 'date_picker_cp.svg',
        //     category: 'base'
        // },
        {
          type: FORM_COMPONENT_TYPES.DATE_PICKER,
          h: 48,
          w: 68,
          displayName: '日期选择',
          icon: 'date_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DATE_TIME_PICKER,
          h: 48,
          w: 68,
          displayName: '日期时间',
          icon: 'time_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.TIME_PICKER,
          h: 48,
          w: 68,
          displayName: '时间选择',
          icon: 'time_picker_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SWITCH,
          h: 48,
          w: 68,
          displayName: '开关',
          icon: 'switch_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.RADIO,
          h: 48,
          w: 68,
          displayName: '单选框',
          icon: 'radio_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.CHECKBOX,
          h: 48,
          w: 68,
          displayName: '复选框',
          icon: 'checkbox_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SELECT_ONE,
          h: 48,
          w: 68,
          displayName: '下拉单选',
          icon: 'select_one_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.SELECT_MUTIPLE,
          h: 48,
          w: 68,
          displayName: '下拉多选',
          icon: 'select_mutiple_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.USER_SELECT,
          h: 48,
          w: 68,
          displayName: '人员选择',
          icon: 'user_select_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.DEPT_SELECT,
          h: 48,
          w: 68,
          displayName: '部门选择',
          icon: 'dept_select_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.FILE_UPLOAD,
          h: 48,
          w: 68,
          displayName: '文件上传',
          icon: 'upload_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.IMG_UPLOAD,
          h: 48,
          w: 68,
          displayName: '图片上传',
          icon: 'upload_cp.svg',
          category: 'base'
        },
        {
          type: FORM_COMPONENT_TYPES.AUTO_CODE,
          h: 48,
          w: 68,
          displayName: '唯一编码',
          icon: 'readonly_cp.svg',
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
          displayName: '表格',
          icon: 'table_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.CALENDAR,
          h: 48,
          w: 68,
          displayName: '日历',
          icon: 'calendar_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.TIMELINE,
          h: 48,
          w: 68,
          displayName: '时间轴',
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
          displayName: '看板',
          icon: 'kanban_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.CAROUSEL,
          h: 48,
          w: 68,
          displayName: '图片轮播',
          icon: 'display_image_cp.svg',
          category: 'base'
        },
        {
          type: LIST_COMPONENT_TYPES.LIST,
          h: 48,
          w: 68,
          displayName: '画布列表',
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
          displayName: '信息公告',
          icon: 'info_notice_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.TEXT,
          h: 48,
          w: 68,
          displayName: '展示文本',
          icon: 'display_text_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.IMAGE,
          h: 48,
          w: 68,
          displayName: '展示图片',
          icon: 'display_image_cp.svg',
          category: 'base'
        },
        {
          type: SHOW_COMPONENT_TYPES.WEB_VIEW,
          h: 48,
          w: 68,
          displayName: '网页组件',
          icon: 'web_component_cp.svg',
          category: 'base'
        }
      ]
    }
  ]
};

/**
 * 获取所有组件的 type 类型数组
 * @returns {string[]} 组件 type 数组
 */
export function getAllComponentTypes(): string[] {
  const types: string[] = [];
  if (allTemplate && Array.isArray(allTemplate.base)) {
    allTemplate.base.forEach((category) => {
      if (Array.isArray(category.items)) {
        category.items.forEach((item) => {
          if (item && typeof item.type === 'string') {
            types.push(item.type);
          }
        });
      }
    });
  }
  return types;
}

export default allTemplate;
