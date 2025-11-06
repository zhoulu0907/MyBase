import { TabBar } from '@arco-design/mobile-react';
import { IconUser, IconHome } from '@arco-design/mobile-react/esm/icon';
import styles from './index.module.less';
import { useState } from 'react';
import Home from './components/home';
export default function RuntimeHome() {
    const [activeIndex, setActiveIndex] = useState(0);
    const tabs = [
        {
            title: '首页',
            icon: <IconHome />,
        },
        {
            title: '我的',
            icon: <IconUser />,
        },
    ];
    
    return (
      <div className={ styles.runtimeHome }>
        {
          activeIndex === 0 ? <Home /> : <div>我的内容</div> 
        }
        <TabBar
          fixed={true}
          className={ styles.tabBar }
          activeIndex={activeIndex}
          onChange={index => {
            setActiveIndex(index);
          }}
        >
            {tabs.map((tab, index) => (
                <TabBar.Item title={tab.title} icon={tab.icon} key={index} />
            ))}
        </TabBar>
      </div>
    );
}