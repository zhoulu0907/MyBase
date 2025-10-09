import React from 'react';

import { FlowNodeRegistry } from '@flowgram.ai/fixed-layout-editor';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import styles from './index.module.less';

/**
 * @param props
 * @constructor
 */
export function FormContent(props: { children?: React.ReactNode }) {
  const { node, expanded } = useNodeRenderContext();
  const isSidebar = useIsSidebar();
  const registry = node.getNodeRegistry<FlowNodeRegistry>();

  return (
    <div className={styles.formWrapper} >
      <>
        {isSidebar && <div className={styles.formTitleDescription}>{registry.info?.description}</div>}
        
        <div className={styles.formContent}>{(expanded || isSidebar) && props.children}</div>
      </>
    </div>
  );
}
