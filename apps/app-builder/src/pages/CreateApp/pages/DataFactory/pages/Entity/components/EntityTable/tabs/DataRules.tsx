import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import type { TableColumnProps } from '@arco-design/web-react';
import { Button, Dropdown, Menu, Message, Space, Table, Tag } from '@arco-design/web-react';
import * as ruleService from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { CreateCustomRule, CreateOtherRule, DeleteConfirmModal } from '../../Modals';
import { VALIDATION_TYPES, validationTypeList, validationTypeMap } from '../../Modals/CreateEditRuleModal/rule';
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
  const [deleteConfirmModalVisible, setDeleteConfirmModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [ruleType, setRuleType] = useState('');
  const [editRule, setEditRule] = useState(null);
  const loadRules = async () => {
    try {
      setLoading(true);
      const params = {
        entityId: entity.id,
        pageNo: page.pageNo,
        pageSize: page.pageSize,
        applicationId: curAppId
      };
      const response = await ruleService.getEntityRules(params);
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

  const handleEdit = (record: any) => {
    console.log('handleEdit', record);
    setRuleType(record.validationType);
    setEditRule(record);

    if (record.validationType === VALIDATION_TYPES.SELF_DEFINED) {
      setCreateCustomRuleModalVisible(true);
    } else {
      setCreateOtherRuleModalVisible(true);
    }
  };

  const handleClickMenu = (value: string) => {
    setEditRule(null);
    setRuleType(value);
    if (value === VALIDATION_TYPES.SELF_DEFINED) {
      // 自定义校验
      setCreateCustomRuleModalVisible(true);
    } else {
      // 其他校验类型
      setCreateOtherRuleModalVisible(true);
    }
  };

  const handleDeleteConfirm = async () => {
    setDeleteLoading(true);

    const ruleHandlers = {
      [VALIDATION_TYPES.REQUIRED]: ruleService.deleteRequiredRule,
      [VALIDATION_TYPES.UNIQUE]: ruleService.deleteUniqueRule,
      [VALIDATION_TYPES.LENGTH]: ruleService.deleteLengthRule,
      [VALIDATION_TYPES.RANGE]: ruleService.deleteRangeRule,
      [VALIDATION_TYPES.FORMAT]: ruleService.deleteFormatRule,
      [VALIDATION_TYPES.CHILD_NOT_EMPTY]: ruleService.deleteChildNotEmptyRule
    };

    try {
      const handler = ruleHandlers[ruleType as keyof typeof ruleHandlers];
      const res = await handler(editRule?.id || '');

      setDeleteConfirmModalVisible(false);
      if (res) {
        Message.success('删除成功');
        loadRules();
      }
    } catch (error) {
      console.error('删除校验规则失败:', error);
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleDelete = (record: any) => {
    setRuleType(record.validationType);
    setEditRule(record);
    setDeleteConfirmModalVisible(true);
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
      dataIndex: 'validationItems',
      key: 'validationItems',
      render: (validationItems: string[]) => validationItems.join(',')
    },
    // 暂时隐藏
    // {
    //   title: '条件设置',
    //   dataIndex: 'validationCondition',
    //   key: 'validationCondition'
    // },
    {
      title: '验证失败提示语',
      dataIndex: 'errorMessage',
      key: 'errorMessage'
    },
    {
      title: '操作',
      key: 'operation',
      render: (col, record) => {
        return (
          <Space>
            <Button type="text" size="mini" onClick={() => handleEdit(record)}>
              编辑
            </Button>
            <Button type="text" size="mini" status="danger" onClick={() => handleDelete(record)}>
              删除
            </Button>
          </Space>
        );
      }
    }
  ];

  useEffect(() => {
    if (activeTab === 'rules') {
      loadRules();
    }
  }, [entity, activeTab, page]);

  return (
    <div className={styles.dataRules}>
      <div className={styles.header}>
        <Dropdown
          droplist={
            <Menu>
              {validationTypeList.map((item) => (
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
        editRule={editRule}
      />
      <CreateOtherRule
        visible={createOtherRuleModalVisible}
        setVisible={setCreateOtherRuleModalVisible}
        successCallback={loadRules}
        entity={entity}
        ruleType={ruleType}
        editRule={editRule}
      />
      <DeleteConfirmModal
        visible={deleteConfirmModalVisible}
        onVisibleChange={setDeleteConfirmModalVisible}
        onConfirm={handleDeleteConfirm}
        confirmLoading={deleteLoading}
        title="确认删除"
        content="确定要删除这个校验规则吗？删除后无法恢复。"
        okText="确认删除"
        cancelText="取消"
      />
    </div>
  );
};

export default DataRules;
