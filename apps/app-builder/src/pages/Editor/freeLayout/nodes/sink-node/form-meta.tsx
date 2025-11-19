import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { useNodeRenderContext } from '../../hooks';
import { getIcon } from '../../form-components/form-header/utils';
import { type FlowNodeJSON } from '../../typings';
import styles from './index.module.less';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const { node } = useNodeRenderContext();
  return <div className={styles.branchIcon}>{getIcon(node)}</div>;
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};
