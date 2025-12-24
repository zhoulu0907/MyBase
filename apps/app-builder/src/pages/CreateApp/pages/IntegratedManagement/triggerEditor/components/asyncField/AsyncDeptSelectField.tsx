import { Form, Select } from '@arco-design/web-react';
import { getDeptList, type DeptVO } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';

/**
 * 异步加载部门选项的 Select 组件
 */
interface AsyncDeptSelectFieldProps {
  fieldName: string;
}

const AsyncDeptSelectField: React.FC<AsyncDeptSelectFieldProps> = ({ fieldName }) => {
  const [options, setOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    const loadDepts = async () => {
      setLoading(true);
      try {
        // 获取部门列表
        const deptList = await getDeptList();

        // 转换为 Select 组件需要的格式
        const deptOptions = (deptList || []).map((dept: DeptVO) => ({
          label: dept.name || '',
          value: String(dept.id || '')
        }));

        setOptions(deptOptions);
      } catch (error) {
        console.error('Failed to load departments:', error);
        setOptions([]);
      } finally {
        setLoading(false);
      }
    };

    loadDepts();
  }, []);

  return (
    <Form.Item field={fieldName}>
      <Select placeholder="请选择部门" options={options} loading={loading} />
    </Form.Item>
  );
};

export default AsyncDeptSelectField;
