import DictManager from '@/pages/Setting/pages/SystemDict/components/dict-manager-component';
import type { DictManagerConfig } from '@/pages/Setting/pages/SystemDict/components/dict-manager-component';
import styles from './index.module.less';

export default function SystemDictPage() {
  const config: DictManagerConfig = {
    ui: {
      title: '系统字典管理',
      emptyText: '暂无字典数据',
      dictSearchPlaceholder: '搜索系统字典',
      dictDataSearchPlaceholder: '搜索字典值',
      addDictButtonText: '新建字典',
      addDictDataButtonText: '添加字典值'
    }
  };

  return (
    <div className={styles.systemDictPage}>
      <DictManager config={config} />
    </div>
  );
}
