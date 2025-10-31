import React from 'react';
import { PicklistOptionConfig } from './PicklistOptionConfig';

/**
 * 多选列表配置组件
 */
interface MultiPicklistConfigProps {
  visible?: boolean;
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: object[], dictTypeId?: string) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[];
  onCancel?: () => void;
}

export const MultiPicklistConfig: React.FC<MultiPicklistConfigProps> = (props) => {
  return <PicklistOptionConfig {...props} />;
};

/**
 * 单选列表配置组件
 */
interface PicklistConfigProps {
  visible?: boolean;
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: object[], dictTypeId?: string) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[];
  onCancel?: () => void;
}

export const PicklistConfig: React.FC<PicklistConfigProps> = (props) => {
  return <PicklistOptionConfig {...props} />;
};
