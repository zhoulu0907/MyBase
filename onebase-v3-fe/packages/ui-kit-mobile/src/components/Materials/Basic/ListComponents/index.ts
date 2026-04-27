import XShowMonitor from './ShowMonitor';
import XLoadMore from './LoadMore';
import XCarousel from './Carousel';
import DraftBox from './LoadMore/DraftBox';

export const ListComp: any = {
  XShowMonitor,
  XLoadMore,
  XCarousel,
  DraftBox
};

export { XShowMonitor, XLoadMore, XCarousel, DraftBox };

export type ListComponentType = typeof ListComp;
