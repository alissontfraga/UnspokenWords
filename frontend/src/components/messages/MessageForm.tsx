"use client"

import { useState } from "react"
import { createMessage } from "@/services/message.service"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { cn } from "@/lib/utils"

interface Props {
  onCreated?: () => void
}

export default function MessageForm({ onCreated }: Props) {
  const categories = [
    "Thanks", "Sorry", "Advice", "Love", "Confession", 
    "Compliment", "Motivation", "Hurt", "Other"
  ]

  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({
    content: "",
    category: "",
    forPerson: "",
    date: ""
  })

  function update(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  async function handleSubmit(e: React.SyntheticEvent<HTMLFormElement>) {
    e.preventDefault()

    if (!form.content.trim() || !form.category.trim() || !form.forPerson.trim()) {
      toast.error("Please fill in all required fields")
      return
    }

    try {
      setLoading(true)

      await createMessage(form)

      toast.success("Message saved successfully")

      setForm({
        content: "",
        category: "",
        forPerson: "",
        date: ""
      })

      onCreated?.()

    } catch (err) {
      toast.error("Failed to save message")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex justify-center">
      <Card className="w-full max-w-lg border-2 border-black shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] rounded-xl overflow-hidden bg-cyan-300">
        
        <CardHeader className="px-6 py-1">
          <h2 className="text-2xl font-black uppercase tracking-tight text-black">
            Anything left unspoken?
          </h2>
        </CardHeader>
        
        <CardContent className="px-6">
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">

            {/* MESSAGE */}
            <div className="space-y-2">
              <Label className="font-bold text-sm uppercase tracking-wider">
                Your Message
              </Label>

              <Textarea
                name="content"
                placeholder="Write your thoughts here..."
                value={form.content}
                onChange={update}
                className="min-h-30 bg-white border-2 border-black focus-visible:border-emerald-500 focus-visible:ring-2 focus-visible:ring-emerald-200 transition-all"
              />

              <p className={cn(
                "text-xs text-right font-medium",
                form.content.length > 1000 ? "text-red-500" : "text-slate-400"
              )}>
                {form.content.length} characters
              </p>
            </div>

            {/* CATEGORY */}
            <div className="space-y-3">
              <Label className="font-bold text-sm uppercase tracking-wider">
                Category
              </Label>

              <div className="flex flex-wrap gap-2">
                {categories.map(c => (
                  <button
                    type="button"
                    key={c}
                    onClick={() =>
                      setForm(prev => ({ ...prev, category: c }))
                    }
                    className={cn(
                      "px-4 py-1.5 rounded-full text-xs font-bold border-2 border-black transition-all transform active:scale-95",
                      form.category === c
                        ? "bg-emerald-500 text-black translate-x-px translate-y-px"
                        : "bg-white text-black hover:bg-emerald-100"
                    )}
                  >
                    {c}
                  </button>
                ))}
              </div>
            </div>

            {/* GRID */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

              {/* FOR */}
              <div className="space-y-2">
                <Label className="font-bold text-sm uppercase tracking-wider">
                  For
                </Label>

                <Input
                  name="forPerson"
                  placeholder="Recipient name"
                  value={form.forPerson}
                  onChange={update}
                  className="bg-white border-2 border-black focus-visible:border-emerald-500 focus-visible:ring-2 focus-visible:ring-emerald-200 transition-all"
                />
              </div>

              {/* DATE */}
              <div className="space-y-2">
                <Label className="font-bold text-sm uppercase tracking-wider">
                  Date
                </Label>

                <Input
                  type="date"
                  name="date"
                  value={form.date}
                  onChange={update}
                  className="bg-white border-2 border-black focus-visible:border-emerald-500 focus-visible:ring-2 focus-visible:ring-emerald-200 transition-all"
                />
              </div>

            </div>

            {/* BUTTON */}
            <Button
              type="submit"
              disabled={loading}
              className="mt-4 h-12 text-lg font-black uppercase border-2 border-black bg-emerald-400 text-black hover:bg-emerald-600 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-none transition-all disabled:opacity-50"
            >
              {loading ? "Saving..." : "Save Message"}
            </Button>

          </form>
        </CardContent>
      </Card>
    </div>
  )
}