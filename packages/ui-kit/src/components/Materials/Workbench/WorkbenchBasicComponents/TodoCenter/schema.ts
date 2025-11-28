import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchStatusConfig,
  workbenchWidthConfig,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  type ICommonBaseWorkbenchType,
  type TWorkbenchStatusSelectKeyType,
  type TWorkbenchWidthSelectKeyType
} from '../../workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES
} from '../../constants';

export interface XTodoCenterSchema {
  editData: TXTodoCenterEditData;
  config: XTodoCenterConfig;
}

export type TXTodoCenterEditData = Array<
  ITextConfigType | IStatusConfigType<TWorkbenchStatusSelectKeyType> | IWidthConfigType<TWorkbenchWidthSelectKeyType>
>;

export interface XTodoCenterConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  props: Record<string, never>;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XTodoCenter: XTodoCenterSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig],
  config: {
    ...workbenchBaseDefault,
    componentName: 'TodoCenter',
    props: {},
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT]
  }
};

export default XTodoCenter;

