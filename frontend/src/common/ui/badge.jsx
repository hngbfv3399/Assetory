import { cn } from '../utils/cn.js'

export function Badge({ className, children }) {
  return <span className={cn('inline-flex w-fit items-center rounded-full bg-brand-soft px-2.5 py-1 text-xs font-semibold text-brand', className)}>{children}</span>
}
