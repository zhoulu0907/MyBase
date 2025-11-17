import { InteractionActionType, OPERATOR_MAP, type FormAction, type InteractionRule } from '@onebase/app';
import { STATUS_OPTIONS, STATUS_VALUES } from '@onebase/ui-kit';
import { Jexl } from '@pawel-up/jexl';

/**
 * 初始化交互规则函数
 */
export async function initInteractionRule(
  formValues: Record<string, any>,
  rules: InteractionRule[],
  pageComponentSchemas: any
) {
  //   console.log('formValues: ', formValues);
  //   console.log('rules: ', rules);
  //   console.log('pageComponentSchemas: ', pageComponentSchemas);

  // 初始化一个map，用于后续交互规则处理
  const fieldMap: Record<string, any> = {};
  Object.entries(pageComponentSchemas).forEach(([key, value]: [string, any]) => {
    if (value.config.dataField?.length > 1) {
      const fieldId = value.config.dataField[1];
      const fieldValue = formValues[fieldId];
      fieldMap[key.replaceAll('-', '')] = fieldValue;
    }
  });

  //   console.log('fieldMap: ', fieldMap);

  let cpActions: Record<string, FormAction[]> = {};

  if (Array.isArray(rules)) {
    // 遍历每一条规则
    for (const rule of rules) {
      let expression = '';
      rule.interactionCondition.map((item, index) => {
        let exp1 = '';

        item.conditions.map((condition, cIndex) => {
          const { cpId, op, value } = condition;
          const newCpId = cpId.replaceAll('-', '');

          if (cIndex == item.conditions.length - 1) {
            exp1 += `${newCpId} ${OPERATOR_MAP[op]} '${value}'`;
          } else {
            exp1 += `${newCpId} ${OPERATOR_MAP[op]} '${value}' && `;
          }
        });

        if (index == rule.interactionCondition.length - 1) {
          expression += `(${exp1})`;
        } else {
          expression += `(${exp1}) || `;
        }
      });

      const jexl = new Jexl();
      const result = await jexl.eval(expression, fieldMap);

      //   console.log('jexl eval result: ', result);

      if (result) {
        for (const action of rule.formAction) {
          if (action.cpIds) {
            for (const cpId of action.cpIds) {
              if (cpActions[cpId]) {
                cpActions[cpId].push(action);
              } else {
                cpActions[cpId] = [action];
              }
            }
          }
          if (action.cpId && action.action === InteractionActionType.SetFieldValue) {
            if (cpActions[action.cpId]) {
              cpActions[action.cpId].push(action);
            } else {
              cpActions[action.cpId] = [action];
            }
          }
        }
      }
    }
  }

  //   console.log(cpActions);

  let cpActionsResult: Record<string, any> = {};

  // 遍历 cpActions 对象，输出每个 cpId 及对应的动作数组
  Object.entries(cpActions).forEach(([cpId, actions]) => {
    let hiddenAssigned = false;
    let readonlyAssigned = false;
    let requiredAssigned = false;
    let setFieldValueAssigned = false;

    let targetAction: Record<string, any> = {};

    for (const action of actions) {
      const actionType = action.action;
      if (actionType === InteractionActionType.Hide || actionType === InteractionActionType.Show) {
        if (hiddenAssigned) {
          continue;
        }
        hiddenAssigned = true;
        targetAction = {
          ...targetAction,
          status:
            actionType === InteractionActionType.Hide
              ? STATUS_VALUES[STATUS_OPTIONS.HIDDEN]
              : STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
        };
      }

      if (actionType === InteractionActionType.Editable || actionType === InteractionActionType.Readonly) {
        if (readonlyAssigned) {
          continue;
        }
        readonlyAssigned = true;
        if (targetAction.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]) {
          continue;
        }

        targetAction = {
          ...targetAction,
          status:
            actionType === InteractionActionType.Editable
              ? STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
              : STATUS_VALUES[STATUS_OPTIONS.READONLY]
        };
      }

      if (actionType === InteractionActionType.Required || actionType === InteractionActionType.NoRequired) {
        if (requiredAssigned) {
          continue;
        }
        requiredAssigned = true;
        targetAction = {
          ...targetAction,
          required: actionType === InteractionActionType.Required ? true : false
        };
      }

      if (actionType === InteractionActionType.SetFieldValue) {
        if (setFieldValueAssigned) {
          continue;
        }
        setFieldValueAssigned = true;
        targetAction = {
          ...targetAction,
          value: action.value || ''
        };
      }
    }

    cpActionsResult[cpId] = targetAction;
  });

  //   console.log('cpActionsResult: ', cpActionsResult);

  return cpActionsResult;
}
