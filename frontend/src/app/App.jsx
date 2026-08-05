import { QueryProvider } from './providers/QueryProvider.jsx'
import { AppRouter } from './router/AppRouter.jsx'
import '../App.css'
import { useSessionBootstrap } from '../pages/Login/useAuthSession.js'

export default function App() {
  return (
    <QueryProvider>
      <AppContent />
    </QueryProvider>
  )
}

function AppContent() {
  useSessionBootstrap()
  return <AppRouter />
}
