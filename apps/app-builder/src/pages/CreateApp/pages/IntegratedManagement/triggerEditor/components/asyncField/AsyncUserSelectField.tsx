import { Form, Select } from '@arco-design/web-react';
import { getSimpleUserPage } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';

/**
 * 异步加载用户选项的 Select 组件
 */
interface AsyncUserSelectFieldProps {
  fieldName: string;
}

const AsyncUserSelectField: React.FC<AsyncUserSelectFieldProps> = ({ fieldName }) => {
  const [options, setOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    const loadUsers = async () => {
      setLoading(true);
      try {
        // 获取用户列表，默认获取第一页，每页100条
        const { list } = await getSimpleUserPage({
          pageNo: 1,
          pageSize: 100,
          keywords: ''
        });

        // 转换为 Select 组件需要的格式
        const userOptions = (list || []).map((user) => ({
          label: user.nickname || '',
          value: user.id || ''
        }));

        setOptions(userOptions);
      } catch (error) {
        console.error('Failed to load users:', error);
        setOptions([]);
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, []);

  return (
    <Form.Item field={fieldName}>
      <Select placeholder="请选择人员" options={options} loading={loading} />
    </Form.Item>
  );
};

export default AsyncUserSelectField;
