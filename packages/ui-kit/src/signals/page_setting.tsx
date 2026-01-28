import { signal } from '@preact/signals-react';

export const createPageSettingSignal = () => {
  const dataTitleType = signal<number>(1);
  const redirectType = signal<number>(1);
  const dataTitle = signal<string>('规则：发起人发起的页面名称 示例：小贝发起的工时填报');

  const setDataTitleType = (value: number) => {
    dataTitleType.value = value;
    if (value === 1) {
      dataTitle.value = '规则：发起人发起的页面名称 示例：小贝发起的工时填报';
    } else if (value === 2) {
      dataTitle.value = '';
    }
  };

  const setRedirectType = (value: number) => {
    redirectType.value = value;
  };

  const setDataTitle = (value: string) => {
    dataTitle.value = value;
  };

  const clear = () => {
    dataTitleType.value = 1;
    redirectType.value = 1;
    dataTitle.value = '规则：发起人发起的页面名称 示例：小贝发起的工时填报';
  };

  return {
    dataTitleType,
    redirectType,
    dataTitle,
    setDataTitleType,
    setRedirectType,
    setDataTitle,
    clear
  };
};

export const usePageSettingSignal = createPageSettingSignal();
