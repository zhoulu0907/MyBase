/**
 * Workbench 模块统一导出
 */

import { WorkbenchBasicComp } from './WorkbenchBasicComponents';
import { WorkbenchAdvancedComp } from './WorkbenchAdvancedComponents';

export const WorkbenchComp: Record<string, any> = { ...WorkbenchBasicComp, ...WorkbenchAdvancedComp };