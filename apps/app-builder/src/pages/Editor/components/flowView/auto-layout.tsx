import { useCallback, useEffect } from 'react';
import { usePlaygroundTools, usePlayground } from '@flowgram.ai/free-layout-editor';

export const AutoLayout = () => {
  const tools = usePlaygroundTools();
  const playground = usePlayground();
  const autoLayout = useCallback(async () => {
    await tools.autoLayout();
  }, [tools]);

  const toggleReadonly = useCallback(() => {
    playground.config.readonly = true;
  }, [playground]);
  useEffect(() => {
    autoLayout();
    toggleReadonly();
  }, []);
  return <></>;
};
