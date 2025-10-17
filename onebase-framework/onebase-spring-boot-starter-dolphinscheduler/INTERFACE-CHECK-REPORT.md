# ProjectApi 接口核对报告

## 接口对比（JSON vs 实现）

### 1. POST /v2/projects - 创建项目
**JSON 定义：**
- operationId: `createProject`
- 路径: `/v2/projects`
- 方法: POST
- 请求体: `ProjectCreateRequest`
- 响应: `ProjectCreateResponse`

**当前实现：**
```java
@POST("projects")  // ✅ 路径正确（v2 在 baseUrl 中）
Call<ProjectCreateResponseDTO> createProject(@Body ProjectCreateRequestDTO body);
```
✅ **状态：正确**

---

### 2. GET /v2/projects/{code} - 查询项目
**JSON 定义：**
- operationId: `queryProjectByCode`
- 路径: `/v2/projects/{code}`
- 方法: GET
- 响应: `ProjectQueryResponse`

**当前实现：**
```java
@GET("projects/{code}")  // ✅ 路径正确
Call<ProjectQueryResponseDTO> queryProjectByCode(@Path("code") Long code);
```
✅ **状态：正确**

---

### 3. PUT /v2/projects/{code} - 更新项目
**JSON 定义：**
- operationId: `updateProject`
- 路径: `/v2/projects/{code}`
- 方法: PUT
- 响应: `ProjectUpdateResponse`

**当前实现：**
```java
@PUT("projects/{code}")  // ✅ 路径正确
Call<ProjectUpdateResponseDTO> updateProject(@Path("code") Long code, @Body ProjectUpdateRequestDTO body);
```
✅ **状态：正确**

---

### 4. DELETE /v2/projects/{code} - 删除项目
**JSON 定义：**
- operationId: `deleteProject`
- 路径: `/v2/projects/{code}`
- 方法: DELETE
- 响应: `ProjectDeleteResponse`

**当前实现：**
```java
@DELETE("projects/{code}")  // ✅ 路径正确
Call<ProjectDeleteResponseDTO> deleteProject(@Path("code") Long code);
```
✅ **状态：正确**

---

### 5. GET /v2/projects - 分页查询项目
**JSON 定义：**
- operationId: `queryProjectListPaging`
- 路径: `/v2/projects`
- 方法: GET
- 参数: searchVal, pageSize, pageNo (query)
- 响应: `ResultPageInfoProject`

**当前实现：**
```java
@GET("projects")  // ✅ 路径正确
Call<ProjectPageResponseDTO> queryProjectListPaging(
    @Query("searchVal") String searchVal,
    @Query("pageNo") Integer pageNo,
    @Query("pageSize") Integer pageSize
);
```
✅ **状态：正确**

---

### 6. GET /v2/projects/list - 查询所有项目
**JSON 定义：**
- operationId: `queryAllProjectList`
- 路径: `/v2/projects/list`
- 方法: GET
- 响应: `ProjectListResponse`

**当前实现：**
```java
@GET("projects/list")  // ✅ 路径正确
Call<ProjectListResponseDTO> queryAllProjectList();
```
✅ **状态：正确**

---

### 7. GET /v2/projects/list-dependent
**JSON 定义：**
- operationId: `queryAllProjectListForDependent`
- 路径: `/v2/projects/list-dependent`
- 方法: GET

**当前实现：**
```java
@GET("projects/list-dependent")  // ✅ 路径正确
Call<ProjectListResponseDTO> queryAllProjectListForDependent();
```
✅ **状态：正确**

---

### 8. GET /v2/projects/created-and-authed
**JSON 定义：**
- operationId: `queryProjectCreatedAndAuthorizedByUser`
- 路径: `/v2/projects/created-and-authed`

**当前实现：**
```java
@GET("projects/created-and-authed")  // ✅ 路径正确
Call<ProjectListResponseDTO> queryProjectCreatedAndAuthorizedByUser();
```
✅ **状态：正确**

---

### 9. GET /v2/projects/authed-project
**JSON 定义：**
- operationId: `queryAuthorizedProject`
- 参数: userId (query, required)

**当前实现：**
```java
@GET("projects/authed-project")  // ✅ 路径正确
Call<ProjectListResponseDTO> queryAuthorizedProject(@Query("userId") Integer userId);
```
✅ **状态：正确**

---

### 10. GET /v2/projects/unauth-project
**JSON 定义：**
- operationId: `queryUnauthorizedProject`
- 参数: userId (query, required)

**当前实现：**
```java
@GET("projects/unauth-project")  // ✅ 路径正确
Call<ProjectListResponseDTO> queryUnauthorizedProject(@Query("userId") Integer userId);
```
✅ **状态：正确**

---

### 11. GET /v2/projects/authed-user
**JSON 定义：**
- operationId: `queryAuthorizedUser`
- 参数: projectCode (query, required)
- 响应: `UserListResponse`

**当前实现：**
```java
@GET("projects/authed-user")  // ✅ 路径正确
Call<UserListResponseDTO> queryAuthorizedUser(@Query("projectCode") Long projectCode);
```
✅ **状态：正确**

---

## 总结

### ✅ 接口路径核对结果
- 所有 11 个接口路径完全正确
- HTTP 方法类型完全正确
- 方法名与 operationId 完全一致

### ✅ 响应类型核对结果
所有响应类型都已创建并正确映射：
- ProjectCreateResponseDTO ✅
- ProjectQueryResponseDTO ✅
- ProjectUpdateResponseDTO ✅
- ProjectDeleteResponseDTO ✅
- ProjectPageResponseDTO ✅
- ProjectListResponseDTO ✅
- UserListResponseDTO ✅

