import { useState, useRef, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import ChatModel from './components/ChatModel';

function App() {
  const [messages, setMessages] = useState([]);
  const [question, setQuestion] = useState('');
  const [loading, setLoading] = useState(false);
  const chatEndRef = useRef(null);

  useEffect (()=>{
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth'})
  },[messages]) // 메세지 변경시 스크롤 셋팅

  const sendMessage = async () =>{
    // 질문이 없는 경우 반환
    if(!question.trim()) return;

    // 스프레드 연산자(...) : 배열이나 객체를 펼쳐서 새로운 배열/객체에 넣을 때 사용
    const newMessages = [...messages, {sender:'user', text:question}]
    setMessages(newMessages);
    setQuestion('');
    setLoading(true)
    // 질문으로 요청해서 응답 받기
    try {
      const response = await fetch(`http://localhost:8080/api/hotel/chat?question=${encodeURIComponent(question)}`);

      // HTTP 응답의 body 가져옴
      const reader = response.body.getReader();
      // 서버에서 받은 바이트(이진 데이터)를 사람이 읽을 수 있는 문자열로 변환하는 객체
      const decoder = new TextDecoder('utf-8');
      let botMessage = '';
      
      // 반복문을 통해서 messages에 값 넣기
      while (true) {
        const {value, done} = await reader.read();
        if(done) {
          // 마지막으로 버퍼에 남은 데이터까지 정리
          botMessage += decoder.decode(); // decoder.decode(undefined, { stream: false })
          setMessages(prev => [...newMessages, {sender:'bot', text:botMessage}]); 
          break;
        }

        // stream true -> 아직 데이터가 더 올 수도 있으니까 완전히 마무리 X
        // stream false (기본값) -> 이게 진짜 마지막이니 마무리해
        const chunk = decoder.decode(value, {stream:true});
        botMessage += chunk;

        setMessages(prev => [...newMessages, {sender:'bot', text:botMessage}]);
      }
    } catch (error) {
     console.log("Error",error)
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className='chat-container'>
      <div className='chat-box'>
        {messages.map((msg, idx) =>(
          <ChatModel key={ idx } message={ msg } />
        ))}
        <div ref={ chatEndRef }></div>
      </div>

      <div className='input-container'>
        <input value={question}
                onChange={e => setQuestion(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && sendMessage()}
                placeholder='호텔 직원에게 문의해 보세요....'
        />
        <button onClick={ sendMessage } disabled={ loading }>전송</button>
      </div>
    </div>
  )
}

export default App
