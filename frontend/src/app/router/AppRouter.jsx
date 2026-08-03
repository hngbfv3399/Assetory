import { BrowserRouter, Route, Routes } from 'react-router-dom'

import { HomePage } from '../../pages/Home/HomePage.jsx'
import { ExplorePage } from '../../pages/Explore/ExplorePage.jsx'
import { PricingPage } from '../../pages/Pricing/PricingPage.jsx'
import { FeaturesPage } from '../../pages/Features/FeaturesPage.jsx'
import { ProductDetailPage } from '../../pages/ProductDetail/ProductDetailPage.jsx'
import { LoginPage } from '../../pages/Login/LoginPage.jsx'
import { RootLayout } from '../../layouts/RootLayout.jsx'

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<RootLayout />}>
          <Route index element={<HomePage />} />
          <Route path="products" element={<ExplorePage />} />
          <Route path="products/:productId" element={<ProductDetailPage />} />
          <Route path="pricing" element={<PricingPage />} />
          <Route path="features" element={<FeaturesPage />} />
          <Route path="login" element={<LoginPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
