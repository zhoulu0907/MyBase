import { Form, Input, Tooltip } from '@arco-design/web-react';
import { IconCopy } from '@arco-design/web-react/icon';
import attributeStyles from './attributes.module.less';

const FormItem = Form.Item;

interface ComponentIdFormItemProps {
  cpID: string;
}

const ComponentIdFormItem = ({ cpID }: ComponentIdFormItemProps) => (
  <div className={attributeStyles.componentId}>
    <FormItem label="组件ID" labelCol={{ span: 5 }}>
      <Input
        readOnly
        value={cpID}
        suffix={
          <Tooltip content="复制">
            <IconCopy
              style={{ cursor: 'pointer' }}
              onClick={() => {
                if (typeof navigator !== 'undefined' && navigator.clipboard) {
                  navigator.clipboard.writeText(cpID);
                }
              }}
            />
          </Tooltip>
        }
      />
    </FormItem>
  </div>
);

export default ComponentIdFormItem;
