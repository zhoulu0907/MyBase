import React from 'react';

import type { FormInstance } from '@arco-design/web-react';
import type { NodeRenderReturnType } from '@flowgram.ai/free-layout-editor';

interface INodeRenderContext extends NodeRenderReturnType {
  configForm?: FormInstance;
  setconfigFormForm?: (form: FormInstance) => void;
}

/** 业务自定义节点上下文 */
export const NodeRenderContext = React.createContext<INodeRenderContext>({} as INodeRenderContext);
