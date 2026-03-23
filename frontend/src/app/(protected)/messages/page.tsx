"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { apiFetch } from "@/lib/api"
import { PenLine } from "lucide-react"

import MessageList from "@/components/messages/MessageList"
import { Button } from "@/components/ui/button"

import { Message } from "@/types/Message"

export default function MessagesPage() {

  const [messages, setMessages] = useState<Message[]>([])
  const [loading, setLoading] = useState(true)

  async function load() {
    try {
      const data = await apiFetch("/messages")
      setMessages(data || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  async function handleDelete(id: number) {
    await apiFetch(`/messages/${id}`, { method: "DELETE" })

    setMessages(messages.filter(m => m.id !== id))
  }

  if (loading) return <p>Loading...</p>

  return (
    <div className="max-w-6xl mx-auto space-y-3 pb-4">
      <div className="flex justify-center"> 
        <Button asChild size={"lg"} className="
  bg-cyan-400
  text-black
  border-2 border-black
  shadow-[2px_2px_0px_0px_rgba(0,0,0,1)]
  transition-all
  hover:bg-cyan-600
  hover:translate-x-px
  hover:translate-y-px
  hover:shadow-[1px_1px_0px_0px_rgba(0,0,0,1)]
  active:translate-x-1
  active:translate-y-1
  active:shadow-none
  active:scale-[0.98]
">
        <Link href="/messages/new" className="flex items-center text-shadow-2xs">
        <PenLine />
          New message
        </Link>
      </Button>
      </div>
      
      <div  className="
    border-2 border-black
    bg-cyan-300
    p-8
    rounded-xl
    shadow-[8px_8px_0px_0px_rgba(0,0,0,1)]
    transition-all">
        <MessageList
          messages={messages}
          onDelete={handleDelete}
        />
      </div>

    </div>
  )
}