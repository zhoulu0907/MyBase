import React, { useEffect, useRef } from 'react';
import { loadMicroApp } from 'qiankun';
import { getAiCopilotURL } from '@onebase/common';

const AiWXMini: React.FC = () => {
  const containerRef = useRef(null);
  const microAppRef = useRef(null);
  const loadedRef = useRef(false);
  useEffect(() => {
    if (containerRef.current && !loadedRef.current) {
      loadedRef.current = true;
      microAppRef.current = loadMicroApp({
        name: 'ai-copilot-doc',
        entry: getAiCopilotURL(),
        container: containerRef.current,
        props: {
          defaultPage: 'wxMini-list',
          fromMain: true
        }
      });
    }
    return () => {
      if (microAppRef.current) {
        microAppRef.current.unmount();
        microAppRef.current = null;
        loadedRef.current = false;
      }
    };
  }, []);
  return (
    <div>
      <div ref={containerRef} />
    </div>
  );
};

export default AiWXMini;
