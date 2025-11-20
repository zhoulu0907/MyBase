import { useI18n } from '@/hooks/useI18n';
import {
  Checkbox,
  ColorPicker,
  DatePicker,
  Form,
  Grid,
  Input,
  InputNumber,
  Radio,
  Select,
  Switch,
  Tabs,
  Tooltip
} from '@arco-design/web-react';
import { IconCopy } from '@arco-design/web-react/icon';
import { CONFIG_TYPES, usePageEditorSignal, getPopupContainer } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import DynamicAutoCodeConfig from './components/DynamicAutoCodeConfig';
import DynamicCarouselConfig from './components/DynamicCarouselConfig';
import DynamicCheckboxConfig from './components/DynamicCheckboxConfig';
import DynamicDataSourceConfig from './components/DynamicDataSourceConfig';
import DynamicFieldConfig from './components/DynamicFieldConfig';
import DynamicFileConfig from './components/DynamicFileConfig';
import DynamicImageConfig from './components/DynamicImageConfig';
import DynamicImageHandleConfig from './components/DynamicImageHandleConfig';
import DynamicOptionsConfig from './components/DynamicOptionsConfig';
import DynamicOptionsMutipleConfig from './components/DynamicOptionsMutipleConfig';
import DynamicRadioConfig from './components/DynamicRadioConfig';
import DynamicRelatedFormConfig from './components/DynamicRelatedFormConfig';
import DynamicTableConfig from './components/DynamicTableConfig';
import DynamicTabsConfig from './components/DynamicTabsConfig';
import DynamicDateFormatConfig from './components/DynamicDateFormatConfig';
import DynamicTimeFormatConfig from './components/DynamicTimeFormatConfig';
import DynamicSwitchFillTextConfig from './components/DynamicSwitchFillTextConfig';
import DynamicDefaultValueConfig from './components/DynamicDefaultValueConfig';
import DynamicVerifyConfig from './components/DynamicVerifyConfig';
import DynamicDateRangeConfig from './components/DynamicDateRangeConfig';
import DynamicTimeRangeConfig from './components/DynamicTimeRangeConfig';
import styles from './index.module.less';
import DynamicDeptDefaultValueConfig from './components/DynamicDeptDefaultValueConfig';
import DynamicSelectScopeConfig from './components/DynamicSelectScopeConfig';
import DynamicSubTableConfig from './components/DynamicSubTableConfig';
import { renderConfigItem } from './registry';

const Row = Grid.Row;
const Col = Grid.Col;
const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

/**
 * 属性配置面板组件
 * @param props.cpID 组件唯一ID
 */
interface ConfigsProps {
  cpID: string;
}

const securityOptions = [
  {
    label: '姓名',
    value: 'name'
  },
  {
    label: '手机号',
    value: 'phone'
  },
  {
    label: '邮箱',
    value: 'email'
  },
  {
    label: '金额',
    value: 'money'
  },
  {
    label: '身份证号',
    value: 'id'
  },
  {
    label: '住址',
    value: 'address'
  },
  {
    label: 'IP地址',
    value: 'ip'
  },
  {
    label: '车牌号',
    value: 'car_id'
  }
];

const Attributes = ({ cpID }: ConfigsProps) => {
  const { t } = useI18n();

  useSignals();
  const { curComponentID, curComponentSchema, setCurComponentSchema, setPageComponentSchemas, subTableComponents } =
    usePageEditorSignal();

  const [editData, setEditData] = useState<any>([]);
  const [configs, setConfigs] = useState<any>({});
  const [isInSubTable, setIsInSubTable] = useState<boolean>(false);

  useEffect(() => {
    if (!cpID) {
      return;
    }

    setEditData(curComponentSchema.editData);
    setConfigs(curComponentSchema.config);
  }, [cpID, curComponentSchema]);

  useEffect(() => {
    const keys = Object.keys(subTableComponents) || [];
    let inSubTable = false;
    for (let key of keys) {
      for (let item of subTableComponents[key] || []) {
        if (item.id === curComponentID) {
          inSubTable = true;
        }
      }
    }
    setIsInSubTable(inSubTable);
  }, [curComponentID]);

  const handlePropsChange = (key: string, value: any) => {
    console.log(`更新了属性: ${key} 值为: `, value);

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        [key]: value
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleConfigsChange = (config: any) => {
    console.log(`更新了属性: config值为: `, config);

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        ...config
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleMultiPropsChange = (updates: { key: string; value: string | number | boolean | any[] }[]) => {
    console.log(`更新了属性: ${updates}`);

    // 将 updates 数组中的每个 key-value 展开到 config 中
    const updatesObj = updates.reduce(
      (acc, cur) => {
        acc[cur.key] = cur.value;
        return acc;
      },
      {} as Record<string, any>
    );

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        ...updatesObj
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleLayoutChange = (key: string, value: string) => {
    console.log(`更新了布局属性: ${key} 值为: ${value}`);

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        [key]: value
      },
      layout: {
        ...curComponentSchema.layout,
        [key === 'width' ? 'w' : key === 'height' ? 'h' : key]: value
      }
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const renderEditItem = (item: any, index: number) => {
    return renderConfigItem({
      id: cpID,
      item,
      index,
      configs,
      isInSubTable,
      handlePropsChange,
      handleConfigsChange,
      handleMultiPropsChange,
      handleLayoutChange
    });
  };

  // 可根据 id 获取/设置对应组件的属性，这里暂时未实现具体逻辑
  return (
    <div className={styles.attributes}>
      {cpID && (
        <Form autoComplete="off" layout="vertical">
          {editData
            .filter((item: any) => !item.advanced)
            .map((item: any, index: number) => (
              <div key={index}>{renderEditItem(item, index)}</div>
            ))}

          <FormItem label="组件ID" labelCol={{ span: 5 }}>
            <Input
              value={cpID}
              suffix={
                <Tooltip content="复制">
                  <IconCopy style={{ cursor: 'pointer' }} />
                </Tooltip>
              }
            />
          </FormItem>
        </Form>
      )}
    </div>
  );
};

export default Attributes;
