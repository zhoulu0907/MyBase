
import { type FlowNodeRegistry } from '../typings';
import { BreakLoopNodeRegistry } from './control/break-loop';
import { CaseNodeRegistry } from './control/case';
import { CaseDefaultNodeRegistry } from './control/case-default';
import { CatchBlockNodeRegistry } from './control/catch-block';
import { EndNodeRegistry } from './control/end';
import { IFNodeRegistry } from './control/if';
import { IFBlockNodeRegistry } from './control/if-block';
import { LoopNodeRegistry } from './control/loop';
import { SwitchNodeRegistry } from './control/switch';
import { TryCatchNodeRegistry } from './control/trycatch';
import { DataAddNodeRegistry } from './data/data-add';
import { DataCalcNodeRegistry } from './data/data-calc';
import { DataDeleteNodeRegistry } from './data/data-delete';
import { DataQueryNodeRegistry } from './data/data-query';
import { DataUpdateNodeRegistry } from './data/data-update';
import { StartNodeRegistry } from './start';

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
    TryCatchNodeRegistry,
    DataAddNodeRegistry,
    DataCalcNodeRegistry,
    DataDeleteNodeRegistry,
    DataQueryNodeRegistry,
    DataUpdateNodeRegistry
];
