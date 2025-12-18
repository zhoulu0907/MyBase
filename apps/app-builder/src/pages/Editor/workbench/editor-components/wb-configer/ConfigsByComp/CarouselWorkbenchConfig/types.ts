import type { ReactNode } from 'react';

export interface CarouselItem {
  id: string; // 唯一标识符
  title?: string;
  image?: string;
  linkType?: 'internal' | 'external';
  internalPageId?: string;
  url?: string;
  text?: string;
  [key: string]: unknown;
}

export interface StaticCarouselListProps {
  carouselConfig: CarouselItem[];
  maxSizeMB?: number;
  maxCount?: number;
  onConfigChange: (config: CarouselItem[]) => void;
}

export interface CarouselFieldMeta {
  key: string;
  label: string;
  placeholder?: string;
  options?: Array<{ label?: ReactNode; value: string }>;
}

export interface CarouselContentMeta {
  modeField?: {
    key: string;
    defaultValue?: string;
    options?: Array<{ key: string; text: string; value: string }>;
  };
  dynamicFields?: CarouselFieldMeta[];
  filterField?: {
    key: string;
    label: string;
    buttonText?: string;
  };
  displayCountField?: {
    key: string;
    label: string;
    min?: number;
    max?: number;
    defaultValue?: number;
  };
  staticFieldKey?: string;
}

export interface Props {
  id: string;
  item: {
    key?: string;
    meta?: CarouselContentMeta;
  };
  configs: Record<string, unknown>;
  handlePropsChange: (key: string, value: unknown) => void;
}

export interface VerifyConfig {
  maxSize?: number;
  maxCount?: number;
}
