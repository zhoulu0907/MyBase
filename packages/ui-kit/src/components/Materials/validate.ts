import { type ComponentType } from './componentTypes';
import { getComponentDescriptor } from './registry';
import { cloneDeep } from 'lodash-es';
import { baseValidate as BaseValidate } from './Basic/validate';

export type ComponentValidate = typeof BaseValidate | any; // 可以根据需要扩展其他组件的类型

/**
 * 根据组件类型获取对应的校验
 * @param componentType 组件类型，如 ALL_COMPONENT_TYPES.INPUT_TEXT
 * @returns 返回该组件的配置对象，包含 editData 和 config
 */
export function getComponentValidate(componentType: ComponentType, props: any): ComponentValidate {
    const descriptor = getComponentDescriptor(componentType);
    if (descriptor?.validate) {
        return descriptor.validate(props);
    }
    return true;
}