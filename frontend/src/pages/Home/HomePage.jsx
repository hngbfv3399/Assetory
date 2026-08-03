import { HomeIntro } from './components/HomeIntro.jsx'
import { useHomeProducts } from './hooks/useHomeProducts.js'

export function HomePage() {
  const productsQuery = useHomeProducts()

  return (
    <main>
      <HomeIntro
        products={productsQuery.data?.products ?? []}
        isProductsLoading={productsQuery.isLoading}
      />
    </main>
  )
}
