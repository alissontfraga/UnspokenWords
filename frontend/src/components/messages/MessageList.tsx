import { Message } from "@/types/Message"
import MessageCard from "./MessageCard"

interface Props {
  messages: Message[]
  onDelete: (id: number) => void
}

export default function MessageList({ messages, onDelete }: Props) {

  if (!messages.length) {
    return <p className="shadow-2xs">No messages yet</p>
  }

  return (
    <div className="columns-1 md:columns-2 xl:columns-3 gap-6 space-y-6">

      {messages.map((message) => (
        <MessageCard
          key={message.id}
          message={message}
          onDelete={onDelete}
        />
      ))}

    </div>
  )
}