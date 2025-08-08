import {
  Card,
  Descriptions,
  Message,
  Modal,
  Space,
  Table,
  Tag,
  Typography,
  Upload,
  type TableColumnProps
} from '@arco-design/web-react';
// import { IconInfoCircle } from '@arco-design/web-react/icon';
import { getPlatFormInfoListApi, uploadPlatformLicenseApi, type LicenseInfo } from '@onebase/platform-center';

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import styles from './index.module.less';

const { Title, Text } = Typography;
// 定义认证记录的数据类型
interface CertificationRecord {
  key: string;
  companyName: string;
  certificationContent: string;
  status: string;
  expireTime: string;
}
const PlatformInfo: React.FC = () => {
  const { t } = useTranslation();
  const [visible, setVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<CertificationRecord | null>(null);
  const [licenseInfoList, setLicenseInfoList] = useState<LicenseInfo[]>([]);
  const [licenseInfo, setLicenseInfo] = useState<LicenseInfo | null>(null);
  // 模拟平台信息数据 license 获取
  const platformData = {
    name: 'ONE BASE Platform',
    // 企业编号
    enterpriseCode: 'F2000909',
    // 企业地址
    enterpriseAddress: '中国上海徐汇区',
    // 超级管理员
    creator: 'admin',
    // 创建时间
    createTime: '2023-01-01 00:00:00',
    // 平台类型
    platformType: '私有化部署',
    // 认证状态
    status: '已认证', // 已过期/已认证/已失效
    // 到期时间
    expireTime: '2023-12-31 23:59:59',
    // 系统版本
    version: '1.0.0',
    // 实际租户数量
    actualTenantCount: 5,
    // 系统获取租户数量
    tenantLimit: 10,
    description: '企业级管理平台，提供用户管理、内容管理、系统设置等功能',
    environment: 'Production',
    lastUpdate: '2024-01-15 10:30:00',
    status: '运行中',
    serverInfo: {
      os: 'Linux Ubuntu 20.04',
      nodeVersion: 'v18.17.0',
      database: 'PostgreSQL 14.0',
      redis: 'Redis 6.2.0'
    }
  };

  // 模拟认证记录数据
  const allData: CertificationRecord[] = [
    {
      key: '1',
      companyName: '${companyName}',
      certificationContent: '租户数量：${租户数量}，用户数量：${用户数量}',
      status: '已认证',
      expireTime: '2026-06-13 08:00:00'
    },
    {
      key: '2',
      companyName: '${companyName}',
      certificationContent: '租户数量：${租户数量}，用户数量：${用户数量}',
      status: '未认证',
      expireTime: '2026-06-13 08:00:00'
    }
  ];

  const getPlatformInfoList = async () => {
    console.log('获取认证记录:');
    try {
      const res = await getPlatFormInfoListApi({ pageNum: 1, pageSize: 10 });
      console.log('infoList:', res.list);
      if (res && Array.isArray(res.list)) {
        setLicenseInfoList(res.list);
        if (res.list.length > 0) {
          setLicenseInfo(res.list[0]);
        } else {
          setLicenseInfo(null);
        }
      } else {
        console.warn('Invalid response format:', res);
        setLicenseInfoList([]);
        setLicenseInfo(null);
      }
    } catch (error: any) {
      Message.error(error.message || t('auth.loginFailed'));
    }
  };

  useEffect(() => {
    getPlatformInfoList();
  }, []);

  // 认证记录table结构
  const columns: TableColumnProps[] = [
    {
      title: '公司名称',
      dataIndex: 'companyName',
      key: 'companyName'
    },
    {
      title: '认证内容',
      // 认证内容
      dataIndex: 'certificationContent',
      key: 'certificationContent'
    },
    {
      title: '当前状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => <Tag color={text === '已认证' ? 'green' : 'red'}>{text}</Tag>
    },
    {
      title: '到期时间',
      dataIndex: 'expireTime',
      key: 'expireTime'
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      render: (_, record) => (
        <div className={styles.operation}>
          <a
            href="#"
            className={styles.btn}
            onClick={(e) => {
              e.preventDefault();
              setSelectedRecord(record);
              setVisible(true);
            }}
          >
            {t('platformInfo.check')}
          </a>
        </div>
      )
    }
  ];

  // 上传认证
  const handleUploadCertification = async () => {
    // console.log('认证已经上传了');
    try {
      const resp = await uploadPlatformLicenseApi(data);
      console.log('uploadPlatformLicense-res: ', resp);
    } catch (error: any) {
      Message.error(error.message || '认证上传失败');
    }
  };
  const [data, setData] = useState(allData);
  // 分页器
  const [pagination, setPagination] = useState({
    // sizeCanChange: true,
    showTotal: true,
    total: data.length,
    pageSize: 10,
    current: 1,
    pageSizeChangeResetCurrent: true
  });
  const [loading, setLoading] = useState(false);
  function onChangeTable(
    pagination: { current?: number; pageSize?: number }
    // sorter: any,
    // filters: any,
    // extra: any
  ) {
    const { current = 1, pageSize = 10 } = pagination;
    setLoading(true);
    setTimeout(() => {
      setData(allData.slice((current - 1) * pageSize, current * pageSize));
      setPagination((pagination) => ({ ...pagination, current, pageSize }));
      setLoading(false);
    }, 1000);
  }

  return (
    <div className={styles.platformInfo}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 页面标题 */}
        <div className={styles.pageHeader}>
          <div className={styles.pageHeaderLeft}>
            <Title heading={4} className={styles.pageHeaderTitle}>
              {licenseInfo?.enterpriseName}
            </Title>
            <div className="companyId">
              <Text type="secondary">{licenseInfo?.enterpriseCode}</Text>
            </div>
            <div className="address">
              <Text type="secondary">{licenseInfo?.enterpriseAddress}</Text>
            </div>
          </div>
          <div className={styles.pageHeaderRight}>
            <div className={styles.superAdmin}>
              <Text type="secondary">
                {t('platformInfo.superAdmin')}：<span className={styles.superAdminText}>{licenseInfo?.creator}</span>
              </Text>
            </div>
            <div className={styles.createdAt}>
              <Text type="secondary">
                {t('platformInfo.createdAt')}：{licenseInfo?.createTime}
              </Text>
            </div>
          </div>
        </div>
        {/* 平台基本信息 */}
        <Card title={t('platformInfo.basicInfo')} className={styles.infoCard}>
          <Descriptions
            column={2}
            colon=":"
            data={[
              {
                label: t('platformInfo.platformType'),
                value: licenseInfo?.platformType
              },
              {
                label: t('platformInfo.authStatus'),
                value: <span className={styles.statusRunning}>{licenseInfo?.status}</span>
              },
              {
                label: t('platformInfo.expireTime'),
                value: licenseInfo?.expireTime
              },
              {
                label: t('platformInfo.version'),
                value: 'v1.0.0'
              },
              {
                label: t('platformInfo.tenantCount'),
                value: (
                  <Space>
                    <span>{licenseInfo?.actualTenantCount}</span>
                    <span> / </span>
                    <span>{licenseInfo?.tenantLimit}</span>
                  </Space>
                )
              }
            ]}
          />
        </Card>
        <div className={styles.authRecord}>
          <Text>{t('platformInfo.authRecord')}</Text>
          <span onClick={handleUploadCertification}>
            {/* {t('platformInfo.uploadAuth')} */}
            <Upload
              className={styles.uploadAuth}
              // limit={1}
              showUploadList={false}
              action="/api/upload" // 这里需要替换为实际的上传接口地址
              headers={{
                authorization: 'authorization-text'
              }}
              onChange={(file) => {
                console.log('File uploaded:', file);
                console.log('File uploaded:', file.length);
                if (file.length > 0) {
                  // 清空file
                  file.splice(0, file.length);
                  console.log('after File uploaded:', file.length);
                }
              }}
            >
              <div className={styles.uploadAuthText}>{t('platformInfo.uploadAuth')}</div>
            </Upload>
          </span>
        </div>
        <Table
          loading={loading}
          columns={columns}
          data={allData}
          pagination={{
            ...pagination,
            className: styles.tablePagination
          }}
          onChange={onChangeTable}
        />
      </Space>
      <Modal
        title={t('platformInfo.licenseDetail')}
        visible={visible}
        onCancel={() => setVisible(false)}
        footer={null}
        className={styles.licenseModal}
      >
        {selectedRecord && (
          <Descriptions
            column={1}
            data={[
              {
                label: t('platformInfo.companyName') || '公司名称',
                value: 'selectedRecord.companyName'
              },
              {
                label: t('platformInfo.certificationContent') || '认证内容',
                value: 'selectedRecord.certificationContent'
              },
              {
                label: t('platformInfo.status') || '当前状态',
                value: <Tag color={selectedRecord.status === '已认证' ? 'green' : 'red'}>{selectedRecord.status}</Tag>
              },
              {
                label: t('platformInfo.expireTime') || '到期时间',
                value: 'selectedRecord.expireTime'
              }
            ]}
            labelStyle={{ fontWeight: 'bold', width: '100px' }}
          />
        )}
      </Modal>
    </div>
  );
};

export default PlatformInfo;
