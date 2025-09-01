import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import type { TableColumnProps } from '@arco-design/web-react';
import { Button, Dropdown, Menu, Message, Space, Table, Tag } from '@arco-design/web-react';
import { getEntityRules } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { CreateCustomRule, CreateOtherRule } from '../../Modals';
import { validationTypeOptions, validationTypeMap } from '../../Modals/CreateRuleModal/rule';
import styles from './tabs.module.less';

interface DataRulesProps {
  entity: EntityListItem;
  activeTab: string;
}

const DataRules: React.FC<DataRulesProps> = ({ entity, activeTab }) => {
  const { curAppId } = useAppStore();
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState({ pageNo: 1, pageSize: 10 });
  const [total, setTotal] = useState(0);
  const [createCustomRuleModalVisible, setCreateCustomRuleModalVisible] = useState(false);
  const [createOtherRuleModalVisible, setCreateOtherRuleModalVisible] = useState(false);
  const [ruleType, setRuleType] = useState('');
  const loadRules = async () => {
    try {
      setLoading(true);
      const params = {
        entityId: entity.id,
        pageNo: page.pageNo,
        pageSize: page.pageSize,
        appId: curAppId
      };
      const response = await getEntityRules(params);
      console.log('getEntityRules', response);
      if (response?.list) {
        setRules(response.list || []);
        setTotal(response.total || 0);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
      Message.error('加载字段列表失败');
    } finally {
      setLoading(false);
    }
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      render: (text: string, record: any, index: number) => index + 1 + (page.pageNo - 1) * page.pageSize
    },
    {
      title: '校验类型',
      dataIndex: 'validationType',
      key: 'validationType',
      render: (validationType: string) => <Tag color="orange">{validationTypeMap[validationType]}</Tag>
    },
    {
      title: '规则名称',
      dataIndex: 'rgName',
      key: 'rgName'
    },
    {
      title: '校验数据项',
      dataIndex: 'fieldName',
      key: 'fieldName'
    },
    {
      title: '条件设置',
      dataIndex: 'validationCondition',
      key: 'validationCondition'
    },
    {
      title: '验证失败提示语',
      dataIndex: 'popPrompt',
      key: 'popPrompt'
    },
    // {
    //   title: '状态',
    //   dataIndex: 'status',
    //   key: 'status',
    //   render: (status: string) => (
    //     <Tag color={status === 'active' ? 'green' : 'red'}>
    //       {status === 'active' ? '启用' : '禁用'}
    //     </Tag>
    //   ),
    // },
    {
      title: '操作',
      key: 'operation',
      render: () => (
        <Space>
          <Button type="text" size="mini">
            编辑
          </Button>
          <Button type="text" size="mini" status="danger">
            删除
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    if (activeTab === 'rules') {
      loadRules();
    }
  }, [entity, activeTab, page]);

  const handleClickMenu = (value: string) => {
    setRuleType(value);
    if (value === 'custom') {
      // 自定义校验
      setCreateCustomRuleModalVisible(true);
    } else {
      // 其他校验类型
      setCreateOtherRuleModalVisible(true);
    }
  };

  return (
    <div className={styles.dataRules}>
      <div className={styles.header}>
        <Dropdown
          droplist={
            <Menu>
              {validationTypeOptions.map((item) => (
                <Menu.Item key={item.value} onClick={() => handleClickMenu(item.value)}>
                  {item.label}
                </Menu.Item>
              ))}
            </Menu>
          }
          trigger="hover"
        >
          <Button type="primary" size="small">
            添加规则
          </Button>
        </Dropdown>
      </div>
      <Table
        columns={columns}
        data={rules}
        rowKey="id"
        pagination={{
          pageSize: page.pageSize,
          current: page.pageNo,
          total: total,
          onChange: (pageNo, pageSize) => {
            setPage({ pageNo, pageSize });
          }
        }}
        loading={loading}
      />
      <CreateCustomRule
        visible={createCustomRuleModalVisible}
        setVisible={setCreateCustomRuleModalVisible}
        successCallback={loadRules}
        entity={entity}
      />
      <CreateOtherRule
        visible={createOtherRuleModalVisible}
        setVisible={setCreateOtherRuleModalVisible}
        successCallback={loadRules}
        entity={entity}
        ruleType={ruleType}
      />
    </div>
  );
};

export default DataRules;
