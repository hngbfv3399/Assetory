import { cn } from '../utils/cn.js'

export function Skeleton({ className }) {
  return <div className={cn('animate-pulse rounded-xl bg-stone-200', className)} />
}
