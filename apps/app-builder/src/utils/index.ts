import type { NodeInstance } from '@arco-design/web-react/es/Tree/interface';

export const displayCorpLogo = (logoName?: string) => {
  return logoName ? logoName.slice(0, 4) : '';
};

export const filterSpace = (value: string) => {
  return value ? value.replace(/\s+/g, '') : '';
};

export const renderDraggedTree = <T>(
  dragNode: NodeInstance | null,
  dropNode: NodeInstance | null,
  dropPosition: number,
  treeData: T[]
) => {
  let data: T[] = treeData;
  let dragItem: any;
  const loop = (data: T[], key: string, callback: any) => {
    data.some((item: any, index, arr) => {
      if (item.key === key) {
        callback(item, index, arr);
        return true;
      }
      if (item.children) {
        return loop(item.children, key, callback);
      }
    });
  };
  loop(data, dragNode?.props._key || '', (item: T, index: number, arr: T[]) => {
    arr.splice(index, 1);
    dragItem = item;
    dragItem.className = 'tree-node-dropover';
  });

  if (dropPosition === 0) {
    loop(data, dropNode?.props._key || '', (item: any) => {
      item.children = item.children || [];
      item.children.push(dragItem);
    });
  } else {
    loop(data, dropNode?.props._key || '', (item: T, index: number, arr: T[]) => {
      arr.splice(dropPosition < 0 ? index : index + 1, 0, dragItem);
    });
  }
  return { data, dragItem };
};
