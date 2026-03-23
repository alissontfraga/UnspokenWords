export async function apiFetch(
  endpoint: string,
  options: RequestInit = {}
) {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}${endpoint}`,
    {
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
      },
      ...options,
    }
  )

  const contentType = response.headers.get("content-type")

  // 🔥 IMPORTANTE
  if (response.status === 401) {
    throw new Error("Unauthorized")
  }

  if (!response.ok) {
    let message = "Erro na requisição"

    if (contentType?.includes("application/json")) {
      const data = await response.json()
      message = data.message || message
    }

    throw new Error(message)
  }

  if (contentType?.includes("application/json")) {
    return response.json()
  }

  return null
}