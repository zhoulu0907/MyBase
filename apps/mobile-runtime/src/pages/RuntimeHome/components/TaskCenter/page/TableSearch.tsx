// import { useState, type FC } from 'react';
// import { Input, Dropdown, Menu, Button, InputTag, Select, DatePicker, Divider } from '@arco-design/web-react';
// import tbSort from '@/assets/images/task_center/tb-sort.svg'
// import tbBatch from '@/assets/images/task_center/tb-batch.svg'
// import tbFilter from '@/assets/images/task_center/tb-filter.svg'
// import { IconCheck } from '@arco-design/web-react/icon';
// import '../style/tcPage.less'

// const Option = Select.Option;
// const { RangePicker } = DatePicker;

// const TableSearch:FC<any> = ({uiConfig={hasInput: true, hasFilter: true, hasSort: true, hasBatch: true}, batchEvent}) => {
//     let [sortCheck, setSortCheck] = useState<string>('')
//     function handleSortItem(key: string) {
//         setSortCheck(key)
//     }
//     function onSelect(dateString:any, date:any) {
//         console.log('onSelect', dateString, date);
//     }

//     function onChange(dateString:any, date:any) {
//         console.log('onChange: ', dateString, date);
//     }
//     return <div className='title-rgt-tb-search'>
//         {uiConfig?.hasInput && <Input.Search allowClear placeholder='输入内容查询' style={{ width: 230, height: 32 }} />}
//         {uiConfig?.hasFilter && <Dropdown
//             position="br" 
//             droplist={
//                 <section className='tb-filter-box arco-dropdown-menu'>
//                     <div style={{padding: '16px'}}>
//                         <div className='filter-line'>
//                             <InputTag className='fisrt-input-tag'
//                                 style={{ width: 150 }}
//                                 addBefore={<IconCheck/>}
//                                 allowClear
//                                 readOnly
//                                 inputValue="流程状态"
//                             />
//                             <Select className='mid-select' placeholder='请选择' style={{ width: 150 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                             <Select className='end-select' placeholder='请选择流程状态' style={{ flex: 1 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                         </div>
//                         <div className='filter-line'>
//                             <InputTag className='fisrt-input-tag'
//                                 style={{ width: 150 }}
//                                 addBefore={<IconCheck/>}
//                                 allowClear
//                                 readOnly
//                                 inputValue="流程表单"
//                             />
//                             <Select className='mid-select' placeholder='请选择' style={{ width: 150 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                             <Select className='end-select' placeholder='请选择流程表单' style={{ flex: 1 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                         </div>
//                         <div className='filter-line'>
//                             <InputTag className='fisrt-input-tag'
//                                 style={{ width: 150 }}
//                                 addBefore={<IconCheck/>}
//                                 allowClear
//                                 readOnly
//                                 inputValue="当前节点"
//                             />
//                             <Select className='mid-select' placeholder='请选择' style={{ width: 150 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                             <Select className='end-select' placeholder='请选择当前节点' style={{ flex: 1 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                         </div>
//                         <Divider />
//                         <div className='filter-line'>
//                             <InputTag className='fisrt-input-tag'
//                                 style={{ width: 150 }}
//                                 addBefore={<IconCheck/>}
//                                 allowClear
//                                 readOnly
//                                 inputValue="发起时间"
//                             />
//                             <Select className='mid-select' placeholder='请选择' style={{ width: 150 }} allowClear>
//                                 <Option value='equal'>等于</Option>
//                                 <Option value='contain'>包含</Option>
//                                 <Option value='choiceRange'>选择范围</Option>
//                             </Select>
//                             <RangePicker 
//                                 mode='date'
//                                 onChange={onChange}
//                                 onSelect={onSelect}
//                                 style={{minWidth: 380, flex: 1}}
//                                 showTime={true}
//                             />
//                         </div>
//                     </div>
//                     <div className='filter-footer'>
//                         <Button type='primary' status='success'>筛选</Button>
//                         <Button type='text' className='clear-filter-btn'><img src={tbFilter} alt='' />清空</Button>
//                     </div>
//                 </section>
//             }>
//             <p><img src={tbFilter} alt='' />筛选</p>
//         </Dropdown>}
//         {uiConfig?.hasSort && <Dropdown
//             position="bottom"
//             droplist={
//                 <Menu onClickMenuItem={(key) => handleSortItem(key)}>
//                     <Menu.Item className={sortCheck==='newest'? 'item-actived' : 'item-no-check'} key='newest'>最新发起的<IconCheck className='svg' /></Menu.Item>
//                     <Menu.Item className={sortCheck==='earlier'? 'item-actived' : 'item-no-check'} key='earlier'>最早发起的<IconCheck className='svg' /></Menu.Item>
//                 </Menu>
//             }>
//             <p><img src={tbSort} alt='' />排序</p>
//         </Dropdown>}
//         {uiConfig?.hasBatch && <p onClick={() => {typeof batchEvent === 'function' && batchEvent(true)}}><img src={tbBatch} alt='' />批量审批</p>}
//     </div>
// }

// export default TableSearch;