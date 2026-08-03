import { cva } from 'class-variance-authority'

import { cn } from '../utils/cn.js'

const buttonVariants = cva(
  'inline-flex items-center justify-center rounded-full text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50',
  {
    variants: {
      variant: {
        primary: 'bg-brand text-white hover:bg-brand/90',
        outline: 'border border-line bg-white text-foreground hover:bg-brand-soft',
        ghost: 'text-foreground hover:bg-brand-soft',
      },
      size: { default: 'h-10 px-5', sm: 'h-8 px-3 text-xs', lg: 'h-12 px-6' },
    },
    defaultVariants: { variant: 'primary', size: 'default' },
  },
)

export function Button({ className, variant, size, ...props }) {
  return <button className={cn(buttonVariants({ variant, size }), className)} {...props} />
}
