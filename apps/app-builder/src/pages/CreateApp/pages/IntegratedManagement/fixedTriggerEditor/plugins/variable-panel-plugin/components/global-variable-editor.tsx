import { useEffect } from 'react';

import { BaseVariableField, GlobalScope, useRefresh, useService } from '@flowgram.ai/fixed-layout-editor';
import { JsonSchemaEditor, JsonSchemaUtils } from '@flowgram.ai/form-materials';

export function GlobalVariableEditor() {
  const globalScope = useService(GlobalScope);

  const refresh = useRefresh();

  const globalVar = globalScope.getVar() as BaseVariableField;

  useEffect(() => {
    const disposable = globalScope.output.onVariableListChange(() => {
      refresh();
    });

    return () => {
      disposable.dispose();
    };
  }, []);

  if (!globalVar) {
    return;
  }

  const value = globalVar.type ? JsonSchemaUtils.astToSchema(globalVar.type) : { type: 'object' };

  return (
    <JsonSchemaEditor
      value={value}
      onChange={(_schema) => globalVar.updateType(JsonSchemaUtils.schemaToAST(_schema))}
    />
  );
}
