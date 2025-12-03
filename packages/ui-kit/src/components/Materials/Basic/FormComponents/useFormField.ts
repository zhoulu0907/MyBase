import { Form } from '@arco-design/web-react'

export const useFormField = (dataField: string[], id: string, type: string) => {
  const { form } = Form.useFormContext()
  const fieldName = dataField.length > 0 ? dataField[dataField.length - 1] : `${type}_${id}`
  const fieldValue = Form.useWatch(fieldName, form)
  return { form, fieldName, fieldValue }
}

export const useFormFieldWatch = (fieldId: string) => {
    const { form } = Form.useFormContext();
    const fieldValue = Form.useWatch(fieldId, form);
  return { form, fieldValue }
}