import { Form, Input, Radio } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES, UPLOAD_VALUES, UPLOAD_OPTIONS, UPLOAD_BUTTON_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: string) => void;
  item: any;
  configs: any;
}

const DynamicUploadButtonConfig = ({ handlePropsChange, item, configs }: Props) => {
  const buttonNameKey = 'buttonName';
  const buttonTypeKey = 'buttonType';

  const range = [
    {
      key: UPLOAD_BUTTON_TYPES.PRIMARY,
      text: '主要按钮',
      value: UPLOAD_BUTTON_TYPES.PRIMARY
    },
    {
      key: UPLOAD_BUTTON_TYPES.SECONDARY,
      text: '次要按钮',
      value: UPLOAD_BUTTON_TYPES.SECONDARY
    },
    {
      key: UPLOAD_BUTTON_TYPES.OUTLINE,
      text: '线框按钮',
      value: UPLOAD_BUTTON_TYPES.OUTLINE
    }
  ];

  return (
    <>
      {configs.uploadType === UPLOAD_VALUES[UPLOAD_OPTIONS.LIST] ? null : (
        <>
          <Form.Item className={styles.formItem} label="按钮名称" required>
            <Input
              placeholder={`请输入按钮名称`}
              value={configs[buttonNameKey]}
              maxLength={8}
              onChange={(value) => {
                handlePropsChange(buttonNameKey, value);
              }}
            />
          </Form.Item>
          <Form.Item className={styles.formItem} label="按钮类型">
            <Radio.Group
              type="button"
              size="default"
              value={configs[buttonTypeKey]}
              onChange={(value) => {
                handlePropsChange(buttonTypeKey, value);
              }}
              style={{ width: '100%', display: 'flex' }}
            >
              {range.map((option: any) => (
                <Radio
                  key={option.key}
                  value={option.value}
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                >
                  {option.text && option.text.startsWith('formEditor.') ? t(option.text) : option.text}
                </Radio>
              ))}
            </Radio.Group>
          </Form.Item>
        </>
      )}
    </>
  );
};

export default DynamicUploadButtonConfig;

registerConfigRenderer(CONFIG_TYPES.UPLOAD_BUTTON, ({ handlePropsChange, item, configs }) => (
  <DynamicUploadButtonConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
