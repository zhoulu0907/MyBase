// import React from 'react';
// import { Form, Input, Modal, Button, type FormInstance } from '@arco-design/web-react';
// // import styles from './index.module.less';

// /**
//  * RenameModal 组件
//  * 用于页面管理器中重命名弹窗的占位组件
//  * 实际弹窗逻辑在 PageManagerPage 中实现
//  */
// interface RenameModalProps {
//   visible: boolean;
//   setVisible: (visible: boolean) => void;
//   handleRename: () => void;
//   form: FormInstance;
// }

// const RenameModal: React.FC<RenameModalProps> = ({ visible, handleRename, setVisible, form }) => {

//   return (
//     <Modal
//       title='重命名'
//       visible={visible}
//       onOk={handleRename}
//       onCancel={() => {
//         setVisible(false);
//       }}
//       autoFocus={false}
//       focusLock={true}
//       unmountOnExit={true}
//       footer={
//         <div style={{ textAlign: 'right'}}>
//           <Button type="default" onClick={() => setVisible(false)} style={{ marginRight: 12 }}>
//             取消
//           </Button>
//           <Button type="primary" onClick={handleRename}>
//             更新
//           </Button>
//         </div>
//       }
//     >
//       <div>
//         <Form
//           layout="vertical"
//           form={form}
//           initialValues={{
//             menuID: form.getFieldValue('menuId'),
//             menuName: form.getFieldValue('menuName')
//           }}
//         >
//           <Form.Item label="页面名称" field="menuName" rules={[{ required: true, message: '请输入页面名称' }]}>
//             <Input placeholder="请输入页面名称" allowClear />
//           </Form.Item>
//         </Form>
//       </div>
//     </Modal>
//   );
// };

// export default RenameModal;
