import XImage from './Image';
import XFile from './File';
import XInfoNotice from './InfoNotice';
import XText from './Text';
import XWebView from './WebView';
import XDivider from './Divider';
import XPlaceholder from './Placeholder';
import XCarouselForm from './Carousel';

export const ShowComp: any = {
  XImage,
  XFile,
  XText,
  XInfoNotice,
  XWebView,
  XDivider,
  XPlaceholder,
  XCarouselForm
};

export { XImage, XFile, XText, XInfoNotice, XWebView, XDivider, XPlaceholder, XCarouselForm };

export type FormComponentType = typeof ShowComp;
