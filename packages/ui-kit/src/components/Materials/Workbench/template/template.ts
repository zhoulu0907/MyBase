import {
  WORKBENCH_COMPONENT_META,
  type WorkbenchComponentCategory,
  WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP
} from '../core/componentTypes';

const buildTemplateSection = (category: WorkbenchComponentCategory) => {
  const items = Object.values(WORKBENCH_COMPONENT_META)
    .filter((meta) => meta.category === category)
    .map((meta) => ({
      type: meta.type,
      h: meta.size.h,
      w: meta.size.w,
      displayName: meta.displayName,
      icon: meta.icon,
      category: meta.category
    }));

  return [
    {
      category,
      items
    }
  ];
};

/**
 * 工作台组件模板配置
 * 根据组件元数据定义分类和展示信息
 */
const workbenchTemplate = {
  basic: buildTemplateSection('basic'),
  advanced: buildTemplateSection('advanced')
};

export { workbenchTemplate, WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP };

