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
import { DataQueryMultipleNodeRegistry } from './data/data-query-multiple';
import { DataUpdateNodeRegistry } from './data/data-update';
import { ModalNodeRegistry } from './interaction/modal';
import { NavigateNodeRegistry } from './interaction/navigate';
import { RefreshNodeRegistry } from './interaction/refresh';
import { TooltipNodeRegistry } from './interaction/tooltip';
import { DataMapperNodeRegistry } from './other/data-mapper';
import { IpaasNodeRegistry } from './other/ipaas';
import { JsonNodeRegistry } from './other/json';
import { LogNodeRegistry } from './other/log';
import { MessageNodeRegistry } from './other/message';
import { ScriptNodeRegistry } from './other/script';
import { StartApiNodeRegistry } from './start/start_api';
import { StartBpmNodeRegistry } from './start/start_bpm';
import { StartDateFieldNodeRegistry } from './start/start_date_field';
import { StartEntityNodeRegistry } from './start/start_entity';
import { StartFormNodeRegistry } from './start/start_form';
import { StartTimeNodeRegistry } from './start/start_time';

export const FlowNodeRegistries: FlowNodeRegistry[] = [
  CaseNodeRegistry,
  CaseDefaultNodeRegistry,
  EndNodeRegistry,
  StartBpmNodeRegistry,
  StartEntityNodeRegistry,
  StartApiNodeRegistry,
  StartDateFieldNodeRegistry,
  StartTimeNodeRegistry,
  StartFormNodeRegistry,
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
  DataQueryMultipleNodeRegistry,
  DataUpdateNodeRegistry,

  IpaasNodeRegistry,
  JsonNodeRegistry,
  LogNodeRegistry,
  MessageNodeRegistry,
  ScriptNodeRegistry,
  DataMapperNodeRegistry,

  ModalNodeRegistry,
  NavigateNodeRegistry,
  RefreshNodeRegistry,
  TooltipNodeRegistry
];
