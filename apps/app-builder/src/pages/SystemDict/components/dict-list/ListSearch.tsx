import { Input } from '@arco-design/web-react';

interface DictListSearchProps {
  value: string;
  onChange: (value: string) => void;
}

export default function TypeListSearch({ value, onChange }: DictListSearchProps) {
  return (
    <Input.Search value={value} onChange={onChange} placeholder="搜索字典类型" style={{ width: '100%' }} allowClear />
  );
}
