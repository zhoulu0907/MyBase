import PageManagerGuide from '@/assets/images/page_manaager_guide.svg';
import { useI18n } from '@/hooks/useI18n';
import { useAppStore } from '@/store/store_app';
import { useCallback, useEffect, useState, type FC } from 'react';
import { Button, Dropdown, Form, Input, Layout, Menu, Message, Tree } from '@arco-design/web-react';
import styles from './index.module.less';
const Sider = Layout.Sider;
const Content = Layout.Content;
const SceeenPort: FC = () => {
  return (
    <div className={styles.pageManagerPage}>
      <Layout style={{ height: '100%' }}>
        <Sider className={styles.sider} style={{ width: 220 }}>
          1111
        </Sider>
        <Content className={styles.content}></Content>
      </Layout>
    </div>
  );
};

export default SceeenPort;
