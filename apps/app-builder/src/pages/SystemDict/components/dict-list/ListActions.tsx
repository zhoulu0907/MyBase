import { Button } from '@arco-design/web-react';

interface DictListActionsProps {
  onAdd: () => void;
  onImport: () => void;
}

export default function DictListActions({ onAdd, onImport }: DictListActionsProps) {
  return (
    <>
      <Button type="primary" style={{ width: '100%', marginBottom: 8 }} onClick={onAdd}>
        新增数据字典
      </Button>
      <Button style={{ width: '100%' }} onClick={onImport}>
        导入数据字典
      </Button>
    </>
  );
}