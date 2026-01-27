import { Button, Checkbox, Form, Message, Popconfirm, Space, Table, Tooltip,List,Card } from '@arco-design/web-react';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import { getFileUrlById } from '@onebase/platform-center';
import { useFormEditorSignal } from 'src/signals/page_editor';
import type { XCardConfig } from './schema';
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '../../../constants';
import CardSearch from './cardSerach';
import './index.css';

type XCardSelectProps = {
  showSelect: boolean;
  defaultSelectedId?: string | number | null;
  onSelectedChange?: (value: any | null, fromDoubleClick?: boolean) => void;
  refreshAfterSelect?: boolean;
  //   隐藏草稿箱
  hiddenDraft?: boolean;
};

const XCard = memo(
  (
    props: XCardConfig & {
      runtime?: boolean;
      preview?: boolean;
      showFromPageData?: Function;
      showAddBtn?: boolean;
      refresh?: number;
      xTableSelectProps?: XCardSelectProps;
      pageSetType?: number;
    }
  ) => {
    useSignals();

    const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
    const { menuPermission, canCreate, canEdit, canDelete } = menuPermissionSignal;

    const { status, runtime = true, metaData,coverField,imageFill, showAddBtn = true, searchItems, pageSetType, cardWidth } = props;
    const [form] = Form.useForm();
    const [cardForm] = Form.useForm();

     const [cardData, setCardData] = useState<any[]>([
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
      { key1:'1',key2:'2'},
    ]);

    // 新增
    const handleCreate = () => {
      console.log('点击新增');
      if (!runtime) {
        return;
      }
    };
    // 查询
    const handleSearch = () => {};

    // 重置
    const handleReset = () => {};

    const handlePage = async () => {
      if (!runtime || !metaData || !isRuntimeEnv()) {
        return;
      }
    };

    const getSpan =()=>{
      if(cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.THIRD] ){
        return 8;
      }
      if(cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.HALF] ){
        return 12;
      }
      if(cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.FULL] ){
        return 24;
      }
      return 6;
    }

    return (
      <div
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
          display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
        }}
      >
        <div className="cardHeader">
          {searchItems?.length ? (
            <div className="searchGroup">
              <Form form={form} layout="vertical" className="searchItems">
                <CardSearch
                  searchItems={searchItems}
                  labelColSpan={100}
                  runtime={runtime}
                  onSearch={handleSearch}
                  onReset={handleReset}
                  pageSetType={pageSetType}
                />
              </Form>
            </div>
          ) : null}
          <div className="headerActions">
            <div className="addButton">
              {showAddBtn && canCreate.value && (
                <Button type="primary" onClick={handleCreate} icon={<IconPlus />}>
                  添加数据
                </Button>
              )}

              {/* todo 草稿 */}
            </div>
            <Button type="text" onClick={() => handlePage()} icon={<IconRefresh />}></Button>
          </div>
        </div>
        <div className='cardContent'>
            {/* 滚动加载 */}
            <Form form={cardForm}>
              <List bordered={false} dataSource={cardData} grid={{span: getSpan(),gutter:[20,20]}} render={(item,index)=>(
                <Card className='card' bordered={false} cover={coverField? <img style={{ width: '100%', height:'128px',objectFit: imageFill || 'fill'}} src={getFileUrlById(item[coverField]?.[0]?.id)} alt=''/> :null}></Card>
              )}></List>
            </Form>
        </div>
      </div>
    );
  }
);

export default XCard;
