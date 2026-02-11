import { type XStepsLayoutConfig } from './schema';

const XStepsLayoutValidate = (props: XStepsLayoutConfig): boolean => {
  if (!props.defaultValue || props.defaultValue.length === 0) {
    return false;
  }

  for (const step of props.defaultValue) {
    if (!step.title || !step.key) {
      return false;
    }
  }

  return true;
};

export default XStepsLayoutValidate;
