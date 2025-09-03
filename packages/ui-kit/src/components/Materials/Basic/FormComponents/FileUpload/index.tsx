import { Form, Message, Upload } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputFileUploadConfig } from './schema';

const XFileUpload = memo((props: XInputFileUploadConfig) => {
  const {
    label,
    status,
    tooltip,
    uploadSize = 10,
    uploadLimit,
    // showPreview, // todo
    // showDownload, // todo
    listType,
    required,
    layout,
    labelColSpan = 0,
    supportFileType
  } = props;

  return (
    <Form.Item
      label={label}
      layout={layout}
      tooltip={tooltip}
      rules={[{ required }]}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Upload
        limit={uploadLimit === -1 ? undefined : Number(uploadLimit)}
        accept={supportFileType}
        listType={listType}
        action="/"
        beforeUpload={async (file) => {
          const fileSizeLimit = uploadSize * 1024; // 转换为kb;
          const fileSize = file.size / 1024;

          if (fileSize > fileSizeLimit) {
            Message.warning('文件大小超出限制');
            return false;
          }
        }}
        style={{
          width: '100%'
        }}
      />
    </Form.Item>
  );
});

export default XFileUpload;
