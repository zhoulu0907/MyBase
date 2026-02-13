import React, { useEffect, useRef } from 'react';
import { loadMicroApp } from 'qiankun';
import { getAiCopilotURL } from '@onebase/common';

const AiCopilotDoc: React.FC = () => {
  const containerRef = useRef(null);
  const microAppRef = useRef(null);
  const loadedRef = useRef(false);
  useEffect(() => {
    const entryAdress = 'http://s25029301301.dev.internal.virtueit.net/v1/aicopilot/';
    if (containerRef.current && !loadedRef.current) {
      loadedRef.current = true;
      microAppRef.current = loadMicroApp({
        name: 'ai-copilot-doc',
        entry: entryAdress||getAiCopilotURL(),
        container: containerRef.current,
        props: {
          defaultPage: 'aiDoc-list',
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

export default AiCopilotDoc;
