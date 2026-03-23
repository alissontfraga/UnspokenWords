"use client"

import Link from "next/link"
import { LogOut } from "lucide-react"

import {
  NavigationMenu,
  NavigationMenuList,
  NavigationMenuItem,
} from "@/components/ui/navigation-menu"

import { Button } from "@/components/ui/button"

export function Navbar() {
  function handleLogout() {
    // chamar endpoint de logout
    fetch("/api/logout", { method: "POST" })
    window.location.href = "/signin"
  }

  return (
    <div className="">
      <div className="flex h-12 items-center justify-between px-6">

        {/* Logo */}
        <Link href="/messages" className="font-bold text-lg bg-cyan-300 px-4 py-1 rounded-md shadow text-shadow-2xs">
          UnspokenWords
        </Link>

        {/* Logout */}
        <NavigationMenu>
          <NavigationMenuList>
            <NavigationMenuItem>
              <Button className="bg-red-500 shadow text-shadow-2xs hover:bg-red-600 gap-1" variant="destructive" onClick={handleLogout}>
              <LogOut />
                Logout
              </Button>
            </NavigationMenuItem>
          </NavigationMenuList>
        </NavigationMenu>

      </div>
    </div>
  )
}