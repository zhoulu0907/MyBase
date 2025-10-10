import React from 'react';
import { FormFooter } from '../form-footer';
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
    <div
      className={styles.formWrapper}
      style={isSidebar ? { height: 'calc(100% - 73px)', position: 'relative', marginBottom: '49px' } : {}}
    >
      <>
        {isSidebar && <div className={styles.formTitleDescription}>{registry.info?.description}</div>}

        <div className={styles.formContent}>{(expanded || isSidebar) && props.children}</div>
        {isSidebar && <FormFooter nodeInfo={props.children}></FormFooter>}
      </>
    </div>
  );
}
