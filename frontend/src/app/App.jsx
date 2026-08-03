import { QueryProvider } from './providers/QueryProvider.jsx'
import { AppRouter } from './router/AppRouter.jsx'
import '../App.css'

export default function App() {
  return (
    <QueryProvider>
      <AppRouter />
    </QueryProvider>
  )
}
