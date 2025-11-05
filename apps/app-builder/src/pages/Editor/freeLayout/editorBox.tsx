import { Editor } from './editor';
import { useRef, useEffect, useState, useContext } from 'react';

import { GlobalConfigProvider } from './components/globalConfig/components/globalConfigProvider';
export const FreeLayout = () => {

  return (
    <div>
      <GlobalConfigProvider>
        <Editor />
      </GlobalConfigProvider>
    </div>
  );
};
