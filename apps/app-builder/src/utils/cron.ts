/**
 * cron 校验配置项（用于统一管理 cron-validator 的校验参数）
 * 在 UI 层直接复用该配置，保证校验行为一致。
 */
export const DEFAULT_CRON_VALIDATOR_OPTIONS = {
  seconds: true,
  allowBlankDay: true,
  alias: true,
  allowSevenAsSunday: true
};
