com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.MetadataDataMethodCoreServiceImpl.createData中应该实现的流程：


初始化上下文	生成订单新增流程载体	"读取：前端请求（rawData={orderNo:""OD20240520001"",goodsName:""手机"",orderAmount:""5999"",userPhone:""138xxxx8888""},headers={Token:""tok-xxx""}）

初始化上下文：requestId=""req-202405201001"",contextId=""ctx-888"",timestamp=""2024-05-20 10:01:00"",user={userId:""U1001"",roleIds:[""销售""],deptId:""D002""},
input = 前端 rawData,
output={}"	无（若 Token 解析失败，直接终止流程，不生成 context）
功能权限校验	判断销售是否有权新增订单	"读取：user.roleIds=[""销售""],biz.objId=""order"",biz.operType=""add""
更新：process.funcPerm={passed:true},error={}"	若用户角色为 “客服”，则 error={code:403,message:"无订单新增权限",step:"功能权限校验"}
数据标准化与补全	转换类型 + 补系统字段	"读取：input.rawData（orderAmount:""5999""）,user.userId=""U1001""/deptId=""D002"", 元数据（订单默认值：status=""待支付""）
更新数据：process.standardData={orderNo:""OD20240520001"", goodsName:""xxxphone"",orderAmount:5999,userPhone:""138xxxx8888"",creatorId:""U1001"",deptId:""D002"",status:""待支付""}"	若 orderAmount 为 "abc"，则 error={code:400,message:"字段 orderAmount 类型转换失败（期望数字）",step:"数据标准化与补全"}
初步数据校验	校验订单必填项与格式	"读取：input.rawData（orderNo/goodsName/orderAmount/userPhone）
元数据校验（订单必填字段：orderNo/goodsName/orderAmount；orderAmount 需为数字）
更新：process.dataCheck={valid:true},error={}"	若 input 缺少 goodsName，则 error={code:400,message:"缺少必填字段（goodsName）",step:"初步数据校验"}
唯一性校验校验和条件校验	防订单号重复	"读取：process.standardData.orderNo=""OD20240520001""
更新：process.autoOps.preCheck={passed:true},error={}"	若查询到重复订单号，则 error={code:400,message:"订单号已存在（OD20240520001）",step:"唯一性校验校验和条件校验"}
前置自动化工作流触发	数据二次校验、衍生字段计算、基础数据处理	"读取：
- process.standardData（orderNo:""OD20240520001"", goodsName:""手机"", orderAmount:5999, ...）
- 外部数据源（商品库：手机→分类 = 3C 数码、库存 = 50；客户标签库：138xxxx8888→等级 = 白银）
- 业务规则（服务费 3%、库存≥1 可下单）
更新：
- process.standardData 新增字段：totalAmount=5999×1.03=6178.97、goodsCategory=""3C 数码""、userLevel=""白银""、stock=50
- process.autoWorkflow.pre={passed:true, log:""计算服务费 + 关联商品分类 + 库存校验通过""}"	若库存不足（商品库查询手机库存 = 0，且规则为 "库存 = 0 禁止下单"），则 error={code:400, message:"商品【手机】库存不足（当前库存 0），无法下单", step:"前置自动化工作流触发"}
数据编号(自动编号的逻辑)
数据存储	自定义 - 写入订单到数据库	"读取：process.standardData
更新：process.storage={success:true},process.standardData.id=""OID100001"",error={}"	若数据库连接超时，则 error={code:500,message:"数据存储失败（数据库连接超时）",step:"数据存储"}
后置自动化工作流触发	自定义 - 执行多场景自定义自动化，如通知用户、同步外部系统、更新会员数据	"读取：
- process.standardData（orderNo:""OD20240520001"", userPhone:""138xxxx8888"", status:""待支付"", totalAmount:6178.97, userLevel:""白银"", userId:""U1001""）
- 外部系统配置（ERP 系统接口地址、会员积分规则：消费 1 元积 1 分）
更新：
- process.autoOps.postAction={
  smsNotify:{success:true, content:""您的订单 OD20240520001 已创建，待支付金额 6178.97 元，点击查看详情""},
  erpSync:{success:true, syncTime:""2024-05-20 10:05:30"", erpOrderId:""ERP20240520001""},
  memberPoint:{success:true, addedPoints:6178, currentPoints:35200}
}"	"不更新 error（单个动作失败仅记录日志，不阻断其他自动化）：
- 若短信失败：日志 “订单 OD20240520001 短信通知失败，原因：手机号 138xxxx8888 停机”
- 若 ERP 同步超时：日志 “订单 OD20240520001 ERP 同步超时，后续将重试”"
结果格式化	组装前端响应	"读取：process.standardData.id=""OID100001"",process.storage.success=true,error={}
更新：output={code:200,message:""订单新增成功"",data:{orderId:""OID100001"",orderNo:""OD20240520001"",status:""待支付""}}"	若存在 error（如步骤 5 重复），则 output={code:400,message:"订单新增失败：订单号已存在",data:{}}
日志记录	异步任务 - 记录流程痕迹	"读取：requestId=""req-202405201001"",user.userId=""U1001"",process 全字段，error={}
无更新"	无（若日志写入失败，仅内部告警：“req-202405201001 日志记录失败”，不影响前端响应）