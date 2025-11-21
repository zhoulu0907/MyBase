/**
 * 工作台复用 Materials 的通用配置
 */
export {
  baseConfig as workbenchBaseConfig,
  baseDefault as workbenchBaseDefault,
  layoutConfig as workbenchLayoutConfig,
  statusConfig as workbenchStatusConfig,
  widthConfig as workbenchWidthConfig
} from '../common';

export type {
  ICommonBaseType as ICommonBaseWorkbenchType,
  TLayoutSelectKeyType as TWorkbenchLayoutSelectKeyType,
  TStatusSelectKeyType as TWorkbenchStatusSelectKeyType,
  TWidthSelectKeyType as TWorkbenchWidthSelectKeyType
} from '../common';

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
} from '../types';

export type { ILayoutConfigType as IWorkbenchLayoutConfigType } from '../types';

