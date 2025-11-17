import { PopupSwiper, Cell, Collapse, Radio, Input, Button } from '@arco-design/mobile-react';
import { IconArrowBack } from '@arco-design/mobile-react/esm/icon';
import { memo, useMemo, useState } from 'react';
import type { XInputDeptSelectConfig } from './schema';
import styles from './index.module.css';

interface DeptNode {
  key: string;
  title: string;
  children?: DeptNode[];
}

// 示例树形结构：一级/二级部门
const treeData: DeptNode[] = [
  {
    key: 'node1',
    title: 'Trunk',
    children: [
      {
        key: 'node2',
        title: 'Leaf'
      }
    ]
  },
  {
    key: 'node3',
    title: 'Trunk2',
    children: [
      {
        key: 'node4',
        title: 'Leaf'
      },
      {
        key: 'node5',
        title: 'Leaf'
      }
    ]
  }
];

const XDeptSelect = memo((props: XInputDeptSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, tooltip, status, verify, layout, labelColSpan = 0, runtime = true } = props;

  const [visible, setVisible] = useState(false);
  const [popupDirection] = useState<'bottom' | 'top' | 'left' | 'right'>('bottom');

  // 选中值（单选）
  const [selected, setSelected] = useState<string | undefined>();
  // 搜索关键字
  const [keyword, setKeyword] = useState('');

  const filteredTree = useMemo(() => {
    if (!keyword.trim()) return treeData;
    const kw = keyword.trim().toLowerCase();
    return treeData
      .map(group => {
        const matchGroup = group.title.toLowerCase().includes(kw);
        const children = (group.children || []).filter(c => c.title.toLowerCase().includes(kw));
        if (matchGroup) return { ...group };
        if (children.length) return { ...group, children };
        return null;
      })
      .filter(Boolean) as DeptNode[];
  }, [keyword]);

  const items = useMemo(() => {
    return filteredTree.map(group => ({
      value: group.key,
      header: (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Radio
            value={group.title}
            checked={selected === group.key}
            onChange={() => setSelected(group.key)}
          />
        </div>
      ),
      content: (
        <div>
          {(group.children || []).map(child => (
            <div key={child.key} className={styles.childNode}>
              <Radio
                value={child.title}
                checked={selected === child.key}
                onChange={() => setSelected(child.key)}
              />
            </div>
          ))}
        </div>
      )
    }));
  }, [filteredTree, selected]);

  const handleConfirm = () => {
    setVisible(false);
  }

  return (
    <div className="inputTextWrapper">
      <Cell label={label?.text}
        showArrow
        onClick={() => setVisible(true)}  // 预览或运行时
      />
      <PopupSwiper visible={visible} close={() => setVisible(false)} direction={popupDirection}>
        <div style={{ height: '100vh', width: '100vw', background: '#fff' }}>
          <div className={styles.popupHeader}>
            <IconArrowBack onClick={() => setVisible(false)} />
            <span>{label?.text}</span>
            <Button
              type="primary"
              onClick={handleConfirm}
              inline
              size="mini"
            >
              确定
            </Button>
          </div>

          <div style={{ padding: '0.24rem 0.32rem' }}>
            <Input
              className={styles.deptSearch}
              placeholder="搜索部门"
              clearable
              value={keyword}
              onChange={(e, val: any) => setKeyword(String(val))}
              onClear={() => setKeyword('')}
            />
          </div>
          <Collapse.Group items={items} />
          {/* <Cell
            className={styles.deptCell}
            icon={
              <div className={styles.deptCellLeft}>
                <Radio
                  value=""
                  // checked={true}
                  onChange={() => { }}
                />
                <IconFile />
              </div>
            }
            label="List Content"
          >
            <IconArrowIn /> 下级
          </Cell> */}
        </div>
      </PopupSwiper>
    </div>
  );
});

export default XDeptSelect;
