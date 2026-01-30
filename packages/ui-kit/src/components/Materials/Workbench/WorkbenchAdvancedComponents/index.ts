

import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '../core/componentTypes';
import XCarouselWorkbench from './CarouselWorkbench';
import XDataList from './DataList';

export const WorkbenchAdvancedComp = {
  XCarouselWorkbench,
  XDataList
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

