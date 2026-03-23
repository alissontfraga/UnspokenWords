import { cookies } from "next/headers"
import { redirect } from "next/navigation"
import { Navbar } from "@/components/mine/Navbar"

export default async function ProtectedLayout({
  children,
}: {
  children: React.ReactNode
}) {

  return (
    <div className="min-h-screen flex flex-col">

      <Navbar />

      <main className="flex-1">
        {children}
      </main>

    </div>
  )
}