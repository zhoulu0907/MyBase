import { SHOW_COMPONENT_TYPES } from "@onebase/ui-kit";

export interface TreeNode {
  key: string; // arco-design default tree props
  title: string; // arco-design default tree props
  id?: string;
  icon?: string;
  isVisible?: number;
  isPage?: boolean;
  children?: TreeNode[];
  [key: string]: any;
}

export interface Options {
  key?: string;
  parentKey?: string;
  children?: string;
  label?: string;
}

export const DEFAULT_OPTIONS = {
  key: 'id',
  parentKey: 'parentId',
  children: 'children',
  label: 'name'
};

/**
 * 将具有父子关系的list转换为树形结构,并添加上arco-tree中默认 tree props
 * @param list - 包含父子关系的平铺列表数据
 * @param options - 转换选项配置
 * @param options.id - 用作唯一标识的键名，默认为 'id'
 * @param options.parentId - 用作父级关联的键名，默认为 'parentId'
 * @param options.children - 用作存储子节点的键名，默认为 'children'
 * @param options.label - 用作节点显示文本的键名，默认为 'name'
 * @param isKeyTypeString arco-tree中key为string类型，此参数表示是否将key转为string类型, 默认为false，即保留原类型
 * @returns TreeNode[]
 *
 * @example
 * const list = [
 *   { id: 1, parentId: 0, name: '节点1' },
 *   { id: 2, parentId: 1, name: '节点2' }
 * ];
 * const tree = listToTree(list);
 */
export const listToTree = <T extends Record<string, any>>(
  list: T[],
  options: Options = {},
  isKeyTypeString = false
): TreeNode[] => {
  const { key, parentKey, children, label } = Object.assign({}, DEFAULT_OPTIONS, options);

  const nodeMap = new Map<any, TreeNode>();

  for (const item of list) {
    nodeMap.set(item[key], {
      ...item,
      key: isKeyTypeString ? String(item[key]) : item[key],
      title: item[label],
      children: item[children] || []
    });
  }

  const tree: TreeNode[] = [];
  for (const item of nodeMap.values()) {
    const pId = item[parentKey];

    if (nodeMap.has(pId)) {
      const parentNode = nodeMap.get(pId)!;
      parentNode.children!.push(item);
    } else {
      tree.push(item);
    }
  }

  return tree;
};

/**
 * 根据过滤函数/关键词过滤树形结构中的节点
 *
 * @param tree - 要过滤的树形结构数据
 * @param filter - 过滤条件函数或关键词，如为函数，则接受节点作为参数，返回布尔值
 * @param options - 过滤选项配置
 * @param options.children - 指定子节点对应字段，默认为 'children'
 * @param options.label - 指定显示文本对应字段，默认为 'label'
 * @returns T[] - 满足过滤条件的树状结构数据
 *
 * @example
 * const result = treeFilter(
 *   treeData,
 *   (node) => node.name.includes('关键词'),
 *   { children: 'children' }
 * );
 */
export const treeFilter = <T = any>(
  data: T[],
  filter: string | ((v: T) => boolean),
  options: Partial<Options> = {}
): T[] => {
  const isFunc = typeof filter === 'function';
  const mergedOptions = Object.assign({}, DEFAULT_OPTIONS, options);
  const { children: children, label } = mergedOptions;

  function loop(data: T[]) {
    return data
      .map((node: any) => ({ ...node }))
      .filter((node) => {
        node[children] = node[children] && loop(node[children]);
        return (
          (isFunc ? filter(node) : node[label].toLowerCase().indexOf(filter.toLowerCase()) > -1) ||
          (node[children] && node[children].length)
        );
      });
  }

  return loop(data);
};

/**
 * 将树形结构的 children 节点递归打平为一维数组
 * @param nodes 要展开的节点数组
 * @returns 返回一个包含所有子节点（多层递归打平）的数组
 */
export const flattenChildren = (nodes: TreeNode[]): TreeNode[] => {
  const result: TreeNode[] = [];

  /**
   * 深度优先遍历（DFS）
   * 递归收集所有层级的节点
   */
  const dfs = (arr: TreeNode[]) => {
    arr.forEach((node) => {
      if (node.isPage) {
        result.push(node);
      }

      if (node.children && node.children.length > 0) {
        dfs(node.children);
      }
    });
  };

  dfs(nodes);
  return result;
};

/**
 * 处理树形数据：
 * - 仅保留第一层中有 children 的节点
 * - 并将其 children 扁平化为一维数组
 */
export const splitAndFlatten = (treeData: TreeNode[]) => {
  return treeData
    .filter((node) => node.children && node.children.length > 0)
    .map(({ children, ...rest }) => ({
      ...rest,
      children: children ? flattenChildren(children) : [],
    }));
};

// 表单组件根据分割线组件进行分割
export const splitByDivider = (data: any[]) => {
  if (!data) return;
  const result = [] as any[];
  let buffer = [] as any[];

  data.forEach((item) => {
    if (item.type === SHOW_COMPONENT_TYPES.DIVIDER) {
      if (buffer.length) {
        result.push({ type: 'Normal', items: buffer });
        buffer = [];
      }
      result.push({ type: SHOW_COMPONENT_TYPES.DIVIDER, item });
    } else {
      buffer.push(item);
    }
  });

  if (buffer.length) {
    result.push({ type: 'Normal', items: buffer });
  }

  return result;
};
