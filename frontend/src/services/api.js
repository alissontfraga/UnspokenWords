import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true // importantíssimo para cookies HttpOnly
});


// resposta global de erro simples (opcional)
api.interceptors.response.use(
  res => res,
  err => {
    // você pode adicionar lógica global de logout quando 401
    return Promise.reject(err);
  }
);