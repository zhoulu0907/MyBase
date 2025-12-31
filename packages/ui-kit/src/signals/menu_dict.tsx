import { DictData } from '@onebase/platform-center';
import { signal } from '@preact/signals-react';

export const createMenuDictSignal = () => {
  const appDict = signal<Map<string, DictData[]>>(new Map());
  const setAppDict = (dictTypeId: string, newAppDict: DictData[]) => {
    appDict.value.set(dictTypeId, newAppDict);
  };

  const batchSetAppDict = (dictMap: Map<string, DictData[]>) => {
    appDict.value = dictMap;
  };

  return {
    appDict,
    setAppDict,
    batchSetAppDict
  };
};

export const menuDictSignal = createMenuDictSignal();
