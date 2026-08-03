import { Button } from './button.jsx'

export function ErrorState({ description = '정보를 불러오지 못했습니다.', onRetry }) {
  return (
    <div className="rounded-2xl border border-line bg-white px-6 py-10 text-center">
      <p className="m-0 text-sm text-muted">{description}</p>
      {onRetry ? <Button className="mt-4" onClick={onRetry}>다시 시도</Button> : null}
    </div>
  )
}
