"use client"

import { Message } from "@/types/Message"

import {
  Card,
  CardContent,
  CardHeader,
  CardFooter,
} from "@/components/ui/card"

import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Trash2 } from "lucide-react"

interface Props {
  message: Message
  onDelete: (id: number) => void
}

export default function MessageCard({ message, onDelete }: Props) {

  return (
    <Card
      className="
        mb-6 w-full break-inside-avoid
        bg-white
        border-2 border-black
        rounded-xl
        shadow-[2px_2px_0px_0px_rgba(0,0,0,1)]
        transition-all
        hover:translate-x-0.5
        hover:translate-y-0.5
        hover:shadow-[1px_1px_0px_0px_rgba(0,0,0,1)]
      "
    >

      <CardHeader className="flex flex-row items-center justify-between pb-2">

        <div>
          <p className="text-cyan-600 font-semibold text-shadow-2xs">
            For: {message.forPerson}
          </p>

          <Badge className="mt-1 bg-emerald-400 text-black border border-black text-shadow-2xs">
            {message.category}
          </Badge>
        </div>

        <Button
          variant="ghost"
          size="icon"
          onClick={() => onDelete(message.id)}
          className="
            text-red-500
            border border-transparent
            transition-all
            hover:scale-105
            hover:text-red-600
          "
        >
          <Trash2 size={18} />
        </Button>

      </CardHeader>

      <CardContent>
        <p className="text-black leading-relaxed whitespace-pre-wrap wrap-break-word text-shadow-2xs">
          {message.content}
        </p>
      </CardContent>

      {message.date && (
        <CardFooter>
          <p className="text-xs text-zinc-600 text-shadow-2xs">
            {message.date}
          </p>
        </CardFooter>
      )}

    </Card>
  )
}