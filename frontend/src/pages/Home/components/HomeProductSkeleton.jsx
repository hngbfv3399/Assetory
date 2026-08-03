import { Skeleton } from '../../../common/ui/skeleton.jsx'

export function HomeProductSkeleton() {
  return <div><Skeleton className="aspect-[1.12] w-full" /><Skeleton className="mt-4 h-4 w-2/5" /><Skeleton className="mt-3 h-5 w-4/5" /></div>
}
