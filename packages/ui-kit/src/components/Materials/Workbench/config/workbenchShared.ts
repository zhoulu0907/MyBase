/**
 * 工作台复用 Materials 的通用配置
 * 从 shared/common 导出，保持向后兼容
 */
export {
  baseConfig as workbenchBaseConfig,
  baseDefault as workbenchBaseDefault,
  layoutConfig as workbenchLayoutConfig,
  statusConfig as workbenchStatusConfig,
  widthConfig as workbenchWidthConfig,
  dataFieldConfig as workbenchDataFieldConfig,
  defaultValueConfig as workbenchDefaultValueConfig
} from '../../common';

export type {
  ICommonBaseType as ICommonBaseWorkbenchType,
  TLayoutSelectKeyType as TWorkbenchLayoutSelectKeyType,
  TStatusSelectKeyType as TWorkbenchStatusSelectKeyType,
  TWidthSelectKeyType as TWorkbenchWidthSelectKeyType
} from '../../common';

export type {
  IBooleanConfigType,
  ILabelConfigType,
  IStatusConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType
} from '../core/types';

export type { ILayoutConfigType as IWorkbenchLayoutConfigType } from '../core/types';

