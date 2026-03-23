"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { login } from "@/services/auth.service"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Field, FieldDescription, FieldGroup, FieldLabel } from "@/components/ui/field"
import { Input } from "@/components/ui/input"

export function SigninForm({
  className,
  ...props
}: React.ComponentProps<"div">) {

  const router = useRouter()

  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  async function handleSubmit( e: React.SyntheticEvent<HTMLFormElement> ) {
    e.preventDefault()
    setError("")
    setLoading(true)

    try {
      await login({ username, password })

      // backend já setou cookie httpOnly
      router.push("/messages")

    } catch (err) {
      setError("Email ou senha inválidos")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={cn("flex flex-col gap-6", className)} {...props}>
      <Card className="shadow-2xl">
        <CardHeader className="text-center">
          <CardTitle className="text-xl">
            Welcome back!
          </CardTitle>
        </CardHeader>

        <CardContent>
          <form onSubmit={handleSubmit}>
            <FieldGroup>

              <Field>
                <FieldLabel htmlFor="username">Username</FieldLabel>
                <Input
                  id="username"
                  type="username"
                  placeholder="usernameexample"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="focus-visible:border-emerald-500 focus-visible:ring-2 focus-visible:ring-emerald-200 transition-all"
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="password">
                  Password
                </FieldLabel>
                <Input
                  id="password"
                  type="password"
                  placeholder="pass123example"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="focus-visible:border-emerald-500 focus-visible:ring-2 focus-visible:ring-emerald-200 transition-all"
                />
              </Field>

              {error && (
                <p className="text-sm text-red-500">
                  {error}
                </p>
              )}

              <Field>
                <Button type="submit" disabled={loading}>
                  {loading ? "Logging in..." : "Login"}
                </Button>

                <FieldDescription className="text-center">
                  Don&apos;t have an account?{" "}
                  <a
                    href="/signup"
                    className="underline"
                  >
                    Sign up
                  </a>
                </FieldDescription>
              </Field>

            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}