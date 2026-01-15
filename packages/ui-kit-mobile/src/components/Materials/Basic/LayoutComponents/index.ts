import XColumnLayout from './ColumnLayout';
import XPreviewColumnLayout from './PreviewColumnLayout';
import XCollapseLayout from './CollapseLayout';
import XPreviewCollapseLayout from './PreviewCollapseLayout';
import XTabsLayout from './TabsLayout';
import XPreviewTabsLayout from './PreviewTabsLayout';

export const LayoutComp: any = {
  XColumnLayout,
  XPreviewColumnLayout,
  XCollapseLayout,
  XPreviewCollapseLayout,
  XTabsLayout,
  XPreviewTabsLayout
};


export { XTabsLayout, XColumnLayout, XCollapseLayout, XPreviewTabsLayout, XPreviewColumnLayout };

export type FormComponentType = typeof LayoutComp;