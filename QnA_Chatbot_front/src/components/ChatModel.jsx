import React from 'react'

const ChatModel = ({ message }) => {
  const isUser = message.sender === 'user';
  return (
    <div className={`chat-message ${isUser ? 'user': 'bot'}`}>
        <div className='bubble'>
            {message.text}
        </div>
    </div>
  )
}

export default ChatModel
