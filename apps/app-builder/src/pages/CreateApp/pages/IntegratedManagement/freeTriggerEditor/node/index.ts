import type { FlowNodeRegistry } from "../typings";
import { ControlBranchNodeRegistry } from "./control_branch";
import { StartNodeRegistry } from "./start";


export const nodeRegistries: FlowNodeRegistry[] = [
    StartNodeRegistry,
    ControlBranchNodeRegistry
];