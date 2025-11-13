import type { InteractionRule } from '@onebase/app';

/**
 * 初始化交互规则函数
 */
export function initInteractionRule(
  formValues: Record<string, any>,
  rules: InteractionRule[],
  pageComponentSchemas: any
) {
  console.log('formValues: ', formValues);
  console.log('rules: ', rules);
  console.log('pageComponentSchemas: ', pageComponentSchemas);
}
