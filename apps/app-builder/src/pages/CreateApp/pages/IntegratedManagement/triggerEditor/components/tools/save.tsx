import { useCallback, useEffect, useState } from 'react';

import { Badge, Button } from '@douyinfe/semi-ui';
import { FlowNodeEntity, getNodeForm, useClientContext } from '@flowgram.ai/fixed-layout-editor';

export function Save(props: { disabled: boolean }) {
  const [errorCount, setErrorCount] = useState(0);
  const clientContext = useClientContext();

  const updateValidateData = useCallback(() => {
    const allForms = clientContext.document.getAllNodes().map((node) => getNodeForm(node));
    const count = allForms.filter((form) => form?.state.invalid).length;
    setErrorCount(count);
  }, [clientContext]);

  /**
   * Validate all node and Save
   */
  const onSave = useCallback(async () => {
    const allForms = clientContext.document.getAllNodes().map((node) => getNodeForm(node));
    await Promise.all(allForms.map(async (form) => form?.validate()));
    console.log('>>>>> save data: ', clientContext.document.toJSON());
  }, [clientContext]);

  useEffect(() => {
    /**
     * Listen single node validate
     */
    const listenSingleNodeValidate = (node: FlowNodeEntity) => {
      const form = getNodeForm(node);
      if (form) {
        const formValidateDispose = form.onValidate(() => updateValidateData());
        node.onDispose(() => formValidateDispose.dispose());
      }
    };
    clientContext.document.getAllNodes().map((node) => listenSingleNodeValidate(node));
    const dispose = clientContext.document.onNodeCreate(({ node }) => listenSingleNodeValidate(node));
    return () => dispose.dispose();
  }, [clientContext]);
  if (errorCount === 0) {
    return (
      <Button disabled={props.disabled} onClick={onSave}>
        保存
      </Button>
    );
  }
  return (
    <Badge count={errorCount} position="rightTop" type="danger">
      <Button type="danger" disabled={props.disabled} onClick={onSave}>
        保存
      </Button>
    </Badge>
  );
}
