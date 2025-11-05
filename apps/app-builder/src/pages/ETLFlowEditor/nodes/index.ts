import { type FlowNodeRegistry } from '../typings';
import { InputNodeRegistry } from './inout/input';

export const FlowNodeRegistries: FlowNodeRegistry[] = [
  InputNodeRegistry
  // OutputNodeRegistry,
  // JoinNodeRegistry,
  // UnionNodeRegistry
];
