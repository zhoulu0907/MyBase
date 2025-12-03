

import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '../componentTypes';
import XCarouselWorkbench from './CarouselWorkbench';

export const WorkbenchAdvancedComp = {
  XCarouselWorkbench
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

