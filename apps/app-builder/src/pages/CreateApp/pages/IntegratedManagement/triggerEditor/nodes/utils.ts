/**
 * 校验 payloadForm 表单并根据校验结果设置 form 的 invalid 字段
 * @param form 外层表单对象
 * @param payloadForm 需要校验的表单对象
 * @param validateOnly 是否只做校验（可选，默认为 true）
 */
export async function validateNodeForm(form: any, payloadForm: any, validateOnly: boolean = false) {
  try {
    form.setValueIn('invalid', false);
    await payloadForm.validate({ validateOnly });
  } catch (error: any) {
    // console.warn('validateNodeForm error: ', error.errors);
    // 捕获校验错误并设置 invalid
    form.setValueIn('invalid', true);
  }
}
