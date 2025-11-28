import { type FlowNodeRegistry } from '../typings';
import { JoinNodeRegistry } from './data/join';
import { SQLNodeRegistry } from './data/sql';
import { UnionNodeRegistry } from './data/union';
import { InputNodeRegistry } from './inout/input';
import { OutputNodeRegistry } from './inout/output';

export const FlowNodeRegistries: FlowNodeRegistry[] = [
  InputNodeRegistry,
  OutputNodeRegistry,
  JoinNodeRegistry,
  UnionNodeRegistry,
  SQLNodeRegistry
];
