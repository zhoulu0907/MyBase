import { Typography } from '@arco-design/web-react';
import styles from './InfoPanel.module.less';

export function InfoPanel() {
  return (
    <div className={styles.infoPanel}>
      <Typography.Title level={5} className={styles.title}>
        使用说明
      </Typography.Title>

      <div className={styles.instructions}>
        <div className={styles.instructionItem}>
          <span className={styles.bullet}>•</span>
          <span>从左侧面板选择字段名和函数，或输入函数</span>
        </div>
        <div className={styles.instructionItem}>
          <span className={styles.bullet}>•</span>
          <span>公式编辑举例：</span>
        </div>
      </div>

      <div className={styles.example}>
        <span className={styles.exampleFunction}>AVERAGE</span>
        <span className={styles.exampleText}>(</span>
        <span className={styles.exampleVariable}>语文成绩</span>
        <span className={styles.exampleText}>,</span>
        <span className={styles.exampleVariable}>数学成绩</span>
        <span className={styles.exampleText}>)</span>
      </div>

      <div className={styles.tips}>
        <Typography.Text type="secondary" className={styles.tipText}>
          提示：点击字段或函数可自动插入到公式中
        </Typography.Text>
      </div>
    </div>
  );
}
