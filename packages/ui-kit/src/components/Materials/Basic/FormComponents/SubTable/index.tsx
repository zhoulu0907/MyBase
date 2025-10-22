import { useSignals } from '@preact/signals-react/runtime';

import { Button, Layout } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import './index.css';
import { type XSubTableConfig } from './schema';

const XSubTable = (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { columns, id, runtime = true, label, layout, tooltip, labelColSpan = 100, status, verify } = props;

  useSignals();

  const handleAdd = () => {};

  return (
    <Layout className="XSubTable">
      <Button
        className="addButton"
        type="outline"
        icon={<IconPlus />}
        style={{ pointerEvents: runtime ? 'unset' : 'none', marginTop: 10 }}
        onClick={handleAdd}
      >
        新增一项
      </Button>
    </Layout>
  );
};

export default XSubTable;
