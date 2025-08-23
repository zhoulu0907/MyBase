import { Tree } from '@douyinfe/semi-ui';
import { useVariableTree } from '@flowgram.ai/form-materials';

export function FullVariableList() {
  const treeData = useVariableTree({});

  return <Tree treeData={treeData} />;
}
