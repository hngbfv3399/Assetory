export function EmptyState({ title, description }) {
  return (
    <div className="rounded-2xl border border-dashed border-line bg-white px-6 py-14 text-center">
      <p className="m-0 font-semibold">{title}</p>
      {description ? <p className="mb-0 mt-2 text-sm text-muted">{description}</p> : null}
    </div>
  )
}
