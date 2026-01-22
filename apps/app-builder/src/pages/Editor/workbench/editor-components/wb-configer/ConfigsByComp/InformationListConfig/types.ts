import type { ReactNode } from 'react';

export interface InformationListItem {
  id: string; // 唯一标识符
  image?: string;
  title?: string;
  subtitle?: string;
  author?: string;
  date?: string;
  linkType?: 'internal' | 'external';
  internalPageId?: string;
  url?: string;
  [key: string]: unknown;
}

export interface StaticInformationListProps {
  staticInformationList: InformationListItem[];
  maxSizeMB?: number;
  maxCount?: number;
  onConfigChange: (config: InformationListItem[]) => void;
}

export interface InformationListFieldMeta {
  key: string;
  label: string;
  placeholder?: string;
  options?: Array<{ label?: ReactNode; value: string }>;
}

export interface InformationListContentMeta {
  modeField?: {
    key: string;
    defaultValue?: string;
    options?: Array<{ key: string; text: string; value: string }>;
  };
  dynamicFields?: InformationListFieldMeta[];
  filterField?: {
    key: string;
    label: string;
    buttonText?: string;
  };
  staticFieldKey?: string;
}

export interface Props {
  id: string;
  item: {
    key?: string;
    meta?: InformationListContentMeta;
  };
  configs: Record<string, unknown>;
  handlePropsChange: (key: string, value: unknown) => void;
}

export interface VerifyConfig {
  maxSize?: number;
  maxCount?: number;
}
