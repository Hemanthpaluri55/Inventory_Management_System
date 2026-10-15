import axios from 'axios'

// Local development defaults to Spring Boot on localhost:8080.
// In Vercel, set VITE_API_BASE_URL to the deployed backend origin (no /api suffix).
const apiOrigin = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '')
const api = axios.create({ baseURL: `${apiOrigin}/api`, timeout: 15000 })

api.interceptors.request.use(config => {
  const credentials = sessionStorage.getItem('inventoryAuth')
  if (credentials) config.headers.Authorization = `Basic ${credentials}`
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 && !window.location.pathname.includes('login')) {
      sessionStorage.removeItem('inventoryAuth')
      sessionStorage.removeItem('inventoryUser')
      window.dispatchEvent(new CustomEvent('inventory:unauthorized'))
    }
    return Promise.reject(error)
  }
)

export default api
