import { FlowNodeEntity } from '@flowgram.ai/fixed-layout-editor';
import { v4 as uuidv4 } from 'uuid';

export const generateNodeId = (n: FlowNodeEntity) => {
  const uuid = uuidv4().replaceAll('-', '');
  return `${n.type || n.flowNodeType}_${uuid}`;
};
