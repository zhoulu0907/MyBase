import { InteractionActionType } from './action';

export interface InteractionRule {
  id: string;
  description: string;
  enabled: boolean;
  formAction: FormAction[];
  interactionCondition: InteractionCondition[];
}

export interface FormAction {
  action: InteractionActionType;
  cpId: string;
}

export interface InteractionCondition {
  conditions: InteractionSubCondition[];
}

export interface InteractionSubCondition {
  cpId: string;
  op: string;
  operatorType: string;
  value: string;
}
