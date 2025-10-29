interface AppProps {
  props: any;
  //   myName: string;
  //   onCountChange?: (count: number) => void;
}

const App: React.FC<AppProps> = ({ props }) => {
  return (
    <>
      <div>
        <h1>Hello World</h1>
      </div>
    </>
  );
};

export default App;
