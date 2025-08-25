
import { type FlowNodeRegistry } from '../typings';
import { BreakLoopNodeRegistry } from './break-loop';
import { CaseNodeRegistry } from './case';
import { CaseDefaultNodeRegistry } from './case-default';
import { CatchBlockNodeRegistry } from './catch-block';
import { EndNodeRegistry } from './end';
import { IFNodeRegistry } from './if';
import { IFBlockNodeRegistry } from './if-block';
import { LoopNodeRegistry } from './loop';
import { StartNodeRegistry } from './start';
import { SwitchNodeRegistry } from './switch';
import { TryCatchNodeRegistry } from './trycatch';

export const FlowNodeRegistries: FlowNodeRegistry[] = [
    CaseNodeRegistry,
    CaseDefaultNodeRegistry,
    EndNodeRegistry,
    StartNodeRegistry,
    CatchBlockNodeRegistry,
    IFNodeRegistry,
    IFBlockNodeRegistry,
    SwitchNodeRegistry,
    LoopNodeRegistry,
    BreakLoopNodeRegistry,
    TryCatchNodeRegistry
];
