
const PartPreviewMobile: React.FC = () => {
  return (
    <div>
      <nav>
        <Link to="/">返回主页</Link>
      </nav>

      {/* 拖拽源区域 */}
      <div style={{ marginBottom: "20px" }}>
        <h3>拖拽源区域</h3>
        <ReactSortable
          list={sourceList}
          setList={setSourceList}
          group={{
            name: "demo-drag-group",
            pull: "clone",
            put: false,
          }}
          sort={true}
          style={{
            display: "flex",
            gap: "24px",
          }}
          forceFallback={true}
          animation={150}
        >
          {sourceList.map((item) => (
            <div
              key={item.id}
              style={{
                width: 100,
                height: 100,
                background: "#90caf9",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                borderRadius: 6,
                boxShadow: "0 1px 6px rgba(0,0,0,0.1)",
                cursor: "grab",
              }}
            >
              {item.displayName}
            </div>
          ))}
        </ReactSortable>
      </div>

      <ReactSortable
        list={targetList}
        setList={setTargetList}
        group={{
          name: "demo-drag-group",
          pull: true,
          put: true,
        }}
        sort={true}
        style={{
          display: "flex",
          gap: "24px",
          minHeight: "120px",
          padding: "20px",
          border: "2px dashed #ccc",
          borderRadius: "8px",
        }}
        forceFallback={true}
        animation={150}
      >
        {targetList.map((item: any) => (
          <div
            key={item.id}
            style={{
              width: 100,
              height: 100,
              background: "#ffe082",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              borderRadius: 6,
              boxShadow: "0 1px 6px rgba(0,0,0,0.1)",
              cursor: "move",
            }}
          >
            {item.displayName}
          </div>
        ))}
      </ReactSortable>

      <div style={{ padding: "20px", margin: "20px 0" }}>
        <h2>主应用收到的子应用数字：{subAppCount}</h2>
      </div>

      <button
        onClick={() => {
          console.log("更新子应用名字");
          actions.setGlobalState({ myName: "张三" });
        }}
      >
        更新子应用名字
      </button>

      <div id="micro-app"></div>
    </div>
  );
}
export default PartPreviewMobile;
