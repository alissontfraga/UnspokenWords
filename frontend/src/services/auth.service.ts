import { apiFetch } from "@/lib/api"

export async function login(data: {
  username: string
  password: string
}) {
  return apiFetch("/api/auth/login", {
    method: "POST",
    body: JSON.stringify(data),
  })
}

export async function register(data: {
  username: string
  password: string
}) {
  return apiFetch("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(data),
  })
}