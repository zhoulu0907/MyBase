import {
  CONFIG_TYPES,
  labelConfig,
  placeholderConfig,
  tooltipConfig,
  dataFieldConfig,
  defaultValueConfig,
  verifyConfig,
  statusConfig,
  alignConfig,
  layoutConfig,
  securityConfig,
  widthConfig,
  baseConfig,
} from '@onebase/ui-kit';

export const CONFIG_MAPPING: Record<string, any> = {
  [CONFIG_TYPES.LABEL_INPUT]: labelConfig,
  [CONFIG_TYPES.PLACEHOLDER_INPUT]: placeholderConfig,
  [CONFIG_TYPES.TOOLTIP_INPUT]: tooltipConfig,
  [CONFIG_TYPES.FIELD_DATA]: dataFieldConfig,
  [CONFIG_TYPES.DEFAULT_VALUE]: defaultValueConfig,
  [CONFIG_TYPES.VERIFY]: verifyConfig,
  [CONFIG_TYPES.STATUS_RADIO]: statusConfig,
  [CONFIG_TYPES.TEXT_ALIGN]: alignConfig,
  [CONFIG_TYPES.FORM_LAYOUT]: layoutConfig,
  [CONFIG_TYPES.SECURITY]: securityConfig,
  [CONFIG_TYPES.WIDTH_RADIO]: widthConfig,
  // Special handling for 'common' string used in templates
  'common': baseConfig,
};

export const resolveEditData = (editData: any[]) => {
  if (!Array.isArray(editData)) return [];
  
  return editData.flatMap(item => {
    if (typeof item === 'string') {
      const config = CONFIG_MAPPING[item];
      if (config) {
        // If it's an array (like baseConfig), spread it
        if (Array.isArray(config)) {
          return config;
        }
        return config;
      }
      // If no mapping found, return as is (might be handled elsewhere or invalid)
      console.warn(`[Attributes] No config mapping found for: ${item}`);
      return item;
    }
    return item;
  });
};
