import { useState, useCallback } from 'react';
import { batchGenerateCodes } from '../utils/codeGenerator';
import type { DictData } from '@onebase/platform-center';

export interface UseCodeGeneratorOptions {
  onSuccess?: (items: DictData[]) => void;
  onError?: (error: Error) => void;
}

export interface UseCodeGeneratorReturn {
  isGenerating: boolean;
  generateCodes: (items: DictData[]) => Promise<DictData[]>;
  canGenerate: (items: DictData[]) => boolean;
}

/**
 * 编码生成Hook
 */
export function useCodeGenerator(options: UseCodeGeneratorOptions = {}): UseCodeGeneratorReturn {
  const [isGenerating, setIsGenerating] = useState(false);

  // 检查是否可以生成编码
  const canGenerate = useCallback((items: DictData[]): boolean => {
    return items.some((item) => item.label && item.label.trim() && (!item.value || !item.value.trim()));
  }, []);

  // 生成编码
  const generateCodes = useCallback(
    async (items: DictData[]): Promise<DictData[]> => {
      if (!canGenerate(items)) {
        throw new Error('没有需要生成编码的项');
      }

      setIsGenerating(true);

      try {
        // 模拟异步操作
        const result = await new Promise<DictData[]>((resolve) => {
          const timeoutId = setTimeout(() => {
            const generatedItems = batchGenerateCodes(items);
            resolve(generatedItems);
          }, 1000);

          const generatedItems = batchGenerateCodes(items);
          if (generatedItems.length <= 10) {
            clearTimeout(timeoutId);
            setTimeout(() => {
              resolve(generatedItems);
            }, 1000);
          }
        });

        options.onSuccess?.(result);
        return result;
      } catch (error) {
        const errorObj = error instanceof Error ? error : new Error('生成编码失败');
        options.onError?.(errorObj);
        throw errorObj;
      } finally {
        setIsGenerating(false);
      }
    },
    [canGenerate, options]
  );

  return {
    isGenerating,
    generateCodes,
    canGenerate
  };
}
