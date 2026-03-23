"use client"

import { useRouter } from "next/navigation"
import MessageForm from "@/components/messages/MessageForm"

export default function NewMessagePage() {

  const router = useRouter()

  return (
    <div className="min-h-screen flex items-start justify-center bg-white py-4">

      <MessageForm
        onCreated={() => router.push("/messages")}
      />

    </div>
  )
}