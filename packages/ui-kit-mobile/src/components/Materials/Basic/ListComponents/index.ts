import XShowMonitor from './ShowMonitor';
import XLoadMore from './LoadMore';
import XCarousel from './Carousel';

export const ListComp: any = {
  XShowMonitor,
  XLoadMore,
  XCarousel
};

export { XShowMonitor, XLoadMore, XCarousel };

export type ListComponentType = typeof ListComp;
