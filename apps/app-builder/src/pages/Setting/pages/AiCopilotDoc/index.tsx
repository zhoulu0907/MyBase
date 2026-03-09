import React, { useEffect, useRef } from 'react';
import { loadMicroApp } from 'qiankun';
import { getAiCopilotURL } from '@onebase/common';
import { generateSignature, TokenManager } from '@onebase/common';

const AiCopilotDoc: React.FC = () => {
  const containerRef = useRef(null);
  const microAppRef = useRef(null);
  const loadedRef = useRef(false);
  const tokenInfo = TokenManager.getTokenInfo();
  const tenantInfo = TokenManager.getTenantInfo();


  useEffect(() => {
    if (containerRef.current && !loadedRef.current) {
      loadedRef.current = true;
      microAppRef.current = loadMicroApp({
        name: 'ai-copilot-doc',
        entry: getAiCopilotURL(),
        container: containerRef.current,
        props: {
          defaultPage: 'aiDoc-list',
          fromMain: true,
          generateSignature: generateSignature,
          tokenInfo: tokenInfo,
          tenantInfo: tenantInfo
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
