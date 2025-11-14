import { InteractionActionType, OPERATOR_MAP, type InteractionRule } from '@onebase/app';
import { Jexl } from '@pawel-up/jexl';

/**
 * 初始化交互规则函数
 */
export async function initInteractionRule(
  formValues: Record<string, any>,
  rules: InteractionRule[],
  pageComponentSchemas: any
) {
  console.log('formValues: ', formValues);
  console.log('rules: ', rules);
  console.log('pageComponentSchemas: ', pageComponentSchemas);

  // 初始化一个map，用于后续交互规则处理
  const fieldMap: Record<string, any> = {};
  Object.entries(pageComponentSchemas).forEach(([key, value]: [string, any]) => {
    const fieldId = value.config.dataField[1];
    const fieldValue = formValues[fieldId];
    fieldMap[key.replaceAll('-', '')] = fieldValue;
  });

  console.log('fieldMap: ', fieldMap);

  let cpActions: Record<string, InteractionActionType[]> = {};

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

      //   console.log('expression: ', expression);
      //   console.log('fieldMap: ', fieldMap);

      const jexl = new Jexl();
      const result = await jexl.eval(expression, fieldMap);

      console.log('result: ', result);
      if (result) {
        for (const action of rule.formAction) {
          if (cpActions[action.cpId]) {
            cpActions[action.cpId].push(action.action);
          } else {
            cpActions[action.cpId] = [action.action];
          }
        }
      }
    }
  }

  console.log(cpActions);

  let cpActionsResult: Record<string, any> = {};

  // 遍历 cpActions 对象，输出每个 cpId 及对应的动作数组
  Object.entries(cpActions).forEach(([cpId, actions]) => {
    // let hidden = pageComponentSchemas[cpId].config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN];
    // let readonly = pageComponentSchemas[cpId].config.status === STATUS_VALUES[STATUS_OPTIONS.READONLY];

    let hiddenAssigned = false; // 标记 hidden 是否已经被赋值
    let readonlyAssigned = false; // 标记 readonly 是否已经被赋值
    let allReadonlyAssigned = false; // 标记所有 readonly 是否已经被赋值
    let requiredAssigned = false; // 标记 required 是否已经被赋值

    let targetAction = {};

    for (const action of actions) {
      if (action === InteractionActionType.Hide || action === InteractionActionType.Show) {
        if (hiddenAssigned) {
          continue;
        }
        hiddenAssigned = true;
        targetAction = {
          ...targetAction,
          hidden: action === InteractionActionType.Hide
        };
      }

      if (
        action === InteractionActionType.Editable ||
        action === InteractionActionType.Readonly ||
        action === InteractionActionType.ReadonlyAll
      ) {
        if (readonlyAssigned || allReadonlyAssigned) {
          continue;
        }
        if (action === InteractionActionType.Editable || action === InteractionActionType.Readonly) {
          readonlyAssigned = true;
          targetAction = {
            ...targetAction,
            editable: action === InteractionActionType.Editable ? true : false
          };
        }
        if (action === InteractionActionType.ReadonlyAll) {
          allReadonlyAssigned = true;
        }
      }

      if (action === InteractionActionType.Required) {
        if (requiredAssigned) {
          continue;
        }
        requiredAssigned = true;
        targetAction = {
          ...targetAction,
          required: action === InteractionActionType.Required ? true : false
        };
      }
    }

    cpActionsResult[cpId] = targetAction;
  });

  console.log('cpActionsResult: ', cpActionsResult);

  return cpActionsResult;
}
