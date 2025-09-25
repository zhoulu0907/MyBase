import { FlowNodeEntity } from '@flowgram.ai/fixed-layout-editor';
import { v4 as uuidv4 } from 'uuid';

export const generateNodeId = (n: FlowNodeEntity) => `${n.type || n.flowNodeType}_${uuidv4()}`;
