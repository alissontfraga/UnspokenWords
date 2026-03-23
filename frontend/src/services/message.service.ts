import { apiFetch } from "@/lib/api"
import { Message } from "@/types/Message"

export function getMessages(): Promise<Message[]> {
  return apiFetch("/api/messages")
}

export function createMessage(data: {
  content: string
  category: string
  forPerson: string
  date?: string
}): Promise<Message> {
  return apiFetch("/api/messages", {
    method: "POST",
    body: JSON.stringify(data),
  })
}

export function deleteMessage(id: number): Promise<void> {
  return apiFetch(`/api/messages/${id}`, {
    method: "DELETE",
  })
}